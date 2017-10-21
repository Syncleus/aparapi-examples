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
 * This material was prepared as an account of work sponsored by an agency of the United States Government.  
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any of 
 * their employees, nor any jurisdiction or organization that has cooperated in the development of these materials, 
 * makes any warranty, express or implied, or assumes any legal liability or responsibility for the accuracy, 
 * completeness, or usefulness or any information, apparatus, product, software, or process disclosed, or represents
 * that its use would not infringe privately owned rights.
 */
package com.aparapi.examples.matrix;

import com.aparapi.Kernel.EXECUTION_MODE;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.util.OpenBitSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This test class performs the following functions:
 *
 * 1) Create a randomly populated set of matrices for correlation/co-occurrence computation
 * 2) Execute the CPU-based computation using Lucene OpenBitSets
 * 3) Execute the GPU-based computation using Aparapi CorrMatrix host and kernel
 * 4) Verify the results of OpenBitSet and CorrMatrix by comparing matrices to each other
 *
 * @author ryan.lamothe at gmail.com
 *
 */
public class Main {
    /**
     * NumTerms and NumLongs (documents) need to be adjusted manually right now to force 'striping' to occur (see Host code for details)
     */
    public static void main(String[] _args) {
        final List<Pair<OpenBitSet, OpenBitSet>> obsPairs = new ArrayList<Pair<OpenBitSet, OpenBitSet>>();
        ;

        final Random rand = new Random();

        int[][] obsResultMatrix;

      /*
       * Populate test data
       */
        System.out.println("----------");
        System.out.println("Populating test matrix data using settings from build.xml...");
        System.out.println("----------");

        final int numTerms = Integer.getInteger("numRows", 300); // # Rows
        // numLongs*64 for number of actual documents since these are 'packed' longs
        final int numLongs = Integer.getInteger("numColumns", 10000); // # Columns

        for (int i = 0; i < numTerms; ++i) {
            final long[] bits = new long[numLongs];
            for (int j = 0; j < numLongs; ++j) {
                bits[j] = rand.nextLong();
            }

            obsPairs.add(i, new ImmutablePair<OpenBitSet, OpenBitSet>(new OpenBitSet(bits, numLongs), new OpenBitSet(bits, numLongs)));
        }

      /*
       * OpenBitSet calculations
       */
        System.out.println("Executing OpenBitSet intersectionCount");

        final long startTime = System.currentTimeMillis();

        obsResultMatrix = new int[obsPairs.size()][obsPairs.size()];

        // This is an N^2 comparison loop
        // FIXME This entire loop needs to be parallelized to show an apples-to-apples comparison to Aparapi
        for (int i = 0; i < obsPairs.size(); i++) {
            final Pair<OpenBitSet, OpenBitSet> docFreqVector1 = obsPairs.get(i);

            for (int j = 0; j < obsPairs.size(); j++) {
                final Pair<OpenBitSet, OpenBitSet> docFreqVector2 = obsPairs.get(j);

                // # of matches in both sets of documents
                final int result = (int) OpenBitSet.intersectionCount(docFreqVector1.getLeft(), docFreqVector2.getRight());
                obsResultMatrix[i][j] = result;
            }
        }

        final long endTime = System.currentTimeMillis() - startTime;

        System.out.println("OpenBitSet Gross Execution Time: " + endTime + " ms <------OpenBitSet");
        System.out.println("----------");

      /*
       * GPU calculations
       */
        System.out.println("Executing Aparapi intersectionCount");

        final long[][] matrixA = new long[obsPairs.size()][];
        final long[][] matrixB = new long[obsPairs.size()][];

        // Convert OpenBitSet pairs to long primitive arrays for use with Aparapi
        // TODO It would be nice if we could find a way to put the obsPairs onto the GPU directly :)
        for (int i = 0; i < obsPairs.size(); i++) {
            final OpenBitSet obsA = obsPairs.get(i).getLeft();
            final OpenBitSet obsB = obsPairs.get(i).getRight();

            matrixA[i] = obsA.getBits();
            matrixB[i] = obsB.getBits();
        }

        // The reason for setting this property is because the CorrMatrix host/kernel code
        // came from a GUI where a user could select "Use Hardware Acceleration" instead
        // of the application forcing the setting globally on the command-line
        final int[][] gpuResultMatrix;
        gpuResultMatrix = CorrMatrixHost.intersectionMatrix(matrixA, matrixB, EXECUTION_MODE.GPU);

        // Compare the two result arrays to make sure we are generating the same output
        System.out.println("[i][j] -> OpenBitSet Result : GPU Result Array");
        for (int i = 0; i < obsResultMatrix.length; i++) {
            for (int j = 0; j < obsResultMatrix[i].length; j++)
                if (obsResultMatrix[i][j] != gpuResultMatrix[i][j]) {
                    System.out.println("[" + i + "][" + j + "] -> " + obsResultMatrix[i][j] + " : " + gpuResultMatrix[i][j]);
            }
        }
        System.out.println("Any elements not listed matched!");
    }
}
