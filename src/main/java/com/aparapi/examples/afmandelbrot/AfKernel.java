package com.aparapi.examples.afmandelbrot;

import com.aparapi.Kernel;

/**
 * Aparapi Fractals
 * 
 * the kernel executes the math with complex numbers. Coordinates refer to
 * complex plane. result is a vector of number of iterations, It is transformed
 * in a color in the GUI, not here
 * 
 * @author marco.stefanetti at gmail.com
 * 
 */
public class AfKernel extends Kernel {

	/** the result of all calculations, the iterations for each pixel */
	private int[][] result;

	/** max iterations for pixel */
	private int max_iterations;

	/** starting point, x lower-left */
	private double cx1;
	/** starting point, y lower-left */
	private double cy1;

	/** max width */
	private int wmax;
	/** max height */
	private int hmax;

	/** one pixel width */
	private double wx;
	/** one pixel height */
	private double hy;

	/** no values on the constructor, we will reuse the kernel after init */
	public AfKernel() {
		super();
	}

	/**
	 * sets the parameters, send only few double to the device and a pointer to an
	 * array to retrieve iterations
	 */
	public void init(double _cx1, double _cy1, double _cx2, double _cy2, int _W, int _H, int _max_iterations) {

		wmax = _W;
		hmax = _H;
		result = new int[_W][_H];
		max_iterations = _max_iterations;
		cx1 = _cx1;
		cy1 = _cy1;

		wx = (_cx2 - cx1) / (double) wmax;
		hy = (_cy2 - cy1) / (double) hmax;

	}

	/**
	 * just executes the "simple" math on a pixel
	 */
	@Override
	public void run() {

		final int w = getGlobalId(0);
		final int h = getGlobalId(1);

		if ((w < wmax) && (h < hmax)) {

			/** from pixel to complex coordinates */
			final double cx = cx1 + w * wx;
			final double cy = cy1 + h * hy;

			double xn = cx;
			double yn = cy;

			double y2 = cy * cy;
			/** I don't save x2, x squared, It's slower **/

			int t = 0;

			/**
			 * the original code gave a "goto" error in some platform while(
			 * (++t<max_iterations) && (xn*xn+y2<4) )
			 */

			for (t = 0; (t < max_iterations) && (xn * xn + y2 < 4); t++) {
				yn = 2d * xn * yn + cy;
				xn = xn * xn - y2 + cx;
				y2 = yn * yn;
			}

			result[w][h] = t;

		}

	}

	public int[][] getResult() {
		return result;
	}
	
	

}
