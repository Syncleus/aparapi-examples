package com.aparapi.test;

import com.aparapi.Kernel;

public class CallObject extends Kernel{
   static class Dummy{
      public int foo() {
         return 42;
      }
   };

   Dummy dummy = new Dummy();

   public void run() {
      out[0] = dummy.foo();
   }

   int out[] = new int[2];
}
/**{Throws{ClassParseException}Throws}**/
