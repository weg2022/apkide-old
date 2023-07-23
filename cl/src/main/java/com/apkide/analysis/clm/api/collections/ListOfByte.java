package com.apkide.analysis.clm.api.collections;

import java.io.IOException;

public class ListOfByte {
   private byte[] values;
   private int size;

   public ListOfByte() {
      this.values = null;
      this.size = 0;
   }

   public ListOfByte(int size) {
      this.values = new byte[size];
      this.size = size;
   }

   public ListOfByte(StoreInputStream stream) throws IOException {
      this.size = stream.readInt();
      if (this.size > 0) {
         this.values = new byte[this.size];

         for(int i = 0; i < this.values.length; ++i) {
            this.values[i] = stream.readByte();
         }
      }
   }

   public void store(StoreOutputStream stream) throws IOException {
      if (this.values == null) {
         stream.writeInt(0);
      } else {
         stream.writeInt(this.size);

         for(int i = 0; i < this.size; ++i) {
            stream.writeInt(this.values[i]);
         }
      }
   }

   public void clear() {
      this.size = 0;
   }

   public void push(byte value) {
      this.add(value);
   }

   public int[] toIntArray() {
      int[] a = new int[this.size()];
      if (this.size() > 0) {
         System.arraycopy(this.values, 0, a, 0, this.size());
      }

      return a;
   }

   public void add(byte value) {
      if (this.values == null) {
         this.values = new byte[10];
      } else if (this.size >= this.values.length) {
         byte[] values = new byte[this.values.length * 2 + 1];
         System.arraycopy(this.values, 0, values, 0, this.values.length);
         this.values = values;
      }

      this.values[this.size++] = value;
   }

   public byte pop() {
      return this.size == 0 ? -1 : this.values[--this.size];
   }

   public byte peek() {
      return this.size == 0 ? -1 : this.get(this.size - 1);
   }

   public byte get(int index) {
      if (this.values == null) {
         return -1;
      } else if (index >= this.values.length) {
         return -1;
      } else {
         return index >= this.size ? -1 : this.values[index];
      }
   }

   public void setSize(int size) {
      this.size = size;
   }

   public void set(int index, byte value) {
      if (this.values == null) {
         this.values = new byte[Math.max(10, index + 1)];
      } else if (index >= this.values.length) {
         byte[] values = new byte[Math.max(index + 1, this.values.length * 2 + 1)];
         System.arraycopy(this.values, 0, values, 0, this.values.length);
         this.values = values;
      }

      if (index >= this.size) {
         this.size = index + 1;
      }

      this.values[index] = value;
   }

   public void remove(int index) {
      if (this.values != null) {
         if (index < this.size) {
            System.arraycopy(this.values, index + 1, this.values, index, this.size - index - 1);
            --this.size;
         }
      }
   }

   public int size() {
      return this.size;
   }

   public int firstindexof(int value) {
      return this.firstindexof(value, 0);
   }

   public int firstindexof(int value, int fromIndex) {
      if (this.values != null) {
         for (int i = fromIndex; i < this.size; ++i) {
            if (this.values[i] == value) {
               return i;
            }
         }

      }
      return -1;
   }

   public void sort() {
      this.sort(0, this.size - 1);
   }

   private void sort(int i, int j) {
      if (i < j) {
         int oldi = i;
         int oldj = j;
         int z = i + (j - i) / 2;
         byte pivot = this.values[z];

         while(i <= j) {
            while(this.values[i] < pivot) {
               ++i;
            }

            while(pivot < this.values[j]) {
               --j;
            }

            if (i <= j) {
               byte temp = this.values[j];
               this.values[j] = this.values[i];
               this.values[i] = temp;
               ++i;
               --j;
            }
         }

         this.sort(oldi, j);
         this.sort(i, oldj);
      }
   }
}
