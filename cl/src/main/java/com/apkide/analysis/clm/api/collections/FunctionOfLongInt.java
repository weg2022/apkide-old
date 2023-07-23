package com.apkide.analysis.clm.api.collections;

import java.io.IOException;
import java.util.Arrays;

public class FunctionOfLongInt {
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
   private long[] keys;
   private long[] oldkeys;
   private int[] values;
   private int[] oldvalues;
   private int slots;
   private int count;
   private int sizeexp;

   public FunctionOfLongInt(int size) {
      this.sizeexp = 0;

      while(SIZES[this.sizeexp] < size * 2) {
         ++this.sizeexp;
      }

      this.keys = new long[SIZES[this.sizeexp]];
      this.values = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public FunctionOfLongInt() {
      this.sizeexp = 0;
      this.keys = new long[SIZES[this.sizeexp]];
      this.values = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public FunctionOfLongInt(StoreInputStream stream) throws IOException {
      int count = stream.readInt();
      this.sizeexp = stream.readInt();
      this.keys = new long[SIZES[this.sizeexp]];
      this.values = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;

      for(int i = 0; i < count; ++i) {
         this.put(stream.readLong(), stream.readInt());
      }
   }

   public void store(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.count);
      stream.writeInt(this.sizeexp);

      for(int i = 0; i < this.keys.length; ++i) {
         long key = this.keys[i];
         if (key != 0L && key != Long.MIN_VALUE) {
            stream.writeLong(this.keys[i]);
            stream.writeInt(this.values[i]);
         }
      }
   }

   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear(int size) {
      if (this.slots > 0) {
         if (this.keys.length < size) {
            Arrays.fill(this.keys, 0L);
         } else {
            this.sizeexp = 0;

            while(SIZES[this.sizeexp] < size * 2) {
               ++this.sizeexp;
            }

            this.keys = new long[SIZES[this.sizeexp]];
            this.values = new int[SIZES[this.sizeexp]];
         }

         this.slots = 0;
         this.count = 0;
      }
   }

   public void clear() {
      if (this.slots > 0) {
         Arrays.fill(this.keys, 0L);

         this.slots = 0;
         this.count = 0;
      }
   }

   public void put(int key1, int key2, int value) {
      this.put((long)key1 << 32 | (long)key2, value);
   }

   public void put(long key, int value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;
      int emptyIndex = -1;

      for(long curKey = this.keys[index]; curKey != 0L; curKey = this.keys[index]) {
         if (curKey == key) {
            if (this.values[index] == value) {
               return;
            }

            this.keys[index] = Long.MIN_VALUE;
            --this.count;
         } else if (curKey == Long.MIN_VALUE) {
            emptyIndex = index;
         }

         index = (index + step) % this.keys.length;
      }

      if (emptyIndex != -1) {
         index = emptyIndex;
      }

      this.keys[index] = key;
      this.values[index] = value;
      ++this.count;
      if (emptyIndex == -1) {
         ++this.slots;
      }

      if (this.slots * 2 > this.keys.length) {
         this.rehash();
      }
   }

   public void remove(int key1, int key2) {
      this.remove((long)key1 << 32 | (long)key2);
   }

   public void remove(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != 0L; curKey = this.keys[index]) {
         if (curKey == key) {
            this.keys[index] = Long.MIN_VALUE;
            --this.count;
            return;
         }

         index = (index + step) % this.keys.length;
      }
   }

   public boolean contains(int key1, int key2) {
      return this.contains((long)key1 << 32 | (long)key2);
   }

   public boolean contains(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return false;
         }

         index = (index + step) % this.keys.length;
      }

      return true;
   }

   public int get(int key1, int key2) {
      return this.get((long)key1 << 32 | (long)key2);
   }

   public int get(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return -1;
         }

         index = (index + step) % this.keys.length;
      }

      return this.values[index];
   }

   private void rehash() {
      long[] keys;
      int[] values;
      if (this.count * 2 > this.keys.length) {
         ++this.sizeexp;
         this.oldkeys = null;
         this.oldvalues = null;
         keys = new long[SIZES[this.sizeexp]];
         values = new int[SIZES[this.sizeexp]];
      } else if (this.oldkeys != null && this.oldkeys.length == this.keys.length) {
         keys = this.oldkeys;

         Arrays.fill(keys, 0L);

         values = this.oldvalues;
         this.oldkeys = this.keys;
         this.oldvalues = this.values;
      } else {
         this.oldkeys = this.keys;
         this.oldvalues = this.values;
         keys = new long[SIZES[this.sizeexp]];
         values = new int[SIZES[this.sizeexp]];
      }

      int slots = 0;

      for(int i = 0; i < this.keys.length; ++i) {
         long key = this.keys[i];
         if (key != 0L && key != Long.MIN_VALUE) {
            int index = (int)((key & Long.MAX_VALUE) % (long)keys.length);
            int step = (int)((key & Long.MAX_VALUE) % ((long)keys.length - 2L)) + 1;

            while(keys[index] != 0L) {
               index = (index + step) % keys.length;
            }

            keys[index] = key;
            values[index] = this.values[i];
            ++slots;
         }
      }

      this.keys = keys;
      this.values = values;
      this.slots = slots;
   }

   public int size() {
      return this.count;
   }

   public class Iterator {
      private int index = 0;
      private int step = 0;
      private long key;
      private int value;

      private Iterator() {
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < FunctionOfLongInt.this.keys.length) {
            this.key = FunctionOfLongInt.this.keys[this.index];
            if (this.key != 0L && this.key != Long.MIN_VALUE) {
               if (this.key == Long.MAX_VALUE) {
                  this.key = 0L;
               }

               this.value = FunctionOfLongInt.this.values[this.index];
               ++this.index;
               return true;
            }

            ++this.index;
         }

         return false;
      }

      public long nextKey() {
         return this.key;
      }

      public int nextValue() {
         return this.value;
      }
   }
}
