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
package com.aparapi.examples.extension;

import com.aparapi.*;
import com.aparapi.device.*;
import com.aparapi.internal.kernel.*;
import com.aparapi.opencl.*;
import com.aparapi.opencl.OpenCL.Resource;

/**
 * An example Aparapi application which displays a view of the Mandelbrot set and lets the user zoom in to a particular point.
 *
 * When the user clicks on the view, this example application will zoom in to the clicked point and zoom out there after.
 * On GPU, additional computing units will offer a better viewing experience. On the other hand on CPU, this example
 * application might suffer with sub-optimal frame refresh rate as compared to GPU.
 *
 * @author gfrost
 * @version $Id: $Id
 */

@Resource("mandel2.cl")
public interface MandelBrot extends OpenCL<MandelBrot>{
   /**
    * <p>createMandleBrot.</p>
    *
    * @param range a {@link com.aparapi.Range} object.
    * @param scale a float.
    * @param offsetx a float.
    * @param offsety a float.
    * @param rgb an array of {@link int} objects.
    * @return a {@link com.aparapi.examples.extension.MandelBrot} object.
    */
   MandelBrot createMandleBrot(//
                               Range range,//
                               @Arg("scale") float scale, //
                               @Arg("offsetx") float offsetx, //
                               @Arg("offsety") float offsety, //
                               @GlobalWriteOnly("rgb") int[] rgb);
}
