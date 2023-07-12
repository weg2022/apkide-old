package com.apkide.common.collections;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class HashtableOfInt<T> {
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
   private T[] myValues;
   private T[] myOldValues;
   private int mySlots;
   private int myCount;
   private int mySizeExp = 0;

   public HashtableOfInt(int size) {
      super();

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = (T[])(new Object[SIZES[this.mySizeExp]]);
      this.mySlots = 0;
      this.myCount = 0;
   }

   public HashtableOfInt() {
      super();
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = (T[])(new Object[SIZES[this.mySizeExp]]);
      this.mySlots = 0;
      this.myCount = 0;
   }

   @NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      Arrays.fill(this.myKeys, 0);

      Arrays.fill(this.myValues, null);

      this.mySlots = 0;
      this.myCount = 0;
      this.myOldValues = null;
      this.myOldKeys = null;
   }

   public void put(int key, T value) {
      this.remove(key);
      this.insert(key, value);
   }

   public void insert(int key, T value) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      boolean inserted = false;
      int emptyIndex = -1;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            if (this.myValues[index] == value) {
               return;
            }
         } else if (curKey == Integer.MIN_VALUE) {
            emptyIndex = index;
         }

         index = (index + step) % this.myKeys.length;
      }

      if (!inserted) {
         if (emptyIndex != -1) {
            index = emptyIndex;
         }

         this.myKeys[index] = key;
         this.myValues[index] = value;
         ++this.myCount;
         if (emptyIndex == -1) {
            ++this.mySlots;
         }

         if (this.mySlots * 2 > this.myKeys.length) {
            this.rehash();
         }
      }
   }

   public void remove(int key, T value) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != key || this.myValues[index] != value; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return;
         }

         index = (index + step) % this.myKeys.length;
      }

      this.myKeys[index] = Integer.MIN_VALUE;
      this.myValues[index] = null;
      --this.myCount;
   }

   public boolean contains(int key, T value) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != key || this.myValues[index] != value; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public boolean contains(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public int count(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int count = 0;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            ++count;
         }

         index = (index + step) % this.myKeys.length;
      }

      return count;
   }

   public void remove(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            this.myKeys[index] = Integer.MIN_VALUE;
            this.myValues[index] = null;
            --this.myCount;
         }

         index = (index + step) % this.myKeys.length;
      }
   }

   public T get(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return null;
         }

         index = (index + step) % this.myKeys.length;
      }

      return this.myValues[index];
   }

   private void rehash() {
      int[] keys;
      T[] values;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         this.myOldValues = null;
         keys = new int[SIZES[this.mySizeExp]];
         values = (T[])(new Object[SIZES[this.mySizeExp]]);
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
         values = (T[])(new Object[SIZES[this.mySizeExp]]);
      }

      int slots = 0;

      for(int i = 0; i < this.myKeys.length; ++i) {
         int key = this.myKeys[i];
         if (key != 0 && key != Integer.MIN_VALUE) {
            int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
            int index = (key & Integer.MAX_VALUE) % keys.length;

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
      if (this.myOldValues != null) {
         Arrays.fill(this.myOldValues, null);
      }
   }

   public int size() {
      return this.myCount;
   }

   public class Iterator {
      private int index = 0;
      private int step = 0;
      private int key;
      private T value;
      private int thekey;

      private Iterator() {
         super();
      }

      public void init() {
         this.index = 0;
         this.step = 0;
      }

      public void init(int key) {
         if (key == 0) {
            key = Integer.MAX_VALUE;
         }

         this.thekey = key;
         this.step = (key & Integer.MAX_VALUE) % (HashtableOfInt.this.myKeys.length - 2) + 1;
         this.index = (key & Integer.MAX_VALUE) % HashtableOfInt.this.myKeys.length;
      }

      public boolean hasMoreElements() {
         if (this.step == 0) {
            while(this.index < HashtableOfInt.this.myKeys.length) {
               this.key = HashtableOfInt.this.myKeys[this.index];
               if (this.key != 0 && this.key != Integer.MIN_VALUE) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = HashtableOfInt.this.myValues[this.index];
                  ++this.index;
                  return true;
               }

               ++this.index;
            }

            return false;
         } else {
            while(true) {
               this.key = HashtableOfInt.this.myKeys[this.index];
               if (this.key == 0) {
                  return false;
               }

               if (this.key == this.thekey) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = HashtableOfInt.this.myValues[this.index];
                  this.index = (this.index + this.step) % HashtableOfInt.this.myKeys.length;
                  return true;
               }

               this.index = (this.index + this.step) % HashtableOfInt.this.myKeys.length;
            }
         }
      }

      public int nextKey() {
         return this.key;
      }

      public T nextValue() {
         return this.value;
      }
   }
}
