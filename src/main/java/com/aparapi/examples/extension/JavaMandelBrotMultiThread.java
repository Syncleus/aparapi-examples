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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import com.aparapi.*;
import com.aparapi.device.*;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.*;
import com.aparapi.Range;

/**
 * <p>JavaMandelBrotMultiThread class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class JavaMandelBrotMultiThread extends OpenCLAdapter<MandelBrot> implements MandelBrot{
   final int MAX_ITERATIONS = 64;

   final int pallette[] = new int[] {
         -65536,
         -59392,
         -53248,
         -112640,
         -106752,
         -166144,
         -160256,
         -219904,
         -279552,
         -339200,
         -399104,
         -985344,
         -2624000,
         -4197376,
         -5770496,
         -7343872,
         -8851712,
         -10425088,
         -11932928,
         -13375232,
         -14817792,
         -16260096,
         -16719602,
         -16720349,
         -16721097,
         -16721846,
         -16722595,
         -16723345,
         -16724351,
         -16725102,
         -16726110,
         -16727119,
         -16728129,
         -16733509,
         -16738889,
         -16744269,
         -16749138,
         -16754006,
         -16758619,
         -16762976,
         -16767077,
         -16771178,
         -16774767,
         -16514932,
         -15662970,
         -14942079,
         -14221189,
         -13631371,
         -13107088,
         -12648342,
         -12320669,
         -11992995,
         -11796393,
         -11665328,
         -11993019,
         -12386248,
         -12845011,
         -13303773,
         -13762534,
         -14286830,
         -14745588,
         -15269881,
         -15728637,
         -16252927,
         0
   };

   /** {@inheritDoc} */
   @Override public MandelBrot createMandleBrot(final Range range, final float scale, final float offsetx, final float offsety,
         final int[] rgb) {

      final int width = range.getGlobalSize(0);
      final int height = range.getGlobalSize(1);
      final int threadCount = 8;
      final Thread[] threads = new Thread[threadCount];
      final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
      for (int thread = 0; thread < threadCount; thread++) {
         final int threadId = thread;
         final int groupHeight = height / threadCount;
         (threads[threadId] = new Thread(new Runnable(){
            @Override public void run() {
               for (int gridy = threadId * groupHeight; gridy < ((threadId + 1) * groupHeight); gridy++) {
                  for (int gridx = 0; gridx < width; gridx++) {
                     final float x = ((((gridx) * scale) - ((scale / 2.0f) * width)) / width) + offsetx;
                     final float y = ((((gridy) * scale) - ((scale / 2.0f) * height)) / height) + offsety;
                     int count = 0;
                     float zx = x;
                     float zy = y;
                     float new_zx = 0.0f;
                     for (; (count < MAX_ITERATIONS) && (((zx * zx) + (zy * zy)) < 8.0f); count++) {
                        new_zx = ((zx * zx) - (zy * zy)) + x;
                        zy = ((2.0f * zx) * zy) + y;
                        zx = new_zx;
                     }
                     rgb[gridx + (gridy * width)] = pallette[count];
                  }
               }
               try {
                  barrier.await();
               } catch (final InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               } catch (final BrokenBarrierException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         })).start();
      }
      try {
         barrier.await();
      } catch (final InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final BrokenBarrierException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (this);
   }

}
