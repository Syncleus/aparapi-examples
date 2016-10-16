package com.syncleus.aparapi.test;

public class ReturnBooleanNewArray{

   boolean[] returnBooleanNewArray() {

      return new boolean[1024];
   }

   public void run() {
      returnBooleanNewArray();
   }
}
/**{Throws{ClassParseException}Throws}**/
