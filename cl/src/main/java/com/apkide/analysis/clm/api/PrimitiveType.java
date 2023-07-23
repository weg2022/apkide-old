package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.IOException;

public final class PrimitiveType extends Type {
   private final FileSpace myFileSpace;
   private final EntitySpace mySpace;
   private int myEntity;
   private int myLanguageID;

   protected PrimitiveType(EntitySpace space, FileSpace fileSpace, int semantic, Language language) {
      super(space, semantic);
      this.myFileSpace = fileSpace;
      this.mySpace = space;
      this.myLanguageID = fileSpace.getLanguageID(language);
      this.myEntity = space.declareEntity(this);
   }

   protected PrimitiveType(EntitySpace space, FileSpace filespace) {
      super(space);
      this.myFileSpace = filespace;
      this.mySpace = space;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.myEntity = stream.readInt();
      this.myLanguageID = stream.readInt();
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.myEntity);
      stream.writeInt(this.myLanguageID);
   }

   @Override
   public int getID() {
      return this.myEntity;
   }

   @Override
   public Language getLanguage() {
      return this.myFileSpace.getLanguage(this.myLanguageID);
   }
}
