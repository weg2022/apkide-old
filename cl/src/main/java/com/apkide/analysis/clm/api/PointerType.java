package com.apkide.analysis.clm.api;

import static com.apkide.analysis.clm.api.TypeSemantic.*;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.IOException;

public final class PointerType extends Type {
   private final EntitySpace mySpace;
   private int myID;
   private Type myType;

   protected PointerType(EntitySpace space) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
   }

   protected PointerType(EntitySpace space, Type type) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
      this.myID = space.declareEntity(this);
      this.myType = type;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.myID = stream.readInt();
      this.myType = (Type)this.mySpace.getEntity(stream.readInt());
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.myID);
      stream.writeInt(this.mySpace.getID(this.myType));
   }

   public Type getType() {
      return this.myType;
   }

   @Override
   public int getID() {
      return this.myID;
   }

   @Override
   public Language getLanguage() {
      return this.getType().getLanguage();
   }
}
