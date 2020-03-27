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
package com.aparapi.examples.reductions;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.ArrayList;

public class ReductionSum {

    public static void main(String[] args) {
        final int size = 512;

        /** Input float array for which square values need to be computed. */
        final float[] values = new float[size];

        /** Initialize input array. */
        for (int i = 0; i < size; i++) {
            values[i] = i;
//            values[i] = 1;
        }

        /** Output array which will be populated with square values of corresponding input array elements. */
        final float[] output = new float[size + 512];

        /** Aparapi Kernel which computes output of input array elements and populates them in corresponding elements of
         * output array.
         **/
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                @Local float[] localSum = new float[32];

                localSum[getLocalId(0)] = values[getGlobalId(0)];
                localBarrier();

                for (int stride = 16; stride > 0; stride = (stride /2)) {
                    if (getLocalId() < stride) {
                        localSum[getLocalId()] += localSum[getLocalId() + stride];
                    }
                    localBarrier();
                }

                if (getLocalId(0) == 0) {
                    output[getGlobalId(0)] = localSum[0];
//                    output[getGlobalId(0)] = localSum[0];
                }

            }
        };

        // Execute Kernel.

        kernel.execute(Range.create(512, 32));

        // Report target execution mode: GPU or JTP (Java Thread Pool).
        System.out.println("Device = " + kernel.getTargetDevice().getShortDescription());

        // Display computed square values.
        for (int i = 0; i < size; i++) {
            System.out.printf("%6.0f %8.0f\n", values[i], output[i]);
        }

        float reduceValue = 0;
        float reduceValuePar = 0;

        for (int i = 0; i < size; i++) {
            reduceValue += values[i];
//            reduceValuePar += output[i];
        }

        for (int i = 0; i < output.length; i++) {
            if (output[i] != 0) {
                System.out.println(" d d d " + i + " : " + output[i]);
            }
            reduceValuePar += output[i];
        }
        System.out.printf("Reduce op %8.0f \n", output[0]);
        System.out.printf("Reduce PAR %8.0f \n", reduceValuePar);
        System.out.printf("Reduce SEQ %8.0f \n", reduceValue);
        // Dispose Kernel resources.
        kernel.dispose();
    }

    public static Kernel reduceOp() {
        return null;
    }
}
