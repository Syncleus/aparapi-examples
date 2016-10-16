java ^
 -agentpath:../../com.syncleus.aparapi.jni/dist/aparapi_x86_64.dll^
 -Dcom.syncleus.aparapi.useAgent=true ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dsequential=false^
 -Dcom.syncleus.aparapi.executionMode=%1 ^
 -Dcom.syncleus.aparapi.enableProfiling=false ^
 -Dcom.syncleus.aparapi.enableVerboseJNI=false ^
 -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;life.jar ^
 com.syncleus.aparapi.sample.life.Main


