package com.apkide.analysis.clm.api.collections;

import java.util.Arrays;

public class HashtableOfLong<T> {
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
   private T[] values;
   private T[] oldvalues;
   private int slots;
   private int count;
   private int sizeexp = 0;

   public HashtableOfLong(int size) {
      while(SIZES[this.sizeexp] < size * 2) {
         ++this.sizeexp;
      }

      this.keys = new long[SIZES[this.sizeexp]];
      this.values = (T[])(new Object[SIZES[this.sizeexp]]);
      this.slots = 0;
      this.count = 0;
   }

   public HashtableOfLong() {
      this.keys = new long[SIZES[this.sizeexp]];
      this.values = (T[])(new Object[SIZES[this.sizeexp]]);
      this.slots = 0;
      this.count = 0;
   }

   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      Arrays.fill(this.keys, 0L);

      Arrays.fill(this.values, null);

      this.slots = 0;
      this.count = 0;
      this.oldkeys = null;
      this.oldvalues = null;
   }

   public void put(long key, T value) {
      this.remove(key);
      this.insert(key, value);
   }

   public void insert(long key, T value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;
      boolean inserted = false;
      int emptyIndex = -1;

      for(long curKey = this.keys[index]; curKey != 0L; curKey = this.keys[index]) {
         if (curKey == key) {
            if (this.values[index] == value) {
               return;
            }
         } else if (curKey == -2147483648L) {
            emptyIndex = index;
         }

         index = (index + step) % this.keys.length;
      }

      if (!inserted) {
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
   }

   public void remove(long key, T value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key || this.values[index] != value; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return;
         }

         index = (index + step) % this.keys.length;
      }

      this.keys[index] = Long.MIN_VALUE;
      this.values[index] = null;
      --this.count;
   }

   public boolean contains(long key, T value) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key || this.values[index] != value; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return false;
         }

         index = (index + step) % this.keys.length;
      }

      return true;
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

   public int count(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int count = 0;
      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != 0L; curKey = this.keys[index]) {
         if (curKey == key) {
            ++count;
         }

         index = (index + step) % this.keys.length;
      }

      return count;
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
            this.values[index] = null;
            --this.count;
         }

         index = (index + step) % this.keys.length;
      }
   }

   public T get(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return null;
         }

         index = (index + step) % this.keys.length;
      }

      return this.values[index];
   }

   private void rehash() {
      long[] keys;
      T[] values;
      if (this.count * 2 > this.keys.length) {
         ++this.sizeexp;
         this.oldkeys = null;
         this.oldvalues = null;
         keys = new long[SIZES[this.sizeexp]];
         values = (T[])(new Object[SIZES[this.sizeexp]]);
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
         values = (T[])(new Object[SIZES[this.sizeexp]]);
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
      if (this.oldvalues != null) {
         Arrays.fill(this.oldvalues, null);
      }
   }

   public int size() {
      return this.count;
   }

   public class Iterator {
      private int index = 0;
      private int step = 0;
      private long key;
      private T value;
      private long thekey;

      private Iterator() {
      }

      public void init() {
         this.index = 0;
         this.step = 0;
      }

      public void init(long key) {
         if (key == 0L) {
            key = Long.MAX_VALUE;
         }

         this.thekey = key;
         this.index = (int)((key & Long.MAX_VALUE) % (long)HashtableOfLong.this.keys.length);
         this.step = (int)((key & Long.MAX_VALUE) % ((long)HashtableOfLong.this.keys.length - 2L)) + 1;
      }

      public boolean hasMoreElements() {
         if (this.step == 0) {
            while(this.index < HashtableOfLong.this.keys.length) {
               this.key = HashtableOfLong.this.keys[this.index];
               if (this.key != 0L && this.key != Long.MIN_VALUE) {
                  if (this.key == Long.MAX_VALUE) {
                     this.key = 0L;
                  }

                  this.value = HashtableOfLong.this.values[this.index];
                  ++this.index;
                  return true;
               }

               ++this.index;
            }

            return false;
         } else {
            while(true) {
               this.key = HashtableOfLong.this.keys[this.index];
               if (this.key == 0L) {
                  return false;
               }

               if (this.key == this.thekey) {
                  if (this.key == Long.MAX_VALUE) {
                     this.key = 0L;
                  }

                  this.value = HashtableOfLong.this.values[this.index];
                  this.index = (this.index + this.step) % HashtableOfLong.this.keys.length;
                  return true;
               }

               this.index = (this.index + this.step) % HashtableOfLong.this.keys.length;
            }
         }
      }

      public long nextKey() {
         return this.key;
      }

      public T nextValue() {
         return this.value;
      }
   }
}
