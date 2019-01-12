# SonarClojure
> A SonarQube plugin to analyze Clojure source.

[![Build Status](https://travis-ci.org/fsantiag/sonar-clojure.svg?branch=master)](https://travis-ci.org/fsantiag/sonar-clojure)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=alert_status
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.sonar.plugins.clojure%3Asonar-clojure-plugin&metric=coverage
)](https://sonarcloud.io/dashboard?id=org.sonar.plugins.clojure%3Asonar-clojure-plugin)


SonarClojure is is a [SonarQube](https://www.sonarqube.org/) [plugin](https://docs.sonarqube.org/display/PLUG/Plugin+Library)
that uses different Clojure libraries to analyze code and detect actual and potential vulnerabilities.

## Current State

SonarClojure uses next libraries:

### Eastwood
[Eastwood](https://github.com/jonase/eastwood) is a lintter for Clojure (no CLJS support) which detects for example misplaced docstrings
 , def in defs and tests which always returns true.
 
### Kibit
[Kibit](https://github.com/jonase/eastwood) is a static code analyzer which detects code that could be rewritten with a more idiomatic 
function or macro. For example: 
```clojure 
(> x 0) 
; more idiomatically
(pos? x)
```

Kibit is most useful for beginning Clojure programmers. Kibit supports also Clojurescript.

### Cloverage

[Cloverage](https://github.com/cloverage/cloverage) is a code coverage tool for Clojure which runs the tests ot a program 
and calculates line and form coverage for namespaces. Only line coverage is supported by Sonarcube and calculation seems to be
be somehow different compared to Cloverage itself by few percents.

### Lein-nvd

[Lein-nvd] is a dependency-checker plugin whichs checks JARS in the programs classpath for known vulnerabilites against 
the [National Vulnerability Database](https://nvd.nist.gov/). The Sonarcube plugin currently marks all the found vulnerabilites to 
the first line of project.clj because the lein-nvd only returns JAR name not the dependency which pulls it directly or transitively.

### Ancient

[Ancient] is a plugin to check your project for outdated dependencies and plugins and  suggest updates. The Sonarqube plugin
marks these as minor vulnerabilities to project.clj file.

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
    :plugins [[lein-ancient "0.6.15"]
              [jonase/eastwood "0.3.3"]
              [lein-cloverage "1.0.13"]
              [lein-kibit "0.1.6"]
              [lein-nvd "0.6.0"]]
    ```
    
    Dependencies can also added to Leiningen user profile.
    
    The plugin analyzes the code by running Leiningen in the analyzed project and either parses the standard output or
    generated report files. If plugins version changes then parsing may fail if the output have changed.
    
2. Create a ***sonar-project.properties*** file in the root folder of your app:

    ```properties
    sonar.projectKey=your-project-key
    sonar.projectName=YourProjectName
    sonar.projectVersion=1.0
    sonar.sources=src,test,resources,project.clj
    clojure.disable.kibit=false
    clojure.disable.leinnvd=false
    clojure.disable.eastwood=false
    clojure.disable.cloverage=false
    clojure.disable.ancient=false
    ```
    
    Differerent analyzers can be disabled by setting property to true. 
    
3. Run [sonnar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) on your project.

## Building from source

```sh
mvn clean package
```

Maven will generate an SNAPSHOT under the folder ***target***.

## Testing the plugin with locally running Sonarqube
```sh
mvn clean package
sonar-docker-image/start-sonarcube.sh
```

Create sonar-project.properties file. Run ```sonar-scanner``` on Clojure applications root directory which you like to analyze.
Open http://localhost:9000/dashboard?id=your-project-key and check for issues.

## Compatibility

At the moment, SonarClojure was tested on SonarQube 6.7 and 7.4 community edition.

## Limitations

Clojure language parser for Sonarcube has not been implemented. This means that for example syntax highlighting doesnt currently 
work. See https://docs.sonarqube.org/display/DEV/Supporting+New+Languages for instructions to maybe make a pull request for this.

## License

SonarClojure is open-sourced software licensed under the [MIT license](https://github.com/fsantiag/sonar-clojure/blob/master/LICENSE).
