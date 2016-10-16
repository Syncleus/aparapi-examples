@echo off

java ^
  -agentpath:../../com.syncleus.aparapi.jni/dist/aparapi_x86_64.dll ^
  -Dcom.syncleus.aparapi.useAgent=true ^
  -Djava.library.path=..\third-party\jogamp ^
  -Dcom.syncleus.aparapi.executionMode=%1 ^
  -Dcom.syncleus.aparapi.enableProfiling=false ^
  -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl-all.jar;..\..\com.syncleus.aparapi\dist\aparapi.jar;nbody.jar ^
  com.syncleus.aparapi.examples.nbody.Main


