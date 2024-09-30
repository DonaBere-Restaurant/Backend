# FROM jelastic/maven:3.9.5-openjdk-21 AS build
# COPY /RestaurantBere-api/pom.xml .
# RUN mvn clean package -DskipTests

# FROM openjdk:21-jdk-slim
FROM amazoncorretto:21-alpine-jdk
COPY /target/RestaurantBere-api-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
