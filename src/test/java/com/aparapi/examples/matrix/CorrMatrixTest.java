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
 * This material was prepared as an account of work sponsored by an agency of the United States Government.  
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any of 
 * their employees, nor any jurisdiction or organization that has cooperated in the development of these materials, 
 * makes any warranty, express or implied, or assumes any legal liability or responsibility for the accuracy, 
 * completeness, or usefulness or any information, apparatus, product, software, or process disclosed, or represents
 * that its use would not infringe privately owned rights.
 */
package com.aparapi.examples.matrix;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.lucene.util.FixedBitSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel.EXECUTION_MODE;

/**
 * This test class performs the following functions:
 *
 * 1) Create a randomly populated set of matrices for correlation/co-occurrence computation
 * 2) Execute the CPU-based computation using Lucene FixedBitSets
 * 3) Execute the GPU-based computation using Aparapi CorrMatrix host and kernel
 * 4) Verify the results of FixedBitSet and CorrMatrix by comparing matrices to each other
 *
 * @author ryan.lamothe at gmail.com
 *
 */
public class CorrMatrixTest {

   private static final Logger LOG = Logger.getLogger(CorrMatrixTest.class);

   private final List<Pair<FixedBitSet, FixedBitSet>> obsPairs = new ArrayList<Pair<FixedBitSet, FixedBitSet>>();;

   private final Random rand = new Random();

   private int[][] obsResultMatrix;

   /**
    * NumTerms and NumLongs (documents) need to be adjusted manually right now to force 'striping' to occur (see Host code for details)
    */
   @Before
   public void setup() throws Exception {
      /*
       * Populate test data
       */
      LOG.debug("----------");
      LOG.debug("Populating test matrix data using settings from build.xml...");
      LOG.debug("----------");

      final int numTerms = Integer.getInteger("numRows", 300); // # Rows
      // numLongs*64 for number of actual documents since these are 'packed' longs
      final int numLongs = Integer.getInteger("numColumns", 10000); // # Columns

      for (int i = 0; i < numTerms; ++i) {
         final FixedBitSet first = new FixedBitSet(numLongs);
         final FixedBitSet second = new FixedBitSet(numLongs);

         //final long[] bits = new long[numLongs];
         for (int j = 0; j < numLongs; ++j) {
            if (rand.nextBoolean()) 
               first.set(j);
            if (rand.nextBoolean()) 
               second.set(j);
         }

         obsPairs.add(i, new ImmutablePair<FixedBitSet, FixedBitSet>(first, second));

      }

      /*
       * FixedBitSet calculations
       */
      LOG.debug("Executing FixedBitSet intersectionCount");

      final long startTime = System.currentTimeMillis();

      obsResultMatrix = new int[obsPairs.size()][obsPairs.size()];

      // This is an N^2 comparison loop
      // FIXME This entire loop needs to be parallelized to show an apples-to-apples comparison to Aparapi
      for (int i = 0; i < obsPairs.size(); i++) {
         final Pair<FixedBitSet, FixedBitSet> docFreqVector1 = obsPairs.get(i);

         for (int j = 0; j < obsPairs.size(); j++) {
            final Pair<FixedBitSet, FixedBitSet> docFreqVector2 = obsPairs.get(j);

            // # of matches in both sets of documents
            final int result = (int) FixedBitSet.intersectionCount(docFreqVector1.getLeft(), docFreqVector2.getRight());
            obsResultMatrix[i][j] = result;
         }
      }

      final long endTime = System.currentTimeMillis() - startTime;

      LOG.debug("FixedBitSet Gross Execution Time: " + endTime + " ms <------FixedBitSet");
      LOG.debug("----------");
   }

   @Test
   public void testCorrelationMatrix() throws Exception {
      /*
       * GPU calculations
       */
      LOG.debug("Executing Aparapi intersectionCount");

      final long[][] matrixA = new long[obsPairs.size()][];
      final long[][] matrixB = new long[obsPairs.size()][];

      // Convert FixedBitSet pairs to long primitive arrays for use with Aparapi
      // TODO It would be nice if we could find a way to put the obsPairs onto the GPU directly :)
      for (int i = 0; i < obsPairs.size(); i++) {
         final FixedBitSet obsA = obsPairs.get(i).getLeft();
         final FixedBitSet obsB = obsPairs.get(i).getRight();

         matrixA[i] = obsA.getBits();
         matrixB[i] = obsB.getBits();
      }

      // The reason for setting this property is because the CorrMatrix host/kernel code
      // came from a GUI where a user could select "Use Hardware Acceleration" instead
      // of the application forcing the setting globally on the command-line
      final int[][] gpuResultMatrix;
      gpuResultMatrix = CorrMatrixHost.intersectionMatrix(matrixA, matrixB, EXECUTION_MODE.GPU);

      // Compare the two result arrays to make sure we are generating the same output
      for (int i = 0; i < obsResultMatrix.length; i++) {
         Assert.assertTrue("Arrays are not equal", Arrays.equals(obsResultMatrix[i], gpuResultMatrix[i]));
      }

      // Visually compare/third-party tool compare if desired
      if (LOG.isTraceEnabled()) {
         // We're not using "try with resources" because Aparapi currently targets JDK 6
         final PrintWriter cpuOut = new PrintWriter(new File(System.getProperty("user.dir"), "trace/cpuOut.txt"));
         final PrintWriter gpuOut = new PrintWriter(new File(System.getProperty("user.dir"), "trace/gpuOut.txt"));

         try {
            for (int i = 0; i < obsResultMatrix.length; i++) {
               if (LOG.isTraceEnabled()) {
                  LOG.trace("obsResultMatrix length: " + obsResultMatrix.length);
                  LOG.trace("gpuResultMatrix length: " + gpuResultMatrix.length);

                  cpuOut.println(Arrays.toString(obsResultMatrix[i]));
                  gpuOut.println(Arrays.toString(gpuResultMatrix[i]));
               }
            }
         } finally {
            if (cpuOut != null) {
               cpuOut.flush();
               cpuOut.close();
            }

            if (gpuOut != null) {
               gpuOut.flush();
               gpuOut.close();
            }
         }
      }
   }
}
