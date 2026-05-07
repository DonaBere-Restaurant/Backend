FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/Bere-api-0.0.1.jar bere-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","bere-api.jar"]
