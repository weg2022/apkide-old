package com.apkide.analysis.api.clm;


import androidx.annotation.NonNull;

import com.apkide.analysis.api.clm.collections.ListOf;
import com.apkide.analysis.api.clm.excpetions.UnknownEntityException;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class ParameterType extends Type {
   private int id;
   private final EntitySpace space;
   private final IdentifierSpace identifiers;
   private final FileSpace filespace;
   private FileEntry file;
   private int declarationNumber;
   private int line;
   private int startcolumn;
   private int endcolumn;
   private boolean boundsloaded;
   private ListOf<Type> boundtypes;
   private int identifier;
   private int number;
   private ClassType classtype;

   protected ParameterType(EntitySpace space, IdentifierSpace identifiers, FileSpace filespace) {
      super(filespace,space, 16);
      this.space = space;
      this.identifiers = identifiers;
      this.filespace = filespace;
   }

   protected ParameterType(EntitySpace space, IdentifierSpace identifiers,
                           FileSpace filespace, FileEntry file, int declarationNumber) {
      super(filespace,space, 16);
      this.space = space;
      this.identifiers = identifiers;
      this.filespace = filespace;
      this.id = space.declareEntity(this);
      this.file = file;
      this.declarationNumber = declarationNumber;
      this.boundsloaded = false;
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
      this.number = stream.readInt();
      this.identifier = stream.readInt();
      this.classtype = (ClassType)this.space.getEntity(stream.readInt());
      this.boundsloaded = stream.readBoolean();
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
      stream.writeInt(this.number);
      stream.writeInt(this.identifier);
      stream.writeInt(this.space.getID(this.classtype));
      stream.writeBoolean(this.boundsloaded);
      stream.writeBoolean(this.boundtypes != null);
      if (this.boundtypes != null) {
         this.boundtypes.store(stream);
      }
   }

   protected void invalidate() {
      if (this.boundtypes != null) {
         this.boundtypes.clear();
      }

      this.boundsloaded = false;
   }

   protected void declarePositions(int line, int startcolumn, int endcolumn) {
      this.line = line;
      this.startcolumn = startcolumn;
      this.endcolumn = endcolumn;
   }

   protected void declareSyntax(int identifier, ClassType classtype, int number) {
      this.number = number;
      this.identifier = identifier;
      this.classtype = classtype;
      if (this.boundtypes == null) {
         this.boundtypes = new ListOf<>(this.space);
      } else {
         this.boundtypes.clear();
      }
   }

   protected void declareBoundtype(Type boundtype) {
      this.boundtypes.add(boundtype);
      this.boundsloaded = true;
   }

   @Override
   public FileEntry getFile() {
      return this.file;
   }

   @Override
   public int getDeclarationNumber() {
      return this.declarationNumber;
   }

   @Override
   public ClassType getEnclosingClassType() {
      return this.classtype;
   }

   @Override
   public ClassType getEnclosingTopLevelClassType() {
      return this.classtype.getEnclosingTopLevelClassType();
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

   public int getBoundTypeCount() {
      this.loadBounds();
      return this.boundtypes != null && this.boundtypes.size() != 0 ?
              this.boundtypes.size() : 1;
   }

   public int getNumber() {
      return this.number;
   }

   public int getAbsoluteNumber() {
      return this.classtype.getAbsoluteParametertypeCount() -
              this.classtype.getParametertypeCount() + this.getNumber();
   }

   public Type getBoundType(int number) throws UnknownEntityException {
      this.loadBounds();
      return this.boundtypes != null && this.boundtypes.size() > 0 ?
              this.boundtypes.get(number) :
              this.space.getRootClasstype(this.file,getLanguage());
   }

   @Override
   public Type getErasedType() throws UnknownEntityException {
      this.loadBounds();
      return this.boundtypes != null && this.boundtypes.size() > 0 ?
              this.boundtypes.get(0) :
              this.space.getRootClasstype(this.file,getLanguage());
   }

   public ClassType getClasstype() {
      return this.classtype;
   }

   @Override
   public int getIdentifier() {
      return this.identifier;
   }

   @Override
   public int getID() {
      return this.id;
   }

   private void loadBoundtypes() {
      if (!this.boundsloaded) {
         this.boundsloaded = true;
         if (this.boundtypes != null) {
            this.space.loadBoundTypes(this.file, this.classtype);
         }
      }
   }

   private void loadBounds() {
      if (!this.boundsloaded) {
         this.classtype.getAllSuperTypes();
         if (this.classtype.areSuperClasstypesLoaded()) {
            this.loadBoundtypes();
         }
      }
   }
}
