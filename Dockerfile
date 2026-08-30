# Stage 1 — Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

# Stage 2 — Runtime
FROM gcr.io/distroless/java21-debian13

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]