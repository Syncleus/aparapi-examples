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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.apache.log4j.Logger;

import com.aparapi.device.Device;

/**
 * Aparapi Fractals
 * 
 * only benchmark, results on the console, no graphics
 * 
 * @author marco.stefanetti at gmail.com
 * 
 */
public class AfBenchmark {

	/** logger */
	private static final Logger LOG = Logger.getLogger(AfBenchmark.class);

	/** flag to stop benchmarks */
	private static boolean running = false;

	/** no localSize specified */
	private static int[][] defaultLocalSizes = { { 0, 0 } };

	/** set of different range2D, localSize x localSize   */
	private static int[][] multipleLocalSizes = { {2,2}, {4,4}, {4,8}, {8,4}, {8,8}, {10,10}, {4,16}, {16,8}, {16,16} };

	/** used by the GUI to ask to stop benchmark */
	public static void requestStop() {
		running = false;
	}

	/**
	 * executes a soft benchmark
	 * 
	 * @param afAparapiUtils
	 */
	public static void benchmarkSoft(AfAparapiUtils afAparapiUtils) {
		LOG.debug("Starting benchmark Soft");
		AfBenchmark.benchmark(afAparapiUtils, false, "Soft", -2, -2, 2, 2, 500, 500, 100000, "ALL", 1000);
	}

	/**
	 * executes a hard benchmark
	 * 
	 * @param afAparapiUtils
	 */
	public static void benchmarkHard(AfAparapiUtils afAparapiUtils) {
		LOG.debug("Starting benchmark Hard");
		AfBenchmark.benchmark(afAparapiUtils, false, "Hard", -2, -2, 2, 2, 500, 500, 1000000, "ALL", 1000);
	}

	/**
	 * executes with different localSizes
	 * 
	 * @param afAparapiUtils
	 */
	public static void benchmarkLocalSizes(AfAparapiUtils afAparapiUtils) {
		LOG.debug("Starting benchmark localSizes");
		AfBenchmark.benchmark(afAparapiUtils, true, "localSizes", -2, -2, 2, 2, 900, 900, 10000, "GPU", 10);
	}

	/**
	 * executes a repeated loop over all devices
	 * 
	 * @param afAparapiUtils
	 */
	public static void benchmarkStress(AfAparapiUtils afAparapiUtils) {
		LOG.debug("Starting benchmark Stress");
		for (int n = 0; n < 100; n++) {
			AfBenchmark.benchmark(afAparapiUtils, false, "Stress" + n, -2, -2, 2, 2, 500, 500, 10000, "ALL", 0);
		}
	}

	public static void benchmark(AfAparapiUtils afAparapiUtils, String mode) {
		
		if ("SOFT".equals(mode)) {

			benchmarkSoft(afAparapiUtils);

		} else if ("HARD".equals(mode)) {

			benchmarkHard(afAparapiUtils);

		} else if ("STRESS".equals(mode)) {

			benchmarkStress(afAparapiUtils);

		} else if ("LSIZE".equals(mode)) {

			benchmarkLocalSizes(afAparapiUtils);

		} else {
			
			LOG.warn("Unknown mode : "+mode);
			
		}
	}
	
	/**
	 * execute the kernel on different devices and tracks timings. The iterations
	 * are discarded, used only here, no image refresh.
	 */
	@SuppressWarnings("deprecation")
	public static void benchmark(AfAparapiUtils afAparapiUtils, boolean loopLocalSizes, String title, double cx1,
			double cy1, double cx2, double cy2, int W, int H, int max_iterations, String deviceTypeFilter, long sleep) {

		running = true;

		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		int cpus = osBean.getAvailableProcessors();
		String osArc = osBean.getArch();
		String osName = osBean.getName();

		System.out.println();
		System.out.println("Starting benchmark...");
		System.out.println("Example : 'GeForce GTX 1650 SUPER' soft 348ms, hard 3058ms ");
		System.out.println("Example : 'Java Alternative Algorithm', AMD 3700X, soft 390ms, hard 3993ms ");
		System.out.println();
		System.out.println("OperatingSystem: " + osName + " CPU:" + cpus + " " + osArc);
		System.out.println(
				"=======================================================================================================================================");
		System.out.printf("AparapiFractals - Mandelbrot Benchmark - %s \n", title);
		System.out.printf("  image size     : %d x %d \n", W, H);
		System.out.printf("  maxIterations  : %,d \n", max_iterations);
		System.out.printf("  complex region : %2.16fd,%2.16fd %2.16fd,%2.16fd \n", cx1, cy1, cx2, cy2);
		System.out.println();

		System.out.println(
				"+-----+--------------------------------+----------------+--------------------------------------------+----------+--------+------------+");
		System.out.println(
				"|Type | shortDescription               | deviceId       | Name                                       | LSizes   | ExMode | Elapsed(ms)|");
		System.out.println(
				"+-----+--------------------------------+----------------+--------------------------------------------+----------+--------+------------+");

		int[][] localSizes;

		if (loopLocalSizes) {
			localSizes = multipleLocalSizes;
		} else {
			localSizes = defaultLocalSizes;
		}

		for (String key : afAparapiUtils.getDeviceKeys()) {

			for (int[] localSize : localSizes) {

				if (!running) {
					System.out.println("benchmark interrupted");
					return;
				}

				if(localSize[0]==0) {
					afAparapiUtils.init(key, W, H);
				} else {
					afAparapiUtils.init(key, W, H, localSize[0], localSize[1]);
				}

				Device device = afAparapiUtils.getDevice();

				if (("ALL".equals(deviceTypeFilter)) || (device.getType().toString().equals(deviceTypeFilter))) {

					String description = device.getShortDescription();

					String execMode;

					long elapsed = 0;

					elapsed = afAparapiUtils.execute(cx1, cy1, cx2, cy2, W, H, max_iterations);
					execMode = afAparapiUtils.getKernel().getExecutionMode().toString();

					/*
					 * int[][] iterations = afAparapiUtils.getResult(); 
					 * long totalIterations = 0;
					 * for (int i = 0; i < W; i++) { for (int j = 0; j < H; j++) { totalIterations
					 * += iterations[i][j]; } }
					 * 
					 * if (totalIterations < 1000) { LOG.warn("FAILED totalIterations:" +
					 * totalIterations); }
					 */

					System.out.printf("| %3s | %-30s | %-14d | %-42s | %-8s | %6s | %10d |\n",
							device.getType().toString(), description, device.getDeviceId(),
							afAparapiUtils.getDeviceName(), afAparapiUtils.getLocalSizes(), execMode, elapsed);
				}

				if (!running) {
					System.out.println("benchmark interrupted");
					return;
				}

				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}

			}

		}
		System.out.println(
				"+-----+--------------------------------+----------------+--------------------------------------------+----------+--------+------------+");
		System.out.println();
		System.out.println(
				"=======================================================================================================================================");

		running = false;
		LOG.debug("Benchmark over");
	}

	public static void main(String[] args) {

		System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "true");
		System.setProperty("com.aparapi.dumpProfilesOnExit", "true");

		String mode = "SOFT";

		if (args.length > 0) {
			mode = args[0];
		}

		AfAparapiUtils afAparapiUtils = new AfAparapiUtils();

		benchmark(afAparapiUtils, mode);

	}

}
