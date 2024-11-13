FROM mongo:latest

ENV MONGO_INITDB_ROOT_USERNAME=user \
    MONGO_INITDB_ROOT_PASSWORD=password \
    MONGO_INITDB_DATABASE=lectures-db

COPY ./.scripts/init-mongo.js /docker-entrypoint-initdb.d/init-mongo.js