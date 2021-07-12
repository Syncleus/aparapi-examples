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

import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * Aparapi Fractals
 *
 * Aparapi code is here and in the kernel.
 *
 * The constructor prepares a map of Aparapi Devices using a String as a key.
 * The strings used as keys are created combining device shortDescription and
 * deviceId. That's for convenience, to show the keys on the gui combo box and
 * use them to retrieve the selected device and kernel from the maps.
 *
 * @author marco.stefanetti at gmail.com
 * @version $Id: $Id
 * @since 2.0.1
 */
public class AfAparapiUtils {

	/** logger */
	private static final Logger LOG = Logger.getLogger(AfAparapiUtils.class);

	/** the list of device keys in an array, used to populate the Swing JComboBox */
	private String[] deviceKeys;

	/** a map between device keys and available devices */
	private TreeMap<String, Device> devicesMap = new TreeMap<>();

	/** keep a kernel instance ready for each device */
	private TreeMap<String, AfKernel> kernelsMap = new TreeMap<>();

	/** best device key, based on KernelManager.instance().bestDevice() */
	private String bestDeviceKey;

	/** selected device */
	private Device device;

	/** the range to be used by the kernel */
	private Range range;

	/** the kernel for the selected device */
	private AfKernel kernel;

	/** the name of the last device used */
	private String deviceName;

	/**
	 * The constructor prepares the keys, the map containing the devices and a map
	 * with a kernel instance for each device.
	 */
	@SuppressWarnings("deprecation")
	public AfAparapiUtils() {

		List<Device> devices = KernelManager.instance().getDefaultPreferences().getPreferredDevices(null);
		deviceKeys = new String[devices.size()];

		int p = 0;
		for (Device device : devices) {

			/** prepare a talking key, description and id **/
			String talkingKey = device.getType().toString() + " " + device.getShortDescription() + " ("
					+ device.getDeviceId() + ")";

			if (device == KernelManager.instance().bestDevice()) {
				talkingKey += " *";
				bestDeviceKey = talkingKey;
			}

			/** add the device to the devices map */
			devicesMap.put(talkingKey, device);

			/**
			 * must instantiate a dedicated new kernel for each device, otherwise It's
			 * always using the first GPU device
			 */
			AfKernel deviceKernel = new AfKernel();

			/** add a kernel dedicated to this device in the map of kernels */
			kernelsMap.put(talkingKey, deviceKernel);

			/**
			 * I set the execution mode to GPU or CPU to have legacyExecutionMode in
			 * Kernel.execute(String _entrypoint, Range _range, int _passes)
			 **/
			if (device.getType().equals(Device.TYPE.GPU)) {
				deviceKernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
			} else if (device.getType().equals(Device.TYPE.CPU)) {
				deviceKernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.CPU);
			} else {
				deviceKernel.setFallbackExecutionMode();
			}

			/** fake execution on the device before adding to the array for the GUI */
			boolean success = fakeExecution(talkingKey);

			if (!success) {

				LOG.warn("AfAparapiUtils device test FAILED : " + talkingKey);

			} else {

				/**
				 * I save the keys both in an array and in a map for convenience. The array
				 * contains devices that succeeded the fake execution. 
				 **/
				deviceKeys[p++] = talkingKey;
				LOG.info("AfAparapiUtils device test OK : " + talkingKey);
			}

		}
	}

	/**
	 * fake execution to see kernel working on a device and to see kernel
	 * compilation at startup
	 * 
	 * @return fake execution success
	 */
	private boolean fakeExecution(String key) {

		try {
			/** prepares the kernel as for an image 1x1 pixels */
			init(key, 1, 1);
			kernel.init(-2, -2, 2, 2, 1, 1, 1);
			kernel.execute(range);
		} catch (Throwable exc) {
			LOG.error("!!! " + exc.getMessage());
			LOG.error("fake execution FAILED");
			return false;
		}

		return true;
	}

	/**
	 * calls the init with a default localSize.
	 *
	 * @param deviceKey a {@link java.lang.String} object.
	 * @param W a int.
	 * @param H a int.
	 */
	public void init(String deviceKey, int W, int H) {

		/**
		 * TODO 8x8 is empirically the best
		 */
		init(deviceKey, W, H, 8, 8);
	}

	/**
	 * Prepares the range and reads device description, based on the device and
	 * image size the range can be reused many times, so we need to instantiate the
	 * range only when device changes or image size changes
	 *
	 * @param deviceKey a {@link java.lang.String} object.
	 * @param W a int.
	 * @param H a int.
	 * @param localSize0 a int.
	 * @param localSize1 a int.
	 */
	public void init(String deviceKey, int W, int H, int localSize0, int localSize1) {

		device = devicesMap.get(deviceKey);

		kernel = kernelsMap.get(deviceKey);

		int localWidth = localSize0;
		int localHeight = localSize1;

		/** global sizes must be a multiple of local sizes */
		int globalWidth = (1 + W / localWidth) * localWidth;
		int globalHeight = (1 + H / localHeight) * localHeight;

		range = device.createRange2D(globalWidth, globalHeight, localWidth, localHeight);

		deviceName = device.getShortDescription();
		if (device instanceof OpenCLDevice) {
			OpenCLDevice ocld = (OpenCLDevice) device;
			deviceName = ocld.getName();
		}

	}

	/**
	 * call the kernel execution and track elapsed time
	 *
	 * @return elapsed milliseconds
	 * @param cx1 a double.
	 * @param cy1 a double.
	 * @param cx2 a double.
	 * @param cy2 a double.
	 * @param w a int.
	 * @param h a int.
	 * @param maxIterations a int.
	 */
	public long execute(double cx1, double cy1, double cx2, double cy2, int w, int h, int maxIterations) {

		if (kernel == null) {
			LOG.error("null Kernel");
			return 0;
		}

		while (kernel.isExecuting()) {
			LOG.warn("Already running, waiting ... " + device.getShortDescription());
			try {
				Thread.sleep(10l);
			} catch (InterruptedException e) {
			}
		}

		long startTime = System.currentTimeMillis();
		kernel.init(cx1, cy1, cx2, cy2, w, h, maxIterations);
		kernel.execute(range);
		long endTime = System.currentTimeMillis();
		long elapsed = (endTime - startTime);

		if ((kernel != null) && (kernel.getProfileInfo() != null)) {
			kernel.cleanUpArrays();
		}

		return elapsed;

	}

	/** @return the list of keys of the devices */
	/**
	 * <p>Getter for the field <code>deviceKeys</code>.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] getDeviceKeys() {
		return deviceKeys;
	}

	/** @return the name of the last device used */
	/**
	 * <p>Getter for the field <code>deviceName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/** @return the dimension XxY of the local widths of the range */
	/**
	 * <p>getLocalSizes.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLocalSizes() {
		String localSizes = range.getLocalSize_0() + " x " + range.getLocalSize_1();
		return localSizes;
	}

	/**
	 * <p>Getter for the field <code>bestDeviceKey</code>.</p>
	 *
	 * @return the key of the best device by KernelManager
	 */
	public String getBestDeviceKey() {
		return bestDeviceKey;
	}

	/**
	 * <p>Getter for the field <code>device</code>.</p>
	 *
	 * @return last device selected
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * <p>Getter for the field <code>kernel</code>.</p>
	 *
	 * @return the kernel of the selected device
	 */
	public AfKernel getKernel() {
		return kernel;
	}

	/**
	 * <p>Getter for the field <code>range</code>.</p>
	 *
	 * @return the range
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * <p>getResult.</p>
	 *
	 * @return an array of {@link int} objects.
	 */
	public int[][] getResult() {
		return kernel.getResult();
	}

}
