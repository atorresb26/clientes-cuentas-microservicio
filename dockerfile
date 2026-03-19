FROM eclipse-temurin:21-jdk

WORKDIR /app

ARG JAR_FILE=target/banking-service*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
