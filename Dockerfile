# Dockerfile
FROM openjdk:24-jdk-slim

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Establecer directorio de trabajo
WORKDIR /app

# Copiar wrapper de Gradle primero
COPY gradlew .
COPY gradle/ ./gradle/

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Copiar archivos de configuración de Gradle
COPY build.gradle .
COPY settings.gradle .

# Descargar dependencias (para aprovechar cache de Docker)
RUN ./gradlew dependencies --no-daemon --refresh-dependencies

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN ./gradlew clean build -x test --no-daemon

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/app-0.0.1-SNAPSHOT.jar"]