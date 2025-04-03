(ns smurl.database.urls
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "smurl/database/sql/urls.sql")

(hugsql/def-sqlvec-fns "smurl/database/sql/urls.sql")
