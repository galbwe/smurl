-- Maps short urls to the original url

-- :name create-urls-table
-- :command :execute
-- :result :raw
-- :doc Create the urls table
create table IF not exists urls (
    id bigint generated always as identity primary key,
    short_url varchar(10),
    long_url text unique not null
);

-- :name drop-urls-table
-- :command :execute
-- :result :raw
-- :doc Drop the urls table
drop table urls;

-- :name create-url :<!
-- :doc Create a new url from a long_url and return the id of the new record
insert into urls (long_url)
values (:long_url)
returning id

-- :name set-short-url :! :n
-- :doc Update the short_url of a given url and return the number of affected rows
update urls
set short_url = :short_url
where id = :id

-- :name get-url-by-long-url :? :1
-- :doc Get a url by its long url
select id, short_url, long_url
from urls
where long_url = :long_url

-- :name get-url-by-short-url :? :1
-- :doc Get a url by its short url
select id, short_url, long_url
from urls
where short_url = :short_url
