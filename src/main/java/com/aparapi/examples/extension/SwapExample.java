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
package com.aparapi.examples.extension;

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.OpenCL;

public class SwapExample{

   interface Swapper extends OpenCL<Swapper>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  float temp=lhs[id];" + "  lhs[id] = rhs[id];\n"//
            + "  rhs[id] = temp;\n"//
            + "}\n")//
      public Swapper swap(//
            Range _range,//
            @GlobalReadWrite("lhs") float[] lhs,//
            @GlobalReadWrite("rhs") float[] rhs);
   }

   public static void main(String[] args) {

      final int size = 32;
      final float[] lhs = new float[size];
      for (int i = 0; i < size; i++) {
         lhs[i] = i;
      }
      final float[] rhs = new float[size];
      final Range range = Range.create(size);

      final Device device = KernelManager.instance().bestDevice();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final Swapper swapper = openclDevice.bind(Swapper.class);
         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, lhs, rhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, lhs, rhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

         swapper.swap(range, rhs, lhs);

         for (int i = 0; i < size; i++) {
            System.out.println(lhs[i] + " " + rhs[i]);
         }

      }
   }

}
