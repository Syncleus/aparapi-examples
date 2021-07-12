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
package com.aparapi.examples.afmandelbrot;

import org.apache.log4j.Logger;

/**
 * Aparapi Fractals
 *
 * The main class coordinates the GUI and Aparapi's executions. Complex plane
 * coordinates and iterations are saved here. If you are interested in Aparapi
 * code, just check AfAparapiUtils and AfKernel.
 *
 * @author marco.stefanetti at gmail.com
 * @version $Id: $Id
 * @since 2.0.1
 */
public class AfMain {

	/** logger */
	private static final Logger LOG = Logger.getLogger(AfMain.class);

	/** the GUI contains only swing controls and events handling */
	private AfGUI gui;

	/** devices list and kernel execution */
	private AfAparapiUtils afAparapiUtils;

	/** the key of the current selected device */
	protected String selectedDeviceKey = null;

	/** image width */
	protected int W = 500;

	/** image height */
	protected int H = 500;

	/** minimal iterations */
	final protected int __MIN_ITERATIONS = 512;

	/** max iterations per image pixel, It will vary while zooming */
	private int maxIterations = 512;

	/** the result of the iterations, It is transformed in colors in the GUI */
	protected int[][] iterations;

	/** lower-left in the complex plane */
	protected double cx1 = -2;
	protected double cy1 = -2;

	/** top-right in the complex plane */
	protected double cx2 = +2;
	protected double cy2 = +2;

	/** width and height in the complex plane */
	protected double dx = +4;
	protected double dy = +4;

	/** minimum width in the complex plane using double */
	protected double minXWidth = 0.000000000001d;

	/** just to show total iterations for each image */
	protected long totalIterations;

	/** elapsed time for last image calculation */
	protected long elapsed;

	/** the thread is used to separate the GUI and the kernel */
	protected GoThread goThread = null;

	/** profile startup of main and GUI */
	private long profilerLastTimeMillis = System.currentTimeMillis();

	/** flag for benchmark running */
	boolean benchmarkRunning = false;

	/** separate thread for benchmarks */
	private Thread benchmarkThread;

	/**
	 * the thread is used to separate calculations and keep the gui responsive to
	 * other events
	 */
	class GoThread extends Thread {

		private volatile boolean running = true;

		/** destination coordinates */
		private double tx1 = -2;
		private double ty1 = -2;
		private double tx2 = +2;
		private double ty2 = +2;

		/** steps to go from current coordinates to destination */
		private double steps = 0;

		public GoThread(double _tx1, double _ty1, double _tx2, double _ty2, double _steps) {
			tx1 = _tx1;
			ty1 = _ty1;
			tx2 = _tx2;
			ty2 = _ty2;
			steps = _steps;
		}

		public void interrupt() {
			running = false;
		}

		public boolean isRunning() {
			return running;
		}

		public void run() {

			if (steps == 0) {
				go(tx1, ty1, tx2, ty2);
				running = false;
				return;
			}

			// start center
			double sox = (cx2 + cx1) / 2d;
			double soy = (cy2 + cy1) / 2d;

			// initial with and height
			double dsx = (cx2 - cx1);
			double dsy = (cy2 - cy1);

			// target center
			double tox = (tx2 + tx1) / 2d;
			double toy = (ty2 + ty1) / 2d;

			// target with and height
			double dtx = (tx2 - tx1);
			double dty = (ty2 - ty1);

			/**
			 * d(0)=dsx ... d(n+1)=d(n)*f=d(0)*f^n ... d(N)=dtx=d(0)*f^N=dsx*f^N
			 * 
			 * f^N=dtx/dsx f=(dtx/dsx)^(1/n)
			 */
			double fx = Math.pow(dtx / dsx, 1 / (steps - 1));
			double fy = Math.pow(dty / dsy, 1 / (steps - 1));

			/** f^n */
			double fxn = 1;
			double fyn = 1;

			/** new coordinates */
			double nx1;
			double ny1;
			double nx2;
			double ny2;

			double ndx;
			double ndy;

			double mx;
			double my;

			double nox;
			double noy;

			/** sleep if the refresh is less than these ms */
			long min_sleep = 30;

			for (double s = 0; s < steps; s++) {

				// used externally to stop the thread
				if (!running) {
					return;
				}

				/** activation function, to move fast but smootly on new center **/
				mx = 2d * (1d / (1 + Math.exp(-(35d * s) / steps)) - 0.5d);
				my = mx;

				nox = sox + (tox - sox) * mx;
				noy = soy + (toy - soy) * my;

				ndx = dsx * fxn;
				ndy = dsy * fyn;

				nx1 = nox - ndx / 2d;
				ny1 = noy - ndy / 2d;
				nx2 = nox + ndx / 2d;
				ny2 = noy + ndy / 2d;

				long t = go(nx1, ny1, nx2, ny2);

				fxn *= fx;
				fyn *= fy;

				/** sleep if the refresh was too fast **/
				if (t < min_sleep)
					try {
						LOG.warn(String.format("sleeping %d ms ... ", min_sleep - t));
						Thread.sleep(min_sleep - t);
					} catch (InterruptedException e) {
					}

			}

			if (!running) {
				return;
			}

			go(tx1, ty1, tx2, ty2);

			running = false;
		}

	}

	/**
	 * constructor
	 */
	public AfMain() {

		profiler("MAIN start");

		afAparapiUtils = new AfAparapiUtils();
		selectedDeviceKey = afAparapiUtils.getBestDeviceKey();
		profiler("MAIN aparapi init");

	}

	/** initialize based on saved values */
	private void init() {
		init(selectedDeviceKey, W, H);
	}

	/**
	 * initialize local iterations array and aparapiUtils
	 *
	 * @param _W                new image width
	 * @param _H                new image height
	 * @param _selectedDeviceKey a {@link java.lang.String} object.
	 */
	public synchronized void init(String _selectedDeviceKey, int _W, int _H) {

		selectedDeviceKey = _selectedDeviceKey;
		W = _W;
		H = _H;

		afAparapiUtils.init(selectedDeviceKey, W, H);

		LOG.debug(String.format("canvas : %dx%d - max_iterations : %,d - LocalSizes %s ", W, H, maxIterations,
				afAparapiUtils.getLocalSizes()));

		LOG.debug(String.format("DeviceKey : %s - Device : %s ", selectedDeviceKey, afAparapiUtils.getDeviceName()));

	}

	/**
	 * go to new coordinates using x,y pixel as new center and a zoom factor.
	 *
	 * @param x          new central pixel x
	 * @param y          new central pixel y
	 * @param zoomFactor zoomFactor is relative to dimensions in complex plane
	 */
	public void move(int x, int y, double zoomFactor) {

		double nx1 = cx1 + x * (cx2 - cx1) / W;
		double ny1 = cy1 + y * (cy2 - cy1) / H;

		double cw = zoomFactor * (cx2 - cx1);
		double ch = zoomFactor * (cy2 - cy1);

		/** stop when too small and zooming in **/
		if ((cw < minXWidth) && (zoomFactor < 1d)) {
			LOG.warn(String.format("!!! Zoom limit !!! x range : %2.20f", cw));
			gui.mouseWheelZooming = false;
			return;
		}

		/** too big or too far, the set is between -2 and 2 **/
		if (((cw > 10) && (zoomFactor > 1d)) || (nx1 > 5) || (nx1 < -5) || (ny1 > 5) || (ny1 < -5)) {
			LOG.warn(String.format("too big or too far, " + zoomFactor));
			gui.mouseWheelZooming = false;
			return;
		}

		threadGo(nx1 - 0.5f * cw, ny1 - 0.5f * ch, nx1 + 0.5f * cw, ny1 + 0.5f * ch, 0);

	}

	/**
	 * go to new complex coordinates
	 * 
	 * @param tx1 target lower-left x
	 * @param ty1 target lower-left y
	 * @param tx2 target up-right x
	 * @param ty2 target up-right y
	 * @return elapsed refresh time
	 */
	private long go(double tx1, double ty1, double tx2, double ty2) {

		dx = tx2 - tx1;

		/**
		 * we do not use ty2-ty1 as dy, to keep always proportional we use a calculated
		 * dy based on dx,H,W
		 **/
		dy = dx * H / W;

		cx1 = tx1;
		cy1 = (ty2 + ty1) / 2d - 0.5d * dy;
		cx2 = tx2;
		cy2 = (ty2 + ty1) / 2d + 0.5d * dy;

		dy = cy2 - cy1;

		/** while zooming increase iterations, empirical formula **/
		maxIterations = (int) (__MIN_ITERATIONS * (1d + 0.5d * Math.log(1 / (cx2 - cx1))));

		if (maxIterations < __MIN_ITERATIONS) {
			maxIterations = __MIN_ITERATIONS;
		}

		return refresh();
	}

	/**
	 * call the aparapi execution and then refresh the gui
	 * 
	 * @return calculations elapsed time, not considering gui refreshing time
	 */
	private synchronized long refresh() {

		if (benchmarkRunning) {
			LOG.warn("Benchmark in progress...");
			return 0;
		}

		gui.deviceLedOn();
		gui.lastMaxIterations = maxIterations;

		elapsed = afAparapiUtils.execute(cx1, cy1, cx2, cy2, W + 1, H + 1, maxIterations);
		iterations = afAparapiUtils.getResult();

		/*
		 * long totalIterations=0; for(int i=0;i<W;i++) { for(int j=0;j<H;j++) {
		 * totalIterations+=iterations[i][j]; } }
		 * 
		 * if(totalIterations<1000) { LOG.fatal("totalIterations:"+totalIterations+
		 * " very small aborting"); System.exit(1); }
		 */

		LOG.debug(String.format("%2.16fd,%2.16fd %2.16fd,%2.16fd MaxIterations : %10d - Elapsed : %d ms", cx1, cy1, cx2,
				cy2, maxIterations, elapsed));

		gui.deviceLedOff();
		gui.refresh();

		return elapsed;
	}

	/**
	 * <p>threadGoHome.</p>
	 *
	 * @param steps a int.
	 */
	public void threadGoHome(int steps) {
		threadGo(-2d, -2d, 2d, 2d, steps);
	}

	/**
	 * <p>goHome.</p>
	 */
	public void goHome() {
		stopThread();
		go(-2d, -2d, 2d, 2d);
	}

	/**
	 * <p>stopThread.</p>
	 */
	public void stopThread() {

		if (goThread == null) {
			return;
		}

		goThread.interrupt();
		try {
			goThread.join();
		} catch (InterruptedException e) {
			LOG.error("error joining threadGO");
		}

	}

	/**
	 * <p>threadGo.</p>
	 */
	public void threadGo() {
		threadGo(cx1, cy1, cx2, cy2, 0);
	}

	/**
	 * <p>threadGo.</p>
	 *
	 * @param tx1 a double.
	 * @param ty1 a double.
	 * @param tx2 a double.
	 * @param ty2 a double.
	 * @param steps a double.
	 */
	public void threadGo(double tx1, double ty1, double tx2, double ty2, double steps) {

		stopThread();
		goThread = new GoThread(tx1, ty1, tx2, ty2, steps);
		goThread.start();

	}

	/**
	 * starts a benchmark in a separate thread
	 *
	 * @param benchmarkMode a {@link java.lang.String} object.
	 */
	public void benchmark(final String benchmarkMode) {

		if (benchmarkRunning) {
			LOG.warn("Benchmark akready running");
			return;
		}

		benchmarkThread = new Thread() {
			public void run() {

				benchmarkRunning = true;

				gui.benchmarkLedOn();

				if ("CURRENT".equals(benchmarkMode)) {
					/** executes the benchmark using current coordinates and max iterations */
					AfBenchmark.benchmark(afAparapiUtils, false, "CurrentRegion", cx1, cy1, cx2, cy2, W, H, maxIterations,
							"ALL", 100);
				} else {

					AfBenchmark.benchmark(afAparapiUtils,benchmarkMode);

				}
				

				init();
				gui.benchmarkLedOff();

				benchmarkRunning = false;
			}

		};

		benchmarkThread.start();

	}

	/**
	 * stops the benchmark thread
	 */
	public void stopBenchmark() {

		if (!benchmarkRunning) {
			return;
		}

		AfBenchmark.requestStop();

		try {
			benchmarkThread.join();
		} catch (InterruptedException e) {
		}

	}

	/**
	 * used by the GUI, the GUI has no direct access to the aparapi stuffs
	 *
	 * @return the list of devices from aparapiUtils
	 */
	public String[] getDeviceKeys() {
		return afAparapiUtils.getDeviceKeys();
	}

	/**
	 * used by the GUI to show the name of the device. It's different from the
	 * combobox (selectedDeviceKey), here you get the real name of the device, e.g.
	 * "NVidia 1650 SUPER""
	 *
	 * @return the name of the current device
	 */
	public String getDeviceName() {
		return afAparapiUtils.getDeviceName();
	}

	/**
	 * used to profile main and gui startup
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	protected void profiler(String message) {
		long ms = System.currentTimeMillis() - profilerLastTimeMillis;
		LOG.debug(String.format("profiler - %-20s : %-10d ms", message, ms));
		profilerLastTimeMillis = System.currentTimeMillis();
	}

	/**
	 * gui creation executed in the swing thread
	 */
	protected void createAndShowGUI() {

		// swing load
		java.awt.Window window = new java.awt.Window(null);
		window.dispose();
		profiler("GUI swing load");

		// create the GUI
		gui = new AfGUI(this);
		profiler("GUI costructor");

	}

	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	public static void main(String[] args) {

		System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "true");
		System.setProperty("com.aparapi.dumpProfilesOnExit", "true");

		LOG.info("AparapiFractals");
		LOG.info("double-click : recenter");
		LOG.info("click        : stop zoom");
		LOG.info("mouse wheel  : zoom");
		LOG.info("mouse drag   : move");

		final AfMain main = new AfMain();

		// creating and showing this application's GUI
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				main.createAndShowGUI();
			}
		});

		// waits the GUI
		while ((main.gui == null) || (main.gui.frame == null) || (!main.gui.frame.isValid())) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		main.profiler("GUI valid");

		// gui validation
		main.gui.frame.validate();
		main.profiler("GUI validate");

		// image refresh
		main.gui.resizeImage();
		main.profiler("GUI refresh");

		// hope to ignore and flush starting events
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		// enables real handling of gui events
		main.gui.guiEvents = true;
		main.profiler("GUI events");

	}

}
