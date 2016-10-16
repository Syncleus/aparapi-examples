java ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dcom.syncleus.aparapi.executionMode=%1 ^
 -Dcom.syncleus.aparapi.enableProfiling=false ^
 -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;extension.jar ^
 com.syncleus.aparapi.sample.extension.FFTExample


