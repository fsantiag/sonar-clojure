FROM sonarqube:7.1

COPY target/sonar-clojure-plugin-*-SNAPSHOT.jar $SONARQUBE_HOME/extensions/plugins/
