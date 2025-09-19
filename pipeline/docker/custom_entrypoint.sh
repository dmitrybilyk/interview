#!/bin/bash
# Custom entrypoint for testing Gradle-built app

umask 0002
PORT=8080
env_indicator=${ENVIRONMENT:-dev}
export env_indicator

export APP_ARGS="-DCERT_DIR=${APPCONFIGPATH}/secret -DCONFIG_DIR=${APPCONFIGPATH}/config -DLOG_DIR=${LOGDIR}"

echo "Java version:"
java -version
echo "Running on ${env_indicator} environment on port number $PORT"

JAVA_CMD="java"
JAR=${APPCONFIGPATH}/app.jar

echo "-------------check-------------"

# Example of javaagent
# APPD_JAVA_AGENT="-javaagent:/opt/appdyn/javaagent/current/javaagent.jar"

JAVA_ARGS="${APPD_JAVA_AGENT} -DAPP_SHORTNAME=$APP_SHORTNAME -Dspring.profiles.active=${env_indicator} ${CERTS} ${APP_ARGS}"

CMD="$JAVA_CMD -jar $JAVA_ARGS $JAR"
echo "Executing: $CMD"
exec $CMD
