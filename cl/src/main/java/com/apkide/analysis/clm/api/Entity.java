package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.IOException;

public abstract class Entity {
   private final EntitySpace mySpace;

   public Entity(EntitySpace space) {
      this.mySpace = space;
   }

   protected void load(StoreInputStream stream) throws IOException {
   }

   protected void store(StoreOutputStream stream) throws IOException {
   }

   public boolean isPrivateVisibleFrom(Entity entity) {
      if (this.getFile() != entity.getFile()) {
         return false;
      } else {
         return getEnclosingClassType().isEnclosedBy(entity.getEnclosingClassType());
      }
   }

   public String getHTMLString() {
      return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getHTMLString(this) : "";
   }

   public int getIdentifier() {
      return 0;
   }

   public boolean isType() {
      return this instanceof Type;
   }

   public boolean isNamespace() {
      return this instanceof Namespace;
   }

   public boolean isMethodParameterTypeVariable() {
      return this instanceof MethodParameterTypeVariable;
   }

   public boolean isMember() {
      return this instanceof Member;
   }

   public boolean isArrayType() {
      return this instanceof ArrayType;
   }

   public boolean isPointerType() {
      return this instanceof PointerType;
   }

   public boolean isPrimitiveType() {
      return this instanceof PrimitiveType;
   }

   public boolean isClassType() {
      return this instanceof ClassType;
   }

   public boolean isParameterType() {
      return this instanceof ParameterType;
   }

   public boolean isMethodParameterType() {
      return this instanceof MethodParameterType;
   }

   public boolean isParameterizedType() {
      return this instanceof ParameterizedType;
   }

   public int getID() {
      return 0;
   }

   public int getModifiers() {
      return 0;
   }

   public boolean isPrivate() {
      return Modifiers.isJavaPrivate(this.getModifiers()) || Modifiers.isCSPrivate(this.getModifiers());
   }

   public boolean isFilePrivate() {
      return this.isPrivate();
   }

   public ClassType getEnclosingClassType() {
      return null;
   }

   public Language getLanguage() {
      return this.getFile().getLanguage();
   }

   public ClassType getEnclosingTopLevelClassType() {
      return null;
   }

   public String getDocumentationString() {
      return this.mySpace.getDocumentationString(this);
   }

   public String getHTMLDocumentationString() {
      return this.mySpace.getHTMLDocumentationString(this);
   }

   public FileEntry getFile() {
      return null;
   }

   public int getDeclarationNumber() {
      return -1;
   }

   public String getFullyQualifiedNameString() {
      return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getFullyQualifiedNameString(this) : "";
   }

   public String getNameString() {
      return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getNameString(this) : "";
   }

   public int getIdentifierLine() {
      return 0;
   }

   public int getIdentifierEndColumn() {
      return 0;
   }

   public int getIdentifierStartColumn() {
      return 0;
   }

   public int getModifiersStartLine() {
      return 0;
   }

   public int getModifiersEndLine() {
      return 0;
   }

   public int getModifiersEndColumn() {
      return 0;
   }

   public int getModifiersStartColumn() {
      return 0;
   }

   public int getEndColumn() {
      return 0;
   }

   public int getEndLine() {
      return 0;
   }

   public int getStartColumn() {
      return 0;
   }

   public int getStartLine() {
      return 0;
   }
}
