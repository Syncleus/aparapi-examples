package com.syncleus.aparapi.test;

public class ReturnFloatArrayVar{

   float[] returnFloatArrayVar() {
      float[] floats = new float[1024];
      return floats;
   }

   public void run() {

      returnFloatArrayVar();
   }
}
/**{Throws{ClassParseException}Throws}**/
