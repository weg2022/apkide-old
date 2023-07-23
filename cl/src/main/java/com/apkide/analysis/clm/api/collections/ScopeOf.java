package com.apkide.analysis.clm.api.collections;

import com.apkide.analysis.clm.api.Entity;
import com.apkide.analysis.clm.api.EntitySpace;

import java.util.Arrays;

public class ScopeOf<E extends Entity> {
   public final Iterator DEFAULT_ITERATOR = new Iterator();
   private final EntitySpace space;
   private int[] identifierHashTable = new int[3000];
   private int[] bindingHashTable = new int[3000];
   private int[] scopeHashTable = new int[3000];
   private int[] identifierStack = new int[1000];
   private int[] bindingStack = new int[1000];
   private int[] slotStack = new int[1000];
   private int stackPos = 0;

   public ScopeOf(EntitySpace space) {
      this.space = space;
   }

   public void declare(int identifier, E binding) {
      if (this.identifierHashTable.length < this.stackPos * 3) {
         this.rehash();
      }

      if (this.identifierStack.length <= this.stackPos) {
         this.resize();
      }

      this.identifierStack[this.stackPos] = identifier;
      this.bindingStack[this.stackPos] = this.space.getID(binding);
      ++this.stackPos;
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & 2147483647) % identifierHashTableLength;

      while(identifierHashTable[index] != 0) {
         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      }

      identifierHashTable[index] = identifier;
      this.bindingHashTable[index] = this.space.getID(binding);
   }

   public E get(int identifier) {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & 2147483647) % identifierHashTableLength;
      int bi = -1;

      int id;
      do {
         id = identifierHashTable[index];
         if (id == identifier) {
            bi = this.bindingHashTable[index];
         }

         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      } while(id != 0);

      return (E)this.space.getEntity(bi);
   }

   public boolean contains(int identifier) {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & 2147483647) % identifierHashTableLength;

      int id;
      do {
         id = identifierHashTable[index];
         if (id == identifier) {
            return true;
         }

         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      } while(id != 0);

      return false;
   }

   public void clear() {
      if (this.stackPos > 0) {
         Arrays.fill(this.identifierHashTable, 0);
      }

      this.stackPos = 0;
   }

   public void enterBlock() {
      if (this.identifierStack.length <= this.stackPos) {
         this.resize();
      }

      this.identifierStack[this.stackPos] = 0;
      ++this.stackPos;
   }

   public void leaveBlock() {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      --this.stackPos;

      for(int identifier = this.identifierStack[this.stackPos]; identifier != 0; identifier = this.identifierStack[this.stackPos]) {
         int index = (identifier & 2147483647) % identifierHashTableLength;
         int i = -1;

         int id;
         do {
            id = identifierHashTable[index];
            if (id == identifier) {
               i = index;
            }

            if (++index >= identifierHashTableLength) {
               index = 0;
            }
         } while(id != 0);

         if (i != -1) {
            identifierHashTable[i] = 0;
         }

         --this.stackPos;
      }
   }

   private void resize() {
      int[] newidentifierStack = new int[this.identifierStack.length * 2 + 1];
      System.arraycopy(this.identifierStack, 0, newidentifierStack, 0, this.identifierStack.length);
      this.identifierStack = newidentifierStack;
      int[] newbindingStack = new int[this.bindingStack.length * 2 + 1];
      System.arraycopy(this.bindingStack, 0, newbindingStack, 0, this.bindingStack.length);
      this.bindingStack = newbindingStack;
      int[] newslotStack = new int[this.slotStack.length * 2 + 1];
      System.arraycopy(this.slotStack, 0, newslotStack, 0, this.slotStack.length);
      this.slotStack = newslotStack;
   }

   private void rehash() {
      int[] newidentifierHashTable = new int[this.identifierHashTable.length * 2 + 1];
      int[] newbindingHashTable = new int[this.identifierHashTable.length * 2 + 1];
      int[] newscopeHashTable = new int[this.identifierHashTable.length * 2 + 1];

      for(int i = 0; i < this.stackPos; ++i) {
         int identifier = this.identifierStack[i];
         if (identifier != 0) {
            int binding = this.bindingStack[i];
            int index = (identifier & 2147483647) % newidentifierHashTable.length;

            while(newidentifierHashTable[index] != 0) {
               if (++index >= newidentifierHashTable.length) {
                  index = 0;
               }
            }

            newidentifierHashTable[index] = identifier;
            newbindingHashTable[index] = binding;
         }
      }

      this.identifierHashTable = newidentifierHashTable;
      this.scopeHashTable = newscopeHashTable;
      this.bindingHashTable = newbindingHashTable;
   }

   public class Iterator {
      private int pos = 0;
      private int key;
      private int value;

      private Iterator() {
      }

      public void init() {
         this.pos = 0;
      }

      public boolean hasMoreElements() {
         if (this.pos < ScopeOf.this.stackPos) {
            while (this.pos < ScopeOf.this.stackPos) {
               this.key = ScopeOf.this.identifierStack[this.pos];
               this.value = ScopeOf.this.bindingStack[this.pos];
               ++this.pos;
               if (this.key != 0) {
                  return true;
               }
            }

         }
         return false;
      }

      public int nextKey() {
         return this.key;
      }

      public E nextValue() {
         return (E)ScopeOf.this.space.getEntity(this.value);
      }
   }
}
