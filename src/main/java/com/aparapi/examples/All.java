/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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

import com.aparapi.examples.blackscholes.Main;
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

import java.util.Scanner;

public class All {
    public static void main(String[] _args) throws Exception {
        System.out.println("Select which example to run:");
        System.out.println("  1) Game of Life");
        System.out.println("  2) Mandelbrot");
        System.out.println("  3) Mandlebrot 2D");
        System.out.println("  4) Convolution");
        System.out.println("  5) Convolution (OpenCL)");
        System.out.println("  6) Convolution (pure Java)");
        System.out.println("  7) Blacksholes");
        System.out.println("  8) Squares");
        System.out.println("  9) Multipass swing worker");
        System.out.println(" 10) Progress and cancel demo");
        System.out.println(" 11) Info");
        System.out.println(" 12) Medians");
        System.out.println(" 13) MDArray");
        System.out.println(" 14) Add");
        System.out.println(" 15) Extension - FFT");
        System.out.println(" 16) Extension - Histogram");
        System.out.println(" 17) Extension - Histogram Ideal");
        System.out.println(" 18) Extension - Mandel");
        System.out.println(" 19) Extension - Square");
        System.out.println(" 20) Configuration - Auto cleanup arrays");
        System.out.println(" 21) Configuration - Cleanup arrays");
        System.out.println(" 22) Configuration - Configuration");
        System.out.println(" 23) Configuration - Custom Configuration");
        System.out.println(" 24) Configuration - Legacy Configuration");
        System.out.println(" 25) Configuration - Profiling");
        System.out.println(" 26) Configuration - Profiling (no binary)");
        System.out.println(" 27) Effects");
        System.out.println(" 28) Javaone - Game of Life");
        System.out.println(" 29) Javaone - Mandlebrot");
        System.out.println(" 30) Javaone - NBody");
        System.out.println(" 31) NBody");
        System.out.println(" 32) NBody - Local");
        System.out.println(" 33) NBody - Sequential");
        System.out.println(" 34) OOPN Body");
        System.out.println(" 35) Map-reduce");
        System.out.println();

        Scanner in = new Scanner(System.in);
        boolean running = true;
        while(running)
        {
            System.out.print("Enter your selection, or q/Q to quit: ");
            if( in.hasNextLine() )
            {
                String line = in.nextLine();
                running = selected(line, _args);
                System.out.println();
            }
            else
                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException ex) {
                    return;
                }
        }
    }

    private static boolean selected(String line, String[] args) throws Exception
    {
        if( line.toUpperCase().equals("Q") )
           return false;

        switch(line)
        {
            case "1":
                com.aparapi.examples.life.Main.main(args);
                break;
            case "2":
                com.aparapi.examples.mandel.Main.main(args);
                break;
            case "3":
                Main2D.main(args);
                break;
            case "4":
                Convolution.main(args);
                break;
            case "5":
                ConvolutionOpenCL.main(args);
                break;
            case "6":
                PureJava.main(args);
                break;
            case "7":
                Main.main(args);
                break;
            case "8":
                com.aparapi.examples.squares.Main.main(args);
                break;
            case "9":
                MultiPassKernelSwingWorkerDemo.main(args);
                break;
            case "10":
                ProgressAndCancelDemo.main(args);
                break;
            case "11":
                com.aparapi.examples.info.Main.main(args);
                break;
            case "12":
                MedianDemo.main(args);
                break;
            case "13":
                MDArray.main(args);
                break;
            case "14":
                com.aparapi.examples.add.Main.main(args);
                break;
            case "15":
                FFTExample.main(args);
                break;
            case "16":
                Histogram.main(args);
                break;
            case "17":
                HistogramIdeal.main(args);
                break;
            case "18":
                MandelExample.main(args);
                break;
            case "19":
                SquareExample.main(args);
                break;
            case "20":
                AutoCleanUpArraysDemo.main(args);
                break;
            case "21":
                CleanUpArraysDemo.main(args);
                break;
            case "22":
                ConfigurationDemo.main(args);
                break;
            case "23":
                CustomConfigurationDemo.main(args);
                break;
            case "24":
                LegacyConfigurationDemo.main(args);
                break;
            case "25":
                ProfilingDemo.main(args);
                break;
            case "26":
                ProfilingDemoNoBinaryCaching.main(args);
                break;
            case "27":
                com.aparapi.examples.effects.Main.main(args);
                break;
            case "28":
                Life.main(args);
                break;
            case "29":
                Mandel.main(args);
                break;
            case "30":
                NBody.main(args);
                break;
            case "31":
                com.aparapi.examples.nbody.Main.main(args);
                break;
            case "32":
                Local.main(args);
                break;
            case "33":
                Seq.main(args);
                break;
            case "34":
                com.aparapi.examples.oopnbody.Main.main(args);
                break;
            case "35":
                com.aparapi.examples.mapreduce.Main.main(args);
                break;
            default:
                System.out.println("Invalid selection.");
        }
        return true;
    }
}
