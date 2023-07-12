package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class MapOfIntLong {
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

   public MapOfIntLong(int size) {
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

   public MapOfIntLong() {
      super();
      this.mySizeExp = 0;
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public MapOfIntLong(@NonNull StoreInputStream stream) throws IOException {
      super();
      this.myCount = stream.readInt();
      this.mySlots = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myKeys.length; ++i) {
         this.myKeys[i] = stream.readInt();
      }

      this.myValues = new long[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myValues.length; ++i) {
         this.myValues[i] = stream.readLong();
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySlots);
      stream.writeInt(this.mySizeExp);

      for (int key : this.myKeys) {
         stream.writeInt(key);
      }

      for (long value : this.myValues) {
         stream.writeLong(value);
      }
   }
@NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      if (this.mySlots > 0) {
         Arrays.fill(this.myKeys, 0);

         Arrays.fill(this.myValues, 0L);

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void put(@NonNull MapOfIntLong map) {
      for(int i = 0; i < map.myKeys.length; ++i) {
         int key = map.myKeys[i];
         long value = map.myValues[i];
         if (key == Integer.MAX_VALUE) {
            this.put(0, value);
         } else if (key != 0 && key != Integer.MIN_VALUE) {
            this.put(key, value);
         }
      }
   }

   public void put(int key, long value) {
      this.remove(key);
      this.insert(key, value);
   }

   public void insert(int key, long value) {
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

         if (this.mySlots * LOAD_FACTOR > this.myKeys.length) {
            this.rehash();
         }
      }
   }

   public void remove(int key, long value) {
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
      --this.myCount;
   }

   public boolean contains(int key, long value) {
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
            --this.myCount;
         }

         index = (index + step) % this.myKeys.length;
      }
   }

   public long get(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;

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
   }

   public int size() {
      return this.myCount;
   }

   public class Iterator {
      private int index = 0;
      private int step = 0;
      private int key;
      private long value;
      private int theKey;

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

         this.theKey = key;
         this.step = (key & Integer.MAX_VALUE) % (MapOfIntLong.this.myKeys.length - 2) + 1;
         this.index = (key & Integer.MAX_VALUE) % MapOfIntLong.this.myKeys.length;
      }

      public boolean hasMoreElements() {
         if (this.step == 0) {
            while(this.index < MapOfIntLong.this.myKeys.length) {
               this.key = MapOfIntLong.this.myKeys[this.index];
               if (this.key != 0 && this.key != Integer.MIN_VALUE) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = MapOfIntLong.this.myValues[this.index];
                  ++this.index;
                  return true;
               }

               ++this.index;
            }

            return false;
         } else {
            while(true) {
               this.key = MapOfIntLong.this.myKeys[this.index];
               if (this.key == 0) {
                  return false;
               }

               if (this.key == this.theKey) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = MapOfIntLong.this.myValues[this.index];
                  this.index = (this.index + this.step) % MapOfIntLong.this.myKeys.length;
                  return true;
               }

               this.index = (this.index + this.step) % MapOfIntLong.this.myKeys.length;
            }
         }
      }

      public int nextKey() {
         return this.key;
      }

      public long nextValue() {
         return this.value;
      }
   }
}
