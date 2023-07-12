package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

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
   private long[] myKeys;
   private long[] myOldKeys;
   private int mySlots;
   private int myCount;
   private int mySizeExp;

   public SetOfLong(int size) {
      super();
      this.mySizeExp = 1;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public SetOfLong() {
      super();
      this.mySizeExp = 1;
      this.myKeys = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public SetOfLong(@NonNull StoreInputStream stream) throws IOException {
      super();
      int count = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new long[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;

      for(int i = 0; i < count; ++i) {
         this.put(stream.readLong());
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySizeExp);

      for (long key : this.myKeys) {
         if (key != 0L && key != Long.MIN_VALUE) {
            stream.writeLong(key);
         }
      }
   }
@NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      if (this.mySlots > 0) {
         Arrays.fill(this.myKeys, 0L);

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void put(@NonNull SetOfLong set) {
      for(int i = 0; i < set.myKeys.length; ++i) {
         long key = set.myKeys[i];
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

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;
      long curKey = this.myKeys[index];

      int emptyIndex;
      for(emptyIndex = -1; curKey != 0L; curKey = this.myKeys[index]) {
         if (curKey == key) {
            return;
         }

         if (curKey == Long.MIN_VALUE) {
            emptyIndex = index;
         }

         index = (index + step) % this.myKeys.length;
      }

      if (emptyIndex != -1) {
         index = emptyIndex;
      }

      this.myKeys[index] = key;
      if (emptyIndex == -1) {
         ++this.mySlots;
      }

      ++this.myCount;
      if (this.mySlots * LOAD_FACTOR > this.myKeys.length) {
         this.rehash();
      }
   }

   public void remove(@NonNull SetOfLong keys) {
      keys.DEFAULT_ITERATOR.init();

      while(keys.DEFAULT_ITERATOR.hasMoreElements()) {
         this.remove(keys.DEFAULT_ITERATOR.nextKey());
      }
   }

   public void remove(long key) {
      if (key == 0L) {
         key = Long.MAX_VALUE;
      }

      int index = (int)((key & Long.MAX_VALUE) % (long)this.myKeys.length);
      int step = (int)((key & Long.MAX_VALUE) % ((long)this.myKeys.length - 2L)) + 1;

      for(long curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0L) {
            return;
         }

         index = (index + step) % this.myKeys.length;
      }

      this.myKeys[index] = Long.MIN_VALUE;
      --this.myCount;
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

   public boolean empty() {
      return this.myCount == 0;
   }

   private void rehash() {
      long[] keys;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         keys = new long[SIZES[this.mySizeExp]];
      } else if (this.myOldKeys != null && this.myOldKeys.length == this.myKeys.length) {
         keys = this.myOldKeys;

         Arrays.fill(keys, 0L);

         this.myOldKeys = this.myKeys;
      } else {
         this.myOldKeys = this.myKeys;
         keys = new long[SIZES[this.mySizeExp]];
      }

      int slots = 0;

      for (long key : this.myKeys) {
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

      this.myKeys = keys;
      this.mySlots = slots;
   }

   public int size() {
      return this.myCount;
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
            for (long l : this.myKeys) {
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
         super();
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < SetOfLong.this.myKeys.length) {
            this.key = SetOfLong.this.myKeys[this.index];
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
