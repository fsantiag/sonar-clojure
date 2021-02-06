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

## Installation
In order to install SonarClojure:
1. Download the [latest](https://github.com/fsantiag/sonar-clojure/releases) jar of the plugin.
2. Place the jar in the SonarQube server plugins directory, usually located under: `/opt/sonarqube/extensions/plugins/`
3. Restart the SonarQube server.

## Usage
1. Change your ***project.clj*** file and add the required plugins:

    ```clojure
    :plugins [[jonase/eastwood "0.3.6"]
              [lein-kibit "0.1.8"]
              [lein-ancient "0.6.15"]
              [lein-cloverage "1.1.2"]
              [lein-nvd "1.4.0"]]
     ```

> Note 1: Please make sure the plugins above are setup correctly for your project. A good way to test this is to
execute each one of them individually on your project. Once they are running fine, SonarClojure should be able to
parse their reports.
>
> Note 2: The lein plugin versions above are the ones we currently support. If you would like to test with a different
version, keep in mind that it might cause errors on SonarClojure analysis. 

2. Create a ***sonar-project.properties*** file in the root folder of your app:

    ```properties
    sonar.projectKey=your-project-key
    sonar.projectName=YourProjectName
    sonar.projectVersion=1.0
    sonar.sources=.
    ```

3. Run [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) on your project.

### Configuring Sensors

#### Disabling
Sensors can be disabled by setting `sonar.clojure.<sensorname>.disabled=true` in the sonar-project.properties or
by using the command line argument `-Dsonar.clojure.<sensorname>.disabled` when running sonar-scanner.
Sensor names are `eastwood`, `kibit`, `ancient`, `nvd` and `cloverage`.

#### Report file location
Some sensors use report files to parse the results. Both cloverage and lein-nvd use this report files.
By default they have a path already set but you can change the file locations by setting the property in the
sonar-project.properties:

`sonar.clojure.cloverage.reportPath=target/coverage/codecov.json`

`sonar.clojure.nvd.reportPath=target/nvd/dependency-check-report.json`

#### Setting a timeout
By default, sensors have a timeout value of 300 seconds. This value applies per sensor while they are executing.
You can change the default value by setting the property `sonar.clojure.sensors.timeout` in the sonar-project.properties
file.

#### Debugging
* SonarClojure is in its early days and therefore you might face problems when trying to run the plugin, especially because
 we rely on other plugins that are also in its early days. A nice way to try to debug
a problem you might have is to make sure the particular plugin you are using is running fine before executing the 
sonar-scanner. For instance, if you are trying to visualize the coverage data on SonarQube, make sure to run cloverage
against your project using `lein cloverage --codecov` for instance. Once you fix the cloverage issue on your project,
then SonarClojure should be able to parse the results. The same idea applies to all the plugins.
 
* In general, plugins should not stop execution in case of errors, unless an exception happens.

* You can use `-X` or `--debug` when running sonar-scanner to get a detailed information of what SonarClojure is trying to do.

## Building from Source
```sh
./mvnw clean package
```

Maven will generate a SNAPSHOT under the folder ***target***.

## Compatibility
At the moment, SonarClojure supports SonarQube up to version 8.6.1.

## License

SonarClojure is open-sourced software licensed under the [MIT license](https://github.com/fsantiag/sonar-clojure/blob/master/LICENSE).
