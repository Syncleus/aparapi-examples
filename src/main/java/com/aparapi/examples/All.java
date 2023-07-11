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
package com.aparapi.examples;

import com.aparapi.examples.afmandelbrot.AfBenchmark;
import com.aparapi.examples.afmandelbrot.AfMain;
import com.aparapi.examples.configuration.*;
import com.aparapi.examples.convolution.Convolution;
import com.aparapi.examples.convolution.ConvolutionOpenCL;
import com.aparapi.examples.convolution.PureJava;
import com.aparapi.examples.extension.*;
import com.aparapi.examples.javaonedemo.Life;
import com.aparapi.examples.javaonedemo.Mandel;
import com.aparapi.examples.javaonedemo.NBody;
import com.aparapi.examples.mandel.Main2D;
import com.aparapi.examples.mdarray.MDArray;
import com.aparapi.examples.median.MedianDemo;
import com.aparapi.examples.nbody.Local;
import com.aparapi.examples.nbody.Seq;
import com.aparapi.examples.progress.MultiPassKernelSwingWorkerDemo;
import com.aparapi.examples.progress.ProgressAndCancelDemo;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * <p>All class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public class All {

    private interface Executable {
        void execute(String[] args) throws Exception;
    }

    private static class App {
        private final String name;
        private final Executable handler;

        private App(String name, Executable handler) {
            this.name = name;
            this.handler = handler;
        }

        public String getName() {
            return name;
        }

        public void execute(String[] args) throws Exception {
            handler.execute(args);
        }
    }

    private static final List<App> APPS = Arrays.asList(
            new App("Game of Life", com.aparapi.examples.life.Main::main),
            new App("Mandelbrot", com.aparapi.examples.mandel.Main::main),
            new App("Mandlebrot 2D", Main2D::main),
            new App("Convolution", Convolution::main),
            new App("Convolution (OpenCL)", ConvolutionOpenCL::main),
            new App("Convolution (pure Java)", PureJava::main),
            new App("Blacksholes", com.aparapi.examples.blackscholes.Main::main),
            new App("Squares", com.aparapi.examples.squares.Main::main),
            new App("Multipass swing worker", MultiPassKernelSwingWorkerDemo::main),
            new App("Progress and cancel demo", ProgressAndCancelDemo::main),
            new App("Info", com.aparapi.examples.info.Main::main),
            new App("Medians", MedianDemo::main),
            new App("MDArray", MDArray::main),
            new App("Add", com.aparapi.examples.add.Main::main),
            new App("Extension - FFT", FFTExample::main),
            new App("Extension - Histogram", Histogram::main),
            new App("Extension - Histogram Ideal", HistogramIdeal::main),
            new App("Extension - Mandel", MandelExample::main),
            new App("Extension - Square", SquareExample::main),
            new App("Configuration - Auto cleanup arrays", AutoCleanUpArraysDemo::main),
            new App("Configuration - Cleanup arrays", CleanUpArraysDemo::main),
            new App("Configuration - Configuration", ConfigurationDemo::main),
            new App("Configuration - Custom Configuration", CustomConfigurationDemo::main),
            new App("Configuration - Legacy Configuration", LegacyConfigurationDemo::main),
            new App("Configuration - Profiling", ProfilingDemo::main),
            new App("Configuration - Profiling (no binary)", ProfilingDemoNoBinaryCaching::main),
            new App("Effects", com.aparapi.examples.effects.Main::main),
            new App("Javaone - Game of Life", Life::main),
            new App("Javaone - Mandlebrot", Mandel::main),
            new App("Javaone - NBody", NBody::main),
            new App("NBody", com.aparapi.examples.nbody.Main::main),
            new App("NBody - Local", Local::main),
            new App("NBody - Sequential", Seq::main),
            new App("OOPN Body", com.aparapi.examples.oopnbody.Main::main),
            new App("Map-reduce", com.aparapi.examples.mapreduce.Main::main),
            new App("Correlation Matrix", com.aparapi.examples.matrix.Main::main),
            new App("AparapiFractals - Mandelbrot explorer", AfMain::main),
            new App("AparapiFractals - soft benchmark", args -> AfBenchmark.main(new String[]{"SOFT"})),
            new App("AparapiFractals - hard benchmark", args -> AfBenchmark.main(new String[]{"HARD"}))
    );

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Select which example to run:");
        for (int i = 0; i < APPS.size(); i++) {
            System.out.printf("%3d) %s%n", i + 1, APPS.get(i).getName());
        }
        System.out.println();

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your selection, or q/Q to quit: ");
            if (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.equalsIgnoreCase("Q")) {
                    break;
                }

                final int index = NumberUtils.toInt(line, 0);
                if (index >= 1 && index <= APPS.size()) {
                    APPS.get(index - 1).execute(args);
                }
                else {
                    System.out.println("Invalid selection.");
                }

                System.out.println();
            }
            else {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException ex) {
                    return;
                }
            }
        }
    }
}
