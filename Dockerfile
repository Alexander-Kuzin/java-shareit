#FROM bellsoft/liberica-openjdk-debian:11 AS shareit
FROM amazoncorretto:11-alpine-jdk AS shareit
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]