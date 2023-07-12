package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class SetOfInt {
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
   public static final SetOfInt EMPTY = new SetOfInt();
   private int[] myKeys;
   private int[] myOldKeys;
   private int mySlots;
   private int myCount;
   private int mySizeExp;

   public SetOfInt(int size) {
      super();
      this.mySizeExp = 1;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public SetOfInt() {
      super();
      this.mySizeExp = 1;
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public SetOfInt(@NonNull StoreInputStream stream) throws IOException {
      super();
      int count = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;

      for(int i = 0; i < count; ++i) {
         this.put(stream.readInt());
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySizeExp);

      for (int key : this.myKeys) {
         if (key != 0 && key != Integer.MIN_VALUE) {
            stream.writeInt(key);
         }
      }
   }
@NonNull
   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      if (this.mySlots > 0) {
         Arrays.fill(this.myKeys, 0);

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void put(@NonNull SetOfInt set) {
      for(int i = 0; i < set.myKeys.length; ++i) {
         int key = set.myKeys[i];
         if (key == Integer.MAX_VALUE) {
            this.put(0);
         } else if (key != 0 && key != Integer.MIN_VALUE) {
            this.put(key);
         }
      }
   }

   public void put(@NonNull ListOfInt vec) {
      for(int i = 0; i < vec.size(); ++i) {
         int key = vec.get(i);
         this.put(key);
      }
   }

   public void put(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int curKey = this.myKeys[index];

      int emptyIndex;
      for(emptyIndex = -1; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            return;
         }

         if (curKey == Integer.MIN_VALUE) {
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

   public void remove(@NonNull SetOfInt keys) {
      keys.DEFAULT_ITERATOR.init();

      while(keys.DEFAULT_ITERATOR.hasMoreElements()) {
         this.remove(keys.DEFAULT_ITERATOR.nextKey());
      }
   }

   public void remove(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return;
         }

         index = (index + step) % this.myKeys.length;
      }

      this.myKeys[index] = Integer.MIN_VALUE;
      --this.myCount;
   }

   public int get() {
      int index = 0;

      while(index < this.myKeys.length) {
         int key = this.myKeys[index];
         ++index;
         if (key != 0 && key != Integer.MIN_VALUE) {
            if (key == Integer.MAX_VALUE) {
               key = 0;
            }

            return key;
         }
      }

      return -1;
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

   public boolean empty() {
      return this.myCount == 0;
   }

   private void rehash() {
      int[] keys;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         keys = new int[SIZES[this.mySizeExp]];
      } else if (this.myOldKeys != null && this.myOldKeys.length == this.myKeys.length) {
         keys = this.myOldKeys;

         Arrays.fill(keys, 0);

         this.myOldKeys = this.myKeys;
      } else {
         this.myOldKeys = this.myKeys;
         keys = new int[SIZES[this.mySizeExp]];
      }

      int slots = 0;

      for (int key : this.myKeys) {
         if (key != 0 && key != Integer.MIN_VALUE) {
            int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
            int index = (key & Integer.MAX_VALUE) % keys.length;

            while (keys[index] != 0) {
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
      if (!(o instanceof SetOfInt)) {
         return false;
      } else {
         SetOfInt set = (SetOfInt)o;
         if (set.size() != this.size()) {
            return false;
         } else {
            for (int j : this.myKeys) {
               int key = j;
               if (key != 0 && key != Integer.MIN_VALUE) {
                  if (key == Integer.MAX_VALUE) {
                     key = 0;
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
      private int key;

      private Iterator() {
         super();
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < SetOfInt.this.myKeys.length) {
            this.key = SetOfInt.this.myKeys[this.index];
            ++this.index;
            if (this.key != 0 && this.key != Integer.MIN_VALUE) {
               if (this.key == Integer.MAX_VALUE) {
                  this.key = 0;
               }

               return true;
            }
         }

         return false;
      }

      public int nextKey() {
         return this.key;
      }
   }
}
