@echo off

java ^
  -Djava.library.path=..\..\com.syncleus.aparapi.jni\dist;..\third-party\jogamp ^
  -Dcom.syncleus.aparapi.executionMode=%1 ^
  -Dcom.syncleus.aparapi.enableProfiling=false ^
  -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\jogl-all.jar;..\third-party\jogamp\gluegen-rt.jar;..\..\com.syncleus.aparapi\dist\aparapi.jar;oopnbody.jar ^
  com.syncleus.aparapi.examples.oopnbody.Main


