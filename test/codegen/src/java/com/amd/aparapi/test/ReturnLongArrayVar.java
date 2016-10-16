package com.syncleus.aparapi.test;

public class ReturnLongArrayVar{

   long[] returnLongArrayVar() {
      long[] longs = new long[1024];
      return longs;
   }

   public void run() {

      returnLongArrayVar();
   }
}
/**{Throws{ClassParseException}Throws}**/
