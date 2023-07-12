package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class OrderedMapOfIntInt {
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
   private int[] myValues;
   private int[] myOldValues;
   private boolean[] mySecondElements;
   private boolean[] myOldSecondElements;
   private int mySlots;
   private int myCount;
   private int mySizeExp;

   public OrderedMapOfIntInt(int size) {
      super();
      this.mySizeExp = 0;

      while(SIZES[this.mySizeExp] < size * LOAD_FACTOR) {
         ++this.mySizeExp;
      }

      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public OrderedMapOfIntInt() {
      super();
      this.mySizeExp = 0;
      this.myKeys = new int[SIZES[this.mySizeExp]];
      this.myValues = new int[SIZES[this.mySizeExp]];
      this.mySlots = 0;
      this.myCount = 0;
   }

   public OrderedMapOfIntInt(@NonNull StoreInputStream stream) throws IOException {
      super();
      this.myCount = stream.readInt();
      this.mySlots = stream.readInt();
      this.mySizeExp = stream.readInt();
      this.myKeys = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myKeys.length; ++i) {
         this.myKeys[i] = stream.readInt();
      }

      this.myValues = new int[SIZES[this.mySizeExp]];

      for(int i = 0; i < this.myValues.length; ++i) {
         this.myValues[i] = stream.readInt();
      }

      if (stream.readBoolean()) {
         this.mySecondElements = new boolean[SIZES[this.mySizeExp]];

         for(int i = 0; i < this.mySecondElements.length; ++i) {
            this.mySecondElements[i] = stream.readBoolean();
         }
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      stream.writeInt(this.myCount);
      stream.writeInt(this.mySlots);
      stream.writeInt(this.mySizeExp);

      for (int key : this.myKeys) {
         stream.writeInt(key);
      }

      for (int value : this.myValues) {
         stream.writeInt(value);
      }

      stream.writeBoolean(this.mySecondElements != null);
      if (this.mySecondElements != null) {
         for (boolean secondelement : this.mySecondElements) {
            stream.writeBoolean(secondelement);
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

         if (this.mySecondElements != null) {
            Arrays.fill(this.mySecondElements, false);
         }

         this.mySlots = 0;
         this.myCount = 0;
      }
   }

   public void insert(@NonNull OrderedMapOfIntInt map) {
      for(int i = 0; i < map.myKeys.length; ++i) {
         int key = map.myKeys[i];
         int value = map.myValues[i];
         if (key == Integer.MAX_VALUE) {
            this.insert(0, value);
         } else if (key != 0 && key != Integer.MIN_VALUE) {
            this.insert(key, value);
         }
      }
   }

   public void put(int key, int value) {
      this.remove(key);
      this.insert(key, value);
   }

   public void insert(int key, int value) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;
      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      boolean second = false;

      for(int curKey = this.myKeys[index]; curKey != 0; curKey = this.myKeys[index]) {
         if (curKey == key) {
            if (this.myValues[index] == value) {
               return;
            }

            second = true;
         }

         index = (index + step) % this.myKeys.length;
      }

      this.myKeys[index] = key;
      this.myValues[index] = value;
      if (second) {
         if (this.mySecondElements == null) {
            this.mySecondElements = new boolean[this.myKeys.length];
         }

         this.mySecondElements[index] = true;
      }

      ++this.myCount;
      ++this.mySlots;
      if (this.mySlots * LOAD_FACTOR > this.myKeys.length) {
         this.rehash();
      }
   }

   public boolean contains(int key, int value) {
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

   public int get(int key) {
      if (key == 0) {
         key = Integer.MAX_VALUE;
      }

      int step = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.myKeys.length;

      for(int curKey = this.myKeys[index]; curKey != key; curKey = this.myKeys[index]) {
         if (curKey == 0) {
            return -1;
         }

         index = (index + step) % this.myKeys.length;
      }

      return this.myValues[index];
   }

   private void rehash() {
      int[] keys;
      int[] values;
      boolean[] secondElements;
      if (this.myCount * LOAD_FACTOR > this.myKeys.length) {
         ++this.mySizeExp;
         this.myOldKeys = null;
         this.myOldValues = null;
         this.myOldSecondElements = null;
         keys = new int[SIZES[this.mySizeExp]];
         values = new int[SIZES[this.mySizeExp]];
         if (this.mySecondElements != null) {
            secondElements = new boolean[SIZES[this.mySizeExp]];
         } else {
            secondElements = null;
         }
      } else if (this.myOldKeys != null && this.myOldKeys.length == this.myKeys.length) {
         keys = this.myOldKeys;

         Arrays.fill(keys, 0);

         if (this.myOldSecondElements != null) {
            secondElements = this.myOldSecondElements;

            Arrays.fill(secondElements, false);
         } else {
            secondElements = null;
         }

         values = this.myOldValues;
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
         this.myOldSecondElements = this.mySecondElements;
      } else {
         this.myOldKeys = this.myKeys;
         this.myOldValues = this.myValues;
         this.myOldSecondElements = this.mySecondElements;
         keys = new int[SIZES[this.mySizeExp]];
         values = new int[SIZES[this.mySizeExp]];
         if (this.mySecondElements != null) {
            secondElements = new boolean[SIZES[this.mySizeExp]];
         } else {
            secondElements = null;
         }
      }

      int slots = 0;
      if (this.mySecondElements == null) {
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
      } else {
         for(int i = 0; i < this.myKeys.length; ++i) {
            int key = this.myKeys[i];
            if (key != 0 && key != Integer.MIN_VALUE) {
               int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
               int index = (key & Integer.MAX_VALUE) % keys.length;
               if (!this.mySecondElements[i]) {
                  while(keys[index] != 0) {
                     index = (index + step) % keys.length;
                  }

                  keys[index] = key;
                  values[index] = this.myValues[i];
                  ++slots;
                  int oldstep = (key & Integer.MAX_VALUE) % (this.myKeys.length - 2) + 1;

                  for(int oldindex = (key & Integer.MAX_VALUE) % this.myKeys.length; this.myKeys[oldindex] != 0; oldindex = (oldindex + oldstep) % this.myKeys.length) {
                     if (this.myKeys[oldindex] == key && oldindex != i) {
                        while(keys[index] != 0) {
                           index = (index + step) % keys.length;
                        }

                        keys[index] = key;
                        values[index] = this.myValues[oldindex];
                        if (secondElements != null) {
                           secondElements[index] = true;
                        }

                        ++slots;
                     }
                  }
               }
            }
         }
      }

      this.mySecondElements = secondElements;
      this.myKeys = keys;
      this.myValues = values;
      this.mySlots = slots;
   }

   public int size() {
      return this.myCount;
   }

   public class Iterator {
      private int index;
      private int step;
      private int newIndex;
      private int newStep;
      private int key;
      private int value;
      private int theKey;

      private Iterator() {
         super();
         if (OrderedMapOfIntInt.this.mySecondElements == null) {
            this.index = 0;
            this.step = -1;
         } else {
            this.index = 0;
            this.step = 0;
            this.newStep = 0;
         }
      }

      public void init() {
         if (OrderedMapOfIntInt.this.mySecondElements == null) {
            this.index = 0;
            this.step = -1;
         } else {
            this.index = 0;
            this.step = 0;
            this.newStep = 0;
         }
      }

      public void init(int key) {
         if (key == 0) {
            key = Integer.MAX_VALUE;
         }

         this.theKey = key;
         this.step = (key & Integer.MAX_VALUE) % (OrderedMapOfIntInt.this.myKeys.length - 2) + 1;
         this.index = (key & Integer.MAX_VALUE) % OrderedMapOfIntInt.this.myKeys.length;
      }

      public boolean hasMoreElements() {
         if (this.step == 0) {
            if (this.newStep == 0) {
               while(this.index < OrderedMapOfIntInt.this.myKeys.length) {
                  this.key = OrderedMapOfIntInt.this.myKeys[this.index];
                  if (!OrderedMapOfIntInt.this.mySecondElements[this.index] && this.key != 0 && this.key != Integer.MIN_VALUE) {
                     this.newStep = (this.key & Integer.MAX_VALUE) % (OrderedMapOfIntInt.this.myKeys.length - 2) + 1;
                     this.newIndex = (this.key & Integer.MAX_VALUE) % OrderedMapOfIntInt.this.myKeys.length;
                     this.theKey = this.key;
                     ++this.index;
                     return this.hasMoreElements();
                  }

                  ++this.index;
               }

               return false;
            } else {
               while(true) {
                  this.key = OrderedMapOfIntInt.this.myKeys[this.newIndex];
                  if (this.key == 0) {
                     this.newStep = 0;
                     return this.hasMoreElements();
                  }

                  if (this.key == this.theKey) {
                     if (this.key == Integer.MAX_VALUE) {
                        this.key = 0;
                     }

                     this.value = OrderedMapOfIntInt.this.myValues[this.newIndex];
                     this.newIndex = (this.newIndex + this.newStep) % OrderedMapOfIntInt.this.myKeys.length;
                     return true;
                  }

                  this.newIndex = (this.newIndex + this.newStep) % OrderedMapOfIntInt.this.myKeys.length;
               }
            }
         } else if (this.step == -1) {
            while(this.index < OrderedMapOfIntInt.this.myKeys.length) {
               this.key = OrderedMapOfIntInt.this.myKeys[this.index];
               if (this.key != 0 && this.key != Integer.MIN_VALUE) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = OrderedMapOfIntInt.this.myValues[this.index];
                  ++this.index;
                  return true;
               }

               ++this.index;
            }

            return false;
         } else {
            while(true) {
               this.key = OrderedMapOfIntInt.this.myKeys[this.index];
               if (this.key == 0) {
                  return false;
               }

               if (this.key == this.theKey) {
                  if (this.key == Integer.MAX_VALUE) {
                     this.key = 0;
                  }

                  this.value = OrderedMapOfIntInt.this.myValues[this.index];
                  this.index = (this.index + this.step) % OrderedMapOfIntInt.this.myKeys.length;
                  return true;
               }

               this.index = (this.index + this.step) % OrderedMapOfIntInt.this.myKeys.length;
            }
         }
      }

      public int nextKey() {
         return this.key;
      }

      public int nextValue() {
         return this.value;
      }
   }
}
