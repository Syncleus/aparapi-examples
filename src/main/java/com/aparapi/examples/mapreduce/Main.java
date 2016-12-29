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

package com.aparapi.examples.mapreduce;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int size = 1048576;
        final int count = 3;
        final int[] V = new int[size];

        //lets fill in V randomly...
        for (int i = 0; i < size; i++) {
            //random number either 0, 1, or 2
            V[i] = (int) (Math.random() * 3);
        }

        //this will hold our values between the phases.
        int[][] totals = new int[count][size];

        ///////////////
        // MAP PHASE //
        ///////////////
        final int[][] kernelTotals = totals;
        Kernel mapKernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                int value = V[gid];
                for(int index = 0; index < count; index++) {
                    if (value == index)
                        kernelTotals[index][gid] = 1;
                }
            }
        };
        mapKernel.execute(Range.create(size));
        mapKernel.dispose();
        totals = kernelTotals;

        //////////////////
        // REDUCE PHASE //
        //////////////////
        while (size > 1) {
            int nextSize = size / 2;
            final int[][] currentTotals  = totals;
            final int[][] nextTotals = new int[count][nextSize];
            Kernel reduceKernel = new Kernel() {
                @Override
                public void run() {
                    int gid = getGlobalId();
                    for(int index = 0; index < count; index++) {
                        nextTotals[index][gid] = currentTotals[index][gid * 2] + currentTotals[index][gid * 2 + 1];
                    }
                }
            };
            reduceKernel.execute(Range.create(nextSize));
            reduceKernel.dispose();

            totals = nextTotals;
            size = nextSize;
        }
        assert size == 1;

        /////////////////////////////
        // Done, just print it out //
        /////////////////////////////
        int[] results = new int[3];
        results[0] = totals[0][0];
        results[1] = totals[1][0];
        results[2] = totals[2][0];

        System.out.println(Arrays.toString(results));
    }
}
