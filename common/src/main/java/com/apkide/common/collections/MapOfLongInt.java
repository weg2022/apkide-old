package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class MapOfLongInt {
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
   private long[] myKeys;
   private long[] myOldKeys;
   private int[] myValues;
   private int[] myOldValues;
   private int mySlots;
   private int myCount;
   private int mySizeExp;

   public MapOfLongInt(int size) {
      super();
      this.mySizeExp = 0;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new long[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public MapOfLongInt() {
      super();
      this.mySizeExp = 0;
      this.myKeys = new long[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public MapOfLongInt(@NonNull StoreInputStream stream) throws IOException {
      super();
      this.myCount = stream.readInt();
      this.mySlots = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new long[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myKeys.length; ++i) {
         this.myKeys[i] = stream.readLong();
      }

      this.myValues = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myValues.length; ++i) {
         this.myValues[i] = stream.readInt();
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySlots);
      stream.writeInt(this.mySizeExp);

      for (long key : this.myKeys) {
         stream.writeLong(key);
      }

      for (int value : this.myValues) {
         stream.writeInt(value);
      }
   }

   @NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear(int size) {
      if (this.mySlots > 0) {
         if (this.myKeys.length < size) {
            Arrays.fill(this.myKeys, 0L);
         } else {
            this.mySizeExp = 0;

            while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
               ++this.mySizeExp;
            }

            this.myKeys = new long[SIZES[this.mySizeExp]];
            this.myValues = new int[SIZES[this.mySizeExp]];
         }

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void clear() {
      if (this.mySlots > 0) {
         Arrays.fill(this.myKeys, 0L);

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void put(long key, int value) {
      this.remove(key);
      this.insert(key, value);
   }

   public int get(int key1, int key2) {
      return this.get((long)key1 << 32 | (long)key2);
   }

   public void put(int key1, int key2, int value) {
      this.put((long)key1 << 32 | (long)key2, value);
   }

   public void insert(int key1, int key2, int value) {
      this.insert((long)key1 << 32 | (long)key2, value);
   }

   public void remove(int key1, int key2) {
      this.remove((long)key1 << 32 | (long)key2);
   }

   public boolean contains(int key1, int key2) {
      return this.contains((long)key1 << 32 | (long)key2);
   }

   public void insert(long key, int value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;
      boolean inserted = false;
      int emptyIndex = -1;

      for(long curKey = this.myKeys[index]; curKey != 0L; curKey = this.myKeys[index]) {
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

   public void remove(long key, int value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != key || this.myValues[index] != value; curKey = this.myKeys[index]) {
         if (curKey == 0L) {
            return;
         }

         index = (index + step) % this.myKeys.length;
      }

      this.myKeys[index] = Long.MIN_VALUE;
      --this.myCount;
   }

   public boolean contains(long key, int value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != key || this.myValues[index] != value; curKey = this.myKeys[index]) {
         if (curKey == 0L) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public boolean contains(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0L) {
            return false;
         }

         index = (index + step) % this.myKeys.length;
      }

      return true;
   }

   public int count(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int count = 0;
      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != 0L; curKey = this.myKeys[index]) {
         if (curKey == key) {
            ++count;
         }

         index = (index + step) % this.myKeys.length;
      }

      return count;
   }

   public void remove(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != 0L; curKey = this.myKeys[index]) {
         if (curKey == key) {
            this.myKeys[index] = Long.MIN_VALUE;
            --this.myCount;
         }

         index = (index + step) % this.myKeys.length;
      }
   }

   public int get(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0L) {
            return -1;
         }

         index = (index + step) % this.myKeys.length;
      }

      return this.myValues[index];
   }

   private void rehash() {
      long[] keys;
      int[] values;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         this.myOldValues = null;
         keys = new long[SIZES[this.mySizeExp]];
         values = new int[SIZES[this.mySizeExp]];
      } else if (this.myOldKeys != null && this.myOldKeys.length == this.myKeys.length) {
         keys = this.myOldKeys;

         Arrays.fill(keys, 0L);

         values = this.myOldValues;
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
      } else {
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
         keys = new long[SIZES[this.mySizeExp]];
         values = new int[SIZES[this.mySizeExp]];
      }

      int slots = 0;

      for(int i = 0; i < this.myKeys.length; ++i) {
         long key = this.myKeys[i];
         if (key != 0L && key != Long.MIN_VALUE) {
            int index = (int)((key & Long.MAX_VALUE) % (long)keys.length);
            int step = (int)((key & Long.MAX_VALUE) % ((long)keys.length - 2L)) + 1;

            while(keys[index] != 0L) {
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
      private long key;
      private int value;
      private long theKey;

      private Iterator() {
         super();
      }

      public void init() {
         this.index = 0;
         this.step = 0;
      }

      public void init(long key) {
         if (key == 0L) {
            key = Long.MAX_VALUE;
         }

         this.theKey = key;
         this.index = (int)((key & Long.MAX_VALUE) % (long)MapOfLongInt.this.myKeys.length);
         this.step = (int)((key & Long.MAX_VALUE) % ((long)MapOfLongInt.this.myKeys.length - 2L)) + 1;
      }

      public boolean hasMoreElements() {
         if (this.step == 0) {
            while(this.index < MapOfLongInt.this.myKeys.length) {
               this.key = MapOfLongInt.this.myKeys[this.index];
               if (this.key != 0L && this.key != Long.MIN_VALUE) {
                  if (this.key == Long.MAX_VALUE) {
                     this.key = 0L;
                  }

                  this.value = MapOfLongInt.this.myValues[this.index];
                  ++this.index;
                  return true;
               }

               ++this.index;
            }

            return false;
         } else {
            while(true) {
               this.key = MapOfLongInt.this.myKeys[this.index];
               if (this.key == 0L) {
                  return false;
               }

               if (this.key == this.theKey) {
                  if (this.key == Long.MAX_VALUE) {
                     this.key = 0L;
                  }

                  this.value = MapOfLongInt.this.myValues[this.index];
                  this.index = (this.index + this.step) % MapOfLongInt.this.myKeys.length;
                  return true;
               }

               this.index = (this.index + this.step) % MapOfLongInt.this.myKeys.length;
            }
         }
      }

      public long nextKey() {
         return this.key;
      }

      public int nextValue() {
         return this.value;
      }
   }
}
