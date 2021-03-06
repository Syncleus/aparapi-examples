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
/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/

package com.aparapi.examples.extension;

import com.aparapi.*;
import com.aparapi.device.*;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.*;
import com.aparapi.opencl.OpenCL.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.concurrent.*;

/**
 * <p>MandelExample class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class MandelExample{

   /** User selected zoom-in point on the Mandelbrot view. */
   public static volatile Point to = null;

   /** Constant <code>mandelBrot</code> */
   public static MandelBrot mandelBrot = null;

   /** Constant <code>gpuMandelBrot</code> */
   public static MandelBrot gpuMandelBrot = null;

   /** Constant <code>javaMandelBrot</code> */
   public static MandelBrot javaMandelBrot = null;

   /** Constant <code>javaMandelBrotMultiThread</code> */
   public static MandelBrot javaMandelBrotMultiThread = null;

   // new JavaMandelBrot();
   //new JavaMandelBrotMultiThread();
   /**
    * <p>main.</p>
    *
    * @param _args an array of {@link java.lang.String} objects.
    */
   @SuppressWarnings("serial") public static void main(String[] _args) {

      final JFrame frame = new JFrame("MandelBrot");

      /** Width of Mandelbrot view. */
      final int width = 768;

      /** Height of Mandelbrot view. */
      final int height = 768;

      /** Mandelbrot image height. */

      /** Image for Mandelbrot view. */
      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      final Object framePaintedDoorBell = new Object();
      final JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {

            g.drawImage(image, 0, 0, width, height, this);
            synchronized (framePaintedDoorBell) {
               framePaintedDoorBell.notify();
            }
         }
      };

      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));

      final Object userClickDoorBell = new Object();

      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view 
      viewer.addMouseListener(new MouseAdapter(){
         @Override public void mouseClicked(MouseEvent e) {
            to = e.getPoint();
            synchronized (userClickDoorBell) {
               userClickDoorBell.notify();
            }
         }
      });

      final JPanel controlPanel = new JPanel(new FlowLayout());

      final String[] choices = new String[] {
            "Java Sequential",
            "Java Threads",
            "GPU OpenCL"
      };

      final JComboBox startButton = new JComboBox(choices);

      startButton.addItemListener(new ItemListener(){
         @Override public void itemStateChanged(ItemEvent e) {
            final String item = (String) startButton.getSelectedItem();

            if (item.equals(choices[2])) {
               mandelBrot = gpuMandelBrot;
            } else if (item.equals(choices[0])) {
               mandelBrot = javaMandelBrot;
            } else if (item.equals(choices[1])) {
               mandelBrot = javaMandelBrotMultiThread;
            }
         }

      });
      controlPanel.add(startButton);

      controlPanel.add(new JLabel("FPS"));
      final JTextField framesPerSecondTextField = new JTextField("0", 5);

      controlPanel.add(framesPerSecondTextField);

      // Swing housework to create the frame
      frame.getContentPane().add(viewer);
      frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      // Extract the underlying RGB buffer from the image.
      // Pass this to the kernel so it operates directly on the RGB buffer of the image

      final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

      /** Mutable values of scale, offsetx and offsety so that we can modify the zoom level and position of a view. */
      float scale = .0f;

      float offsetx = .0f;

      float offsety = .0f;
      Device device = KernelManager.instance().bestDevice();
      if (device instanceof OpenCLDevice) {
         final OpenCLDevice openclDevice = (OpenCLDevice) device;

         System.out.println("max memory = " + openclDevice.getGlobalMemSize());
         System.out.println("max mem alloc size = " + openclDevice.getMaxMemAllocSize());
         gpuMandelBrot = openclDevice.bind(MandelBrot.class);
      }

      javaMandelBrot = new JavaMandelBrot();
      javaMandelBrotMultiThread = new JavaMandelBrotMultiThread();
      mandelBrot = javaMandelBrot;
      final float defaultScale = 3f;
      scale = defaultScale;
      offsetx = -1f;
      offsety = 0f;
      final Range range = device.createRange2D(width, height);
      mandelBrot.createMandleBrot(range, scale, offsetx, offsety, imageRgb);
      viewer.repaint();

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         @Override public void windowClosing(WindowEvent _windowEvent) {
            // mandelBrot.dispose();
            System.exit(0);
         }
      });

      while (true) {
         // Wait for the user to click somewhere
         while (to == null) {
            synchronized (userClickDoorBell) {
               try {
                  userClickDoorBell.wait();
               } catch (final InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }

         float x = -1f;
         float y = 0f;
         final float tox = ((float) (to.x - (width / 2)) / width) * scale;
         final float toy = ((float) (to.y - (height / 2)) / height) * scale;

         // This is how many frames we will display as we zoom in and out.
         final int frames = 128;
         long startMillis = System.currentTimeMillis();
         int frameCount = 0;
         for (int sign = -1; sign < 2; sign += 2) {
            for (int i = 0; i < (frames - 4); i++) {
               scale = scale + ((sign * defaultScale) / frames);
               x = x - (sign * (tox / frames));
               y = y - (sign * (toy / frames));
               offsetx = x;
               offsety = y;
               mandelBrot.createMandleBrot(range, scale, offsetx, offsety, imageRgb);
               viewer.repaint();
               synchronized (framePaintedDoorBell) {
                  try {
                     framePaintedDoorBell.wait();
                  } catch (final InterruptedException ie) {
                     ie.getStackTrace();
                  }
               }
               frameCount++;
               final long endMillis = System.currentTimeMillis();
               final long elapsedMillis = endMillis - startMillis;
               if (elapsedMillis > 1000) {
                  framesPerSecondTextField.setText("" + ((frameCount * 1000) / elapsedMillis));
                  frameCount = 0;
                  startMillis = endMillis;
               }
            }
         }

         // Reset zoom-in point.
         to = null;

      }

   }

}
