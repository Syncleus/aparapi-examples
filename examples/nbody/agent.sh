java \
  -agentpath:../../com.syncleus.aparapi.jni/dist/libaparapi_x86_64.so\
  -Dcom.syncleus.aparapi.useAgent=true\
  -Djava.library.path=../third-party/jogamp \
  -Dcom.syncleus.aparapi.executionMode=$1 \
  -Dbodies=$2 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.syncleus.aparapi/dist/aparapi.jar:nbody.jar \
  com.syncleus.aparapi.examples.nbody.Main
