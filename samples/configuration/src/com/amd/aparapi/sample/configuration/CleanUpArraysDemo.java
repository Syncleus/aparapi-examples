package com.syncleus.aparapi.sample.configuration;

import com.syncleus.aparapi.sample.mandel.*;

public class CleanUpArraysDemo {
   public static void main(String[] ignored) {

      System.setProperty("com.syncleus.aparapi.enableVerboseJNI", "true");
      System.setProperty("com.syncleus.aparapi.enableVerboseJNIOpenCLResourceTracking", "true");
      System.setProperty("com.syncleus.aparapi.enableExecutionModeReporting", "true");
      System.setProperty("com.syncleus.aparapi.dumpProfileOnExecution", "true");

      int size = 1024;
      int[] rgbs = new int[size * size];
      Main.MandelKernel kernel = new Main.MandelKernel(size, size, rgbs);
      kernel.execute(size * size);
      System.out.println("length = " + kernel.getRgbs().length);
      System.out.println("Cleaning up arrays");
      kernel.cleanUpArrays();
      System.out.println("length = " + kernel.getRgbs().length);
      kernel.resetImage(size, size, rgbs);
      kernel.execute(size * size);
      System.out.println("length = " + kernel.getRgbs().length);
   }
}
