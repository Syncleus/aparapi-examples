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

class ZMatMul1D extends Kernel{
   boolean[] A;

   boolean[] B;

   boolean[] C;

   int N;

   public ZMatMul1D(boolean[] A, boolean[] B, boolean[] C, int N) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.N = N;
   }

   @Override public void run() {
      int id = getGlobalId();
      int i = id / N;
      int j = id % N;
      for (int k = 0; k < N; k++) {
         C[i * N + j] ^= A[i * N + k] & B[k * N + j];
      }
   }
}
