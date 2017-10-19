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

import com.aparapi.ProfileInfo;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.OpenCL;
import com.aparapi.opencl.OpenCL.Resource;
import com.aparapi.opencl.OpenCL.Source;
import java.util.List;

public class SquareExample{

   interface Squarer extends OpenCL<Squarer>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  out[id] = in[id]*in[id];\n"//
            + "}\n")//
      public Squarer square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @Resource("squarer.cl") interface SquarerWithResource extends OpenCL<SquarerWithResource>{
      public SquarerWithResource square(//
            Range _range,//
            @GlobalReadWrite("in") float[] in,//
            @GlobalReadWrite("out") float[] out);
   }

   @Source("\n"//
         + "__kernel void square (\n" //
         + "   __global float *in,\n"//
         + "   __global float *out\n" + "){\n"//
         + "   const size_t id = get_global_id(0);\n"//
         + "   out[id] = in[id]*in[id];\n"//
         + "}\n") interface SquarerWithSource extends OpenCL<SquarerWithSource>{
      public SquarerWithSource square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }

   public static void main(String[] args) {
      final int size = 32;
      final float[] in = new float[size];

      for (int i = 0; i < size; i++) {
         in[i] = i;
      }

      final float[] squares = new float[size];
      final float[] quads = new float[size];
      final Range range = Range.create(size);

      final Device device = KernelManager.instance().bestDevice();

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         for (int l=0; l<5; l++){

         final SquarerWithResource squarer = openclDevice.bind(SquarerWithResource.class);
         squarer.square(range, in, squares);

         for (int i = 0; i < size; i++) {
            System.out.println(l+" "+in[i] + " " + squares[i]);
         }

         squarer.square(range, squares, quads);

         for (int i = 0; i < size; i++) {
            System.out.println(l+" "+ in[i] + " " + squares[i] + " " + quads[i]);
         }
         final List<ProfileInfo> profileInfo = squarer.getProfileInfo();
         if ((profileInfo != null) && (profileInfo.size() > 0)) {
             for (final ProfileInfo p : profileInfo) {
                 System.out.print(" " + p.getType() + " " + p.getLabel() + " " + (p.getStart() / 1000) + " .. "
                 + (p.getEnd() / 1000) + " " + ((p.getEnd() - p.getStart()) / 1000) + "us");
                 System.out.println();
             }
         }
         squarer.dispose();
         }
      }
   }
}
