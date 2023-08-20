FROM amazoncorretto:11-alpine-jdk as gateway
COPY target/*.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]