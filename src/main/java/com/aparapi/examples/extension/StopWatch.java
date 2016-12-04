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
package com.aparapi.examples.extension;

public class StopWatch{
   long start = 0L;

   public void start() {
      start = System.nanoTime();
   }

   public void print(String _str) {
      long end = (System.nanoTime() - start) / 1000000;
      System.out.println(_str + " " + end);
   }

}
