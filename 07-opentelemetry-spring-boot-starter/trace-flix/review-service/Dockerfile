FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
