# SonarClojure
> A SonarQube plugin to analyze Clojure source.

[![Build Status](https://travis-ci.org/fsantiag/sonar-clojure.svg?branch=master)](https://travis-ci.org/fsantiag/sonar-clojure)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=alert_status
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=coverage
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)


SonarClojure is is a [SonarQube](https://www.sonarqube.org/) [plugin](https://docs.sonarqube.org/display/PLUG/Plugin+Library)
that uses [Eastwood](https://github.com/jonase/eastwood) lint tool to analyze Clojure source.

## Current State

### Eastwood
[Eastwood](https://github.com/jonase/eastwood) is a lintter for Clojure (no CLJS support) which detects for example misplaced docstrings
 , def in defs and tests which always returns true.

### ancient-clj

[ancient-clj](https://github.com/xsc/lein-ancient) is a Clojure library with Leiningen plugin which checks your project for outdated dependencies and plugins and  suggest updates. The Sonarqube plugin
marks these as minor vulnerabilities to project.clj file.

ancient-clj sensor requires project.clj to be included in sonar.sources property.

### Cloverage

[Cloverage](https://github.com/cloverage/cloverage) is a code coverage tool for Clojure which runs the tests ot a program 
and calculates line and form coverage for namespaces. Only line coverage is supported by Sonarcube and calculation seems to be
be somehow different compared to Cloverage itself by few percents.

Remember to add target/coverage/codecov.json to SonarQube properties or the sensor cannot read Cloverage output.

>This plugin was inspired in the previous [SonarClojure](https://github.com/zmsp/sonar-clojure) that at
this moment is not under development anymore and doesn't support SonarQube 6.7. Since the changes to port
the old plugin were very extensive, I decided to start from scratch and use the old plugin as inspiration.

## Installation

In order to install SonarClojure:
1. Download the [latest](https://github.com/fsantiag/sonar-clojure/releases) jar of the plugin.
2. Place the jar in the SonarQube server plugins directory, usually located under: `/opt/sonarqube/extensions/plugins/`
3. Restart the SonarQube server.

## Usage
1. Change your ***project.clj*** file and add Eastwood to the list of plugins:

    ```clojure
    :plugins [[jonase/eastwood "0.2.5"]
              [lein-ancient "0.6.15"]
              [lein-cloverage "1.0.13"]]
    ```

2. Create a ***sonar-project.properties*** file in the root folder of your app:

    ```properties
    sonar.projectKey=your-project-key
    sonar.projectName=YourProjectName
    sonar.projectVersion=1.0
    sonar.sources=src,project.clj,target/coverage/codecov.json
    ```

## Disabling sensors

Sensors can be disabled by setting ```sonar.clojure.sensorname.disabled=true```  or
by using command line switch ```-Dsonar.clojure.sensorname.disabled``` when running ```sonar-scanner```.

Sensor names are ```eastwood```, ```ancient-clj``` and ```cloverage```.

3. Run [sonnar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) on your project.

## Building from source

```sh
./mvnw clean package
```

Maven will generate an SNAPSHOT under the folder ***target***.

## Testing the plugin with locally running dockerized Sonarqube 

```sh
mvn clean package
start-sonarqube.sh
```

Create sonar-project.properties file. Run ```sonar-scanner``` on Clojure applications root directory which you like to analyze.
Open http://localhost:9000/dashboard?id=your-project-key and check for issues.

## Compatibility

At the moment, SonarClojure was tested on SonarQube 7.1 Community Edition.

If using later versions than SonarQube 7.1 then the project overview might be empty. This can be fixed by adding a xml file or other SonarQube analyzable
file to scanned files for example to resources folder. Remember to check that the folder is also in sonar.sources property.

## License

SonarClojure is open-sourced software licensed under the [MIT license](https://github.com/fsantiag/sonar-clojure/blob/master/LICENSE).