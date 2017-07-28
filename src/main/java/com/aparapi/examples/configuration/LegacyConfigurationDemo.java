/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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

/**
 * Tests device selection when circumventing the {@link com.aparapi.internal.kernel.KernelManager} by using the legacy mechanism
 * (setExecutionMode, etc.).
 */
public class LegacyConfigurationDemo {

   @SuppressWarnings("deprecation")
   public static void main(String[] ignored) {
      System.setProperty("com.aparapi.executionMode", "GPU,CPU,SEQ");
      System.setProperty("com.aparapi.dumpProfilesOnExit", "true");

      KernelWithAlternateFallbackAlgorithm kernel = new KernelWithAlternateFallbackAlgorithm();
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
      int globalRange = 1;
      kernel.execute(globalRange);

      StringBuilder report = new StringBuilder("\n");
      KernelManager.instance().reportDeviceUsage(report, true);
      System.out.println(report);
   }
}
