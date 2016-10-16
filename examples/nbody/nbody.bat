@echo off

java ^
  -Djava.library.path=..\..\com.syncleus.aparapi.jni\dist;..\third-party\jogamp ^
  -Dcom.syncleus.aparapi.executionMode=%1 ^
  -Dcom.syncleus.aparapi.enableProfiling=false ^
  -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
  -Dcom.syncleus.aparapi.logLevel=SEVERE ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl-all.jar;..\..\com.syncleus.aparapi\dist\aparapi.jar;nbody.jar ^
  com.syncleus.aparapi.examples.nbody.Main


