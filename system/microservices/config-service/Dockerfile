FROM openjdk:jre-slim-stretch

ADD /build/libs/config-service.jar config-service.jar

CMD ["java", "-Xmx200m", "-jar", "config-service.jar"]

HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://config:8888/actuator/health || exit 1

EXPOSE 8888