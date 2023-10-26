[![DOI](https://zenodo.org/badge/687502368.svg)](https://zenodo.org/badge/latestdoi/687502368)
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://jonasschaub.github.io/scaffold-graph-vis/javadoc/latest/index.html)
[![License: GPL v3](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](http://www.gnu.org/licenses/lgpl-2.1.html)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-blue.svg)](https://GitHub.com/JonasSchaub/scaffold-graph-vis/graphs/commit-activity)
[![GitHub issues](https://img.shields.io/github/issues/JonasSchaub/scaffold-graph-vis.svg)](https://GitHub.com/JonasSchaub/scaffold-graph-vis/issues/)
[![GitHub contributors](https://img.shields.io/github/contributors/JonasSchaub/scaffold-graph-vis.svg)](https://GitHub.com/JonasSchaub/scaffold-graph-vis/graphs/contributors/)
[![GitHub release](https://img.shields.io/github/release/JonasSchaub/scaffold-graph-vis.svg)](https://github.com/JonasSchaub/scaffold-graph-vis/releases/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jonasschaub/Scaffold-graph-vis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.jonasschaub/Scaffold-graph-vis)

# scaffold-graph-vis
Basic utilities for visualising [cdk-scaffold](https://github.com/cdk/cdk-scaffold) graphs using the [GraphStream library](https://github.com/graphstream).

## Description
This repository contains very basic but easy-to-use visualisation functionalities for molecular scaffold networks and
scaffold trees generated using the [cdk-scaffold module](https://github.com/cdk/cdk-scaffold). The open
[GraphStream](https://github.com/graphstream) dynamic graph library is used for visualisation.
The functionalities were part of the [Scaffold Generator](https://github.com/Julian-Z98/ScaffoldGenerator) library earlier
and are described in the accompanying scientific publication 
["Schaub, J., Zander, J., Zielesny, A. et al. Scaffold Generator: a Java library implementing molecular scaffold functionalities in the Chemistry Development Kit (CDK). J Cheminform 14, 79 (2022)"](https://doi.org/10.1186/s13321-022-00656-x).
Please note that this is a very basic functionality primarily meant for visual inspection and debugging. GraphStream 
might also throw errors in some cases where the problem lies with the library, not this functionality here.

## Example initialization and usage of ART2a-Clustering-for-Java
See the <a href="https://github.com/JonasSchaub/scaffold-graph-vis/wiki">wiki</a> of this repository.

## JavaDoc
The JavaDoc of this library can be found <a href="https://jonasschaub.github.io/scaffold-graph-vis/javadoc/latest">here</a>.

## Installation
Scaffold-graph-vis is hosted as a package/artifact on the sonatype maven central repository. See the
<a href="https://central.sonatype.com/artifact/io.github.jonasschaub/scaffold-graph-vis/">artifact page</a> for installation
guidelines using build tools like maven or gradle.
<br>
To install Scaffold-graph-vis via its JAR archive, you can get it from the
<a href="https://github.com/JonasSchaub/scaffold-graph-vis/releases">releases</a>. Note that other dependencies
will need to be installed via JAR archives as well this way.
<br>
In order to open the project locally, e.g. to extend it, download or clone the repository and
open it in a Gradle-supporting IDE (e.g. IntelliJ) as a Gradle project and execute the build.gradle file.
Gradle will then take care of installing all dependencies. A Java Development Kit (JDK) of version 17 or higher must also
be pre-installed.

## Contents of this repository
### Sources
The <a href="https://github.com/JonasSchaub/scaffold-graph-vis/tree/main/src">"src"</a> subfolder contains
all source code files including JUnit tests.

### Tests
The test class
<a href="https://github.com/JonasSchaub/scaffold-graph-vis/blob/main/src/test/java/de/unijena/cheminf/scaffolds/GraphStreamUtilityTest.java">
<i>GraphStreamUtilityTest</i></a> mainly contains executable demo code for how to use the library functionalities.

## Dependencies for local installation
**Needs to be pre-installed:**
* Java Development Kit (JDK) version 17
    * [Adoptium OpenJDK](https://adoptium.net) (as one possible source of the JDK)
* Gradle version 8.2
    * [Gradle Build Tool](https://gradle.org)

**Managed by Gradle:**
* JUnit Jupiter version 5.9.1
    * [JUnit ](https://junit.org/junit5/)
    * License: Eclipse Public License - v 2.0
* Spotless version 6.19
    * [Spotless GitHub repository](https://github.com/diffplug/spotless)
    * License: Apache-2.0 license
* Javadoc-publisher version 2.4
    * [Javadoc-publisher GitHub repository](https://github.com/MathieuSoysal/Javadoc-publisher.yml)
    * License: Apache-2.0 license
