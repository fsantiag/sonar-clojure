FROM sonarqube:8.6.1-community

COPY target/sonar-clojure-plugin-*-SNAPSHOT.jar $SONARQUBE_HOME/extensions/plugins/