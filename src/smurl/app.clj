(ns smurl.app
  (:require 
   [compojure.core :refer [defroutes GET POST]] 
   [compojure.route :as route] 
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.util.response :refer [response status redirect not-found]]
   [smurl.database :refer [create-url get-url-by-long-url get-url-by-short-url]]))

;; TODO: add middleware to convert to and from camel case
;; TODO: add middleware that handles other types of exceptions
;; TODO: validate the long url
(defn create-url-handler
"Handler that creates a new shortened url if it does not already exist, and retrieves an existing url"
  [request]
  (try
    (let
     [long-url (-> request :body (get "longUrl"))
      url (create-url long-url)
      body {"id" (:id url) "shortUrl" (:short-url url) "longUrl" (:long-url url)}]
      (-> body response (status 201)))
    (catch org.postgresql.util.PSQLException e
      (let
       [sql-state (.getSQLState e)]
        (if
         (= sql-state "23505")
        ;;  the url already exists in the db. Return the url.
          (let 
           [long-url (-> request :body (get "longUrl")) url (get-url-by-long-url long-url)]
            (response {"id" (:id url) "shortUrl" (:short-url url) "longUrl" (:long-url url)}))
          (throw e))))))


;; TODO: handle database exceptions
(defn redirect-handler
 "Handler that retrieves a long url from a short url and redirects to the long url" 
 [request]
 (let 
  [short-url (-> request :params :hash)
   url (get-url-by-short-url short-url)
   long-url (:long-url url)]
   (if (nil? long-url) nil (redirect long-url 301))
    )
  )


(defroutes app
  (GET "/" [] "<h1>Smurl - A small url shortener written in Clojure</h1>")
  (-> (GET "/healthcheck" [] "healthy") wrap-json-response)
  (-> (POST "/api/urls" request (create-url-handler request)) wrap-json-body wrap-json-response)
  (GET "/:hash{[a-zA-Z0-9]+}" request (redirect-handler request))
  ;; (GET "/api/urls")
  (route/not-found "<h1>Page not found</h1>"))
