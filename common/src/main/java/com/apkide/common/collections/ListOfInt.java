package com.apkide.common.collections;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.Comparator;

public class ListOfInt {
   private int[] myValues;
   private int mySize;

   public ListOfInt() {
      super();
      this.myValues = null;
      this.mySize = 0;
   }

   public ListOfInt(int size) {
      super();
      this.myValues = new int[size];
      this.mySize = size;
   }

   public ListOfInt(@NonNull StoreInputStream stream) throws IOException {
      super();
      this.mySize = stream.readInt();
      if (this.mySize > 0) {
         this.myValues = new int[this.mySize];

         for(int i = 0; i < this.myValues.length; ++i) {
            this.myValues[i] = stream.readInt();
         }
      }
   }

   public void store(@NonNull StoreOutputStream stream) throws IOException {
      if (this.myValues == null) {
         stream.writeInt(0);
      } else {
         stream.writeInt(this.mySize);

         for(int i = 0; i < this.mySize; ++i) {
            stream.writeInt(this.myValues[i]);
         }
      }
   }

   public void clear() {
      this.mySize = 0;
   }

   public void push(int value) {
      this.add(value);
   }

   public int[] toIntArray() {
      int[] a = new int[this.size()];
      if (this.size() > 0) {
         System.arraycopy(this.myValues, 0, a, 0, this.size());
      }

      return a;
   }

   public void add(int value) {
      if (this.myValues == null) {
         this.myValues = new int[10];
      } else if (this.mySize >= this.myValues.length) {
         int[] values = new int[this.myValues.length * 2 + 1];
         System.arraycopy(this.myValues, 0, values, 0, this.myValues.length);
         this.myValues = values;
      }

      this.myValues[this.mySize++] = value;
   }

   public int pop() {
      return this.mySize == 0 ? -1 : this.myValues[--this.mySize];
   }

   public int peek() {
      return this.mySize == 0 ? -1 : this.get(this.mySize - 1);
   }

   public int get(int index) {
      if (this.myValues == null) {
         return -1;
      } else if (index >= this.myValues.length) {
         return -1;
      } else {
         return index >= this.mySize ? -1 : this.myValues[index];
      }
   }

   public void setSize(int size) {
      this.mySize = size;
   }

   public void set(int index, int value) {
      if (this.myValues == null) {
         this.myValues = new int[Math.max(10, index + 1)];
      } else if (index >= this.myValues.length) {
         int[] values = new int[Math.max(index + 1, this.myValues.length * 2 + 1)];
         System.arraycopy(this.myValues, 0, values, 0, this.myValues.length);
         this.myValues = values;
      }

      if (index >= this.mySize) {
         this.mySize = index + 1;
      }

      this.myValues[index] = value;
   }

   public void remove(int index) {
      if (this.myValues != null) {
         if (index < this.mySize) {
            System.arraycopy(this.myValues, index + 1, this.myValues, index, this.mySize - index - 1);
            --this.mySize;
         }
      }
   }

   public int size() {
      return this.mySize;
   }

   public int firstIndexOf(int value) {
      return this.firstIndexOf(value, 0);
   }

   public int firstIndexOf(int value, int fromIndex) {
      if (this.myValues == null) {
         return -1;
      } else {
         for(int i = fromIndex; i < this.mySize; ++i) {
            if (this.myValues[i] == value) {
               return i;
            }
         }

         return -1;
      }
   }

   public void sort() {
      this.sort(0, this.mySize - 1);
   }

   private void sort(int i, int j) {
      if (i < j) {
         int oldi = i;
         int oldj = j;
         int z = i + (j - i) / 2;
         int pivot = this.myValues[z];

         while(i <= j) {
            while(this.myValues[i] < pivot) {
               ++i;
            }

            while(pivot < this.myValues[j]) {
               --j;
            }

            if (i <= j) {
               int temp = this.myValues[j];
               this.myValues[j] = this.myValues[i];
               this.myValues[i] = temp;
               ++i;
               --j;
            }
         }

         this.sort(oldi, j);
         this.sort(i, oldj);
      }
   }

   public void sort(@NonNull Comparator<Integer> comp) {
      this.sort(0, this.mySize - 1, comp);
   }

   private void sort(int i, int j, Comparator<Integer> comp) {
      if (i < j) {
         int oldi = i;
         int oldj = j;
         int z = i + (j - i) / 2;
         int pivot = this.myValues[z];

         while(i <= j) {
            while(comp.compare(this.myValues[i], pivot) < 0) {
               ++i;
            }

            while(comp.compare(pivot, this.myValues[j]) < 0) {
               --j;
            }

            if (i <= j) {
               int temp = this.myValues[j];
               this.myValues[j] = this.myValues[i];
               this.myValues[i] = temp;
               ++i;
               --j;
            }
         }

         this.sort(oldi, j, comp);
         this.sort(i, oldj, comp);
      }
   }
}
