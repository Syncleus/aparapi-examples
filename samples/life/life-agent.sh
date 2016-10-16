java\
 -agentpath:../../com.syncleus.aparapi.jni/dist/libaparapi_x86_64.so\
 -Dcom.syncleus.aparapi.executionMode=$1\
 -Dcom.syncleus.aparapi.useAgent=true\
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar:life.jar\
 com.syncleus.aparapi.sample.life.Main
