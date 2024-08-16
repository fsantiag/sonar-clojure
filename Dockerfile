FROM sonarqube:10.6-community

COPY target/sonar-clojure-plugin-*-SNAPSHOT.jar $SONARQUBE_HOME/extensions/plugins/