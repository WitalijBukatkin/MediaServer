FROM openjdk:jre-slim-stretch

ADD /build/libs/auth-service.jar auth-service.jar

CMD ["java", "-Xmx200m", "-jar", "auth-service.jar"]

HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost:8200/actuator/health || exit 1

EXPOSE 8200