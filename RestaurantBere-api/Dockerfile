FROM eclipse-temurin:21-jdk

RUN apt-get update && \
    apt-get install -y libfreetype6 libxrender1 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=target/Bere-api-0.0.1.jar

COPY ${JAR_FILE} bere-api.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","bere-api.jar"]FROM eclipse-temurin:21-jdk-slim

RUN apt-get update && \
    apt-get install -y libfreetype6 libxrender1 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=target/Bere-api-0.0.1.jar

COPY ${JAR_FILE} bere-api.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","bere-api.jar"]
