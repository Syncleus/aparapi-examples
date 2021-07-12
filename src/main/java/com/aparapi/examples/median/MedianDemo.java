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

import com.aparapi.internal.kernel.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URISyntaxException;

/**
 * Demonstrate use of __private namespaces and @NoCL annotations.
 *
 * @author freemo
 * @version $Id: $Id
 */
public class MedianDemo {
   /** Constant <code>testImage</code> */
   public static BufferedImage testImage;

   static {
      try {
         File imageFile = new File(MedianDemo.class.getResource("/testcard.jpg").toURI()).getCanonicalFile();
         if (imageFile.exists()) {
            testImage = ImageIO.read(imageFile);
         }
      } catch (IOException | URISyntaxException e) {
         throw new IllegalStateException("Could not open image", e);
      }
   }

   /**
    * <p>main.</p>
    *
    * @param ignored an array of {@link java.lang.String} objects.
    */
   public static void main(String[] ignored) {
      final int size = 5;
      System.setProperty("com.aparapi.dumpProfilesOnExit", "true");
      boolean verbose = false;
      if (verbose)
      {
          System.setProperty("com.aparapi.enableVerboseJNI", "true");
          System.setProperty("com.aparapi.dumpFlags", "true");
          System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "true");
          System.setProperty("com.aparapi.enableVerboseJNIOpenCLResourceTracking", "true");
          System.setProperty("com.aparapi.enableExecutionModeReporting", "true");
      }

      System.out.println(KernelManager.instance().bestDevice());

      int[] argbs = testImage.getRGB(0, 0, testImage.getWidth(), testImage.getHeight(), null, 0, testImage.getWidth());
      MedianKernel7x7 kernel = createMedianKernel(argbs);

      kernel.processImages(new MedianSettings(size));
      BufferedImage out = new BufferedImage(testImage.getWidth(), testImage.getHeight(), BufferedImage.TYPE_INT_RGB);
      out.setRGB(0, 0, testImage.getWidth(), testImage.getHeight(), kernel._destPixels, 0, testImage.getWidth());
      ImageIcon icon1 = new ImageIcon(testImage);
      JLabel label1 = new JLabel(icon1);
      ImageIcon icon2 = new ImageIcon(out);
      JLabel label2 = new JLabel(icon2);
      JFrame frame = new JFrame("Test Median");
      frame.setLayout(new FlowLayout());
      frame.getContentPane().add(label1);
      frame.getContentPane().add(label2);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      StringBuilder builder = new StringBuilder();
      KernelManager.instance().reportDeviceUsage(builder, true);
      System.out.println(builder);

      int reps = 50;
      final boolean newKernel = false;
      for (int rep = 0; rep < reps; ++rep) {
         if (newKernel) {
            kernel.dispose();
            kernel = createMedianKernel(argbs);
         }
         long start = System.nanoTime();
         kernel.processImages(new MedianSettings(size));
         long elapsed = System.nanoTime() - start;
         System.out.println("elapsed = " + elapsed / 1000000f + "ms");
      }

      builder = new StringBuilder();
      KernelManager.instance().reportDeviceUsage(builder, true);
      System.out.println(builder);
   }

   private static MedianKernel7x7 createMedianKernel(int[] argbs) {
      MedianKernel7x7 kernel = new MedianKernel7x7();
      kernel._imageTypeOrdinal = MedianKernel7x7.RGB;
      kernel._sourceWidth = testImage.getWidth();
      kernel._sourceHeight = testImage.getHeight();
      kernel._sourcePixels = argbs;
      kernel._destPixels = new int[argbs.length];
      return kernel;
   }
}
