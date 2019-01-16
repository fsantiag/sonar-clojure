#!/usr/bin/env bash
set -eu
./mvnw clean package
docker build . --tag sonarqube_local_image
if [ ! "$(docker ps -a -q -f 'name=sonarqube_local_test')" ]; then
  echo "Sonar container is not running"
  docker run --name sonarqube_local_test  -p 9000:9000 sonarqube_local_image
else
  echo "Sonar is running - removing and restarting the image"
  docker rm -f sonarqube_local_test
  docker run --name sonarqube_local_test  -p 9000:9000 sonarqube_local_image
fi

