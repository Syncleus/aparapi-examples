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
package com.aparapi.examples.configuration;

import com.aparapi.*;
import com.aparapi.internal.kernel.*;
import com.aparapi.examples.blackscholes.Main.*;
import com.aparapi.examples.mandel.*;

/**
 * Demonstrate new enhanced profiling capability, profiling the kernel from the blackscholes sample.
 */
public class ProfilingDemo {

   private static BlackScholesKernel kernel;

   public static void main(String[] ignored) {

      final int size = 1024;
      newBlackScholesKernel(size);

      // first execute an arbitrary Kernel (not the one we are profiling!) a few times to ensure class loading and initial JIT optimisations have
      // been performed before we start the profiling
      int warmups = 5;
      for (int i = 0; i < warmups; ++i) {
         runWarmup();
      }

      String tableHeader = KernelDeviceProfile.getTableHeader();

      boolean newKernel = false;

      runOnce(size, newKernel);
      System.out.println("First run:");
      printLastProfile(tableHeader);


      int reps = 20;

      System.out.println("\nSubsequent runs using same kernel:");
      for (int rep = 0; rep < reps; ++rep) {
         runOnce(size, newKernel);
         printLastProfile(tableHeader);
      }

      newKernel = true;
      System.out.println("\nSubsequent runs using new kernels:");
      for (int rep = 0; rep < reps; ++rep) {
         runOnce(size, newKernel);
         printLastProfile(tableHeader);
      }

      // Note. You will see from the output that there is a substantial cost to Kernel creation (vs Kernel reuse), almost entirely due to KernelRunner#initJNI

   }

   private static void printLastProfile(String tableHeader) {
      KernelProfile profile = KernelManager.instance().getProfile(BlackScholesKernel.class);
      KernelDeviceProfile deviceProfile = profile.getLastDeviceProfile();
      String row = deviceProfile.getLastAsTableRow();
      System.out.println(tableHeader);
      System.out.println(row);
   }

   private static void runOnce(int size, boolean newKernel) {
      if (newKernel) {
         newBlackScholesKernel(size);
      }
      kernel.execute(size);
   }

   private static void runWarmup() {
      int[] rgb = new int[512 * 512];
      Kernel warmupKernel = new Main.MandelKernel(512, 512, rgb);
      warmupKernel.execute(512 * 512);
   }

   private static void newBlackScholesKernel(int size) {
      if (kernel != null) {
         kernel.dispose();
      }
      System.gc();
      kernel = new BlackScholesKernel(size);
   }
}
