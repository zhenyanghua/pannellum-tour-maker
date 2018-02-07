#!/usr/bin/env bash
export DOCKER_REGISTRY=localhost:5000
export IMAGE_NAME=pannellum-tour-maker

mvn clean package
docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME} .
docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}