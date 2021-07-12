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
package com.aparapi.examples.extension;

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.OpenCL;

/**
 * <p>HistogramIdeal class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class HistogramIdeal{

   // @Resource("com/amd/aparapi/sample/extension/HistogramKernel.cl")
   interface HistogramKernel extends OpenCL<HistogramKernel>{

      public HistogramKernel histogram256(//
            Range _range,//
            @GlobalReadOnly("data") byte[] data,//
            @Local("sharedArray") byte[] sharedArray,//
            @GlobalWriteOnly("binResult") int[] binResult,//
            @Arg("binSize") int binSize);

      public HistogramKernel bin256(//
            Range _range,//        
            @GlobalWriteOnly("histo") int[] histo,//
            @GlobalReadOnly("binResult") int[] binResult,//
            @Arg("subHistogramSize") int subHistogramSize);
   }

   /**
    * <p>main.</p>
    *
    * @param args an array of {@link java.lang.String} objects.
    */
   public static void main(String[] args) {
      final int WIDTH = 1024 * 16;
      final int HEIGHT = 1024 * 8;
      final int BIN_SIZE = 128;
      final int GROUP_SIZE = 128;
      final int SUB_HISTOGRAM_COUNT = ((WIDTH * HEIGHT) / (GROUP_SIZE * BIN_SIZE));

      final byte[] data = new byte[WIDTH * HEIGHT];
      for (int i = 0; i < (WIDTH * HEIGHT); i++) {
         data[i] = (byte) ((Math.random() * BIN_SIZE) / 2);
      }
      final byte[] sharedArray = new byte[GROUP_SIZE * BIN_SIZE];
      final int[] binResult = new int[SUB_HISTOGRAM_COUNT * BIN_SIZE];
      System.out.println("binResult size=" + binResult.length);
      final int[] histo = new int[BIN_SIZE];
      final int[] refHisto = new int[BIN_SIZE];
      final Device device = KernelManager.instance().bestDevice();

      if (device != null) {
         System.out.println(((OpenCLDevice) device).getOpenCLPlatform().getName());
         final Range rangeBinSize = device.createRange(BIN_SIZE);

         final Range range = Range.create((WIDTH * HEIGHT) / BIN_SIZE, GROUP_SIZE);

         if (device instanceof OpenCLDevice) {
            final OpenCLDevice openclDevice = (OpenCLDevice) device;

            final HistogramKernel histogram = openclDevice.bind(HistogramKernel.class, Histogram.class.getClassLoader()
                  .getResourceAsStream("HistogramKernel.cl"));
            long start = System.nanoTime();
            histogram.begin()//
                  .put(data)//
                  .histogram256(range, data, sharedArray, binResult, BIN_SIZE)//
                  // by leaving binResult on the GPU we can save two 1Mb transfers
                  .bin256(rangeBinSize, histo, binResult, SUB_HISTOGRAM_COUNT)//
                  .get(histo)//
                  .end();
            System.out.println("opencl " + ((System.nanoTime() - start) / 1000000));
            start = System.nanoTime();
            for (int i = 0; i < (WIDTH * HEIGHT); i++) {
               refHisto[data[i]]++;
            }
            System.out.println("java " + ((System.nanoTime() - start) / 1000000));
            for (int i = 0; i < 128; i++) {
               if (refHisto[i] != histo[i]) {
                  System.out.println(i + " " + histo[i] + " " + refHisto[i]);
               }
            }

         }
      } else {
         System.out.println("no GPU device");
      }
   }
}
