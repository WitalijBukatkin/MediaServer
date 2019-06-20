FROM openjdk:jre-slim-stretch

ADD /config-service.jar config-service.jar

CMD ["java", "-Xmx200m", "-jar", "config-service.jar"]

EXPOSE 8888