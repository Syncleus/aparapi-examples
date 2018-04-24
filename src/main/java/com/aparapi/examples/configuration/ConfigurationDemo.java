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

import com.aparapi.*;
import com.aparapi.internal.kernel.*;

import java.util.*;

/**
 * Tests device selection via {@link com.aparapi.internal.kernel.KernelManager}.
 */
public class ConfigurationDemo {
   public static void main(String[] ignored) {
      StringBuilder report;

      List<Integer> tests = Arrays.asList(0, 1, 2, 3);
      int reps = 1;
      for (int rep = 0; rep < reps; ++rep) {
         runTests(rep == 0, tests);

         if (rep % 100 == 99 || rep == 0 || rep == reps - 1) {
            report = new StringBuilder("rep = " + rep + "\n");
            KernelManager.instance().reportDeviceUsage(report, true);
            System.out.println(report);
         }
      }
   }

   private static void runTests(boolean verbose, List<Integer> testIndicesToRun) {
      final int globalSize = 1;
      Kernel kernel;
      if (testIndicesToRun.contains(0)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which cannot be run in OpenCL, with fallback algorithm");
            System.out.println();
         }
         kernel = new KernelWithAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(1)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which cannot be run in OpenCL, without fallback algorithm");
            System.out.println();
         }
         kernel = new KernelWithoutAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(2)) {
         if (verbose) {
            System.out.println();
            System.out.println("Retesting previous case, should jump straight to regular java implementation without warnings");
            System.out.println();
         }
         kernel = new KernelWithoutAlternateFallbackAlgorithm();
         kernel.execute(globalSize);
         kernel.dispose();
      }

      if (testIndicesToRun.contains(3)) {
         if (verbose) {
            System.out.println();
            System.out.println("Testing default KernelPreferences with kernel which should be run in OpenCL");
            System.out.println();
         }
         KernelOkayInOpenCL clKernel = new KernelOkayInOpenCL();
         kernel = clKernel;
         kernel.execute(clKernel.inChars.length);
         String result = new String(clKernel.outChars);
         if (verbose) {
            System.out.println("kernel output: " + result);
         }
         kernel.dispose();
      }
   }
}
