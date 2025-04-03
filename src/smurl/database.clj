(ns smurl.database
  (:require
   [clojure.tools.logging :as log]
   [smurl.database.urls :as urls]
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

(defn create-tables [] (do (log/info "creating database tables ...") (urls/create-urls-table db)))

(defn drop-tables [] (do (log/info "dropping database tables") (urls/drop-urls-table db)))


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
  (do
    (log/info (str "Creating shortened url for " long-url))
    (let [insert-res (urls/create-url tx {:long_url long-url})
          row (first insert-res)
          id (:id row)
          short-url (hashing/base-62 id)
          _ (urls/set-short-url tx {:id id :short_url short-url})] {:id id :short-url short-url :long-url long-url}))
  )
  ;; get the id of the created url?
)

(defn get-url-by-long-url [long-url]
  (let [row (urls/get-url-by-long-url db {:long_url long-url})]
    (handle-urls-row row)))

(defn get-url-by-short-url [short-url]
  (let 
   [row (urls/get-url-by-short-url db {:short_url short-url})]
   (handle-urls-row row)))


(def local-dev-data {:urls (map (fn [url] {:long-url url}) ["https://en.wikipedia.org/wiki/URL_shortening"
                                                 "https://en.wikipedia.org/wiki/Hash_function"
                                                 "https://www.base64encode.org/"
                                                 "https://en.wikipedia.org/wiki/Checksum"
                                                 "https://en.wikipedia.org/wiki/Message_authentication"
                                                 "https://en.wikipedia.org/wiki/Authenticated_encryption"
                                                 "https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation"
                                                 "https://en.wikipedia.org/wiki/Merkle_tree"
                                                 "https://en.wikipedia.org/wiki/BitTorrent"
                                                 "https://en.wikipedia.org/wiki/Popcorn_Time"
                                                 "https://en.wikipedia.org/wiki/Big_Buck_Bunny"
                                                 "https://en.wikipedia.org/wiki/UV_mapping"
                                                 "https://en.wikipedia.org/wiki/Conformal_map"
                                                 "https://en.wikipedia.org/wiki/Liouville%27s_theorem_(conformal_mappings)"
                                                 ])})


(defn populate-local-dev-db
  "Insert data for development into the local database"
  ([] (populate-local-dev-db local-dev-data))
  ([data]
   (do (log/info "Populating local dev database ...")
       (let [long-urls (map :long-url (:urls data))] (loop [urls long-urls]
                                                       (if (not-empty urls) (do (create-url (first urls)) (recur (rest urls))) nil))))))


(comment
  local-dev-data
  (do (log/info "Populating local dev database ...")
      (let [_ (println local-dev-data) long-urls (map :long-url (:urls local-dev-data)) _ (println long-urls)] (loop [urls long-urls]
                                                                                                                 (if (not-empty urls) (do (println (first urls)) (recur (rest urls))) nil))))
  )

(defn init-localdev
  "Create database schema and insert localdev data"
  []
  (do (create-tables) (populate-local-dev-db))
  )

(defn teardown-localdev
  "Drop tables that were used for local development"
  []
  (drop-tables)
  )
