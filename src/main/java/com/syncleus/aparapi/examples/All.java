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
                com.aparapi.examples.mandel.Main2D.main(args);
                break;
            case "4":
                com.aparapi.examples.convolution.Convolution.main(args);
                break;
            case "5":
                com.aparapi.examples.convolution.ConvolutionOpenCL.main(args);
                break;
            case "6":
                com.aparapi.examples.convolution.PureJava.main(args);
                break;
            case "7":
                com.aparapi.examples.blackscholes.Main.main(args);
                break;
            case "8":
                com.aparapi.examples.squares.Main.main(args);
                break;
            case "9":
                com.aparapi.examples.progress.MultiPassKernelSwingWorkerDemo.main(args);
                break;
            case "10":
                com.aparapi.examples.progress.ProgressAndCancelDemo.main(args);
                break;
            case "11":
                com.aparapi.examples.info.Main.main(args);
                break;
            case "12":
                com.aparapi.examples.median.MedianDemo.main(args);
                break;
            case "13":
                com.aparapi.examples.mdarray.MDArray.main(args);
                break;
            case "14":
                com.aparapi.examples.add.Main.main(args);
                break;
            case "15":
                com.aparapi.examples.extension.FFTExample.main(args);
                break;
            case "16":
                com.aparapi.examples.extension.Histogram.main(args);
                break;
            case "17":
                com.aparapi.examples.extension.HistogramIdeal.main(args);
                break;
            case "18":
                com.aparapi.examples.extension.MandelExample.main(args);
                break;
            case "19":
                com.aparapi.examples.extension.SquareExample.main(args);
                break;
            case "20":
                com.aparapi.examples.configuration.AutoCleanUpArraysDemo.main(args);
                break;
            case "21":
                com.aparapi.examples.configuration.CleanUpArraysDemo.main(args);
                break;
            case "22":
                com.aparapi.examples.configuration.ConfigurationDemo.main(args);
                break;
            case "23":
                com.aparapi.examples.configuration.CustomConfigurationDemo.main(args);
                break;
            case "24":
                com.aparapi.examples.configuration.LegacyConfigurationDemo.main(args);
                break;
            case "25":
                com.aparapi.examples.configuration.ProfilingDemo.main(args);
                break;
            case "26":
                com.aparapi.examples.configuration.ProfilingDemoNoBinaryCaching.main(args);
                break;
            case "27":
                com.aparapi.examples.effects.Main.main(args);
                break;
            case "28":
                com.aparapi.examples.javaonedemo.Life.main(args);
                break;
            case "29":
                com.aparapi.examples.javaonedemo.Mandel.main(args);
                break;
            case "30":
                com.aparapi.examples.javaonedemo.NBody.main(args);
                break;
            case "31":
                com.aparapi.examples.nbody.Main.main(args);
                break;
            case "32":
                com.aparapi.examples.nbody.Local.main(args);
                break;
            case "33":
                com.aparapi.examples.nbody.Seq.main(args);
                break;
            case "34":
                com.aparapi.examples.oopnbody.Main.main(args);
                break;
            default:
                System.out.println("Invalid selection.");
        }
        return true;
    }
}
