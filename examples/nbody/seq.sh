
java \
  -Djava.library.path=../../com.syncleus.aparapi.jni/dist:../third-party/jogamp \
  -Dcom.syncleus.aparapi.executionMode=$1 \
  -Dbodies=$2 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.syncleus.aparapi/dist/aparapi.jar:nbody.jar \
  com.syncleus.aparapi.examples.nbody.Seq

