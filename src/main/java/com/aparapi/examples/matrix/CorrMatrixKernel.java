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

import com.aparapi.Kernel;

/**
 * This kernel attempts to re-implement the Lucene OpenBitSet functionality on a GPU
 *
 * Based on code from:
 * <a href="http://grepcode.com/file/repo1.maven.org/maven2/org.apache.lucene/lucene-core/3.1.0/org/apache/lucene/util/BitUtil.java">apache.lucene.util.BitUtil.java</a>
 *
 * @author ryan.lamothe at gmail.com
 * @author sedillard at gmail.com
 * @version $Id: $Id
 */
public class CorrMatrixKernel extends Kernel {

   final long[] matrixA;

   final int matrixA_NumTerms;

   final long[] matrixB;

   final int matrixB_NumTerms;

   int numLongs;

   int[] resultMatrix;

   /**
    * Default constructor
    *
    * @param matrixA          Matrix A.
    * @param matrixB          Matrix B.
    * @param matrixA_NumTerms Number of terms in Matrix A.
    * @param matrixB_NumTerms Number of terms in Matrix B.
    * @param numLongs         Number of longs.
    * @param resultMatrix     The matrix to store the results in.
    */
   public CorrMatrixKernel(final long[] matrixA, final int matrixA_NumTerms, final long[] matrixB, final int matrixB_NumTerms,
         final int numLongs, final int[] resultMatrix) {
      this.matrixA = matrixA;
      this.matrixA_NumTerms = matrixA_NumTerms;
      this.matrixB = matrixB;
      this.matrixB_NumTerms = matrixB_NumTerms;
      this.numLongs = numLongs;
      this.resultMatrix = resultMatrix;
   }

   /** {@inheritDoc} */
   @Override
   public void run() {
      final int i = this.getGlobalId(0);

      if (i < matrixA_NumTerms) {
         final int j = this.getGlobalId(1);

         if (j < matrixB_NumTerms) {
            // For testing purposes, you can use the naive implementation to compare performance
            resultMatrix[(i * matrixB_NumTerms) + j] = pop_intersect(matrixA, i * numLongs, matrixB, j * numLongs, numLongs);
            // this.resultMatrix[i * matrixB_NumTerms + j] = this.naive_pop_intersect(matrixA, i * numLongs, matrixB, j * numLongs, numLongs);
         }
      }
   }

   /**
    * A naive implementation of the pop_array code below
    *
    * @param matrixA  Matrix A.
    * @param matrixB  Matrix B.
    * @param aStart   Offset for Matrix A.
    * @param bStart   Offset for Matrix B.
    * @param numWords The number of words to operate on.
    */
   private int naive_pop_intersect(final long matrixA[], final int aStart, final long matrixB[], final int bStart, final int numWords) {
      int sum = 0;

      for (int i = 0; i < numWords; i++) {
         sum += pop(matrixA[aStart + i] & matrixB[bStart + i]);
      }

      return sum;
   }

   /**
    * Returns the popcount or cardinality of the two sets after an intersection.
    * Neither array is modified.
    *
    * Modified for the purposes of this kernel from its original version
    *
    * @param matrixA  Matrix A.
    * @param matrixB  Matrix B.
    * @param aStart   Offset for Matrix A.
    * @param bStart   Offset for Matrix B.
    * @param numWords The number of words to operate on.
    */
   private int pop_intersect(final long matrixA[], final int aStart, final long matrixB[], final int bStart, final int numWords) {

      /*
       * http://grepcode.com/file/repo1.maven.org/maven2/org.apache.lucene/lucene-core/3.1.0/org/apache/lucene/util/BitUtil.java
       */

      // generated from pop_array via sed 's/A\[\([^]]*\)\]/\(A[\1] \& B[\1]\)/g'
      final int n = numWords;
      int tot = 0, tot8 = 0;
      long ones = 0, twos = 0, fours = 0;

      int i;
      for (i = 0; i <= (n - 8); i += 8) {
         long twosA = 0;
         long twosB = 0;
         long foursA = 0;
         long foursB = 0;
         long eights = 0;

         final int ai = aStart + i;
         final int bi = bStart + i;

         // CSA(twosA, ones, ones, (A[i] & B[i]), (A[i+1] & B[i+1]))
         {
            final long b = matrixA[ai] & matrixB[bi], c = matrixA[ai + 1] & matrixB[bi + 1];
            final long u = ones ^ b;
            twosA = (ones & b) | (u & c);
            ones = u ^ c;
         }

         // CSA(twosB, ones, ones, (A[i+2] & B[i+2]), (A[i+3] & B[i+3]))
         {
            final long b = matrixA[ai + 2] & matrixB[bi + 2], c = matrixA[ai + 3] & matrixB[bi + 3];
            final long u = ones ^ b;
            twosB = (ones & b) | (u & c);
            ones = u ^ c;
         }

         // CSA(foursA, twos, twos, twosA, twosB)
         {
            final long u = twos ^ twosA;
            foursA = (twos & twosA) | (u & twosB);
            twos = u ^ twosB;
         }

         // CSA(twosA, ones, ones, (A[i+4] & B[i+4]), (A[i+5] & B[i+5]))
         {
            final long b = matrixA[ai + 4] & matrixB[bi + 4], c = matrixA[ai + 5] & matrixB[bi + 5];
            final long u = ones ^ b;
            twosA = (ones & b) | (u & c);
            ones = u ^ c;
         }

         // CSA(twosB, ones, ones, (A[i+6] & B[i+6]), (A[i+7] & B[i+7]))
         {
            final long b = matrixA[ai + 6] & matrixB[bi + 6], c = matrixA[ai + 7] & matrixB[bi + 7];
            final long u = ones ^ b;
            twosB = (ones & b) | (u & c);
            ones = u ^ c;
         }

         // CSA(foursB, twos, twos, twosA, twosB)
         {
            final long u = twos ^ twosA;
            foursB = (twos & twosA) | (u & twosB);
            twos = u ^ twosB;
         }

         // CSA(eights, fours, fours, foursA, foursB)
         {
            final long u = fours ^ foursA;
            eights = (fours & foursA) | (u & foursB);
            fours = u ^ foursB;
         }

         tot8 += pop(eights);
      }

      if (i <= (n - 4)) {
         final int ai = aStart + i;
         final int bi = bStart + i;

         long twosA = 0;
         long twosB = 0;
         long foursA = 0;
         long eights = 0;

         {
            final long b = matrixA[ai] & matrixB[bi], c = matrixA[ai + 1] & matrixB[bi + 1];
            final long u = ones ^ b;
            twosA = (ones & b) | (u & c);
            ones = u ^ c;
         }

         {
            final long b = matrixA[ai + 2] & matrixB[bi + 2], c = matrixA[ai + 3] & matrixB[bi + 3];
            final long u = ones ^ b;
            twosB = (ones & b) | (u & c);
            ones = u ^ c;
         }

         {
            final long u = twos ^ twosA;
            foursA = (twos & twosA) | (u & twosB);
            twos = u ^ twosB;
         }

         eights = fours & foursA;
         fours = fours ^ foursA;

         tot8 += pop(eights);
         i += 4;
      }

      if (i <= (n - 2)) {
         final int ai = aStart + i;
         final int bi = bStart + i;

         final long b = matrixA[ai] & matrixB[bi], c = matrixA[ai + 1] & matrixB[bi + 1];
         final long u = ones ^ b;
         final long twosA = (ones & b) | (u & c);
         ones = u ^ c;

         final long foursA = twos & twosA;
         twos = twos ^ twosA;

         final long eights = fours & foursA;
         fours = fours ^ foursA;

         tot8 += pop(eights);
         i += 2;
      }

      if (i < n) {
         final int ai = aStart + i;
         final int bi = bStart + i;

         tot += pop(matrixA[ai] & matrixB[bi]);
      }

      tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

      return tot;
   }

   /**
    * Returns the number of bits set in the long
    *
    * @param x The long whose bit count is needed.
    */
   private int pop(long x) {

      /*
       * http://grepcode.com/file/repo1.maven.org/maven2/org.apache.lucene/lucene-core/3.1.0/org/apache/lucene/util/BitUtil.java
       */

      /*
       * Hacker's Delight 32 bit pop function:
       * http://www.hackersdelight.org/HDcode/newCode/pop_arrayHS.c.txt
       *
       * int pop(unsigned x) {
       * x = x - ((x >> 1) & 0x55555555);
       * x = (x & 0x33333333) + ((x >> 2) & 0x33333333);
       * x = (x + (x >> 4)) & 0x0F0F0F0F;
       * x = x + (x >> 8);
       * x = x + (x >> 16);
       * return x & 0x0000003F;
       * }
       * *
       */

      // 64 bit java version of the C function from above
      x = x - ((x >>> 1) & 0x5555555555555555L);
      x = (x & 0x3333333333333333L) + ((x >>> 2) & 0x3333333333333333L);
      x = (x + (x >>> 4)) & 0x0F0F0F0F0F0F0F0FL;
      x = x + (x >>> 8);
      x = x + (x >>> 16);
      x = x + (x >>> 32);
      return (int) x & 0x7F;
   }
}
