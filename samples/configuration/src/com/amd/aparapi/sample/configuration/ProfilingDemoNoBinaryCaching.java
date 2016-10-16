package com.syncleus.aparapi.sample.configuration;

import com.syncleus.aparapi.internal.kernel.*;

/**
 * Created by Barney on 13/09/2015.
 */
public class ProfilingDemoNoBinaryCaching {

   public static void main(String[] ignored) {
      KernelRunner.BINARY_CACHING_DISABLED = true;
      ProfilingDemo.main(null);
   }
}
