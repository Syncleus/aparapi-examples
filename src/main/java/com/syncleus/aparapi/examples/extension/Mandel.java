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
package com.syncleus.aparapi.examples.extension;

import com.syncleus.aparapi.*;
import com.syncleus.aparapi.device.*;
import com.syncleus.aparapi.internal.kernel.*;
import com.syncleus.aparapi.opencl.*;
import com.syncleus.aparapi.opencl.OpenCL.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.concurrent.*;
import com.syncleus.aparapi.opencl.OpenCL.Resource;

/**
 * An example Aparapi application which displays a view of the Mandelbrot set and lets the user zoom in to a particular point.
 *
 * When the user clicks on the view, this example application will zoom in to the clicked point and zoom out there after.
 * On GPU, additional computing units will offer a better viewing experience. On the other hand on CPU, this example
 * application might suffer with sub-optimal frame refresh rate as compared to GPU.
 *
 * @author gfrost
 *
 */

@Resource("com/syncleus/aparapi/examples/extension/mandel2.cl")
public interface Mandel extends OpenCL<com.syncleus.aparapi.examples.extension.Mandel>{
   com.syncleus.aparapi.examples.extension.Mandel createMandleBrot(//
                                                                 Range range,//
                                                                 @Arg("scale") float scale, //
                                                                 @Arg("offsetx") float offsetx, //
                                                                 @Arg("offsety") float offsety, //
                                                                 @GlobalWriteOnly("rgb") int[] rgb);
}
