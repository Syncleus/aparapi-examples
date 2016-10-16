java ^
 -Djava.library.path=../../com.syncleus.aparapi.jni/dist ^
 -Dcom.syncleus.aparapi.executionMode=%1 ^
 -classpath ../../com.syncleus.aparapi/dist/aparapi.jar;convolution.jar ^
 com.syncleus.aparapi.sample.convolution.ConvolutionOpenCL %2

