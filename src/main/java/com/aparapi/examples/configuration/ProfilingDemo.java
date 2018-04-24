/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.aparapi.examples.mandel.Main;
import com.aparapi.*;
import com.aparapi.internal.kernel.*;

/**
 * Demonstrate new enhanced profiling capability, profiling the kernel from the blackscholes sample.
 */
public class ProfilingDemo {

   private static com.aparapi.examples.blackscholes.Main.BlackScholesKernel kernel;

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
      KernelProfile profile = KernelManager.instance().getProfile(com.aparapi.examples.blackscholes.Main.BlackScholesKernel.class);
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
      kernel = new com.aparapi.examples.blackscholes.Main.BlackScholesKernel(size);
   }
}
