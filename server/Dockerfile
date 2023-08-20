FROM amazoncorretto:11-alpine-jdk as server
COPY target/*.jar server.jar
ENTRYPOINT ["java","-jar","/server.jar"]