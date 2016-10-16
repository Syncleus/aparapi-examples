
java ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dsequential=false^
 -Dcom.syncleus.aparapi.executionMode=GPU ^
 -Dcom.syncleus.aparapi.enableProfiling=false ^
 -Dcom.syncleus.aparapi.enableVerboseJNI=false ^
 -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;life.jar ^
 com.syncleus.aparapi.sample.life.Main


