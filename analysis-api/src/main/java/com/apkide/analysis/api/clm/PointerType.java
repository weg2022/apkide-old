package com.apkide.analysis.api.clm;


import static com.apkide.analysis.api.clm.TypeSemantic.UNKNOWN_SEMANTIC;

import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class PointerType extends Type {
   private EntitySpace space;
   private int id;
   private Type type;

   protected PointerType(FileSpace fileSpace, EntitySpace space) {
      super(fileSpace,space, UNKNOWN_SEMANTIC);
      this.space = space;
   }

   protected PointerType(FileSpace fileSpace, EntitySpace space, Type type) {
      super(fileSpace,space, UNKNOWN_SEMANTIC);
      this.space = space;
      this.id = space.declareEntity(this);
      this.type = type;
   }

   @Override
   protected void load(@NonNull StoreInputStream stream) throws IOException {
      super.load(stream);
      this.id = stream.readInt();
      this.type = (Type)this.space.getEntity(stream.readInt());
   }

   @Override
   protected void store(@NonNull StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.id);
      stream.writeInt(this.space.getID(this.type));
   }

   public Type getType() {
      return this.type;
   }

   @Override
   public int getID() {
      return this.id;
   }

   @Override
   public Language getLanguage() {
      return this.getType().getLanguage();
   }
}
