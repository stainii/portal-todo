FROM openjdk:14-jdk
VOLUME /tmp
EXPOSE 2007
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS_TODO -Djava.security.egd=file:/dev/./urandom -jar /app.jar --security.jwt.secret=${JWT_SECRET} --spring.data.mongodb.host=${DATABASE_HOST_TODO} --spring.data.mongodb.port=${DATABASE_PORT_TODO} --spring.data.mongodb.username=${DATABASE_USERNAME_TODO} --spring.data.mongodb.password=${DATABASE_PASSWORD_TODO} --spring.data.mongodb.database=${DATABASE_NAME_TODO} --eureka.client.service-url.defaultZone=${EUREKA_SERVICE_URL}
