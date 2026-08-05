# --- Compilation Layer ---
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B    #
COPY src ./src
RUN mvn clean package -DskipTests

# --- Runtime Execution Layer ---
FROM eclipse-temurin:17-jre-jammy

# For the production environment, we should create and run as a non-root user
# RUN useradd -m appuser
# USER appuser

WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]