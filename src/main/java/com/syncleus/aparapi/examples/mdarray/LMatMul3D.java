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
package com.syncleus.aparapi.examples.mdarray;

import com.syncleus.aparapi.Kernel;

class LMatMul3D extends Kernel{
   long[][][] A;

   long[][][] B;

   long[][][] C;

   int N;

   public LMatMul3D(long[][][] A, long[][][] B, long[][][] C, int N) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.N = N;
   }

   @Override public void run() {
      int id = getGlobalId();
      int i = id / (N * N);
      int j = (id / N) % N;
      int k = id % N;
      for (int l = 0; l < N; l++) {
         C[i][j][k] += A[i][j][l] * B[l][j][k];
      }
   }
}
