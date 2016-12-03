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

public class MedianSettings {
   public final int windowWidth;
   public final int windowHeight;

   public MedianSettings(int windowSize) {
      this(windowSize, windowSize);
   }

   public MedianSettings(int windowWidth, int windowHeight) {
      this.windowWidth = windowWidth;
      this.windowHeight = windowHeight;
   }
}
