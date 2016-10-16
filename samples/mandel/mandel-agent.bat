java ^
 -agentpath:../../com.syncleus.aparapi.jni/dist/aparapi_x86_64.dll ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dcom.syncleus.aparapi.useAgent=true ^
 -Dcom.syncleus.aparapi.executionMode=%1 ^
 -Dcom.syncleus.aparapi.logLevel=OFF^
 -Dcom.syncleus.aparapi.enableVerboseJNI=false ^
 -Dcom.syncleus.aparapi.enableProfiling=false ^
 -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true ^
 -Dcom.syncleus.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.syncleus.aparapi.dumpFlags=true ^
 -Dcom.syncleus.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;mandel.jar ^
 com.syncleus.aparapi.sample.mandel.Main


