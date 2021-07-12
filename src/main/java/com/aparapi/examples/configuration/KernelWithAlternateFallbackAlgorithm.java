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
package com.aparapi.examples.configuration;

import com.aparapi.*;

/**
 * Kernel which will always fail to run on an OpenCLDevice but has an alternative fallback algorithm.
 *
 * @author freemo
 * @version $Id: $Id
 */
public class KernelWithAlternateFallbackAlgorithm extends Kernel {
   /** {@inheritDoc} */
   @Override
   public void run() {
      // deliberately, will fail to generate OpenCL as println is unsupported
      System.out.println("Running in Java (regular algorithm)");
   }

   /** {@inheritDoc} */
   @Override
   public boolean hasFallbackAlgorithm() {
      return true;
   }

   /** {@inheritDoc} */
   @Override
   public void executeFallbackAlgorithm(Range _range, int _passes) {
      System.out.println("Running in Java (alternate non-parallel algorithm)");
   }
}
