# ======================
# Build Stage
# ======================
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copiar configuración Maven primero
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn -B clean package -DskipTests

# ======================
# Runtime Stage
# ======================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Instalar curl para el healthcheck del contenedor
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario sin privilegios
RUN useradd -m springuser

# Copiar el JAR generado
COPY --from=build /workspace/target/*.jar ./app.jar

# Asignar permisos
RUN chown -R springuser:springuser /app

# Cambiar usuario
USER springuser

# Puerto del microservicio
EXPOSE 8081

# Ejecutar aplicación con límites de memoria JVM
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","/app/app.jar"]