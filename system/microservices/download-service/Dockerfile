FROM openjdk:jre-slim-stretch
EXPOSE 8200

ADD /download-service.jar download-service.jar
ADD . /sharedfolders/root/

ENTRYPOINT ["java", "-Xmx200m", "-jar", "download-service.jar"]