# Base image avec Java 17
FROM eclipse-temurin:21-jdk-alpine

# Argument pour le jar
ARG JAR_FILE=target/client-service-0.0.1-SNAPSHOT.jar

# Copier le jar dans l'image
COPY ${JAR_FILE} app.jar

# Exposer le port de l'application
EXPOSE 8081

# Commande pour lancer l'application
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]