package com.syncleus.aparapi.test;

public class ReturnDoubleArrayNew{

   double[] returnDoubleArrayNew() {
      return new double[1024];
   }

   public void run() {
      returnDoubleArrayNew();
   }
}
/**{Throws{ClassParseException}Throws}**/