@echo off 
java ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;javaonedemo.jar ^
 com.syncleus.aparapi.examples.javaonedemo.Mandel


