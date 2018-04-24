if [ ! -z "$TRAVIS_TAG" ]; then
    sed -i "s/[0-9].\+-SNAPSHOT/${TRAVIS_TAG:1}/g" pom.xml
fi

mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=fsantiag -Dsonar.login=$SONAR_TOKEN
