![](http://aparapi.com/images/logo-text-adjacent.png)

[![License](http://img.shields.io/:license-apache-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi-examples/badge.png?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.aparapi/aparapi-examples/)
[![Gitter](https://badges.gitter.im/Syncleus/aparapi.svg)](https://gitter.im/Syncleus/aparapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A framework for executing native Java code on the GPU.

**Licensed under the Apache Software License v2**

This project is a collection of examples for the Aparapi project. For more information see the Aparapi [website](http://Aparapi.com) or the [Github page](https://github.com/Syncleus/aparapi-examples).

For detailed documentation see [Aparapi.com](http://Aparapi.com).

For support please use [Gitter](https://gitter.im/Syncleus/aparapi) or the [official Aparapi mailing list](https://groups.google.com/a/syncleus.com/d/forum/aparapi-list).

Please file bugs and feature requests on [Github](https://github.com/Syncleus/aparapi-examples/issues).

## Prerequisites

The examples should run on any system as-is. For GPU acceleration support you must have OpenCL installed and a compatible graphics card.

**Aparapi runs on all operating systems and platforms, however GPU acceleration support is currently provided for the following platforms: Windows 64bit, Windows 32bit, Mac OSX 64bit, Linux 64bit, and Linux 32bit.**

## Obtaining the Source

The official source repository for Aparapi Examples is located in the Syncleus Github repository and can be cloned using the
following command.

```bash

git clone https://github.com/Syncleus/aparapi-examples.git
```

## Running

To run the examples simply checkout the git tag for the version you want to run and execute it through maven. Unless you
specifically want to try the latest snapshot it is important you checkout a specific git tag instead of the master
branch. To use the snapshot in the master branch you will have to manually install the core aparapi snapshot that
matches it since snapshots do not appear in maven central.

```bash

git checkout v1.5.0
mvn clean package exec:java
```
