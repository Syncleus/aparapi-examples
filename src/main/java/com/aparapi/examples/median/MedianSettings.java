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
package com.aparapi.examples.median;

/**
 * <p>MedianSettings class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class MedianSettings {
   public final int windowWidth;
   public final int windowHeight;

   /**
    * <p>Constructor for MedianSettings.</p>
    *
    * @param windowSize a int.
    */
   public MedianSettings(int windowSize) {
      this(windowSize, windowSize);
   }

   /**
    * <p>Constructor for MedianSettings.</p>
    *
    * @param windowWidth a int.
    * @param windowHeight a int.
    */
   public MedianSettings(int windowWidth, int windowHeight) {
      this.windowWidth = windowWidth;
      this.windowHeight = windowHeight;
   }
}
