package com.apkide.analysis.clm.api;

import static com.apkide.analysis.clm.api.TypeSemantic.*;

import com.apkide.analysis.clm.api.collections.ListOf;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;

import java.io.IOException;

public final class ParameterType extends Type {
   private int myID;
   private final EntitySpace mySpace;
   private final IdentifierSpace myIdentifiers;
   private final FileSpace myFileSpace;
   private FileEntry myFile;
   private int myDeclarationNumber;
   private int myLine;
   private int myStartColumn;
   private int myEndColumn;
   private boolean myBoundsLoaded;
   private ListOf<Type> myBoundTypes;
   private int myIdentifier;
   private int myNumber;
   private ClassType myClassType;

   protected ParameterType(EntitySpace space, IdentifierSpace identifiers, FileSpace fileSpace) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
      this.myIdentifiers = identifiers;
      this.myFileSpace = fileSpace;
   }

   protected ParameterType(EntitySpace space, IdentifierSpace identifiers, FileSpace fileSpace, FileEntry file, int declarationNumber) {
      super(space, UNKNOWN_SEMANTIC);
      this.mySpace = space;
      this.myIdentifiers = identifiers;
      this.myFileSpace = fileSpace;
      this.myID = space.declareEntity(this);
      this.myFile = file;
      this.myDeclarationNumber = declarationNumber;
      this.myBoundsLoaded = false;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.myLine = stream.readInt();
      this.myStartColumn = stream.readInt();
      this.myEndColumn = stream.readInt();
      this.myID = stream.readInt();
      this.myFile = this.myFileSpace.getFileEntry(stream.readInt());
      this.myDeclarationNumber = stream.readInt();
      this.myNumber = stream.readInt();
      this.myIdentifier = stream.readInt();
      this.myClassType = (ClassType)this.mySpace.getEntity(stream.readInt());
      this.myBoundsLoaded = stream.readBoolean();
      if (stream.readBoolean()) {
         this.myBoundTypes = new ListOf<>(this.mySpace, stream);
      }
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.myLine);
      stream.writeInt(this.myStartColumn);
      stream.writeInt(this.myEndColumn);
      stream.writeInt(this.myID);
      stream.writeInt(this.myFile.getID());
      stream.writeInt(this.myDeclarationNumber);
      stream.writeInt(this.myNumber);
      stream.writeInt(this.myIdentifier);
      stream.writeInt(this.mySpace.getID(this.myClassType));
      stream.writeBoolean(this.myBoundsLoaded);
      stream.writeBoolean(this.myBoundTypes != null);
      if (this.myBoundTypes != null) {
         this.myBoundTypes.store(stream);
      }
   }

   protected void invalidate() {
      if (this.myBoundTypes != null) {
         this.myBoundTypes.clear();
      }

      this.myBoundsLoaded = false;
   }

   protected void declarePositions(int line, int startColumn, int endColumn) {
      this.myLine = line;
      this.myStartColumn = startColumn;
      this.myEndColumn = endColumn;
   }

   protected void declareSyntax(int identifier, ClassType classType, int number) {
      this.myNumber = number;
      this.myIdentifier = identifier;
      this.myClassType = classType;
      if (this.myBoundTypes == null) {
         this.myBoundTypes = new ListOf<>(this.mySpace);
      } else {
         this.myBoundTypes.clear();
      }
   }

   protected void declareBoundType(Type boundType) {
      this.myBoundTypes.add(boundType);
      this.myBoundsLoaded = true;
   }

   @Override
   public FileEntry getFile() {
      return this.myFile;
   }

   @Override
   public int getDeclarationNumber() {
      return this.myDeclarationNumber;
   }

   @Override
   public ClassType getEnclosingClassType() {
      return this.myClassType;
   }

   @Override
   public ClassType getEnclosingTopLevelClassType() {
      return this.myClassType.getEnclosingTopLevelClassType();
   }

   @Override
   public int getIdentifierLine() {
      this.mySpace.loadPositions(this.myFile);
      return this.myLine;
   }

   @Override
   public int getIdentifierEndColumn() {
      this.mySpace.loadPositions(this.myFile);
      return this.myEndColumn;
   }

   @Override
   public int getIdentifierStartColumn() {
      this.mySpace.loadPositions(this.myFile);
      return this.myStartColumn;
   }

   public int getBoundTypeCount() {
      this.loadBounds();
      return this.myBoundTypes != null && this.myBoundTypes.size() != 0 ? this.myBoundTypes.size() : 1;
   }

   public int getNumber() {
      return this.myNumber;
   }

   public int getAbsoluteNumber() {
      return this.myClassType.getAbsoluteParametertypeCount() - this.myClassType.getParametertypeCount() + this.getNumber();
   }

   public Type getBoundType(int number) throws UnknownEntityException {
      this.loadBounds();
      return this.myBoundTypes != null && this.myBoundTypes.size() > 0 ? this.myBoundTypes.get(number) : this.mySpace.getRootClasstype(this.myFile);
   }

   @Override
   public Type getErasedType() throws UnknownEntityException {
      this.loadBounds();
      return this.myBoundTypes != null && this.myBoundTypes.size() > 0 ? this.myBoundTypes.get(0) : this.mySpace.getRootClasstype(this.myFile);
   }

   public ClassType getClassType() {
      return this.myClassType;
   }

   @Override
   public int getIdentifier() {
      return this.myIdentifier;
   }

   @Override
   public int getID() {
      return this.myID;
   }

   private void loadBoundTypes() {
      if (!this.myBoundsLoaded) {
         this.myBoundsLoaded = true;
         if (this.myBoundTypes != null) {
            this.mySpace.loadBoundTypes(this.myFile, this.myClassType);
         }
      }
   }

   private void loadBounds() {
      if (!this.myBoundsLoaded) {
         this.myClassType.getAllSuperTypes();
         if (this.myClassType.areSuperClasstypesLoaded()) {
            this.loadBoundTypes();
         }
      }
   }
}
