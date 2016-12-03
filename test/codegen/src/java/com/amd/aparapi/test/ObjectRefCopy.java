package com.aparapi.test;

import com.aparapi.Kernel;

public class ObjectRefCopy extends Kernel{

   final static class DummyOOA{
      int mem;

      float floatField;
   };

   final int size = 8;

   DummyOOA dummy[] = new DummyOOA[size];

   public void run() {
      int myId = getGlobalId();
      dummy[myId] = dummy[myId + 1];
   }
}

/**{Throws{ClassParseException}Throws}**/
