FROM mysql:latest

ENV MYSQL_DATABASE=study-database \
    MYSQL_USER=user \
    MYSQL_PASSWORD=password \
    MYSQL_ROOT_PASSWORD=root-password

COPY .scripts/1-grant.sql /docker-entrypoint-initdb.d/
COPY .scripts/2-create.sql /docker-entrypoint-initdb.d/
COPY .scripts/3-insert.sql /docker-entrypoint-initdb.d/