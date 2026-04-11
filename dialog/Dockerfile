FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -pl dialog -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/dialog/target/*.jar dialog.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/dialog.jar"]