package com.apkide.codemodel.api.parentMatch;

import java.util.NoSuchElementException;

public class ByteStack {
   private byte[] bytes;
   private int count;

   public ByteStack(int initialCapacity) {
      this.bytes = new byte[initialCapacity];
   }

   public ByteStack() {
      this(10);
   }

   public void push(byte elem) {
      if (this.count == this.bytes.length) {
         byte[] newBytes = new byte[this.bytes.length * 2];
         System.arraycopy(this.bytes, 0, newBytes, 0, this.bytes.length);
         this.bytes = newBytes;
      }

      this.bytes[this.count++] = elem;
   }

   public byte top() {
      try {
         return this.bytes[this.count - 1];
      } catch (NegativeArraySizeException var2) {
         throw new NoSuchElementException("ByteStack.top(): empty stack");
      }
   }

   public byte pop() {
      try {
         return this.bytes[--this.count];
      } catch (NegativeArraySizeException var2) {
         this.count = 0;
         throw new NoSuchElementException("ByteStack.top(): empty stack");
      }
   }

   public int elementCount() {
      return this.count;
   }

   public void clear() {
      this.count = 0;
   }
}
