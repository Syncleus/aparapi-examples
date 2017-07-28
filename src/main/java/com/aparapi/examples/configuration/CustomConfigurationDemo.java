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

import com.aparapi.device.*;
import com.aparapi.internal.kernel.*;

import java.util.*;

/**
 * Created by Barney on 31/08/2015.
 */
public class CustomConfigurationDemo {

   public static void main(String[] ignored) {
      System.setProperty("com.aparapi.dumpProfilesOnExit", "true");
      KernelManager manager = new KernelManager() {
         @Override
         protected List<Device.TYPE> getPreferredDeviceTypes() {
            return Arrays.asList(Device.TYPE.CPU, Device.TYPE.ALT, Device.TYPE.JTP);
         }
      };
      KernelManager.setKernelManager(manager);

      System.out.println("\nTesting custom KernelPreferences with kernel, preferences choose CPU");
      KernelOkayInOpenCL kernel = new KernelOkayInOpenCL();
      kernel.execute(kernel.inChars.length);
      System.out.println(kernel.outChars);

      System.out.println("\nTesting custom KernelPreferences with kernel, preferences specify CPU but kernel vetos CPU");
      kernel = new KernelOkayInOpenCL() {
         @Override
         public boolean isAllowDevice(Device _device) {
            return _device.getType() != Device.TYPE.CPU;
         }
      };
      kernel.execute(kernel.inChars.length);
      System.out.println(kernel.outChars);

      StringBuilder report = new StringBuilder("\n");
      KernelManager.instance().reportDeviceUsage(report, true);
      System.out.println(report);
   }
}
