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
package com.aparapi.examples.mdarray;

import com.aparapi.Kernel;

class ZMatMul3D extends Kernel{
   boolean[][][] A;

   boolean[][][] B;

   boolean[][][] C;

   int N;

   public ZMatMul3D(boolean[][][] A, boolean[][][] B, boolean[][][] C, int N) {
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
         C[i][j][k] ^= A[i][j][l] & B[l][j][k];
      }
   }
}
