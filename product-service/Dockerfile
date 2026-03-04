FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw -q -DskipTests -pl product-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/product-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [\"java\",\"-jar\",\"/app/app.jar\"]
