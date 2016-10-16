java ^
   -Djava.library.path=..\..\com.syncleus.aparapi.jni\dist ^
   -Dcom.syncleus.aparapi.executionMode=%1 ^
   -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
   -Dcom.syncleus.aparapi.enableShowFakeLocalVariableTable=true ^
   -Dsize=%2 ^
   -Diterations=%3 ^
   -classpath blackscholes.jar;..\..\com.syncleus.aparapi\dist\aparapi.jar ^
   com.syncleus.aparapi.sample.blackscholes.Main
