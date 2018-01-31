#!/bin/bash
set -x

docker run -it -e "SPRING_DATA_MONGODB_URI=mongodb://172.17.0.3:27017/panorama" \
               -e "SPRING_RABBITMQ_HOST=172.17.0.4" \
               -e "SPRING_RABBITMQ_PORT=5672" \
               -e "SPRING_RABBITMQ_USERNAME=guest" \
               -e "SPRING_RABBITMQ_PASSWORD=guest" \
               -e "APPLICATION_BASEURL=http://tour-editor.leafyjava.com/editor" \
    -v /data:/home/pannellum-tour-maker \
    --name tour-editor \
    -p 8080:8080 \
    -d \
    downhillski/pannellum-tour-maker