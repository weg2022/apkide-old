package com.apkide.common.collections;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class FunctionOfIntLong {
   public final Iterator DEFAULT_ITERATOR = new Iterator();
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
   private int[] myKeys;
   private int[] myOldKeys;
   private long[] myValues;
   private long[] myOldValues;
   private int mySlots;
   private int myCount;
   private int mySizeExp;

   public FunctionOfIntLong(int size) {
      super();
      this.mySizeExp = 0;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public FunctionOfIntLong() {
      super();
      this.mySizeExp = 0;
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public FunctionOfIntLong(@NonNull StoreInputStream stream) throws IOException {
      super();
      int count = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;

      for(int i = 0; i < count; ++i) {
         this.put(stream.readInt(), stream.readLong());
      }
   }

   public void put(@NonNull FunctionOfIntLong set) {
      for(int i = 0; i < set.myKeys.length; ++i) {
         int key = set.myKeys[i];
         if (key == MAX_VALUE) {
            this.put(0, set.myValues[i]);
         } else if (key != 0 && key != MIN_VALUE) {
            this.put(key, set.myValues[i]);
         }
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySizeExp);

      for(int i = 0; i < this.myKeys.length; ++i) {
         int key = this.myKeys[i];
         if (key != 0 && key != MIN_VALUE) {
            stream.writeInt(this.myKeys[i]);
            stream.writeLong(this.myValues[i]);
         }
      }
   }

   @NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear(int size) {
      if (this.mySlots > 0) {
         if (this.myKeys.length < size) {
            Arrays.fill(this.myKeys, 0);
         } else {
            this.mySizeExp = 0;

            while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
               ++this.mySizeExp;
            }

            this.myKeys = new int[SIZES[this.mySizeExp]];
            this.myValues = new long[SIZES[this.mySizeExp]];
         }

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void clear() {
      if (this.mySlots > 0) {
         Arrays.fill(this.myKeys, 0);

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void put(int key, long value) {
      if (key == 0) {
         key = MAX_VALUE;
      }

      int index = (key & MAX_VALUE) % this.myKeys.length;
      int step = (key & MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int emptyIndex = -1;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            if (this.myValues[index] == value) {
               return;
            }

            this.myKeys[index] = MIN_VALUE;
            --this.myCount;
         } else if (curKey == MIN_VALUE) {
            emptyIndex = index;
         }

         index = (index + step) % this.myKeys.length;
      }

      if (emptyIndex != -1) {
         index = emptyIndex;
      }

      this.myKeys[index] = key;
      this.myValues[index] = value;
      ++this.myCount;
      if (emptyIndex == -1) {
         ++this.mySlots;
      }

      if (this.mySlots * LOAD_FACTOR > this.myKeys.length) {
         this.rehash();
      }
   }

   public boolean contains(int key, long value) {
      if (key == 0) {
         key = MAX_VALUE;
      }

      int index = (key & MAX_VALUE) % this.myKeys.length;
      int step = (key & MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != key || this.myValues[index] != value; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public void remove(int key) {
      if (key == 0) {
         key = MAX_VALUE;
      }

      int index = (key & MAX_VALUE) % this.myKeys.length;
      int step = (key & MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            this.myKeys[index] = MIN_VALUE;
            --this.myCount;
            return;
         }

         index = (index + step) % this.myKeys.length;
      }
   }

   public boolean contains(int key) {
      if (key == 0) {
         key = MAX_VALUE;
      }

      int step = (key & MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public long get(int key) {
      if (key == 0) {
         key = MAX_VALUE;
      }

      int step = (key & MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return -1L;
         }

         index = (index + step) % this.myKeys.length;
      }

      return this.myValues[index];
   }

   private void rehash() {
      int[] keys;
      long[] values;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         this.myOldValues = null;
         keys = new int[SIZES[this.mySizeExp]];
         values = new long[SIZES[this.mySizeExp]];
      } else if (this.myOldKeys != null && this.myOldKeys.length == this.myKeys.length) {
         keys = this.myOldKeys;

         Arrays.fill(keys, 0);

         values = this.myOldValues;
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
      } else {
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
         keys = new int[SIZES[this.mySizeExp]];
         values = new long[SIZES[this.mySizeExp]];
      }

      int slots = 0;

      for(int i = 0; i < this.myKeys.length; ++i) {
         int key = this.myKeys[i];
         if (key != 0 && key != MIN_VALUE) {
            int step = (key & MAX_VALUE) % (keys.length - 2) + 1;
            int index = (key & MAX_VALUE) % keys.length;

            while(keys[index] != 0) {
               index = (index + step) % keys.length;
            }

            keys[index] = key;
            values[index] = this.myValues[i];
            ++slots;
         }
      }

      this.myKeys = keys;
      this.myValues = values;
      this.mySlots = slots;
   }

   public int size() {
      return this.myCount;
   }

   public class Iterator {
      private int index = 0;
      private int key;
      private long value;

      private Iterator() {
         super();
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < FunctionOfIntLong.this.myKeys.length) {
            this.key = FunctionOfIntLong.this.myKeys[this.index];
            if (this.key != 0 && this.key != MIN_VALUE) {
               if (this.key == MAX_VALUE) {
                  this.key = 0;
               }

               this.value = FunctionOfIntLong.this.myValues[this.index];
               ++this.index;
               return true;
            }

            ++this.index;
         }

         return false;
      }

      public int nextKey() {
         return this.key;
      }

      public long nextValue() {
         return this.value;
      }
   }
}
