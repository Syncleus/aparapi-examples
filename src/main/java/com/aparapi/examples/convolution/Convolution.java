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

package com.aparapi.examples.convolution;

import com.aparapi.*;

import java.io.*;
import java.net.URISyntaxException;

/**
 * <p>Convolution class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class Convolution {

    /**
     * <p>main.</p>
     *
     * @param _args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     */
    public static void main(final String[] _args) throws IOException {
        final File file;
        try{
            file = new File(Convolution.class.getResource("/testcard.jpg").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("could not get testcard", e);
        }

        final ImageConvolution convolution = new ImageConvolution();

        final float convMatrix3x3[] = new float[] {
                0f,
                -10f,
                0f,
                -10f,
                40f,
                -10f,
                0f,
                -10f,
                0f,
        };

        new ConvolutionViewer(file, convMatrix3x3) {

            private static final long serialVersionUID = 7858079467616904028L;

            @Override
            protected void applyConvolution(float[] _convMatrix3x3, byte[] _inBytes, byte[] _outBytes, int _width,
                    int _height) {
                convolution.applyConvolution(_convMatrix3x3, _inBytes, _outBytes, _width, _height);
            }
        };
    }

    final static class ImageConvolution extends Kernel {

        private float convMatrix3x3[];

        private int width, height;

        private byte imageIn[], imageOut[];

        public void processPixel(int x, int y, int w, int h) {
            float accum = 0f;
            int count = 0;
            for (int dx = -3; dx < 6; dx += 3) {
                for (int dy = -1; dy < 2; dy += 1) {
                    final int rgb = 0xff & imageIn[((y + dy) * w) + (x + dx)];

                    accum += rgb * convMatrix3x3[count++];
                }
            }
            final byte value = (byte) (max(0, min((int) accum, 255)));
            imageOut[(y * w) + x] = value;

        }

        @Override
        public void run() {
            final int x = getGlobalId(0) % (width * 3);
            final int y = getGlobalId(0) / (width * 3);

            if ((x > 3) && (x < ((width * 3) - 3)) && (y > 1) && (y < (height - 1))) {
                processPixel(x, y, width * 3, height);
            }

        }

        public void applyConvolution(float[] _convMatrix3x3, byte[] _imageIn, byte[] _imageOut, int _width, int _height) {
            imageIn = _imageIn;
            imageOut = _imageOut;
            width = _width;
            height = _height;
            convMatrix3x3 = _convMatrix3x3;
            execute(3 * width * height);
        }
    }
}
