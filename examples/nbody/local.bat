@echo off

java ^
  -Djava.library.path=..\..\com.syncleus.aparapi.jni\dist;..\third-party\jogamp ^
  -Dcom.syncleus.aparapi.executionMode=%1 ^
  -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
  -Dcom.syncleus.aparapi.enableVerboseJNI=false ^
  -Dcom.syncleus.aparapi.enableProfiling=true ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl.all.jar;..\..\com.syncleus.aparapi\dist\aparapi.jar;nbody.jar ^
  com.syncleus.aparapi.examples.nbody.Local


