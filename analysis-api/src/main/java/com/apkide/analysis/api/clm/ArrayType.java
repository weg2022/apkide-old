package com.apkide.analysis.api.clm;


import static com.apkide.analysis.api.clm.TypeSemantic.UNKNOWN_SEMANTIC;

import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class ArrayType extends Type {
   private final EntitySpace space;
   private int id;
   private Type elementtype;
   private int dimension;

   protected ArrayType(FileSpace fileSpace, EntitySpace space) {
      super(fileSpace,space, UNKNOWN_SEMANTIC);
      this.space = space;
   }

   protected ArrayType(FileSpace fileSpace, EntitySpace space, Type elementtype, int dimension) {
      super(fileSpace,space, UNKNOWN_SEMANTIC);
      this.space = space;
      this.id = space.declareEntity(this);
      this.elementtype = elementtype;
      this.dimension = dimension;
   }

   @Override
   protected void load(@NonNull StoreInputStream stream) throws IOException {
      super.load(stream);
      this.id = stream.readInt();
      this.elementtype = (Type)this.space.getEntity(stream.readInt());
      this.dimension = stream.readInt();
   }

   @Override
   protected void store(@NonNull StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.id);
      stream.writeInt(this.space.getID(this.elementtype));
      stream.writeInt(this.dimension);
   }

   public Type getComponenttype() {
      Type type = this.getElementType();

      while(type.isArrayType()) {
         type = ((ArrayType)type).getElementType();
      }

      return type;
   }

   public int getDimension() {
      return this.dimension;
   }

   public Type getElementType() {
      return this.elementtype;
   }

   @Override
   public Language getLanguage() {
      return this.getElementType().getLanguage();
   }

   @Override
   public int getID() {
      return this.id;
   }
}
