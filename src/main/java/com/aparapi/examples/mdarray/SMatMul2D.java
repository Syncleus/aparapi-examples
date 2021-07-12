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
package com.aparapi.examples.mdarray;

import com.aparapi.Kernel;

class SMatMul2D extends Kernel{
   short[][] A;

   short[][] B;

   short[][] C;

   int N;

   /**
    * <p>Constructor for SMatMul2D.</p>
    *
    * @param A an array of {@link short} objects.
    * @param B an array of {@link short} objects.
    * @param C an array of {@link short} objects.
    * @param N a int.
    */
   public SMatMul2D(short[][] A, short[][] B, short[][] C, int N) {
      this.A = A;
      this.B = B;
      this.C = C;
      this.N = N;
   }

   /** {@inheritDoc} */
   @Override public void run() {
      int id = getGlobalId();
      int i = id / N;
      int j = id % N;
      for (int k = 0; k < N; k++) {
         C[i][j] += (short) (A[i][k] * B[k][j]);
      }
   }
}
