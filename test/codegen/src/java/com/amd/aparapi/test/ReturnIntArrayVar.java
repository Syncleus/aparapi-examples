package com.syncleus.aparapi.test;

public class ReturnIntArrayVar{

   int[] returnIntArrayVar() {
      int[] ints = new int[1024];
      return ints;
   }

   public void run() {

      returnIntArrayVar();
   }
}
/**{Throws{ClassParseException}Throws}**/
