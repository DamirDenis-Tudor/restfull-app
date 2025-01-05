FROM mongo:latest

ENV MONGO_INITDB_ROOT_USERNAME=root \
    MONGO_INITDB_ROOT_PASSWORD=root \
    MONGO_INITDB_DATABASE=lectures-db

ENV MONGODB_USER=user \
    MONGODB_PASS=password \
    MONGODB_DATABASE=lectures-db

COPY .scripts/1-grant.js /docker-entrypoint-initdb.d/1-grant.js
COPY .scripts/2-init-mongo.js /docker-entrypoint-initdb.d/2-init-mongo.js