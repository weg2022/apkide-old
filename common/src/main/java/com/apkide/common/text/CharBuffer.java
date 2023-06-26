package com.apkide.common.text;

 class CharBuffer {

      static char[] obtain(int len) {
         char[] buf;

         synchronized (CharBuffer.class) {
             buf = sTemp;
             sTemp = null;
         }

         if (buf == null || buf.length < len) {
             buf =new char[len];
         }

         return buf;
     }


      static void recycle(char[] temp) {
         if (temp.length > 1000) return;

         synchronized (CharBuffer.class) {
             sTemp = temp;
         }
     }

     private static char[] sTemp = null;
}
