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
package com.syncleus.aparapi.examples;

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
                com.syncleus.aparapi.examples.life.Main.main(args);
                break;
            case "2":
                com.syncleus.aparapi.examples.mandel.Main.main(args);
                break;
            case "3":
                com.syncleus.aparapi.examples.mandel.Main2D.main(args);
                break;
            case "4":
                com.syncleus.aparapi.examples.convolution.Convolution.main(args);
                break;
            case "5":
                com.syncleus.aparapi.examples.convolution.ConvolutionOpenCL.main(args);
                break;
            case "6":
                com.syncleus.aparapi.examples.convolution.PureJava.main(args);
                break;
            default:
                System.out.println("Invalid selection.");
        }
        return true;
    }
}
