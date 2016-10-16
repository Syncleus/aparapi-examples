java\
 -agentpath:../../com.syncleus.aparapi.jni/dist/libaparapi_x86_64.so\
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist\
 -Dcom.syncleus.aparapi.useAgent=true\
 -Dcom.syncleus.aparapi.executionMode=$1\
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar:mandel.jar\
 com.syncleus.aparapi.sample.mandel.Main
