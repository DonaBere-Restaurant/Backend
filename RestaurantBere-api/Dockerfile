FROM openjdk:21-jdk-slim

ARG JAR_FILE=target/Bere-api-0.0.1.jar

COPY ${JAR_FILE} bere-api.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","bere-api.jar"]
