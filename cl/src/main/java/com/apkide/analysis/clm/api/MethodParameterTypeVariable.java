package com.apkide.analysis.clm.api;

import static com.apkide.analysis.clm.api.TypeSemantic.UNKNOWN_SEMANTIC;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.IOException;

public final class MethodParameterTypeVariable extends Type {
   private final EntitySpace mySpace;
   private int myEntity;
   private MethodParameterType myMethodParameterType;

   public MethodParameterTypeVariable(EntitySpace space) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
   }

   public MethodParameterTypeVariable(EntitySpace space, MethodParameterType methodParameterType) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
      this.myEntity = space.declareEntity(this);
      this.myMethodParameterType = methodParameterType;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.myEntity = stream.readInt();
      this.myMethodParameterType = (MethodParameterType)this.mySpace.getEntity(stream.readInt());
   }

   @Override
   public int getID() {
      return this.myEntity;
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.myEntity);
      stream.writeInt(this.mySpace.getID(this.myMethodParameterType));
   }

   @Override
   public Language getLanguage() {
      return this.getMethodParameterType().getLanguage();
   }

   public MethodParameterType getMethodParameterType() {
      return this.myMethodParameterType;
   }
}
