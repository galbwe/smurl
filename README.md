# smurl

Make small urls.

This is a url-shortener pet project for learning the Clojure development ecosystem.

## Local Development

To run and work with the project locally, you will need the following installed:
- Docker
- Leiningen
- psql


To run the project:
1. Run the local development database with `docker-compose up db`.
1. Run the api with lein api. This should also create and populate database tables in a database called smurl. 
1. If needed, connect to the local development database with `psql -h localhost -U smurl` and the password `smurl`. Alternatively, you may connect to the database with the client of your choice using th postgres credentials for local development found in the `dev` profile in `project.clj`. List shortened urls that were created with `SELECT * from urls;`.
1. Check that the api will redirect to a shortened url by opening [localhost:8080/a](localhost:8080/a) in a browser.
1. Create a new shortened url with an http request similar to
```bash
curl --location 'http://localhost:8080/api/urls' \
--header 'Content-Type: application/json' \
--data '{
    "longUrl": "https://en.wikipedia.org/wiki/Clojure"
}'
```
1. The database tables should get cleaned up automatically when you stop the ring server.
