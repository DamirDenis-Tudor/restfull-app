FROM mysql:latest

ENV MYSQL_DATABASE=study-database \
    MYSQL_USER=user \
    MYSQL_PASSWORD=password \
    MYSQL_ROOT_PASSWORD=root-password

COPY ./.scripts/init.sql /docker-entrypoint-initdb.d/
# COPY ./.scripts/insert.sql /docker-entrypoint-initdb.d/