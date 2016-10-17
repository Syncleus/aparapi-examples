/**
 * This product currently only contains code developed by authors
 * of specific components, as identified by the source code files.
 *
 * Since product implements StAX API, it has dependencies to StAX API
 * classes.
 *
 * For additional credits (generally to people who reported problems)
 * see CREDITS file.
 */
package com.syncleus.aparapi.examples.configuration;

import com.syncleus.aparapi.*;

/**
 * Kernel which will always fail to run on an OpenCLDevice but has an alternative fallback algorithm.
 */
public class KernelWithAlternateFallbackAlgorithm extends Kernel {
   @Override
   public void run() {
      // deliberately, will fail to generate OpenCL as println is unsupported
      System.out.println("Running in Java (regular algorithm)");
   }

   @Override
   public boolean hasFallbackAlgorithm() {
      return true;
   }

   @Override
   public void executeFallbackAlgorithm(Range _range, int _passes) {
      System.out.println("Running in Java (alternate non-parallel algorithm)");
   }
}
