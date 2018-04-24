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
package com.aparapi.examples.progress;

import com.aparapi.Kernel;

/**
 * Kernel which performs very many meaningless calculations, used to demonstrate progress tracking and cancellation of multi-pass Kernels.
 */
public class LongRunningKernel extends Kernel {

   public static final int RANGE = 20000;
   private static final int REPETITIONS = 1 * 1000 * 1000;

   public final long[] data = new long[RANGE];

   @Override
   public void run() {
      int id = getGlobalId();
      if (id == 0) {
         report();
      }
      for (int rep = 0; rep < REPETITIONS; ++rep) {
         data[id] += (int) sqrt(1);
      }
   }

   @NoCL
   public void report() {
      int passId = getPassId();
      System.out.println("Java execution: passId = " + passId);
   }
}
