#!/bin/bash 
java -Djava.library.path=../../com.syncleus.aparapi.jni/dist:../third-party/jogamp  \
  -classpath ../third-party/jogamp/gluegen-rt.jar:../third-party/jogamp/jogl-all.jar:../../com.syncleus.aparapi/dist/aparapi.jar:javaonedemo.jar \
  com.syncleus.aparapi.examples.javaonedemo.NBody
