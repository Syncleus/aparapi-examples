# Aparapi Examples Changelog

## 1.4.1

* Updated parent pon to v6.
* Removed explicit version on nexus-staging-maven-plugin plugin, relies on parent now.
* createProgram had the wrong signature producing a unsatisfied link exception that is now fixed.
* Fixed several bad references to resource locations.
* Fixed all of the broken examples that werent running.

## 1.4.0

* Synced to Aparapi 1.4.0

## 1.3.4

* Updated to aparapi-jni 1.1.2 thus fixing `UnsatisfiedLinkError` which occured only on Windows.
* Synced to Aparapi 1.3.4

## 1.3.3

* Fixed "`CXXABI_1.3.8' not found " error encountered on some older systems.
* Synced to Aparapi 1.3.3

## 1.3.2

* Synced to Aparapi 1.3.2
* Examples now work on Windows 64bit GPUs.

## 1.3.1

* Synced to Aparapi 1.3.1
* Examples now work on Mac OSX SPUs.

## 1.3.0

* Synced to Aparapi 1.3.0.

## 1.2.0

* Synced to Aparapi 1.2.0.

## 1.1.2

* Fixed some bugs causing the OpenCL kernel to be compiled twice.

## 1.1.0

* Changed package to com.aparapi

## 1.1.0

* Changed group id and package to com.aparapi