# jaxrs client

This project allows to generate a basic rest client based on jaxrs 2.0 standard
covered for javaee-api

## Getting Started

### Prerequisites

* Java 8

### Using this module


## Java Doc

For check java doc about playlist service once you have already download the sources you can run **mvn clean install** or **mvn clean package** command, 
this will generate ear file for  services, also that will run unit and integration tests and generate javadoc for this project. 
After the process will done, check on every child project this route:

**{{ the child project }}/target/apidocs** inside must be a file named 'index.html' open with your prefered browser and enjoy.

## Java Test and Code Coverage

In this project I'm using:

* [JUnit 5](https://junit.org/junit5/)
* [Mockito](https://site.mockito.org/)

After you build the project you can inspect:

- /target/failsafe-reports -> Integration test results
- /target/surefire-reports -> Unit test results

**NOTE:**  **Jacoco is active only in dev-qa profile**
- /site/jacoco-ut          -> Java Code Coverage (JaCoCo) provided by unit test (inside must be a file named 'index.html' open with your prefered browser)
- /site/jacoco-it          -> Java Code Coverage (JaCoCo) provided by integration test (inside must be a file named 'index.html' open with your prefered browser)
- /target/jacoco-ut.exec   -> Merged JaCoCo unit test file for export metrics to analyze with tools like SonarQube
- /target/jacoco-it.exec   -> Merged JaCoCo integration test file for export metrics to analyze with tools like SonarQube

For skip unit test you can use the flag 	   -> -Dskip.unit.tests=true
For skip integration test you can use the flag -> -Dskip.integration.tests=true
You can check project metrics using the provided sonar-project.properties in parent project. Enjoy

## Formatter

At this moment I'm using formatter-maven-plugin for avoid to have diff between code format on developer ide's.
That's because in a collaborative team when our formmater is different it may cause a lot of git diffs and is not convenient for merge our changes. 
During the build this plugin run the goal validate, so if one class doesn't comply with established format, 
build will fails, to fix run -> **mvn formatter:format**.
Ensure before your merges to run that goal please. 
If you want to know more about the plugin or want to change to a custom checkstyle please check [formatter-maven-plugin](https://code.revelc.net/formatter-maven-plugin/) for more details. 
**I suggest to run this goal before merge your changes for CI/Push.**

## Built With

* [Java](https://www.java.com/en/download/) - Programming language
* [Maven](https://maven.apache.org/) - Dependency Management
* [Wildfly](http://wildfly.org/) - Server/Container
* [Jenkins](https://jenkins.io/) - CI
