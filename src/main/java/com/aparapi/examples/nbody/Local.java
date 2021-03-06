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
package com.aparapi.examples.nbody;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.aparapi.Kernel;
import com.aparapi.ProfileInfo;
import com.aparapi.Range;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * An NBody clone which uses local memory to cache NBody positions for execution.
 *
 * http://www.browndeertechnology.com/docs/BDT_OpenCL_Tutorial_NBody-rev3.html
 *
 * @see Main
 * @author gfrost
 * @version $Id: $Id
 */
public class Local{

   public static class NBodyKernel extends Kernel{
      protected final float delT = .005f;

      protected final float espSqr = 1.0f;

      protected final float mass = 5f;

      private final Range range;

      private final float[] xyz; // positions xy and z of bodies

      private final float[] vxyz; // velocity component of x,y and z of bodies 

      @Local private final float[] localStuff; // local memory

      public NBodyKernel(Range _range) {
         range = _range;
         localStuff = new float[range.getLocalSize(0) * 3];

         xyz = new float[range.getGlobalSize(0) * 3];
         vxyz = new float[range.getGlobalSize(0) * 3];
         final float maxDist = 20f;
         for (int body = 0; body < (range.getGlobalSize(0) * 3); body += 3) {
            final float theta = (float) (Math.random() * Math.PI * 2);
            final float phi = (float) (Math.random() * Math.PI * 2);
            final float radius = (float) (Math.random() * maxDist);

            // get the 3D dimensional coordinates
            xyz[body + 0] = (float) (radius * Math.cos(theta) * Math.sin(phi));
            xyz[body + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi));
            xyz[body + 2] = (float) (radius * Math.cos(phi));

            // divide into two 'spheres of bodies' by adjusting x 
            if ((body % 2) == 0) {
               xyz[body + 0] += maxDist * 1.5;
            } else {
               xyz[body + 0] -= maxDist * 1.5;
            }
         }
         setExplicit(true);
      }

      /** 
       * Here is the kernel entrypoint. Here is where we calculate the position of each body
       */
      @Override public void run() {

         final int globalId = getGlobalId(0) * 3;

         float accx = 0.f;
         float accy = 0.f;
         float accz = 0.f;
         final float myPosx = xyz[globalId + 0];
         final float myPosy = xyz[globalId + 1];
         final float myPosz = xyz[globalId + 2];

         for (int tile = 0; tile < (getGlobalSize(0) / getLocalSize(0)); tile++) {
            // load one tile into local memory
            final int gidx = ((tile * getLocalSize(0)) + getLocalId()) * 3;
            final int lidx = getLocalId(0) * 3;
            localStuff[lidx + 0] = xyz[gidx + 0];
            localStuff[lidx + 1] = xyz[gidx + 1];
            localStuff[lidx + 2] = xyz[gidx + 2];
            // Synchronize to make sure data is available for processing
            localBarrier();

            for (int i = 0; i < (getLocalSize() * 3); i += 3) {
               final float dx = localStuff[i + 0] - myPosx;
               final float dy = localStuff[i + 1] - myPosy;
               final float dz = localStuff[i + 2] - myPosz;
               final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
               final float s = mass * invDist * invDist * invDist;
               accx = accx + (s * dx);
               accy = accy + (s * dy);
               accz = accz + (s * dz);
            }
            localBarrier();
         }
         accx = accx * delT;
         accy = accy * delT;
         accz = accz * delT;
         xyz[globalId + 0] = myPosx + (vxyz[globalId + 0] * delT) + (accx * .5f * delT);
         xyz[globalId + 1] = myPosy + (vxyz[globalId + 1] * delT) + (accy * .5f * delT);
         xyz[globalId + 2] = myPosz + (vxyz[globalId + 2] * delT) + (accz * .5f * delT);

         vxyz[globalId + 0] = vxyz[globalId + 0] + accx;
         vxyz[globalId + 1] = vxyz[globalId + 1] + accy;
         vxyz[globalId + 2] = vxyz[globalId + 2] + accz;
      }

      /**
       * Render all particles to the OpenGL context
       * @param gl
       */

      protected void render(GL2 gl) {
         gl.glBegin(GL2.GL_QUADS);

         for (int i = 0; i < (range.getGlobalSize(0) * 3); i += 3) {
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(xyz[i + 0], xyz[i + 1] + 1, xyz[i + 2]);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(xyz[i + 0], xyz[i + 1], xyz[i + 2]);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(xyz[i + 0] + 1, xyz[i + 1], xyz[i + 2]);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(xyz[i + 0] + 1, xyz[i + 1] + 1, xyz[i + 2]);
         }
         gl.glEnd();
      }

   }

   /** Constant <code>width</code> */
   public static int width;

   /** Constant <code>height</code> */
   public static int height;

   /** Constant <code>running</code> */
   public static boolean running;

   /**
    * <p>main.</p>
    *
    * @param _args an array of {@link java.lang.String} objects.
    */
   public static void main(String _args[]) {

      final NBodyKernel kernel = new NBodyKernel(Range.create(Integer.getInteger("bodies", 8192), 256));

      final JFrame frame = new JFrame("NBody");

      final JPanel panel = new JPanel(new BorderLayout());
      final JPanel controlPanel = new JPanel(new FlowLayout());
      panel.add(controlPanel, BorderLayout.SOUTH);

      final JButton startButton = new JButton("Start");

      startButton.addActionListener(new ActionListener(){
         @Override public void actionPerformed(ActionEvent e) {
            running = true;
            startButton.setEnabled(false);
         }
      });
      controlPanel.add(startButton);
      controlPanel.add(new JLabel(kernel.getExecutionMode().toString()));

      controlPanel.add(new JLabel("   Particles"));
      controlPanel.add(new JTextField("" + kernel.range.getGlobalSize(0), 5));

      controlPanel.add(new JLabel("FPS"));
      final JTextField framesPerSecondTextField = new JTextField("0", 5);

      controlPanel.add(framesPerSecondTextField);
      controlPanel.add(new JLabel("Score("));
      final JLabel miniLabel = new JLabel("<html><small>calcs</small><hr/><small>&micro;sec</small></html>");

      controlPanel.add(miniLabel);
      controlPanel.add(new JLabel(")"));

      final JTextField positionUpdatesPerMicroSecondTextField = new JTextField("0", 5);

      controlPanel.add(positionUpdatesPerMicroSecondTextField);
      final GLCapabilities caps = new GLCapabilities(null);
      caps.setDoubleBuffered(true);
      caps.setHardwareAccelerated(true);
      final GLCanvas canvas = new GLCanvas(caps);
      final Dimension dimension = new Dimension(Integer.getInteger("width", 742), Integer.getInteger("height", 742));
      canvas.setPreferredSize(dimension);

      canvas.addGLEventListener(new GLEventListener(){
         private double ratio;

         private final float xeye = 0f;

         private final float yeye = 0f;

         private final float zeye = 100f;

         private final float xat = 0f;

         private final float yat = 0f;

         private final float zat = 0f;

         public final float zoomFactor = 1.0f;

         private int frames;

         private long last = System.currentTimeMillis();

         @Override public void dispose(GLAutoDrawable drawable) {

         }

         @Override public void display(GLAutoDrawable drawable) {

            final GL2 gl = drawable.getGL().getGL2();

            gl.glLoadIdentity();
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glColor3f(1f, 1f, 1f);

            final GLU glu = new GLU();
            glu.gluPerspective(45f, ratio, 1f, 1000f);

            glu.gluLookAt(xeye, yeye, zeye * zoomFactor, xat, yat, zat, 0f, 1f, 0f);
            if (running) {
               kernel.execute(kernel.range);
               if (kernel.isExplicit()) {
                  kernel.get(kernel.xyz);
               }
               final List<ProfileInfo> profileInfo = kernel.getProfileInfo();
               if ((profileInfo != null) && (profileInfo.size() > 0)) {
                  for (final ProfileInfo p : profileInfo) {
                     System.out.print(" " + p.getType() + " " + p.getLabel() + ((p.getEnd() - p.getStart()) / 1000) + "us");
                  }
                  System.out.println();
               }
            }
            kernel.render(gl);

            final long now = System.currentTimeMillis();
            final long time = now - last;
            frames++;

            if (time > 1000) { // We update the frames/sec every second
               if (running) {
                  final float framesPerSecond = (frames * 1000.0f) / time;
                  final int updatesPerMicroSecond = (int) ((framesPerSecond * kernel.range.getGlobalSize(0) * kernel.range
                        .getGlobalSize(0)) / 1000000);
                  framesPerSecondTextField.setText(String.format("%5.2f", framesPerSecond));
                  positionUpdatesPerMicroSecondTextField.setText(String.format("%4d", updatesPerMicroSecond));
               }
               frames = 0;
               last = now;
            }
            gl.glFlush();

         }

         @Override public void init(GLAutoDrawable drawable) {
            final GL2 gl = drawable.getGL().getGL2();

            gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            try {
               final InputStream textureStream = Local.class.getResourceAsStream("/particle.jpg");
               final Texture texture = TextureIO.newTexture(textureStream, false, null);
               texture.enable(gl);
            } catch (final IOException e) {
               e.printStackTrace();
            } catch (final GLException e) {
               e.printStackTrace();
            }

         }

         @Override public void reshape(GLAutoDrawable drawable, int x, int y, int _width, int _height) {
            width = _width;
            height = _height;

            final GL2 gl = drawable.getGL().getGL2();
            gl.glViewport(0, 0, width, height);

            ratio = (double) width / (double) height;

         }

      });

      panel.add(canvas, BorderLayout.CENTER);
      frame.getContentPane().add(panel, BorderLayout.CENTER);

      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);

      final FPSAnimator animator = new FPSAnimator(canvas, 100);
      animator.start();

   }

}
