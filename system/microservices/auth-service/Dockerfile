FROM openjdk:jre-slim-stretch

ADD /auth-service.jar auth-service.jar

CMD ["java", "-Xmx200m", "-jar", "auth-service.jar"]

EXPOSE 5000