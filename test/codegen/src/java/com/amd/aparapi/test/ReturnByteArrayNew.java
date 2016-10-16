package com.syncleus.aparapi.test;

public class ReturnByteArrayNew{

   byte[] returnByteArrayNew() {
      return new byte[1024];
   }

   public void run() {
      returnByteArrayNew();
   }
}
/**{Throws{ClassParseException}Throws}**/
