FROM openjdk:26-jdk-slim-bullseye
WORKDIR /app
COPY target/_AMEBA_-_PESEZ_-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]