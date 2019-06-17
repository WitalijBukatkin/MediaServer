FROM openjdk:jre-slim-stretch
EXPOSE 8100

ADD /build/libs/file-service.jar file-service.jar
ADD . /mediaserver
ADD . /Users/vitalijbukatkin/IdeaProjects/file-service/testdirectory

ENTRYPOINT ["java", "-Xmx200m", "-jar", "file-service.jar"]