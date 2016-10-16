java ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dcom.syncleus.aparapi.executionMode=%1 ^
 -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;add.jar ^
 com.syncleus.aparapi.sample.add.Main

