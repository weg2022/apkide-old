package com.apkide.analysis.api.clm;


import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class PrimitiveType extends Type {
   private final FileSpace filespace;
   private final EntitySpace space;
   private int entity;
   private int languageID;

   protected PrimitiveType(EntitySpace space, FileSpace filespace, int semantic, Language language) {
      super(filespace,space, semantic);
      this.filespace = filespace;
      this.space = space;
      this.languageID = filespace.getLanguageID(language);
      this.entity = space.declareEntity(this);
   }

   protected PrimitiveType(EntitySpace space, FileSpace filespace) {
      super(filespace,space);
      this.filespace = filespace;
      this.space = space;
   }

   @Override
   protected void load(@NonNull StoreInputStream stream) throws IOException {
      super.load(stream);
      this.entity = stream.readInt();
      this.languageID = stream.readInt();
   }

   @Override
   protected void store(@NonNull StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.entity);
      stream.writeInt(this.languageID);
   }

   @Override
   public int getID() {
      return this.entity;
   }

   @Override
   public Language getLanguage() {
      return this.filespace.getLanguage(this.languageID);
   }
}
