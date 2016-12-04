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
