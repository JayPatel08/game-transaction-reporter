# --- Compilation Layer ---
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY ./src ./src
RUN mvn clean package -DskipTests

# --- Runtime Execution Layer ---
FROM tomcat:10.1-jdk17
WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY --from=build /app/target/*.war webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]