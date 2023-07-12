package com.apkide.common.collections;

import java.util.Arrays;

public class LevelScopeOfInt {
   private int[] myIdentifierHashTable = new int[3000];
   private int[] myBindingHashTable = new int[3000];
   private int[] myScopeHashTable = new int[3000];
   private int[] myIdentifierStack = new int[1000];
   private int[] myLevelStack = new int[1000];
   private int[] myBindingStack = new int[1000];
   private int[] mySlotStack = new int[1000];
   private int myStackPos = 0;

   public LevelScopeOfInt() {
      super();
   }

   public void declare(int identifier, int binding, int level) {
      if (this.myIdentifierHashTable.length < this.myStackPos * 3) {
         this.rehash();
      }

      if (this.myIdentifierStack.length <= this.myStackPos) {
         this.resize();
      }

      this.myIdentifierStack[this.myStackPos] = identifier;
      this.myLevelStack[this.myStackPos] = level;
      this.myBindingStack[this.myStackPos] = binding;
      ++this.myStackPos;
      int[] identifierHashTable = this.myIdentifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;

      while(identifierHashTable[index] != 0) {
         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      }

      identifierHashTable[index] = identifier;
      this.myBindingHashTable[index] = binding;
      this.myScopeHashTable[index] = level;
   }

   public int get(int identifier, int level) {
      int[] identifierHashTable = this.myIdentifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;
      int bi = -1;

      int id = identifierHashTable[index];
      if (id == identifier && this.myScopeHashTable[index] == level) {
         bi = this.myBindingHashTable[index];
      }

      if (++index >= identifierHashTableLength) {
         index = 0;
      }
      while (id != 0) {
         id = identifierHashTable[index];
         if (id == identifier && this.myScopeHashTable[index] == level) {
            bi = this.myBindingHashTable[index];
         }

         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      }

      return bi;
   }

   public boolean contains(int identifier, int scope) {
      int[] identifierHashTable = this.myIdentifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;

      int id = identifierHashTable[index];
      if (id == identifier && this.myScopeHashTable[index] == scope) {
         return true;
      }

      if (++index >= identifierHashTableLength) {
         index = 0;
      }
      while (id != 0) {
         id = identifierHashTable[index];
         if (id == identifier && this.myScopeHashTable[index] == scope) {
            return true;
         }

         if (++index >= identifierHashTableLength) {
            index = 0;
         }
      }

      return false;
   }

   public void clear() {
      if (this.myStackPos > 0) {
         Arrays.fill(this.myIdentifierHashTable, 0);
      }

      this.myStackPos = 0;
   }

   public void enterBlock() {
      if (this.myIdentifierStack.length <= this.myStackPos) {
         this.resize();
      }

      this.myIdentifierStack[this.myStackPos] = 0;
      ++this.myStackPos;
   }

   public void enterClass() {
      if (this.myIdentifierStack.length <= this.myStackPos) {
         this.resize();
      }

      this.myIdentifierStack[this.myStackPos] = 0;
      ++this.myStackPos;
   }

   public void leaveBlock() {
      int[] identifierHashTable = this.myIdentifierHashTable;
      int identifierHashTableLength = identifierHashTable.length;
      --this.myStackPos;

      for(int identifier = this.myIdentifierStack[this.myStackPos]; identifier != 0; identifier = this.myIdentifierStack[this.myStackPos]) {
         int index = (identifier & Integer.MAX_VALUE) % identifierHashTableLength;
         int i = -1;

         int id = identifierHashTable[index];
         if (id == identifier) {
            i = index;
         }

         if (++index >= identifierHashTableLength) {
            index = 0;
         }
         while (id != 0) {
            id = identifierHashTable[index];
            if (id == identifier) {
               i = index;
            }

            if (++index >= identifierHashTableLength) {
               index = 0;
            }
         }

         if (i != -1) {
            identifierHashTable[i] = 0;
         }

         --this.myStackPos;
      }
   }

   private void resize() {
      int[] newIdentifierStack = new int[this.myIdentifierStack.length * 2 + 1];
      System.arraycopy(this.myIdentifierStack, 0, newIdentifierStack, 0, this.myIdentifierStack.length);
      this.myIdentifierStack = newIdentifierStack;
      int[] newScopeStack = new int[this.myLevelStack.length * 2 + 1];
      System.arraycopy(this.myLevelStack, 0, newScopeStack, 0, this.myLevelStack.length);
      this.myLevelStack = newScopeStack;
      int[] newBindingStack = new int[this.myBindingStack.length * 2 + 1];
      System.arraycopy(this.myBindingStack, 0, newBindingStack, 0, this.myBindingStack.length);
      this.myBindingStack = newBindingStack;
      int[] newSlotStack = new int[this.mySlotStack.length * 2 + 1];
      System.arraycopy(this.mySlotStack, 0, newSlotStack, 0, this.mySlotStack.length);
      this.mySlotStack = newSlotStack;
   }

   private void rehash() {
      int[] newIdentifierHashTable = new int[this.myIdentifierHashTable.length * 2 + 1];
      int[] newBindingHashTable = new int[this.myIdentifierHashTable.length * 2 + 1];
      int[] newScopeHashTable = new int[this.myIdentifierHashTable.length * 2 + 1];

      for(int i = 0; i < this.myStackPos; ++i) {
         int identifier = this.myIdentifierStack[i];
         if (identifier != 0) {
            int scope = this.myLevelStack[i];
            int binding = this.myBindingStack[i];
            int index = (identifier & Integer.MAX_VALUE) % newIdentifierHashTable.length;

            while(newIdentifierHashTable[index] != 0) {
               if (++index >= newIdentifierHashTable.length) {
                  index = 0;
               }
            }

            newIdentifierHashTable[index] = identifier;
            newBindingHashTable[index] = binding;
            newScopeHashTable[index] = scope;
         }
      }

      this.myIdentifierHashTable = newIdentifierHashTable;
      this.myScopeHashTable = newScopeHashTable;
      this.myBindingHashTable = newBindingHashTable;
   }
}
