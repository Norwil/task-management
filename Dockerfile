FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/task-management-api.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT java \
  -Dspring.profiles.active=prod \
  -Dspring.datasource.username=${POSTGRES_USER} \
  -Dspring.datasource.password=${POSTGRES_PASSWORD} \
  -Dspring.datasource.url=jdbc:postgresql://task-postgres:5432/${POSTGRES_DB} \
  -jar app.jar