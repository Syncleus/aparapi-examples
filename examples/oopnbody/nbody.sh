
java \
  -Djava.library.path=../../com.syncleus.aparapi.jni/dist:../third-party/jogamp \
  -Dcom.syncleus.aparapi.executionMode=$1 \
  -Dcom.syncleus.aparapi.logLevel=INFO \
  -Dcom.syncleus.aparapi.enableShowGeneratedOpenCL=true \
  -Dbodies=$2 \
  -Dheight=800 \
  -Dwidth=1200 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.syncleus.aparapi/dist/aparapi.jar:oopnbody.jar \
  com.syncleus.aparapi.examples.oopnbody.Main

