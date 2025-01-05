FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/*SNAPSHOT-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]