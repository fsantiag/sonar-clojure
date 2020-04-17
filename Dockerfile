FROM sonarqube:7.9-community

COPY target/sonar-clojure-plugin-*-SNAPSHOT.jar $SONARQUBE_HOME/extensions/plugins/