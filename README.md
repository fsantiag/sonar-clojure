# SonarClojure
> A SonarQube plugin to analyze Clojure source.

[![Build Status](https://travis-ci.org/fsantiag/sonar-clojure.svg?branch=master)](https://travis-ci.org/fsantiag/sonar-clojure)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=alert_status
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=coverage
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)

## Current State

### Features:
* Static code analysis powered by [eastwood](https://github.com/jonase/eastwood) and [kibit](https://github.com/jonase/kibit).
* Detection of outdated dependencies/plugins powered by [lein-ancient](https://github.com/xsc/lein-ancient).
* Coverage reports powered by [cloverage](https://github.com/cloverage/cloverage).
* Detection of vulnerable dependencies powered by [lein-nvd](https://github.com/rm-hull/lein-nvd).

> This plugin was inspired in the previous [SonarClojure](https://github.com/zmsp/sonar-clojure) that at
this moment is not under development and doesn't support SonarQube 6.7. Since the changes to port
the old plugin were very extensive, I decided to start from scratch and use the old plugin as inspiration.

## Installation

In order to install SonarClojure:
1. Download the [latest](https://github.com/fsantiag/sonar-clojure/releases) jar of the plugin.
2. Place the jar in the SonarQube server plugins directory, usually located under: `/opt/sonarqube/extensions/plugins/`
3. Restart the SonarQube server.

## Usage
1. Change your ***project.clj*** file and add the required plugins:

    ```clojure
    :plugins [[jonase/eastwood "0.2.5"]
              [jonase/kibit "0.1.6"]
              [lein-ancient "0.6.15"]
              [lein-cloverage "1.0.13"]
              [lein-nvd "0.6.0"]]
              ```

2. Create a ***sonar-project.properties*** file in the root folder of your app:

    ```properties
    sonar.projectKey=your-project-key
    sonar.projectName=YourProjectName
    sonar.projectVersion=1.0
    sonar.sources=src,project.clj
    sonar.clojure.lein-nvd.json-output-location=target/nvd/dependency-check-report.json
    sonar.clojure.cloverage.json-output-location=target/coverage/codecov.json
    ```

3. Run [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) on your project.

### Disabling Sensors

Sensors can be disabled by setting `sonar.clojure.<sensorname>.disabled=true` or
by using command line switch `-Dsonar.clojure.<sensorname>.disabled` when running sonar-scanner.

Sensor names are `eastwood`, `kibit`, `ancient-clj`, `lein-nvd` and `cloverage`.

> Some sensors have mandatory properties. Keep in mind that if you don't disable it, you will have to set the property.

## Building from Source
```sh
./mvnw clean package
```

Maven will generate a SNAPSHOT under the folder ***target***.

## Compatibility

At the moment, SonarClojure was tested on SonarQube up to version 7.1.

We noticed that in later versions of SonarQube, the project overview might be empty.
This normally suggests that SonarClojure was not able to detect analyzable files during
the scanning.
## License

SonarClojure is open-sourced software licensed under the [MIT license](https://github.com/fsantiag/sonar-clojure/blob/master/LICENSE).
