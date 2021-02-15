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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

/**
 * Aparapi Fractals
 *  
 * The GUI, a swing JFrame with components. There is no Aparapi code here, only
 * swing and events handling. The GUI has no access to aparapiUtils, It
 * interacts only with the AfMain.
 * 
 * @author marco.stefanetti at gmail.com
 * 
 */

public class AfGUI {

	/** logger */
	private static final Logger LOG = Logger.getLogger(AfGUI.class);

	/**
	 * a pointer to the main to read calculated iterations and Mandelbrot settings
	 */
	private AfMain main;

	/** arbitrary choice on max colors */
	private int MAX_COLORS = 128;

	/** colors palette is calculated once in colors[] */
	private int[] colors = new int[MAX_COLORS];

	/** used for the Mandelbrot set, always black */
	private int RGB_BLACK = Color.BLACK.getRGB();

	/** colors offset, may be one day we will cycle colors */
	private int colorOffset = 0;

	/** the main window */
	JFrame frame;

	/** the image */
	private BufferedImage image;

	/** the panel that contains the image, to be refreshed */
	private JPanel imagePanel;

	/** swing fields to refreshed **/
	private JTextField tfcx1;
	private JTextField tfcy1;
	private JTextField tfcx2;
	private JTextField tfcy2;
	private JTextField tfcdx;
	private JTextField tfcdy;

	private JTextField tfW;
	private JTextField tfH;
	private JTextField tfMaxIterations;
	private JTextField tfZoom;
	private JSlider jsZoom;

	private JTextField tfDeviceName;
	private JTextField tfElapsed;
	private JTextField tfTotalIterations;
	private JLabel lblDeviceLed;
	private JLabel lblBenchmarkLed;
	private JComboBox<String> deviceComboBox;

	/** DEBUG enable a button to execute multiple loops on all devices */
	private boolean showDeviceLoopButton = false;

	/** mouse events dragging flag */
	boolean dragging = false;
	/** drag starting x */
	private int dragging_px;
	/** drag starting y */
	private int dragging_py;
	/** drag x remaining due to fractions */
	private float dragging_rx;
	/** drag y remaining due to fractions */
	private float dragging_ry;

	/** mouse wheel zooming */
	boolean mouseWheelZooming = false;

	/** refreshing flag, disable the slider event while setting It's value */
	protected boolean jsZoomDisable = false;

	/** starts the gui ignoring events */
	protected boolean guiEvents = false;

	int yOffset = 0;
	int xOffset = 0;

	/** refreshing the GUI */
	protected boolean refreshing = false;

	/** last maxIterations used is saved in the GUI for the GUI refresh */
	protected long lastMaxIterations;

	/** setup all the swing components and event listeners */
	public AfGUI(AfMain _main) {

		main = _main;

		/** setup once color palette */
		for (int c = 0; c < MAX_COLORS; c++) {
			float hue = (float) c / (float) MAX_COLORS;
			float saturation = 1.0f;
			float brightness = 1.0f;
			Color color = Color.getHSBColor(hue, saturation, brightness);
			colors[c] = color.getRGB();
		}

		/** frame is the main window */
		frame = new JFrame("Aparapi Fractals - Mandelbrot set");
		frame.setMinimumSize(new Dimension(100, 100));
		Dimension dim = new Dimension((int) (main.W + 400), (int) (main.H + 50));
		frame.setPreferredSize(dim);
		frame.setSize(dim);
		frame.setBackground(Color.BLACK);

		Container contentPane = frame.getContentPane();

		/** image panel */
		Border imageBevel = BorderFactory.createLoweredBevelBorder();
		JPanel imageBorderedPanel = new JPanel(new GridBagLayout());
		imageBorderedPanel.setBorder(imageBevel);

		imagePanel = new JPanel() {
			private static final long serialVersionUID = -2006337199526432552L;

			public void paint(Graphics g) {
				g.drawImage(image, xOffset, yOffset, this);
			}
		};
		imagePanel.setBackground(Color.BLACK);

		imageBorderedPanel.add(imagePanel, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		/** inputs panel */
		Border inputBevel = BorderFactory.createRaisedBevelBorder();
		JPanel inputBorderedPanel = new JPanel(new GridBagLayout());
		inputBorderedPanel.setBorder(inputBevel);

		int lineSpace = 5;
		int elementsSpace = 10;

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		JButton b0 = new JButton("Home");
		JButton b1 = new JButton("Sun");
		JButton b2 = new JButton("Spider");
		JButton b3 = new JButton("Crystal");
		JButton b4 = new JButton("Cell");
		JButton b5 = new JButton("Flower");
		JButton b6 = new JButton("Psyco");
		JButton b7 = new JButton("Needlework");
		JButton b8 = new JButton("Peter");
		JButton b9 = new JButton("Rings");
		JButton b10 = new JButton("Jellyfish");
		JButton b11 = new JButton("FastHome");
		{
			JPanel buttons = new JPanel();
			buttons.setLayout(new GridLayout(4, 3));
			buttons.add(b0);
			buttons.add(b1);
			buttons.add(b2);
			buttons.add(b3);
			buttons.add(b4);
			buttons.add(b5);
			buttons.add(b6);
			buttons.add(b7);
			buttons.add(b8);
			buttons.add(b9);
			buttons.add(b10);
			buttons.add(b11);
			buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(buttons);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		{
			JLabel complexplane = new JLabel("Complex Plane");
			complexplane.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(complexplane);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		{
			JPanel c1p = new JPanel();
			c1p.setLayout(new BoxLayout(c1p, BoxLayout.X_AXIS));
			JLabel lcx1 = new JLabel("x1 ");
			c1p.add(lcx1);
			tfcx1 = new JTextField(1);
			tfcx1.setEditable(false);
			c1p.add(tfcx1);
			c1p.add(Box.createRigidArea(new Dimension(10, 0)));
			JLabel lcy1 = new JLabel("y1 ");
			c1p.add(lcy1);
			tfcy1 = new JTextField(1);
			tfcy1.setEditable(false);
			c1p.add(tfcy1);
			c1p.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(c1p);
		}

		{
			JPanel c2p = new JPanel();
			c2p.setLayout(new BoxLayout(c2p, BoxLayout.X_AXIS));
			JLabel lcx2 = new JLabel("x2 ");
			c2p.add(lcx2);
			tfcx2 = new JTextField(1);
			tfcx2.setEditable(false);
			c2p.add(tfcx2);
			c2p.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			JLabel lcy2 = new JLabel("y2 ");
			c2p.add(lcy2);
			tfcy2 = new JTextField(1);
			tfcy2.setEditable(false);
			c2p.add(tfcy2);
			c2p.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(c2p);
		}

		{
			JPanel cd = new JPanel();
			cd.setLayout(new BoxLayout(cd, BoxLayout.X_AXIS));
			JLabel cdx = new JLabel("dx ");
			cd.add(cdx);
			tfcdx = new JTextField(1);
			tfcdx.setEditable(false);
			cd.add(tfcdx);
			cd.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			JLabel cdy = new JLabel("dy ");
			cd.add(cdy);
			tfcdy = new JTextField(1);
			tfcdy.setEditable(false);
			cd.add(tfcdy);
			cd.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cd);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));
		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		{
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
			JLabel canvas = new JLabel("Image ");
			cp.add(canvas);
			cp.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			JLabel cW = new JLabel(" W ");
			cp.add(cW);
			tfW = new JTextField(1);
			cp.add(tfW);
			tfW.setEditable(false);
			cp.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			JLabel cH = new JLabel(" H ");
			cp.add(cH);
			tfH = new JTextField(1);
			tfH.setEditable(false);
			cp.add(tfH);
			JLabel lmi = new JLabel("  MaxIterations ");
			cp.add(lmi);
			tfMaxIterations = new JTextField(2);
			tfMaxIterations.setEditable(false);
			cp.add(tfMaxIterations);
			cp.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cp);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		{

			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
			JLabel zl = new JLabel("Zoom ");
			cp.add(zl);
			tfZoom = new JTextField(10);
			cp.add(tfZoom);
			tfZoom.setEditable(false);
			cp.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			jsZoom = new JSlider(0, 100);
			/** POI scrollbar zoom */
			jsZoom.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent event) {

					if ((!jsZoomDisable && guiEvents) && !(jsZoom.getValueIsAdjusting() && main.elapsed > 200)) {
						int value = jsZoom.getValue();
						stopAll();
						zoom(value);
					}

				}
			});
			cp.add(jsZoom);
			cp.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cp);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));
		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		{
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
			JLabel cW = new JLabel("total iterations ");
			cp.add(cW);
			tfTotalIterations = new JTextField(3);
			tfTotalIterations.setEditable(false);
			cp.add(tfTotalIterations);
			JLabel cH = new JLabel(" ms ");
			cp.add(cH);
			tfElapsed = new JTextField(1);
			tfElapsed.setEditable(false);
			cp.add(tfElapsed);
			cp.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cp);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));
		{
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
			deviceComboBox = new JComboBox<String>(main.getDeviceKeys());
			int index = 0;
			for (int i = 0; i < main.getDeviceKeys().length; i++) {
				if (main.selectedDeviceKey.equals(main.getDeviceKeys()[i])) {
					index = i;
				}
			}
			deviceComboBox.setSelectedIndex(index);

			/** POI device change listener */
			deviceComboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						deviceComboBox.setEnabled(false);
						String newDeviceKey = (String) deviceComboBox.getSelectedItem();
						deviceChange(newDeviceKey);
					}
				}

			});
			cp.add(deviceComboBox);
			cp.add(Box.createRigidArea(new Dimension(elementsSpace, 0)));
			lblDeviceLed = new JLabel("OFF", JLabel.CENTER);
			cp.add(lblDeviceLed);

			cp.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cp);

		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));
		{
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
			JLabel cW = new JLabel("Device ");
			cp.add(cW);
			tfDeviceName = new JTextField(10);
			tfDeviceName.setEditable(false);
			cp.add(tfDeviceName);
			cp.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(cp);
		}

		inputPanel.add(Box.createRigidArea(new Dimension(1, lineSpace)));

		JButton benchSoft = new JButton("Soft");
		JButton benchHard = new JButton("Hard");
		JButton benchSizes = new JButton("Sizes");
		JButton benchHere = new JButton("Here");
		JButton benchStop = new JButton("Stop");
		lblBenchmarkLed = new JLabel("OFF", JLabel.CENTER);
		{
			JLabel l = new JLabel("Benchmark");
			l.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(l);
			JPanel buttons = new JPanel();
			buttons.setLayout(new GridLayout(1, 6));
			buttons.add(benchSoft);
			buttons.add(benchHard);
			buttons.add(benchHere);
			buttons.add(benchSizes);
			buttons.add(benchStop);
			buttons.add(lblBenchmarkLed);
			buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(buttons);

		}

		JButton randomDebug = new JButton("DEBUG device loop");
		if (showDeviceLoopButton) {
			JPanel p = new JPanel();
			p.add(randomDebug);
			p.setAlignmentX(Component.LEFT_ALIGNMENT);
			inputPanel.add(p);
		}

		inputBorderedPanel.add(inputPanel, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		// ----- frame -----

		contentPane.setLayout(new GridBagLayout());
		contentPane.add(imageBorderedPanel, new GridBagConstraints(1, 1, 1, 1, 0.99, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		contentPane.add(inputBorderedPanel, new GridBagConstraints(2, 1, 1, 1, 0.01, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		{
			Dimension d = lblDeviceLed.getSize();
			lblDeviceLed.setPreferredSize(d);
			lblDeviceLed.setMaximumSize(d);
			lblDeviceLed.setMinimumSize(d);
			deviceLedOff();
		}

		{
			Dimension d = lblBenchmarkLed.getSize();
			lblBenchmarkLed.setPreferredSize(d);
			lblBenchmarkLed.setMaximumSize(d);
			lblBenchmarkLed.setMinimumSize(d);
			benchmarkLedOff();
		}

		/** close event */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		b0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.threadGoHome(10);
			}

		});

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(0.1329154802887031d, 0.6706861139480367d, 0.1329154802897748d, 0.6706861139491085d, 100d);
			}

		});

		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-1.9687843996500283d, -0.0000000000623248d, -1.9687843995067890d, 0.0000000000628460d,
						100d);

			}
		});

		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(0.4480950090233584d, -0.4102090411357999d, 0.4480950091319934d, -0.4102090410418815d,
						80d);
			}
		});

		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-0.6907229455219234d, 0.4652530104374417d, -0.6907229455187313d, 0.4652530104399114d,
						100d);
			}
		});

		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-1.7496850545473188d, -0.0000000451105513d, -1.7496849592009920d, 0.0000000426514086d,
						100d);
			}
		});

		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-0.7379598288776590d, -0.2191410537224941d, -0.7379598288765801d, -0.2191410537215613d,
						80d);
			}
		});

		b7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-0.7499210743130593d, 0.0315822442134116d, -0.7499210743119602d, 0.0315822442143673d,
						100d);
			}
		});

		b8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-1.7494348011240726d, 0.0000005026481909d, -1.7494348011229415d, 0.0000005026492321d,
						100d);
			}
		});

		b9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-0.2175644994305805d, -1.1144202070198740d, -0.2175644994295201d, -1.1144202070190840d,
						100d);
			}
		});

		b10.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
				main.threadGo(-1.7494326807753284d, -0.0000000000012870d, -1.7494326807728570d, 0.0000000000011843d,
						100d);
			}
		});

		b11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
				main.goHome();
			}
		});

		benchSoft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startBenchmark("SOFT");
			}
		});

		benchHard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startBenchmark("HARD");
			}
		});

		benchSizes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startBenchmark("LSIZE");
			}
		});

		benchHere.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startBenchmark("CURRENT");
			}
		});

		benchStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAll();
			}

		});

		randomDebug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String startingKey = main.selectedDeviceKey;
				LOG.debug("randomDebug start");
				int badTotalIterations = 0;
				for (int n = 0; n < 100; n++) {
					for (String key : main.getDeviceKeys()) {
						deviceChange(key);
						if (main.totalIterations < 1000) {
							LOG.warn("!!!!!!!!!!!!!!!!  total iterations only : " + main.totalIterations);
							badTotalIterations++;
						}
					}
				}
				deviceChange(startingKey);
				LOG.debug("randomDebug end, bad total iterations : " + badTotalIterations);
			}

		});

		imagePanel.addMouseWheelListener(new MouseWheelListener() {

			/** POI mouse wheel zoom */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				e.consume();

				if (!mouseWheelZooming) {

					/** it will be reenabled at the end of gui refresh */
					mouseWheelZooming = true;

					int amount = (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) ? e.getUnitsToScroll()
							: (e.getWheelRotation() < 0 ? -1 : 1);

					LOG.debug(String.format("wheel %d", amount));

					double zoom = 1f + ((double) amount * 8d / 100d);
					stopAll();
					main.move(main.W / 2, main.H / 2, zoom);

				}
			}
		});

		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
					LOG.debug("Double Click");
					stopAll();
					main.move(e.getPoint().x, main.H - e.getPoint().y - 1, 1f);
				}

				if (e.getClickCount() == 1 && !e.isConsumed()) {
					e.consume();
					LOG.debug("Single Click");
					stopAll();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				LOG.debug(String.format("mouse pressed %d %d", e.getPoint().x, e.getPoint().y));
				dragging_px = e.getPoint().x;
				dragging_py = e.getPoint().y;
				dragging = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				xOffset = 0;
				yOffset = 0;

				int nx = e.getPoint().x;
				int ny = e.getPoint().y;

				dragging_px = dragging_px - nx;
				dragging_py = dragging_py - ny;

				int newCenterX = main.W / 2 + dragging_px;
				dragging_rx += (float) main.W % 2;
				if (dragging_rx >= 2f) {
					newCenterX += dragging_rx / 2;
					dragging_rx -= dragging_rx;
				}

				int newCenterY = main.H / 2 - dragging_py;
				dragging_ry += (float) main.H % 2;
				if (dragging_ry >= 2f) {
					newCenterY += dragging_ry / 2;
					dragging_ry -= dragging_ry;
				}

				main.move(newCenterX, newCenterY, 1f);

				dragging = false;
			}

		});

		/** POI drag */
		imagePanel.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (dragging) {

					int nx = e.getPoint().x;
					int ny = e.getPoint().y;

					xOffset = nx - dragging_px;
					yOffset = ny - dragging_py;

					imagePanel.update(imagePanel.getGraphics());

					main.stopThread();

					{
						int dx = dragging_px - nx;
						int dy = dragging_py - ny;

						dragging_px = e.getPoint().x;
						dragging_py = e.getPoint().y;

						int newCenterX = main.W / 2 + dx;
						dragging_rx += (float) main.W % 2;
						if (dragging_rx >= 2f) {
							newCenterX += dragging_rx / 2;
							dragging_rx -= dragging_rx;
						}

						int newCenterY = main.H / 2 - dy;
						dragging_ry += (float) main.H % 2;
						if (dragging_ry >= 2f) {
							newCenterY += dragging_ry / 2;
							dragging_ry -= dragging_ry;
						}

						main.move(newCenterX, newCenterY, 1f);
					}

				}
			}

		});

		/** disable dynamic resizing, called only when mouse released */
		Toolkit.getDefaultToolkit().setDynamicLayout(false);

		/** frame resize event */
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (guiEvents) {
					resizeImage();
				}
			}
		});

	}
	
	
	private void startBenchmark(String benchmarkMode){

		if (main.benchmarkRunning) {
			LOG.warn("Benchmark already running");
			return;
		}

		stopAll();
		main.benchmark(benchmarkMode);

	}

	/** zoom in or out based on an empirical formula */
	private void zoom(int percentage) {
		double tdx = 4d * Math.pow(10, (percentage * Math.log10(main.minXWidth / 4d) / 100d));
		double mx = (main.cx1 + main.cx2) / 2d;
		double my = (main.cy1 + main.cy2) / 2d;
		double tx1 = mx - tdx / 2d;
		double ty1 = my - main.dy * (tdx / main.dx) / 2d;
		double tx2 = mx + tdx / 2d;
		double ty2 = my + main.dy * (tdx / main.dx) / 2d;
		main.threadGo(tx1, ty1, tx2, ty2, 0);
	}

	/** stops running threads and stop dragging */
	private synchronized void stopAll() {
		dragging = false;
		xOffset = 0;
		yOffset = 0;

		main.stopBenchmark();

		main.stopThread();

		/** waits refresh stop */
		while (refreshing) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * after frame resize we have to instantiate a new image and reinitialize the
	 * main
	 */
	public void resizeImage() {

		stopAll();

		int W = imagePanel.getWidth() + 1;
		if (W < 100) {
			W = 100;
		}
		int H = imagePanel.getHeight() + 1;
		if (H < 100) {
			H = 100;
		}

		image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

		main.init(main.selectedDeviceKey, W, H);

		/**
		 * move to the pixels center, same complex coordinates. 
		 * refresh centering the image
		 */
		main.move(W / 2, H / 2, 1);
	}

	/** on device change initialize the main and refresh */
	private void deviceChange(String newDeviceKey) {
		stopAll();
		main.init(newDeviceKey, main.W, main.H);
		main.threadGo();
	}

	/**
	 * after the main has completed calculations It calls the GUI refresh. We
	 * refresh the image and all other input controls.
	 */
	public void refresh() {

		if (refreshing) {
			LOG.warn("already refreshing...");
			return;
		}

		/** flag refreshing */
		refreshing = true;

		/** total iterations on all pixels, just to show the total number **/
		main.totalIterations = 0;

		/** image coordinates **/
		int color;

		/** transform iterations in a color and set the pixel on the image **/
		for (int w = 0; w < main.W; w++)
			for (int h = 0; h < main.H; h++) {

				int iterations = main.iterations[w][h];

				if (iterations >= lastMaxIterations) {
					/** the Mandelbrot set is black */
					color = RGB_BLACK;
				} else {
					/** color from the palette */
					color = colors[(iterations + colorOffset) % MAX_COLORS];
				}

				image.setRGB(w, main.H - h - 1, color);

				main.totalIterations += iterations;
			}

		if (main.totalIterations < 1000) {
			LOG.warn("only " + main.totalIterations + " total iterations");
		}

		/** ready for a new image without drag offsets **/
		xOffset = 0;
		yOffset = 0;

		/** instead of frame.repaint(); image.update works better on resizing */
		imagePanel.update(imagePanel.getGraphics());

		/** input controls refresh */
		tfcx1.setText(String.format("%+2.16f", main.cx1));
		tfcy1.setText(String.format("%+2.16f", main.cy1));
		tfcx2.setText(String.format("%+2.16f", main.cx2));
		tfcy2.setText(String.format("%+2.16f", main.cy2));
		tfcdx.setText(String.format("%+2.16f", main.dx));
		tfcdy.setText(String.format("%+2.16f", main.dy));

		tfW.setText(String.format("%5d", main.W));
		tfH.setText(String.format("%5d", main.H));
		tfMaxIterations.setText(String.format("%,d", lastMaxIterations));

		tfElapsed.setText("" + main.elapsed);
		tfDeviceName.setText("" + main.getDeviceName());
		tfTotalIterations.setText(String.format("%,d", main.totalIterations));

		/** I assume -2,+2 as basic size, zoom=1 at home */
		long zoom = (long) (4d / (main.dx));
		tfZoom.setText(String.format("%,d", zoom));

		/** zoom logarithmic scale for the scrollbar */
		jsZoomDisable = true;
		int zoomLog = (int) (100d * Math.log10(main.dx / 4d) / Math.log10(main.minXWidth / 4d));
		jsZoom.setValue(zoomLog);
		jsZoomDisable = false;

		refreshing = false;

		/** re-enable mouse wheel zooming */
		mouseWheelZooming = false;

		/** re-enable device change */
		if (!deviceComboBox.isEnabled()) {
			deviceComboBox.setEnabled(true);
		}

	}

	public void deviceLedOn() {
		lblDeviceLed.setText("ON");
		lblDeviceLed.setForeground(Color.RED);
	}

	public void deviceLedOff() {
		lblDeviceLed.setText("");
		lblDeviceLed.setForeground(Color.GRAY);
	}

	public void benchmarkLedOn() {
		lblBenchmarkLed.setText("ON");
		lblBenchmarkLed.setForeground(Color.RED);
	}

	public void benchmarkLedOff() {
		lblBenchmarkLed.setText("");
		lblBenchmarkLed.setForeground(Color.GRAY);
	}
}
