(defproject smurl "0.1.0-SNAPSHOT"
  :description "A little url shortener written in Clojure."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.10.238"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [ring/ring-devel "1.8.2"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.1"]
                 [com.layerware/hugsql "0.5.3"]
                 [com.layerware/hugsql-core "0.5.3"]
                 [com.layerware/hugsql-adapter-next-jdbc "0.5.3"]
                 [org.postgresql/postgresql "42.3.1"]
                 [environ "1.2.0"]]
  :plugins [[lein-ring "0.12.6"]
            [lein-environ "1.2.0"]]
            ;; [lein-figwheel "0.5.18"]] ; 
  :ring {:handler smurl.app/app :init smurl.database/create-tables}
  ;; TODO: set up frontend client
  ;; :cljsbuild {:builds [{:id "smurl"
  ;;                       :source-paths ["src/smurl/client"]
  ;;                       :figwheel true
  ;;                       :compiler {:main "example.core"
  ;;                                  :asset-path "js/out"
  ;;                                  :output-to "resources/public/js/example.js"
  ;;                                  :output-dir "resources/public/js/out"}}]}
  :profiles {
             :dev {
                   :env {
                         :db-type "postgresql"
                         :db-name "postgres"
                         :db-host "localhost"
                         :db-user "smurl"
                         :db-password "smurl"
                         :db-ssl false
                   }
             }
  }
  ) 
