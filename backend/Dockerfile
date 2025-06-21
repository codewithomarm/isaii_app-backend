# Dockerfile
FROM openjdk:17-jdk-slim

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties* ./
COPY gradlew .
COPY gradle/ ./gradle/

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Descargar dependencias (para aprovechar cache de Docker)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN ./gradlew build -x test --no-daemon

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/app-0.0.1-SNAPSHOT.jar"]