package com.syncleus.aparapi.test;

public class ReturnBooleanVarArray{

   boolean[] returnBooleanVarArray() {

      boolean[] ba = new boolean[1024];

      return ba;
   }

   public void run() {
      returnBooleanVarArray();
   }
}
/**{Throws{ClassParseException}Throws}**/
