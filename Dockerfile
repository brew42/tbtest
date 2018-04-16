FROM java:8
VOLUME /tmp
ADD build/libs/awscodedeploy-null.null.jar awscodedeploy.jar
RUN bash -c 'touch /awscodedeploy.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/awscodedeploy.jar", "-Dspring.profiles.active=dev"]

 