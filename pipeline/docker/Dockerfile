FROM eclipse-temurin:21-jdk

ENV APP_SHORTNAME=my-demo-app
ENV APPCONFIGPATH=/opt/appconfig
ENV LOGDIR=${APPCONFIGPATH}/logs

RUN mkdir -p ${APPCONFIGPATH}/config ${APPCONFIGPATH}/secret ${LOGDIR}
RUN umask 0002

#RUN apt-get update && apt-get install -y openssh-client filebeat && rm -rf /var/lib/apt/lists/*

COPY docker/custom_entrypoint.sh /opt/custom_entrypoint.sh
RUN chmod +x /opt/custom_entrypoint.sh

# Copy Gradle jar output
COPY build/libs/app.jar ${APPCONFIGPATH}/app.jar

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "/opt/custom_entrypoint.sh"]
