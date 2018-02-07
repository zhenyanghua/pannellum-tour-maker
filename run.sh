#!/bin/bash

echo "********************************************************"
echo "Waiting for the configuration server to start on port $CONFIGSERVER_PORT"
echo "********************************************************"
while ! `nc -z config-server $CONFIGSERVER_PORT `; do sleep 3; done
echo ">>>>>>>>>>>> Configuration Server has started"

echo "********************************************************"
echo "Waiting for the MongoDB server to start on port $MONGO_PORT"
echo "********************************************************"
while ! `nc -z one-mongo $MONGO_PORT`; do sleep 3; done
echo ">>>>>>>>>>>> MongoDB Server has started"

echo "********************************************************"
echo "Waiting for the RabbitMQ server to start on port $RABBIT_PORT"
echo "********************************************************"
while ! `nc -z one-rabbit $RABBIT_PORT`; do sleep 3; done
echo ">>>>>>>>>>>> RabbitMQ Server has started"


echo "********************************************************"
echo "Starting Tour Editor Server with Configuration Service :  $CONFIGSERVER_URI";
echo "********************************************************"
java -Dspring.cloud.config.uri=$CONFIGSERVER_URI \
     -Dspring.profiles.active=$PROFILE \
     -Dapplication.baseUrl=$APP_BASE_URL/tour-editor \
     -Djava.security.egd=file:/dev/./urandom \
     -jar /tour-editor.jar
