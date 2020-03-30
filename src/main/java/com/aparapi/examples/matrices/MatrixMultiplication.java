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

package com.aparapi.examples.matrices;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Arrays;
import java.util.Random;

/**
 * An example Aparapi application which computes and displays squares of a set of 512 input values.
 * While executing on GPU using Aparpi framework, each square value is computed in a separate kernel invocation and 
 * can thus maximize performance by optimally utilizing all GPU computing units 
 *
 */

public class MatrixMultiplication {

    public static void main(String[] _args) {
        int size = 256;

        float[] arrayA = new float[size * size];
        float[] arrayB = new float[size * size];

        initializeInputArrays(arrayA, size);
        initializeInputArrays(arrayB, size);

        float[] arrayC = new float[size * size];
        final float[] arrayCParallel = new float[size * size];

        Arrays.fill(arrayC,0f);
        Arrays.fill(arrayCParallel,0f);

        MatrixMultiplicationKernel kernel = new MatrixMultiplicationKernel(arrayA, arrayB, arrayCParallel, size);

        Range range = Range.create2D(kernel.getTargetDevice(), size, size);

        kernel.put(arrayA);
        kernel.put(arrayB);
        kernel.put(arrayCParallel);

        kernel.execute(range);

        kernel.get(arrayCParallel);


        System.out.println("Device info " + kernel.getTargetDevice().toString() + " ! ");
        for (int iter = 0; iter < 15; iter++) {
            kernel.execute(range);
            System.out.println("Time " + kernel.getExecutionTime() + " (ms) "); //Determine the execution time of the previous Kernel.execute(range) call.
        }

        kernel.get(arrayCParallel);

        arrayC = matrixMultiplicationSerial(arrayA, arrayB, arrayC, size);

        System.out.println("Verify :  " + verify(arrayC, arrayCParallel, size));

        kernel.dispose();
    }

    private static boolean verify(float[] seq, float[] par, int size) {
        boolean check = true;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Math.abs(seq[j * size + i] - par[j * size + i]) > 0f) {
                    check = false;
                    break;
                }
            }
        }
        return check;
    }

    private static float[] matrixMultiplicationSerial(float[] a, float[] b, float[] c, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float sum = 0f;
                for (int y = 0; y < size; y++) {
                    sum += a[y * size + i] * b[j * size + y];
                }
                c[j * size + i] = sum;
            }
        }
        return c;
    }

    private static float[] initializeInputArrays(float[] input, int size) {
        Random number = new Random();
        for (int i = 0; i < size * size; i++) {
//            input[i] = number.nextFloat();
//            input[i] = number.nextFloat();
            input[i] = 2f;
        }
        return input;
    }

    public static class MatrixMultiplicationKernel extends Kernel {

        final float[] arrayA;
        final float[] arrayB;
        final float[] arrayC;
        final int size;

        MatrixMultiplicationKernel(float[] arrayA, float[] arrayB, float[] arrayC, int size) {
            this.arrayA = arrayA;
            this.arrayB = arrayB;
            this.arrayC = arrayC;
            this.size = size;
        }

        @Override
        public void run() {
            float sum = 0f;
            if (getGlobalId(0) < size && getGlobalId(1) < size)
                for (int y = 0; y < size; y++) {
                    sum += arrayA[(getGlobalId(1) * size) + y] * arrayB[(y * size) + getGlobalId(0)];
                }
            arrayC[(getGlobalId(1) * size) + getGlobalId(0)] = sum;
        }
    }
}
