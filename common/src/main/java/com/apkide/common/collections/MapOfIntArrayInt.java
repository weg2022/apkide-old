package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class MapOfIntArrayInt {
   private static final int LOAD_FACTOR = 2;
   private static final int[] SIZES = new int[]{
      5,
      11,
      17,
      37,
      67,
      131,
      257,
      521,
      1031,
      2053,
      4099,
      8209,
      16411,
      32771,
      65537,
      131101,
      262147,
      524309,
      1048583,
      2097169,
      4194319,
      8388617,
      16777259,
      33554467,
      67108879,
      134217757,
      268435459,
      536870923,
      1073741827,
      2147383649
   };
   private int[] myArrays;
   private int myArrayPos;
   private int[] myPoss;
   private int[] myValues;
   private int mySlots;
   private int myCount;
   private int mySizeExp;
   private int[] myBuffer;

   public MapOfIntArrayInt(int size) {
      super();
      this.mySizeExp = 0;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myPoss = new int[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.myArrays = new int[10];
      this.myArrayPos = 1;
      this.mySlots = 0;
      this.myCount = 0;
   }

   public MapOfIntArrayInt() {
      super();
      this.mySizeExp = 0;
      this.myPoss = new int[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.myArrays = new int[10];
      this.myArrayPos = 1;
      this.mySlots = 0;
      this.myCount = 0;
   }

   public MapOfIntArrayInt(@NonNull StoreInputStream stream) throws IOException {
      super();
      this.myCount = stream.readInt();
      this.mySlots = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myPoss = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myPoss.length; ++i) {
         this.myPoss[i] = stream.readInt();
      }

      this.myValues = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myValues.length; ++i) {
         this.myValues[i] = stream.readInt();
      }

      this.myArrayPos = stream.readInt();
      this.myArrays = new int[stream.readInt()];

      for(int i = 0; i < this.myArrayPos; ++i) {
         this.myArrays[i] = stream.readInt();
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySlots);
      stream.writeInt(this.mySizeExp);

      for (int j : this.myPoss) {
         stream.writeInt(j);
      }

      for (int value : this.myValues) {
         stream.writeInt(value);
      }

      stream.writeInt(this.myArrayPos);
      stream.writeInt(this.myArrays.length);

      for(int i = 0; i < this.myArrayPos; ++i) {
         stream.writeInt(this.myArrays[i]);
      }
   }

   public void clear() {
      Arrays.fill(this.myPoss, 0);

      this.mySlots = 0;
      this.myCount = 0;
   }

   private int getHash(int[] array, int off, int len) {
      int key = 0;

      for(int i = 0; i < len; ++i) {
         key ^= array[i + off];
      }

      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      return key & Integer.MAX_VALUE;
   }

   public void put(int array0, int[] array1, int[] array2, int off, int len, int value) {
      int bufferLen = 1 + 2 * len;
      if (this.myBuffer == null || this.myBuffer.length <= bufferLen) {
         this.myBuffer = new int[bufferLen];
      }

      this.myBuffer[0] = array0;
      System.arraycopy(array1, off, this.myBuffer, 1, len);
      System.arraycopy(array2, off, this.myBuffer, 1 + len, len);
      this.put(this.myBuffer, 0, bufferLen, value);
   }

   public int get(int array0, int[] array1, int[] array2, int off, int len) {
      int bufferLen = 1 + 2 * len;
      if (this.myBuffer == null || this.myBuffer.length <= bufferLen) {
         this.myBuffer = new int[bufferLen];
      }

      this.myBuffer[0] = array0;
      System.arraycopy(array1, off, this.myBuffer, 1, len);
      System.arraycopy(array2, off, this.myBuffer, 1 + len, len);
      return this.get(this.myBuffer, 0, bufferLen);
   }

   public boolean contains(int array0, int[] array1, int[] array2, int off, int len) {
      int bufferLen = 1 + 2 * len;
      if (this.myBuffer == null || this.myBuffer.length <= bufferLen) {
         this.myBuffer = new int[bufferLen];
      }

      this.myBuffer[0] = array0;
      System.arraycopy(array1, off, this.myBuffer, 1, len);
      System.arraycopy(array2, off, this.myBuffer, 1 + len, len);
      return this.contains(this.myBuffer, 0, bufferLen);
   }

   public void put(int[] array, int off, int len, int value) {
      int hash = this.getHash(array, off, len);
      int index = hash % this.myPoss.length;
      int step = hash % (this.myPoss.length - 2) + 1;

      int curPos;
      for(curPos = this.myPoss[index]; curPos != 0 && curPos != Integer.MIN_VALUE; curPos = this.myPoss[index]) {
         if (this.myArrays[curPos] == len) {
            ++curPos;
            int i = 0;

            while(true) {
               if (i >= len) {
                  this.myValues[index] = value;
                  return;
               }

               if (this.myArrays[curPos++] != array[off + i]) {
                  break;
               }

               ++i;
            }
         }

         index = (index + step) % this.myPoss.length;
      }

      this.myPoss[index] = this.myArrayPos;
      if (this.myArrayPos + len + 1 >= this.myArrays.length) {
         int[] arrays = new int[Math.max(this.myArrays.length + len + 1, this.myArrays.length * 2 + 1)];
         System.arraycopy(this.myArrays, 0, arrays, 0, this.myArrays.length);
         this.myArrays = arrays;
      }

      this.myArrays[this.myArrayPos++] = len;

      for(int i = 0; i < len; ++i) {
         this.myArrays[this.myArrayPos++] = array[off + i];
      }

      this.myValues[index] = value;
      if (curPos != Integer.MIN_VALUE) {
         ++this.mySlots;
      }

      ++this.myCount;
      if (this.mySlots * LOAD_FACTOR > this.myPoss.length) {
         this.rehash();
      }
   }

   public boolean contains(int[] array, int off, int len) {
      int hash = this.getHash(array, off, len);
      int index = hash % this.myPoss.length;
      int step = hash % (this.myPoss.length - 2) + 1;

      for(int curPos = this.myPoss[index]; curPos != 0 && curPos != Integer.MIN_VALUE; curPos = this.myPoss[index]) {
         if (this.myArrays[curPos] == len) {
            ++curPos;
            int i = 0;

            while(true) {
               if (i >= len) {
                  return true;
               }

               if (this.myArrays[curPos++] != array[off + i]) {
                  break;
               }

               ++i;
            }
         }

         index = (index + step) % this.myPoss.length;
      }

      return false;
   }

   public int get(int[] array, int off, int len) {
      int hash = this.getHash(array, off, len);
      int index = hash % this.myPoss.length;
      int step = hash % (this.myPoss.length - 2) + 1;

      for(int curPos = this.myPoss[index]; curPos != 0 && curPos != Integer.MIN_VALUE; curPos = this.myPoss[index]) {
         if (this.myArrays[curPos] == len) {
            ++curPos;
            int i = 0;

            while(true) {
               if (i >= len) {
                  return this.myValues[index];
               }

               if (this.myArrays[curPos++] != array[off + i]) {
                  break;
               }

               ++i;
            }
         }

         index = (index + step) % this.myPoss.length;
      }

      return -1;
   }

   public void remove(int[] array, int off, int len) {
      int hash = this.getHash(array, off, len);
      int index = hash % this.myPoss.length;
      int step = hash % (this.myPoss.length - 2) + 1;

      for(int curPos = this.myPoss[index]; curPos != 0 && curPos != Integer.MIN_VALUE; curPos = this.myPoss[index]) {
         if (this.myArrays[curPos] == len) {
            ++curPos;
            int i = 0;

            while(true) {
               if (i >= len) {
                  this.myPoss[index] = Integer.MIN_VALUE;
                  break;
               }

               if (this.myArrays[curPos++] != array[off + i]) {
                  break;
               }

               ++i;
            }
         }

         index = (index + step) % this.myPoss.length;
      }
   }

   private void rehash() {
      ++this.mySizeExp;
      int[] poss = new int[SIZES[this.mySizeExp]];
      int[] values = new int[SIZES[this.mySizeExp]];
      int slots = 0;

      for(int i = 0; i < this.myPoss.length; ++i) {
         int pos = this.myPoss[i];
         if (pos != 0 && pos != Integer.MIN_VALUE) {
            int hash = this.getHash(this.myArrays, pos + 1, this.myArrays[pos]);
            int index = hash % poss.length;
            int step = hash % (poss.length - 2) + 1;

            while(poss[index] != 0) {
               index = (index + step) % poss.length;
            }

            poss[index] = pos;
            values[index] = this.myValues[i];
            ++slots;
         }
      }

      this.myPoss = poss;
      this.myValues = values;
      this.mySlots = slots;
   }

   public int size() {
      return this.myCount;
   }
}
