FROM openjdk:21-jdk-slim


RUN apt-get update && \
    apt-get install -y libfreetype6 libxrender1 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*


ARG JAR_FILE=target/Bere-api-0.0.1.jar

# Copiar el archivo JAR al contenedor
COPY ${JAR_FILE} bere-api.jar

# Exponer el puerto 8080
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","bere-api.jar"]