package com.syncleus.aparapi.test;

public class AssignField{
   int field = 1024;

   public void run() {
      field = 100;
   }
}
/**{Throws{ClassParseException}Throws}**/