FROM openjdk:jre-slim-stretch
EXPOSE 8100

ADD /file-service.jar file-service.jar
ADD . /sharedfolders/root/

ENTRYPOINT ["java", "-Xmx200m", "-jar", "file-service.jar"]