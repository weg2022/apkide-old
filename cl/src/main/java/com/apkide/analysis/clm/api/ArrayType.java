package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.IOException;

public final class ArrayType extends Type {
   private final EntitySpace mySpace;
   private int myId;
   private Type myElementType;
   private int myDimension;

   protected ArrayType(EntitySpace space) {
      super(space, 16);
      this.mySpace = space;
   }

   protected ArrayType(EntitySpace space, Type elementType, int dimension) {
      super(space, 16);
      this.mySpace = space;
      this.myId = space.declareEntity(this);
      this.myElementType = elementType;
      this.myDimension = dimension;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.myId = stream.readInt();
      this.myElementType = (Type)this.mySpace.getEntity(stream.readInt());
      this.myDimension = stream.readInt();
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.myId);
      stream.writeInt(this.mySpace.getID(this.myElementType));
      stream.writeInt(this.myDimension);
   }

   public Type getComponentType() {
      Type type = this.getElementType();

      while(type.isArrayType()) {
         type = ((ArrayType)type).getElementType();
      }

      return type;
   }

   public int getDimension() {
      return this.myDimension;
   }

   public Type getElementType() {
      return this.myElementType;
   }

   @Override
   public Language getLanguage() {
      return this.getElementType().getLanguage();
   }

   @Override
   public int getID() {
      return this.myId;
   }
}
