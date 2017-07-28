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

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.opencl.OpenCL;
import com.aparapi.opencl.OpenCL.Resource;

public class Histogram{

   @Resource("com/aparapi/examples/extension/HistogramKernel.cl") interface HistogramKernel extends OpenCL<HistogramKernel>{

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
      final Kernel k = new Kernel(){

         @Override public void run() {
            final int j = getGlobalId(0);
            for (int i = 0; i < SUB_HISTOGRAM_COUNT; ++i) {
               histo[j] += binResult[(i * BIN_SIZE) + j];
            }
         }

      };
      final Device device = k.getTargetDevice();
      final Range range2 = device.createRange(BIN_SIZE);
      k.execute(range2);

      final Range range = Range.create((WIDTH * HEIGHT) / BIN_SIZE, GROUP_SIZE);

      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         final HistogramKernel histogram = openclDevice.bind(HistogramKernel.class);

         final StopWatch timer = new StopWatch();
         timer.start();

         histogram.histogram256(range, data, sharedArray, binResult, BIN_SIZE);
         final boolean java = false;
         final boolean aparapiKernel = false;
         if (java) {
            // Calculate final histogram bin 
            for (int j = 0; j < BIN_SIZE; ++j) {
               for (int i = 0; i < SUB_HISTOGRAM_COUNT; ++i) {
                  histo[j] += binResult[(i * BIN_SIZE) + j];
               }
            }
         } else if (aparapiKernel) {
            k.execute(range2);
         } else {
            histogram.bin256(range2, histo, binResult, SUB_HISTOGRAM_COUNT);
         }
         timer.print("opencl");
         timer.start();
         for (int i = 0; i < (WIDTH * HEIGHT); i++) {
            refHisto[data[i]]++;
         }
         timer.print("java");
         for (int i = 0; i < 128; i++) {
            if (refHisto[i] != histo[i]) {
               System.out.println(i + " " + histo[i] + " " + refHisto[i]);
            }
         }
      }
   }
}
