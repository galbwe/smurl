(ns smurl.database
  (:require [smurl.database.urls :as urls]
            [hugsql.core :as hugsql]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [next.jdbc :as jdbc]
            [smurl.hashing :as hashing]
            [environ.core :refer [env]]))

;; TODO: read database configuration from environment variables
(def db-spec {:dbtype (env :db-type)
          :dbname (env :db-name)
          :host (env :db-host)
          :user (env :db-user)
          :password (env :db-password)
          :ssl (= (env :db-ssl) "true")})
            ;; :sslfactory "org.postgresql.ssl.NonValidatingFactory"
        ;;   })

(def db (jdbc/get-datasource db-spec))

;; TODO: read any adapter config from environment variables
(hugsql/set-adapter! (next-adapter/hugsql-adapter-next-jdbc))

(defn create-tables [] (urls/create-urls-table db))

(defn drop-tables [] (urls/drop-urls-table db))


(defn- handle-urls-row [row]  (if
     (nil? row)
      nil
     {
      :id (:id row)
      :short-url (:short_url row)
      :long-url (:long_url row)
     } 
  ))

;; TODO: exception handling
(defn create-url [long-url]
(jdbc/with-transaction [tx db]
  (let [
        insert-res (urls/create-url tx {:long_url long-url})
        row (first insert-res)
        id (:id row)
        short-url (hashing/base-62 id)
        _ (urls/set-short-url tx {:id id :short_url short-url})
        ] {:id id :short-url short-url :long-url long-url}))
  ;; get the id of the created url?
)

(defn get-url-by-long-url [long-url]
  (let [row (urls/get-url-by-long-url db {:long_url long-url})]
    (handle-urls-row row)))

(defn get-url-by-short-url [short-url]
  (let 
   [row (urls/get-url-by-short-url db {:short_url short-url})]
   (handle-urls-row row)))

(get-url-by-short-url "4")
