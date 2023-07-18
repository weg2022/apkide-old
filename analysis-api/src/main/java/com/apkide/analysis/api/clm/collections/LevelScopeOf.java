package com.apkide.analysis.api.clm.collections;


import static java.lang.System.arraycopy;

import com.apkide.analysis.api.clm.Entity;
import com.apkide.analysis.api.clm.EntitySpace;

import java.util.Arrays;

public class LevelScopeOf<E extends Entity> {
   public final Iterator DEFAULT_ITERATOR = new Iterator();
   private final EntitySpace space;
   private int[] identifierHashTable = new int[3000];
   private int[] bindingHashTable = new int[3000];
   private int[] scopeHashTable = new int[3000];
   private int[] identifierStack = new int[1000];
   private int[] levelStack = new int[1000];
   private int[] bindingStack = new int[1000];
   private int[] slotStack = new int[1000];
   private int stackPos = 0;

   public LevelScopeOf(EntitySpace space) {
      this.space = space;
   }

   public void declare(int identifier, E entity, int level) {
      if (this.identifierHashTable.length < this.stackPos * 3)
         rehash();

      if (this.identifierStack.length <= this.stackPos)
         resize();

      this.identifierStack[this.stackPos] = identifier;
      this.levelStack[this.stackPos] = level;
      this.bindingStack[this.stackPos] = this.space.getID(entity);
      ++this.stackPos;
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;

      while(identifierHashTable[index] != 0) {
         if (++index >= identifierHashTableLength)
            index = 0;
      }

      identifierHashTable[index] = identifier;
      this.bindingHashTable[index] = this.space.getID(entity);
      this.scopeHashTable[index] = level;
   }

   public E get(int identifier, int level) {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;
      int bi = -1;

      int id = identifierHashTable[index];
      if (id == identifier && this.scopeHashTable[index] == level)
         bi = this.bindingHashTable[index];


      if (++index >= identifierHashTableLength)
         index = 0;

      while (id != 0) {
         id = identifierHashTable[index];
         if (id == identifier && this.scopeHashTable[index] == level)
            bi = this.bindingHashTable[index];

         if (++index >= identifierHashTableLength)
            index = 0;
      }

      return (E)this.space.getEntity(bi);
   }

   public boolean contains(int identifier, int scope) {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;

      int id = identifierHashTable[index];
      if (id == identifier && this.scopeHashTable[index] == scope)
         return true;

      if (++index >= identifierHashTableLength)
         index = 0;

      while (id != 0) {
         id = identifierHashTable[index];
         if (id == identifier && this.scopeHashTable[index] == scope)
            return true;

         if (++index >= identifierHashTableLength)
            index = 0;
      }

      return false;
   }

   public void clear() {
      if (this.stackPos > 0)
         Arrays.fill(this.identifierHashTable, 0);

      this.stackPos = 0;
   }

   public void enterBlock() {
      if (this.identifierStack.length <= this.stackPos)
         resize();

      this.identifierStack[this.stackPos] = 0;
      ++this.stackPos;
   }

   public void enterClass() {
      if (this.identifierStack.length <= this.stackPos)
         resize();

      this.identifierStack[this.stackPos] = 0;
      ++this.stackPos;
   }

   public void leaveBlock() {
      int[] identifierHashTable = this.identifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      --this.stackPos;

      for(int identifier = this.identifierStack[this.stackPos]; identifier != 0;
          identifier = this.identifierStack[this.stackPos]) {
         int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;
         int i = -1;

         int id = identifierHashTable[index];
         if (id == identifier)
            i = index;

         if (++index >= identifierHashTableLength)
            index = 0;

         while (id != 0) {
            id = identifierHashTable[index];
            if (id == identifier)
               i = index;

            if (++index >= identifierHashTableLength)
               index = 0;
         }

         if (i != -1)
            identifierHashTable[i] = 0;

         --this.stackPos;
      }
   }

   private void resize() {
      int[] newidentifierStack = new int[this.identifierStack.length * 2 + 1];
      arraycopy(this.identifierStack, 0, newidentifierStack, 0, this.identifierStack.length);
      this.identifierStack = newidentifierStack;
      int[] newscopeStack = new int[this.levelStack.length * 2 + 1];
      arraycopy(this.levelStack, 0, newscopeStack, 0, this.levelStack.length);
      this.levelStack = newscopeStack;
      int[] newbindingStack = new int[this.bindingStack.length * 2 + 1];
      arraycopy(this.bindingStack, 0, newbindingStack, 0, this.bindingStack.length);
      this.bindingStack = newbindingStack;
      int[] newslotStack = new int[this.slotStack.length * 2 + 1];
      arraycopy(this.slotStack, 0, newslotStack, 0, this.slotStack.length);
      this.slotStack = newslotStack;
   }

   private void rehash() {
      int[] newidentifierHashTable = new int[this.identifierHashTable.length * 2 + 1];
      int[] newbindingHashTable = new int[this.identifierHashTable.length * 2 + 1];
      int[] newscopeHashTable = new int[this.identifierHashTable.length * 2 + 1];

      for(int i = 0; i < this.stackPos; ++i) {
         int identifier = this.identifierStack[i];
         if (identifier != 0) {
            int scope = this.levelStack[i];
            int binding = this.bindingStack[i];
            int index = (identifier & Integer.MAX_VALUE) % newidentifierHashTable.length;

            while(newidentifierHashTable[index] != 0) {
               if (++index >= newidentifierHashTable.length)
                  index = 0;
            }

            newidentifierHashTable[index] = identifier;
            newbindingHashTable[index] = binding;
            newscopeHashTable[index] = scope;
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
      private int level;

      private Iterator() {
      }

      public void init(int level) {
         this.level = level;
         this.pos = LevelScopeOf.this.stackPos - 1;
      }

      public boolean hasMoreElements() {
         if (this.pos < 0) {
            return false;
         } else {
            while(this.pos >= 0) {
               this.key = LevelScopeOf.this.identifierStack[this.pos];
               this.value = LevelScopeOf.this.bindingStack[this.pos];
               int l = LevelScopeOf.this.levelStack[this.pos];
               --this.pos;
               if (this.key != 0 && this.level == l)
                  return true;
            }

            return false;
         }
      }

      public int nextKey() {
         return this.key;
      }

      public E nextValue() {
         return (E)LevelScopeOf.this.space.getEntity(this.value);
      }
   }
}
