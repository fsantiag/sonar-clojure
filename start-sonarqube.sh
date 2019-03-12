#!/usr/bin/env bash
set -eu
./mvnw clean package
docker build . --tag sonarqube_local_image
docker rm -f sonarqube_local_image
docker run --name sonarqube_local_image  -p 9000:9000 sonarqube_local_image


