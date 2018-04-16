FROM java:8
VOLUME /tmp
RUN echo $(ls -l)
ADD build/libs/tbtest-null.null.jar awscodedeploy.jar
RUN bash -c 'touch /awscodedeploy.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/awscodedeploy.jar", "-Dspring.profiles.active=dev"]

 