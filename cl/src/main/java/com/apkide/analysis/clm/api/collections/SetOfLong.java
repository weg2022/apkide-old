package com.apkide.analysis.clm.api.collections;

import java.io.IOException;
import java.util.Arrays;

public class SetOfLong {
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
   public static final SetOfLong EMPTY = new SetOfLong();
   private long[] keys;
   private long[] oldkeys;
   private int slots;
   private int count;
   private int sizeexp;

   public SetOfLong(int size) {
      this.sizeexp = 1;

      while(SIZES[this.sizeexp] < size * 2) {
         ++this.sizeexp;
      }

      this.keys = new long[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public SetOfLong() {
      this.sizeexp = 1;
      this.keys = new long[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public SetOfLong(StoreInputStream stream) throws IOException {
      int count = stream.readInt();
      this.sizeexp = stream.readInt();
      this.keys = new long[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;

      for(int i = 0; i < count; ++i) {
         this.put(stream.readLong());
      }
   }

   public void store(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.count);
      stream.writeInt(this.sizeexp);

      for (long key : this.keys) {
         if (key != 0L && key != Long.MIN_VALUE) {
            stream.writeLong(key);
         }
      }
   }

   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      if (this.slots > 0) {
         Arrays.fill(this.keys, 0L);

         this.slots = 0;
         this.count = 0;
      }
   }

   public void put(SetOfLong set) {
      for(int i = 0; i < set.keys.length; ++i) {
         long key = set.keys[i];
         if (key == Long.MAX_VALUE) {
            this.put(0L);
         } else if (key != 0L && key != Long.MIN_VALUE) {
            this.put(key);
         }
      }
   }

   public void put(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;
      long curKey = this.keys[index];

      int emptyIndex;
      for(emptyIndex = -1; curKey != 0L; curKey = this.keys[index]) {
         if (curKey == key) {
            return;
         }

         if (curKey == Long.MIN_VALUE) {
            emptyIndex = index;
         }

         index = (index + step) % this.keys.length;
      }

      if (emptyIndex != -1) {
         index = emptyIndex;
      }

      this.keys[index] = key;
      if (emptyIndex == -1) {
         ++this.slots;
      }

      ++this.count;
      if (this.slots * 2 > this.keys.length) {
         this.rehash();
      }
   }

   public void remove(SetOfLong keys) {
      keys.DEFAULT_ITERATOR.init();

      while(keys.DEFAULT_ITERATOR.hasMoreElements()) {
         this.remove(keys.DEFAULT_ITERATOR.nextKey());
      }
   }

   public void remove(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.keys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.keys.length - 2L)) + 1;

      for(long curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0L) {
            return;
         }

         index = (index + step) % this.keys.length;
      }

      this.keys[index] = Long.MIN_VALUE;
      --this.count;
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

   public boolean empty() {
      return this.count == 0;
   }

   private void rehash() {
      long[] keys;
      if (this.count * 2 > this.keys.length) {
         ++this.sizeexp;
         this.oldkeys = null;
         keys = new long[SIZES[this.sizeexp]];
      } else if (this.oldkeys != null && this.oldkeys.length == this.keys.length) {
         keys = this.oldkeys;

         Arrays.fill(keys, 0L);

         this.oldkeys = this.keys;
      } else {
         this.oldkeys = this.keys;
         keys = new long[SIZES[this.sizeexp]];
      }

      int slots = 0;

      for (long key : this.keys) {
         if (key != 0L && key != Long.MIN_VALUE) {
            int index = (int) ((key & Long.MAX_VALUE) % (long) keys.length);
            int step = (int) ((key & Long.MAX_VALUE) % ((long) keys.length - 2L)) + 1;

            while (keys[index] != 0L) {
               index = (index + step) % keys.length;
            }

            keys[index] = key;
            ++slots;
         }
      }

      this.keys = keys;
      this.slots = slots;
   }

   public int size() {
      return this.count;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof SetOfLong)) {
         return false;
      } else {
         SetOfLong set = (SetOfLong)o;
         if (set.size() != this.size()) {
            return false;
         } else {
            for (long l : this.keys) {
               long key = l;
               if (key != 0L && key != Long.MIN_VALUE) {
                  if (key == Long.MAX_VALUE) {
                     key = 0L;
                  }

                  if (!set.contains(key)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public class Iterator {
      private int index = 0;
      private long key;

      private Iterator() {
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < SetOfLong.this.keys.length) {
            this.key = SetOfLong.this.keys[this.index];
            ++this.index;
            if (this.key != 0L && this.key != Long.MIN_VALUE) {
               if (this.key == Long.MAX_VALUE) {
                  this.key = 0L;
               }

               return true;
            }
         }

         return false;
      }

      public long nextKey() {
         return this.key;
      }
   }
}
