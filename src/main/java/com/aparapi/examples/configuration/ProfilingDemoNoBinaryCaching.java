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

import com.aparapi.internal.kernel.*;

/**
 * Created by Barney on 13/09/2015.
 */
public class ProfilingDemoNoBinaryCaching {

   public static void main(String[] ignored) {
      KernelRunner.BINARY_CACHING_DISABLED = true;
      ProfilingDemo.main(null);
   }
}
