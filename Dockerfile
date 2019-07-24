FROM java:8
ADD target/caam-user.jar  caam-user.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "caam-user.jar"]