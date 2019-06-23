FROM openjdk:jre-slim-stretch
EXPOSE 5000

ADD /auth-service.jar auth-service.jar
ADD . /sharedfolders/root/

ENTRYPOINT ["java", "-Xmx200m", "-jar", "auth-service.jar"]