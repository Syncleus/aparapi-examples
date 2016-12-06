/*

Copyright (c) 2010-2011, Advanced Micro Devices, Inc.

All rights reserved.



Redistribution and use in source and binary forms, with or without modification, are permitted provided that the

following conditions are met:



Redistributions of source code must retain the above copyright notice, this list of conditions and the following

disclaimer. 



Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following

disclaimer in the documentation and/or other materials provided with the distribution. 



Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products

derived from this software without specific prior written permission. 



THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,

INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE

DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,

SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR

SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 

WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 

OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export

laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through

774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,

you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 

Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 

Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,

E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups

D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject

to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774

of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations

under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 



*/

package com.aparapi.examples.add;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.opencl.OpenCLPlatform;
import java.util.List;

public class MainSelectPlatform {

   public static void listPlatformsAndDevices()
   {
      List<OpenCLPlatform> platforms = (new OpenCLPlatform()).getOpenCLPlatforms();

      int platformc = 0;
      for (OpenCLPlatform platform : platforms) 
      {

           System.out.println("Platform " + platformc + "{");

           System.out.println("   Name    : \"" + platform.getName() + "\"");

           System.out.println("   Vendor  : \"" + platform.getVendor() + "\"");

           System.out.println("   Version : \"" + platform.getVersion() + "\"");

           List<OpenCLDevice> devices = platform.getOpenCLDevices();

           System.out.println("   Platform contains " + devices.size() + " OpenCL devices");

           int devicec = 0;

           for (OpenCLDevice device : devices) 
           {
               System.out.println("   Device " + devicec + "{");

               System.out.println("       Type                  : " + device.getType());

               System.out.println("       GlobalMemSize         : " + device.getGlobalMemSize());

               System.out.println("       LocalMemSize          : " + device.getLocalMemSize());

               System.out.println("       MaxComputeUnits       : " + device.getMaxComputeUnits());

               System.out.println("       MaxWorkGroupSizes     : " + device.getMaxWorkGroupSize());

               System.out.println("       MaxWorkItemDimensions : " + device.getMaxWorkItemDimensions());

               System.out.println("   }");

               devicec++;
           }

           // close platform bracket
           System.out.println("}");

           platformc++;
      }
   }

   public static void main(String[] args) {



      final int size = 1000*1000;



      final float[] a = new float[size];

      final float[] b = new float[size];



      for (int i = 0; i < size; i++) {

         a[i] = (float) (Math.random() * 100);

         b[i] = (float) (Math.random() * 100);

      }



      final float[] sum = new float[size];



      Kernel kernel = new Kernel(){

         @Override public void run() {

            int gid = getGlobalId();

            sum[gid] = a[gid] + b[gid];

         }

      };



     

      // !!! oren -> add time measurement 

      System.out.printf("Running kernel..");



      long startTime = System.nanoTime();
      
     
      // !!! experiment with platform/device selection
      System.out.printf("**** listPlatformsAndDevices ****\n");
      listPlatformsAndDevices();
      System.out.printf("****************\n");
      if(args.length<2)
      {
         System.out.printf("****************\n");
         System.out.printf("Usage is: select platformHint deviceType\n");
         System.out.printf("****************\n");
         return;
      }

      String platformHint = args[0];
      String deviceType = args[1];
      int deviceId = (args.length>2) ? Integer.parseInt(args[2]) : 0;
      String flowTypeStr = (args.length>3) ? args[3] : null;
      if(flowTypeStr!=null)
    	  kernel.setFlowType(flowTypeStr);
      System.out.printf("**** getDevice ****\n");
      Device device = Device.getDevice(platformHint,deviceType,deviceId);
      kernel.execute(Range.create(device,512,16)); 
      System.out.printf("****************\n");
      
      // test new range functionality
      Range.create(device,Range.create(512,16));



      long elapsedTimeNano = System.nanoTime() - startTime;

      

      long elapsedTimeSec = TimeUnit.SECONDS.convert(elapsedTimeNano, TimeUnit.NANOSECONDS);

      

      long elapsedTimeMilli = TimeUnit.MILLISECONDS.convert(elapsedTimeNano, TimeUnit.NANOSECONDS);

      

      System.out.printf("****************\n");

      System.out.printf("Elapsed time in milli: %d\n",elapsedTimeMilli);

      System.out.printf("Elapsed time in sec  : %d\n",elapsedTimeSec);

      System.out.printf("****************\n");



      // !!! oren change -> show first 10 only 

      //for (int i = 0; i < size; i++) {

      int displayRange = (size > 20) ? 20 : size; 

      System.out.printf("**************** Showing first %d results ****************\n",displayRange);

      for (int i = 0; i < displayRange; i++) {

         System.out.printf("%6.2f + %6.2f = %8.2f\n", a[i], b[i], sum[i]);

      }



      kernel.dispose();

   }



}
