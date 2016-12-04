package com.aparapi.test;

import com.aparapi.Kernel;

public class DirectRecursion extends Kernel{

   public void run() {
      intout[0] = fact(10);
      @SuppressWarnings("unused") boolean pass = false;
   }

   int fact(int n) {
      return (n <= 1 ? n : n * fact(n - 1));
   }

   int intout[] = new int[1];

}
/**{Throws{ClassParseException}Throws}**/
