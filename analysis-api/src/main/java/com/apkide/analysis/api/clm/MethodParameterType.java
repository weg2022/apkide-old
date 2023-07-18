package com.apkide.analysis.api.clm;


import androidx.annotation.NonNull;

import com.apkide.analysis.api.clm.collections.ListOf;
import com.apkide.analysis.api.clm.excpetions.UnknownEntityException;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class MethodParameterType extends Type {
   private int id;
   private final EntitySpace space;
   private final IdentifierSpace identifiers;
   private final FileSpace filespace;
   private FileEntry file;
   private int declarationNumber;
   private int line;
   private int startcolumn;
   private int endcolumn;
   private int identifier;
   private Member method;
   private int number;
   private ListOf<Type> boundtypes;
   private boolean typesloaded;

   protected MethodParameterType(EntitySpace space, IdentifierSpace identifiers, FileSpace filespace) {
      super(filespace,space, 16);
      this.space = space;
      this.identifiers = identifiers;
      this.filespace = filespace;
   }

   protected MethodParameterType(EntitySpace space, IdentifierSpace identifiers, FileSpace filespace, FileEntry file, int declarationNumber) {
      super(filespace,space, 16);
      this.space = space;
      this.identifiers = identifiers;
      this.filespace = filespace;
      this.id = space.declareEntity(this);
      this.file = file;
      this.declarationNumber = declarationNumber;
      this.boundtypes = null;
      this.typesloaded = false;
   }

   @Override
   protected void load(@NonNull StoreInputStream stream) throws IOException {
      super.load(stream);
      this.line = stream.readInt();
      this.startcolumn = stream.readInt();
      this.endcolumn = stream.readInt();
      this.id = stream.readInt();
      this.file = this.filespace.getFileEntry(stream.readInt());
      this.declarationNumber = stream.readInt();
      this.identifier = stream.readInt();
      this.method = (Member)this.space.getEntity(stream.readInt());
      this.number = stream.readInt();
      this.typesloaded = stream.readBoolean();
      if (stream.readBoolean()) {
         this.boundtypes = new ListOf<>(this.space, stream);
      }
   }

   @Override
   protected void store(@NonNull StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.line);
      stream.writeInt(this.startcolumn);
      stream.writeInt(this.endcolumn);
      stream.writeInt(this.id);
      stream.writeInt(this.file.getID());
      stream.writeInt(this.declarationNumber);
      stream.writeInt(this.identifier);
      stream.writeInt(this.space.getID(this.method));
      stream.writeInt(this.number);
      stream.writeBoolean(this.typesloaded);
      stream.writeBoolean(this.boundtypes != null);
      if (this.boundtypes != null) {
         this.boundtypes.store(stream);
      }
   }

   protected void invalidate() {
      if (this.boundtypes != null) {
         this.boundtypes.clear();
      }

      this.typesloaded = false;
   }

   protected void declareBoundtype(Type boundtype) {
      this.boundtypes.add(boundtype);
      this.typesloaded = true;
   }

   protected void declarePositions(int line, int startcolumn, int endcolumn) {
      this.line = line;
      this.startcolumn = startcolumn;
      this.endcolumn = endcolumn;
   }

   protected void declareSyntax(int identifier, Member method, int number) {
      this.identifier = identifier;
      this.method = method;
      this.number = number;
      if (this.boundtypes == null) {
         this.boundtypes = new ListOf<>(this.space);
      } else {
         this.boundtypes.clear();
      }
   }

   @Override
   public int getIdentifierLine() {
      this.space.loadPositions(this.file);
      return this.line;
   }

   @Override
   public int getIdentifierEndColumn() {
      this.space.loadPositions(this.file);
      return this.endcolumn;
   }

   @Override
   public int getIdentifierStartColumn() {
      this.space.loadPositions(this.file);
      return this.startcolumn;
   }

   public Member getMethod() {
      return this.method;
   }

   public int getNumber() {
      return this.number;
   }

   @Override
   public FileEntry getFile() {
      return this.file;
   }

   @Override
   public int getDeclarationNumber() {
      return this.declarationNumber;
   }

   public int getBoundTypeCount() {
      this.loadBounds();
      return this.boundtypes != null && this.boundtypes.size() != 0 ? this.boundtypes.size() : 1;
   }

   public Type getBoundType(Type type, int number) throws UnknownEntityException {
      Type membertype = this.getBoundType(number);
      if (type.isClassType()) {
         Type containingtype = ((ClassType)type).getContainingTypeOfMember(this.method);
         return containingtype.isParameterizedType() ? ((ParameterizedType)containingtype).replaceType(membertype) : membertype;
      } else {
         Type containingtype = ((ParameterizedType)type).getClassType().getContainingTypeOfMember(this.method);
         return !containingtype.isParameterizedType()
            ? ((ParameterizedType)type).replaceType(membertype)
            : ((ParameterizedType)type).replaceType(((ParameterizedType)containingtype).replaceType(membertype));
      }
   }

   public Type getBoundType(int number) throws UnknownEntityException {
      this.loadBounds();
      return (Type)(this.boundtypes != null && this.boundtypes.size() > 0 ? this.boundtypes.get(number) : this.space.getRootClasstype(this.file));
   }

   @Override
   public Type getErasedType() throws UnknownEntityException {
      this.loadBounds();
      return (Type)(this.boundtypes != null && this.boundtypes.size() > 0 ? this.boundtypes.get(0) : this.space.getRootClasstype(this.file));
   }

   @Override
   public ClassType getEnclosingClassType() {
      return this.method.getEnclosingClassType();
   }

   @Override
   public ClassType getEnclosingTopLevelClassType() {
      return this.method.getEnclosingClassType().getEnclosingTopLevelClassType();
   }

   @Override
   public int getIdentifier() {
      return this.identifier;
   }

   @Override
   public int getID() {
      return this.id;
   }

   private void loadBounds() {
      if (!this.typesloaded) {
         this.typesloaded = true;
         if (getLanguage().getTypeSystem().inheritsBoundTypes() && this.method.getOverriddenMembers().size() > 0) {
            try {
               Member omethod = this.method.getOverriddenMembers().get();
               int count = omethod.getMethodParametertypeCount();

               for(int number = 0; number < count; ++number) {
                  MethodParameterType omethodparametertype = omethod.getMethodParametertype(number);
                  int identifier = omethodparametertype.getIdentifier();
                  MethodParameterType methodparametertype = this.method.accessParametertype(identifier);
                  int boundcount = omethodparametertype.getBoundTypeCount();

                  for(int j = 0; j < boundcount; ++j) {
                     Type boundtype = omethodparametertype.getBoundType(this.method.getEnclosingClassType().parameterizeCanonical(), j);
                     this.space.declareBoundtype(methodparametertype, boundtype);
                  }
               }
            } catch (UnknownEntityException ignored) {
            }
         } else {
            this.space.loadTypes(this.file);
         }
      }
   }
}
