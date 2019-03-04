# Contributing to SonarClojure

Thanks for taking the time to contribute!

The following is a set of guidelines for contributing to SonarClojure. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

#### Table of Contents

- [Code of Conduct](#code-of-conduct)
- [I don't want to read this whole thing I just have a question!!!](#i-don-t-want-to-read-this-whole-thing-i-just-have-a-question)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Requesting Features or Reporting Bugs](#requesting-features-or-reporting-bugs)
  - [Code Contributions](#code-contributions)
    - [What should I know before I get started with the code contributions?](#what-should-i-know-before-i-get-started-with-the-code-contributions)
      - [SonarQube API](#sonarqube-api)
      - [Running the plugin locally](#running-the-plugin-locally)
      - [Styleguides](#styleguides)
      - [Git Commit Messages](#git-commit-messages)
    - [Pull Requests](#pull-requests)

## Code of Conduct

By partipating on this project, you are expected to uphold the following [CODE OF CONDUCT](CODE_OF_CONDUCT.md).

## I don't want to read this whole thing I just have a question!!!
If you have a question, feel free to open a Github issue. Please make sure you follow the github templates provided.

## How Can I Contribute?
### Requesting Features or Reporting Bugs
You can contribute to SonarClojure by submitting a feature request or reporting a bug. In both cases, please submit an
 issue using the appropriate template. Follow the guidelines provided in the template.

### Code Contributions
Have a look in the [issues](https://github.com/fsantiag/sonar-clojure/issues) we have opened. If it is 
your first time, the ones with the tag `good first issue` might be a good idea. If you want to explore something more
challenging, feel free to have a look into the other tags: `feature`, `help wanted`, `bug`. In any case, make sure to reply
 in the issue so we know you are working on it.
 
#### What should I know before I get started with the code contributions?
##### SonarQube API
SonarQube has an API for developing plugins. If you are not familar with the API, please have a look into their documentation [here](https://docs.sonarqube.org/display/DEV/Developing+a+Plugin). That will guide you through the basics.

##### Running the plugin locally

This is how you can run SonarQube with SonarClojure:

```sh
./mvnw clean package
./start-sonarqube.sh     # This script requires docker
```

SonarQube will be running on `http://localhost:9000`. 

##### Styleguides
We try to follow Jetbrains Java style guide. Please refer to their [website](https://www.jetbrains.com/help/idea/code-style-java.html)

##### Git Commit Messages
* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line
* When only changing documentation, include `[ci skip]` in the commit title


#### Pull Requests
Before you open a PR, please make sure you squash the commits that should be squashed. What we mean is to squash commits
that describe in progress work or minor fixes. If you have major milestones during development, it is ok to have 
multiple commits for them. Just make sure the build is working fine for every commit. The idea behind this decision is
 to keep the history clean while maintaining stable builds for each one of them.
