@echo off

java ^
  -Djava.library.path=..\..\com.syncleus.aparapi.jni\dist ^
  -Dcom.syncleus.aparapi.executionMode=%1 ^
  -Dcom.syncleus.aparapi.enableProfiling=false ^
  -classpath ..\..\com.syncleus.aparapi\dist\aparapi.jar;mdarray.jar ^
  gov.pnnl.aparapi.sample.mdarray.MDArray 