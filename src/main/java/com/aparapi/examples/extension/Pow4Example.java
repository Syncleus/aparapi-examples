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
package com.aparapi.examples.extension;

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.OpenCL;
import com.aparapi.opencl.OpenCL.Resource;

public class Pow4Example{

   @Resource("com/aparapi/examples/extension/squarer.cl")
   interface Squarer extends OpenCL<Squarer>{

      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   public static void main(String[] args) {

      final int size = 32;
      final float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      final float[] squares = new float[size];
      final Range range = Range.create(size);

      final Device device = KernelManager.instance().bestDevice();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final Squarer squarer = openclDevice.bind(Squarer.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            System.out.println(in[i] + " " + squares[i]);
         }

         squarer.square(range, squares, in);

         for (int i = 0; i < size; i++) {
            System.out.println(i + " " + squares[i] + " " + in[i]);
         }
      }
   }

}
