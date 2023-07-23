package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.callback.StopCallback;
import com.apkide.analysis.clm.api.collections.FunctionOfIntInt;
import com.apkide.analysis.clm.api.collections.ListOfInt;
import com.apkide.analysis.clm.api.collections.MapOf;
import com.apkide.analysis.clm.api.collections.MapOfInt;
import com.apkide.analysis.clm.api.collections.MapOfIntArrayInt;
import com.apkide.analysis.clm.api.collections.MapOfIntInt;
import com.apkide.analysis.clm.api.collections.MapOfIntLong;
import com.apkide.analysis.clm.api.collections.MapOfLongInt;
import com.apkide.analysis.clm.api.collections.RelationOfIntInt;
import com.apkide.analysis.clm.api.collections.SetOf;
import com.apkide.analysis.clm.api.collections.SetOfFileEntry;
import com.apkide.analysis.clm.api.collections.SetOfInt;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;

import java.io.IOException;

public class EntitySpace {
   private static final boolean DEBUG_UPDATE = false;
   private static final boolean DEBUG_LOAD = false;
   private static final long MAX_MEMSIZE = 6000000L;
   private final FileSpace filespace;
   private final SyntaxTreeSpace trees;
   private final IdentifierSpace identifiers;
   private final StopCallback stopcallback;
   private ListOfInt superClasstypeAnalyzedClasses = new ListOfInt();
   private boolean namespacesLoaded;
   private FileInfo[] fileInfos;
   private int[] fileIDs;
   private long[] fileTimes;
   private MapOfIntLong usedDirectories;
   private RelationOfIntInt fileinheritancedependences;
   private int entityCount;
   private Entity[] entities;
   private MapOfLongInt arrayTypes;
   private MapOfLongInt primitiveTypes;
   private FunctionOfIntInt pointerTypes;
   private MapOfIntArrayInt parameterizedTypes;
   private FunctionOfIntInt methodParametertypeVariables;
   private int[] absoluteargumentids = new int[1000];
   private Namespace rootNamespace;
   private MapOfInt<ClassType> allClasstypes;
   protected Member.InferAlgorithm infer = new Member.InferAlgorithm(this);

   public EntitySpace(IdentifierSpace identifiers, FileSpace filespace, SyntaxTreeSpace trees, StopCallback stopcallback) {
      this.identifiers = identifiers;
      this.filespace = filespace;
      this.trees = trees;
      this.stopcallback = stopcallback;
      this.usedDirectories = new MapOfIntLong();
      this.fileinheritancedependences = new RelationOfIntInt();
      this.fileInfos = new FileInfo[1000];
      this.entityCount = 30;
      this.entities = new Entity[1000];
      this.arrayTypes = new MapOfLongInt();
      this.primitiveTypes = new MapOfLongInt();
      this.pointerTypes = new FunctionOfIntInt();
      this.methodParametertypeVariables = new FunctionOfIntInt();
      this.parameterizedTypes = new MapOfIntArrayInt();
   }

   protected void shrink(boolean full) {
      int memSize = 0;

      for (FileInfo info : this.fileInfos) {
         if (info != null) {
            memSize += info.memSize();
         }
      }

      long allowedMemsize;
      if (full) {
         allowedMemsize = 0L;
      } else {
         allowedMemsize = 6000000L;
      }

      if ((long)memSize > allowedMemsize) {
         if (this.fileIDs == null || this.fileIDs.length < this.fileInfos.length) {
            this.fileIDs = new int[this.fileInfos.length];
            this.fileTimes = new long[this.fileInfos.length];
         }

         int f = 0;

         for(int fileid = 0; fileid < this.fileInfos.length; ++fileid) {
            if (this.fileInfos[fileid] != null && this.fileInfos[fileid].memSize() > 0) {
               this.fileIDs[f] = fileid;
               this.fileTimes[f] = this.fileInfos[fileid].lastAccessTime();
               ++f;
            }
         }

         this.sortFiles(0, f - 1);

         for(int i = 0; i < f && (long)memSize > allowedMemsize; ++i) {
            FileInfo fileInfo = this.fileInfos[this.fileIDs[i]];
            if (fileInfo.memSize() < 1000000) {
               memSize -= fileInfo.memSize();
               fileInfo.invalidate();
            }
         }

         for(int i = 0; i < this.entities.length; ++i) {
            if (this.getEntity(i) instanceof ParameterizedType) {
               ((ParameterizedType)this.getEntity(i)).invalidate();
            }
         }
      }
   }

   private void sortFiles(int i, int j) {
      if (i < j) {
         int oldi = i;
         int oldj = j;
         int z = i + (j - i) / 2;
         long pivotTime = this.fileTimes[z];

         while(i <= j) {
            while(this.fileTimes[i] < pivotTime) {
               ++i;
            }

            while(pivotTime < this.fileTimes[j]) {
               --j;
            }

            if (i <= j) {
               int tempFile = this.fileIDs[j];
               this.fileIDs[j] = this.fileIDs[i];
               this.fileIDs[i] = tempFile;
               long tempTime = this.fileTimes[j];
               this.fileTimes[j] = this.fileTimes[i];
               this.fileTimes[i] = tempTime;
               ++i;
               --j;
            }
         }

         this.sortFiles(oldi, j);
         this.sortFiles(i, oldj);
      }
   }

   protected void storeEntries(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.entityCount);

      for(int i = 0; i < this.entityCount; ++i) {
         if (this.entities[i] == null) {
            stream.writeByte(0);
         } else if (this.getEntity(i).isMember()) {
            stream.writeByte(1);
         } else if (this.getEntity(i).isClassType()) {
            stream.writeByte(3);
         } else if (this.getEntity(i).isArrayType()) {
            stream.writeByte(4);
         } else if (this.getEntity(i).isNamespace()) {
            stream.writeByte(6);
         } else if (this.getEntity(i).isMethodParameterType()) {
            stream.writeByte(7);
         } else if (this.getEntity(i).isParameterType()) {
            stream.writeByte(8);
         } else if (this.getEntity(i).isParameterizedType()) {
            stream.writeByte(9);
         } else if (this.getEntity(i).isMethodParameterTypeVariable()) {
            stream.writeByte(10);
         } else if (this.getEntity(i).isPointerType()) {
            stream.writeByte(11);
         } else if (this.getEntity(i).isPrimitiveType()) {
            stream.writeByte(12);
         } else {
            stream.writeByte(0);
         }
      }
   }

   protected void loadEntries(StoreInputStream stream) throws IOException {
      this.entityCount = stream.readInt();
      this.entities = new Entity[this.entityCount * 2];

      for(int i = 0; i < this.entityCount; ++i) {
         switch(stream.readByte()) {
            case 1:
               this.entities[i] = new Member(this, this.identifiers, this.filespace);
            case 2:
            case 5:
            default:
               break;
            case 3:
               this.entities[i] = new ClassType(this, this.filespace, this.identifiers);
               break;
            case 4:
               this.entities[i] = new ArrayType(this);
               break;
            case 6:
               this.entities[i] = new Namespace(this, this.identifiers, this.filespace);
               break;
            case 7:
               this.entities[i] = new MethodParameterType(this, this.identifiers, this.filespace);
               break;
            case 8:
               this.entities[i] = new ParameterType(this, this.identifiers, this.filespace);
               break;
            case 9:
               this.entities[i] = new ParameterizedType(this);
               break;
            case 10:
               this.entities[i] = new MethodParameterTypeVariable(this);
               break;
            case 11:
               this.entities[i] = new PointerType(this);
               break;
            case 12:
               this.entities[i] = new PrimitiveType(this, this.filespace);
         }
      }
   }

   protected void load(StoreInputStream stream) throws IOException {
      this.rootNamespace = (Namespace)this.getEntity(stream.readInt());
      this.usedDirectories = new MapOfIntLong(stream);
      this.fileinheritancedependences = new RelationOfIntInt(stream);
      this.fileInfos = new FileInfo[stream.readInt()];

      for(int i = 0; i < this.fileInfos.length; ++i) {
         if (stream.readBoolean()) {
            this.fileInfos[i] = new FileInfo(this, stream);
         }
      }

      this.arrayTypes = new MapOfLongInt(stream);
      this.primitiveTypes = new MapOfLongInt(stream);
      this.pointerTypes = new FunctionOfIntInt(stream);
      this.methodParametertypeVariables = new FunctionOfIntInt(stream);
      this.parameterizedTypes = new MapOfIntArrayInt(stream);

      for(int i = 0; i < this.entityCount; ++i) {
         if (this.entities[i] != null) {
            this.entities[i].load(stream);
            if (stream.readChar() != 'E') {
               throw new IOException("Corrupted file " + this.entities[i]);
            }
         }
      }
   }

   protected void store(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.getID(this.rootNamespace));
      this.usedDirectories.store(stream);
      this.fileinheritancedependences.store(stream);
      stream.writeInt(this.fileInfos.length);

      for (FileInfo fileInfo : this.fileInfos) {
         stream.writeBoolean(fileInfo != null);
         if (fileInfo != null) {
            fileInfo.store(stream);
         }
      }

      this.arrayTypes.store(stream);
      this.primitiveTypes.store(stream);
      this.pointerTypes.store(stream);
      this.methodParametertypeVariables.store(stream);
      this.parameterizedTypes.store(stream);

      for(int i = 0; i < this.entityCount; ++i) {
         if (this.entities[i] != null) {
            this.entities[i].store(stream);
            stream.writeChar(69);
         }
      }
   }

   protected void update() {
      SetOfFileEntry modifiedFiles = new SetOfFileEntry(this.filespace);
      SetOfFileEntry classModifiedFiles = new SetOfFileEntry(this.filespace);

      for(int fileid = 0; fileid < this.fileInfos.length; ++fileid) {
         if (this.fileInfos[fileid] != null) {
            FileEntry file = this.filespace.getFileEntry(fileid);
            long syntaxVersion = file.getSyntaxVersion();
            if (syntaxVersion != this.getFileInfo(file).getVersion() && this.getFileInfo(file).getVersion() != -1L) {
               this.getFileInfo(file).invalidatePositions();
               SyntaxTree ast = this.trees.getSyntaxTree(file);
               long declarationDigest = ast.getDeclarationDigest();
               if (declarationDigest != this.getFileInfo(file).getDeclarationDigest() && this.getFileInfo(file).getDeclarationDigest() != -1L) {
                  this.getFileInfo(file).setDeclarationDigest(declarationDigest);
                  modifiedFiles.put(file);
               }

               long classDeclarationDigest = ast.getClassDeclarationDigest();
               if (classDeclarationDigest != this.getFileInfo(file).getClassDeclarationDigest() && this.getFileInfo(file).getClassDeclarationDigest() != -1L) {
                  this.getFileInfo(file).setClassDeclarationDigest(classDeclarationDigest);
                  classModifiedFiles.put(file);
               }

               long archiveVersion = file.getArchiveVersion();
               if (archiveVersion != this.getFileInfo(file).getArchiveVersion()) {
                  classModifiedFiles.put(file);
               }

               this.getFileInfo(file).setVersion(syntaxVersion);
               this.trees.releaseSyntaxTree(file);
            }
         }
      }

      if (classModifiedFiles.size() > 0) {
         for(int fileid = 0; fileid < this.fileInfos.length; ++fileid) {
            if (this.fileInfos[fileid] != null) {
               FileEntry file = this.filespace.getFileEntry(fileid);
               if (classModifiedFiles.contains(file)) {
                  this.fileInfos[fileid].invalidate();
                  this.fileInfos[fileid].invalidateNamespaces();
               }
            }
         }

         this.invalidateAll();
      } else {
         if (modifiedFiles.size() > 0) {
            for(int i = 0; i < this.entities.length; ++i) {
               if (this.getEntity(i) instanceof ParameterizedType) {
                  ((ParameterizedType)this.getEntity(i)).invalidate();
               }
            }
         }

         SetOfFileEntry invalidFiles = new SetOfFileEntry(this.filespace);

         for(int fileid = 0; fileid < this.fileInfos.length; ++fileid) {
            if (this.fileInfos[fileid] != null) {
               FileEntry file = this.filespace.getFileEntry(fileid);
               if (modifiedFiles.contains(file)) {
                  invalidFiles.put(file);
                  this.fileInfos[fileid].invalidate();
               } else {
                  modifiedFiles.DEFAULT_ITERATOR.init();

                  while(modifiedFiles.DEFAULT_ITERATOR.hasMoreElements()) {
                     if (this.fileinheritancedependences.contains(fileid, modifiedFiles.DEFAULT_ITERATOR.nextKey().getID())) {
                        invalidFiles.put(file);
                        this.fileInfos[fileid].invalidate();
                        break;
                     }
                  }
               }
            }
         }

         this.fileinheritancedependences.remove(invalidFiles);
      }
   }

   protected void invalidateAll() {
      this.namespacesLoaded = false;

      for(int i = 0; i < this.entities.length; ++i) {
         if (this.getEntity(i) instanceof Namespace) {
            ((Namespace)this.getEntity(i)).invalidate();
         }
      }

      for(int i = 0; i < this.entities.length; ++i) {
         if (this.getEntity(i) instanceof ParameterizedType) {
            ((ParameterizedType)this.getEntity(i)).invalidate();
         }
      }

      this.allClasstypes = null;

      for (FileInfo fileInfo : this.fileInfos) {
         if (fileInfo != null) {
            fileInfo.invalidate();
         }
      }

      this.fileinheritancedependences.clear();
   }

   protected void invalidateNamespaces() {
      this.namespacesLoaded = false;

      for(int i = 0; i < this.entities.length; ++i) {
         if (this.getEntity(i) instanceof Namespace) {
            ((Namespace)this.getEntity(i)).invalidate();
         }
      }

      for(int i = 0; i < this.entities.length; ++i) {
         if (this.getEntity(i) instanceof ParameterizedType) {
            ((ParameterizedType)this.getEntity(i)).invalidate();
         }
      }
   }

   public Namespace getRootNamespace() {
      if (this.rootNamespace == null) {
         this.rootNamespace = new Namespace(this, this.identifiers, this.filespace, this.identifiers.get(""), null);
      }

      return this.rootNamespace;
   }

   public Entity getEntity(int id) {
      return id == -1 ? null : this.entities[id];
   }

   public int getID(Entity entity) {
      return entity == null ? -1 : entity.getID();
   }

   public SetOf<Entity> getDeclaredEntitiesOfFile(FileEntry file) {
      return this.getFileInfo(file).getAllDeclarations();
   }

   public ClassType getMainClasstypeOfFile(FileEntry file) throws UnknownEntityException {
      return this.getFileInfo(file).getMainClasstype();
   }

   public MapOf<ClassType, Namespace> getToplevelClasstypesOfFile(FileEntry file) {
      return this.getFileInfo(file).getTopLevelClasstypes();
   }

   public SetOf<Namespace> getNamespacesOfFile(FileEntry file) {
      return this.getFileInfo(file).getNamespaces();
   }

   public MapOf<ClassType, Namespace> getAllClasstypes(int identifier, int argCount, FileEntry referingFile) {
      this.loadAllClasstypes();
      MapOf<ClassType, Namespace> classtypes = new MapOf<>(this);
      this.allClasstypes.DEFAULT_ITERATOR.init(identifier);

      while(this.allClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         ClassType classtype = this.allClasstypes.DEFAULT_ITERATOR.nextValue();
         if (classtype.isReferable(referingFile) && classtype.isApplicable(referingFile, argCount)) {
            classtypes.insert(classtype, classtype.getNamespace());
         }
      }

      return classtypes;
   }

   public SetOfInt getMissingAssemblyReferences(int identifier, int argCount, FileEntry referingFile) {
      this.loadAllClasstypes();
      SetOfInt assemblies = new SetOfInt();
      this.allClasstypes.DEFAULT_ITERATOR.init(identifier);

      while(this.allClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         ClassType classtype = this.allClasstypes.DEFAULT_ITERATOR.nextValue();
         if (classtype.isApplicable(referingFile, argCount) && !classtype.isReferable(referingFile)) {
            assemblies.put(classtype.getFile().getAssembly());
         }
      }

      return assemblies;
   }

   public MapOfInt<ClassType> getAllClasstypes() {
      this.loadAllClasstypes();
      return this.allClasstypes;
   }

   public MapOfInt<ClassType> getAllClasstypes(FileEntry referingFile) {
      this.loadAllClasstypes();
      MapOfInt<ClassType> classtypes = new MapOfInt<>(this);
      this.allClasstypes.DEFAULT_ITERATOR.init();

      while(this.allClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         int id = this.allClasstypes.DEFAULT_ITERATOR.nextKey();
         ClassType classtype = this.allClasstypes.DEFAULT_ITERATOR.nextValue();
         if (classtype.isReferable(referingFile)) {
            classtypes.insert(id, classtype);
         }
      }

      return classtypes;
   }

   private void loadAllClasstypes() {
      if (this.allClasstypes == null) {
         this.loadNamespaces();
         this.allClasstypes = new MapOfInt<>(this);
         SetOfFileEntry files = this.filespace.getSolutionFiles();
         files.DEFAULT_ITERATOR.init();

         while(files.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry file = files.DEFAULT_ITERATOR.nextKey();
            if (file.exists()) {
               MapOf<ClassType, Namespace> types = this.getToplevelClasstypesOfFile(file);
               types.DEFAULT_ITERATOR.init();

               while(types.DEFAULT_ITERATOR.hasMoreElements()) {
                  ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
                  Namespace namespace = types.DEFAULT_ITERATOR.nextValue();
                  this.allClasstypes.insert(this.getFileInfo(file).getIdentifierOfClasstype(classtype), classtype);
               }
            }
         }
      }
   }

   public int declareEntity(Entity e) {
      if (this.entityCount >= this.entities.length) {
         Entity[] entities = new Entity[this.entities.length * 2 + 1];
         System.arraycopy(this.entities, 0, entities, 0, this.entities.length);
         this.entities = entities;
      }

      this.entities[this.entityCount] = e;
      return this.entityCount++;
   }

   public void declareFileClasstype(FileEntry file, Namespace pakage, int identifier, boolean module, int typeParameterCount) {
      try {
         ClassType classtype = this.getFileInfo(file).getClasstype(0, pakage, identifier);
         pakage.declareMemberClasstype(identifier, classtype);
         classtype.declareSyntax(pakage, identifier, module, typeParameterCount);
         this.declarePackage(pakage, file);
         this.getFileInfo(file).declareToplevelClasstype(pakage, classtype, identifier);
      } catch (UnknownEntityException ignored) {
      }
   }

   public boolean canDeclareOtherSuperClasstypes() {
      return this.superClasstypeAnalyzedClasses.size() == 0;
   }

   public boolean canDeclareSyntax(FileEntry file) {
      return this.getFileInfo(file).canDeclareSyntax();
   }

   public void declareSuperClasstypeDeclarationBegin(ClassType classtype) {
      if (!classtype.isToplevel()) {
         ClassType enclosingClassType = classtype.getEnclosingClassType();
         if (enclosingClassType.isCycliclyDefined()) {
            classtype.declareCycliclyDefined();
         }
      }

      this.superClasstypeAnalyzedClasses.push(classtype.getID());
      if (!classtype.areSuperClasstypesLoaded()) {
         classtype.declareSuperClasstypesLoading();
      }
   }

   public void declareSuperClasstypeDeclarationEnd(ClassType classtype) {
      this.superClasstypeAnalyzedClasses.pop();
      if (!classtype.areSuperClasstypesLoaded()) {
         classtype.declareSuperClasstypesLoaded();
      }
   }

   public void declareCorruptedSupertypes(ClassType classtype) {
      classtype.declareCorruptedSupertypes();
      this.declareFileCorrupted(classtype.getFile());
   }

   public void declareSuperClasstype(ClassType classtype, ClassType superClassType) {
      this.declareInheritanceDependence(classtype.getFile(), superClassType);
      if (!classtype.areSuperClasstypesLoaded()) {
         for(int i = 0; i < this.superClasstypeAnalyzedClasses.size(); ++i) {
            if (superClassType.isEnclosedBy((ClassType)this.getEntity(this.superClasstypeAnalyzedClasses.get(i)))) {
               classtype.declareCycliclyDefined();
               superClassType.declareCycliclyDefined();
               this.declareFileCorrupted(classtype.getFile());
               return;
            }
         }

         if (superClassType.hasCorruptedSupertypes()) {
            classtype.declareCorruptedSupertypes();
            this.declareFileCorrupted(classtype.getFile());
         }

         if (superClassType.isCycliclyDefined()) {
            classtype.declareCycliclyDefined();
            this.declareFileCorrupted(classtype.getFile());
         } else {
            classtype.declareSuperClasstype(superClassType);
         }
      }
   }

   public void declareTypeOfDelegateClasstype(ClassType classtype, Type delegatetype) {
      classtype.declareDelegateType(delegatetype);
   }

   public void declareTypeOfEnumClasstype(ClassType classtype, Type enumtype) {
      classtype.declareEnumType(enumtype);
   }

   public void declareFileCorrupted(FileEntry file) {
      this.getFileInfo(file).setCorrupted();
   }

   public void declareSuperType(ClassType classtype, Type superType) {
      classtype.declareSuperType(superType);
   }

   public Namespace declarePackage(Namespace pakage, FileEntry file) {
      pakage.declareExists(file);
      this.getFileInfo(file).declareNamespace(pakage);
      return pakage;
   }

   public ClassType declareToplevelClasstype(
      FileEntry file,
      int declarationNumber,
      Namespace pakage,
      int typeParameterCount,
      int modifiers,
      int identifier,
      boolean hasSupertypes,
      boolean isModule,
      boolean hasDocumentation
   ) {
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(
         identifier, modifiers, pakage, typeParameterCount, classtype, classtype, hasSupertypes, false, false, false, isModule, hasDocumentation
      );
      this.getFileInfo(file).declareToplevelClasstype(pakage, classtype, identifier);
      return classtype;
   }

   public ClassType declareMemberClasstype(
      FileEntry file,
      int declarationNumber,
      Namespace pakage,
      int typeParameterCount,
      ClassType enclosingClassType,
      int modifiers,
      int identifier,
      boolean hasSupertypes,
      boolean hasDocumentation
   ) {
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(
         identifier,
         modifiers,
         pakage,
         typeParameterCount,
         enclosingClassType.getEnclosingTopLevelClassType(),
              enclosingClassType,
         hasSupertypes,
         false,
         false,
         false,
         false,
         hasDocumentation
      );
      enclosingClassType.declareMemberClasstype(identifier, classtype);
      return classtype;
   }

   public ClassType declareLocalClasstype(
      FileEntry file,
      int declarationNumber,
      Namespace pakage,
      int typeParameterCount,
      ClassType enclosingClassType,
      int modifiers,
      int identifier,
      boolean inStaticMethod,
      boolean hasSupertypes,
      boolean hasDocumentation
   ) {
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(
         identifier,
         modifiers,
         pakage,
         typeParameterCount,
         enclosingClassType.getEnclosingTopLevelClassType(),
              enclosingClassType,
         hasSupertypes,
         false,
         true,
         inStaticMethod,
         false,
         hasDocumentation
      );
      enclosingClassType.declareMemberClasstype(identifier, classtype);
      return classtype;
   }

   public ClassType declareAnonymousClasstype(
           FileEntry file, int declarationNumber, Namespace pakage, ClassType enclosingClassType, boolean inStaticMethod, boolean hasDocumentation
   ) {
      int identifier = this.identifiers.get(String.valueOf(declarationNumber));
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(
         identifier,
         0,
         pakage,
         0,
         enclosingClassType.getEnclosingTopLevelClassType(),
              enclosingClassType,
         true,
         true,
         false,
         inStaticMethod,
         false,
         hasDocumentation
      );
      return classtype;
   }

   public ClassType declareToplevelDelegateClasstype(
      FileEntry file, int declarationNumber, Namespace pakage, int typeParameterCount, int modifiers, int identifier, boolean hasDocumentation
   ) {
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(identifier, modifiers, pakage, typeParameterCount, classtype, classtype, false, false, false, false, false, hasDocumentation);
      this.getFileInfo(file).declareToplevelClasstype(pakage, classtype, identifier);
      return classtype;
   }

   public ClassType declareMemberDelegateClasstype(
      FileEntry file,
      int declarationNumber,
      Namespace pakage,
      int typeParameterCount,
      ClassType enclosingClassType,
      int modifiers,
      int identifier,
      boolean hasDocumentation
   ) {
      ClassType classtype = this.getFileInfo(file).declareClasstype(declarationNumber, pakage, identifier);
      classtype.declareSyntax(
         identifier,
         modifiers,
         pakage,
         typeParameterCount,
         enclosingClassType.getEnclosingTopLevelClassType(),
              enclosingClassType,
         false,
         false,
         false,
         false,
         false,
         hasDocumentation
      );
      enclosingClassType.declareMemberClasstype(identifier, classtype);
      return classtype;
   }

   public void declareParametertype(FileEntry file, int declarationNumber, ClassType classtype, int number, int identifier) {
      ParameterType parametertype = this.getFileInfo(file).declareParametertype(declarationNumber);
      parametertype.declareSyntax(identifier, classtype, number);
      classtype.declareParametertype(number, identifier, parametertype);
   }

   public void declareMethodParametertype(FileEntry file, int declarationNumber, Member method, int number, int identifier) {
      MethodParameterType methodparametertype = this.getFileInfo(file).declareMethodParametertype(declarationNumber);
      methodparametertype.declareSyntax(identifier, method, number);
      method.declareMethodParametertype(number, identifier, methodparametertype);
   }

   public void declareClassPositions(
      ClassType classtype,
      int identifierline,
      int identifierstartcolumn,
      int identifierendcolumn,
      int modifiersstartline,
      int modifiersstartcolumn,
      int modifiersendline,
      int modifiersendcolumn,
      int startline,
      int startcolumn,
      int endline,
      int endcolumn
   ) {
      classtype.declarePositions(
         identifierline,
         identifierstartcolumn,
         identifierendcolumn,
         modifiersstartline,
         modifiersstartcolumn,
         modifiersendline,
         modifiersendcolumn,
         startline,
         startcolumn,
         endline,
         endcolumn
      );
   }

   public Member declareField(
      FileEntry file,
      int declarationNumber,
      ClassType classtype,
      int modifiers,
      int identifier,
      boolean hasinitializer,
      boolean isProperty,
      boolean isGetProperty,
      boolean isSetProperty,
      boolean hasDocumentation
   ) {
      Member field = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareField(identifier, field);
      field.declareSyntax(classtype, modifiers, identifier, hasinitializer, isProperty, isGetProperty, isSetProperty, hasDocumentation);
      return field;
   }

   public void declareTypeOfField(Member field, Type type) {
      field.declareType(type);
   }

   public void declareValueOfField(Member field, long value) {
      field.declareValue(value);
   }

   public void declareStringValueOfField(Member field, String value) {
      field.declareStringValue(value);
   }

   public void declareExplicitOverridingTypeOfMember(Member member, Type type) {
      member.declareExplicitOverridingType(type);
   }

   public void declareException(Member method, Type exception, int number) {
      method.declareException(exception, number);
   }

   public void declareTypeOfParameter(Member method, Type ptype, int number) {
      method.declareTypeOfParameter(ptype, number);
   }

   public void declareTypeOfMethod(Member method, Type type) {
      method.declareType(type);
   }

   public void declareTypeOfMethodParameter(Member method, int number, Type type) {
      method.declareTypeOfParameter(type, number);
   }

   public void declareSyntaxOfMethodParameter(Member method, int modifiers, int identifier, int number) {
      method.declareSyntaxOfParameter(modifiers, identifier, number);
   }

   public void declareBoundtype(MethodParameterType methodparametertype, Type boundtype) {
      methodparametertype.declareBoundType(boundtype);
   }

   public void declareBoundtype(ParameterType parametertype, Type boundtype) {
      parametertype.declareBoundType(boundtype);
   }

   public void declareMemberPositions(
      Member member,
      int identifierline,
      int identifierstartcolumn,
      int identifierendcolumn,
      int modifiersstartline,
      int modifiersstartcolumn,
      int modifiersendline,
      int modifiersendcolumn,
      int startline,
      int startcolumn,
      int endline,
      int endcolumn
   ) {
      member.declarePositions(
         identifierline,
         identifierstartcolumn,
         identifierendcolumn,
         modifiersstartline,
         modifiersstartcolumn,
         modifiersendline,
         modifiersendcolumn,
         startline,
         startcolumn,
         endline,
         endcolumn
      );
   }

   public void declareMethodParametertypePositions(MethodParameterType methodparametertype, int line, int startcolumn, int endcolumn) {
      methodparametertype.declarePositions(line, startcolumn, endcolumn);
   }

   public void declareParametertypePositions(ParameterType parametertype, int line, int startcolumn, int endcolumn) {
      parametertype.declarePositions(line, startcolumn, endcolumn);
   }

   public void declareVersion(Member method, int version) {
      method.declareVersion(version);
   }

   public Member declareMethod(
      FileEntry file,
      int declarationNumber,
      ClassType classtype,
      int modifiers,
      int identifier,
      int typeParameterCount,
      int parameterCount,
      boolean hasVarargs,
      int exceptionCount,
      boolean hasDocumentation
   ) {
      Member method = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareMethod(identifier, method);
      method.declareSyntax(
         classtype, modifiers, typeParameterCount, parameterCount, hasVarargs, 0, identifier, false, false, false, exceptionCount, hasDocumentation
      );
      return method;
   }

   public Member declarePropertyMethod(
      FileEntry file,
      int declarationNumber,
      ClassType classtype,
      int modifiers,
      int identifier,
      int typeParameterCount,
      int parameterCount,
      boolean hasVarargs,
      int exceptionCount,
      boolean hasDocumentation
   ) {
      Member method = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareMethod(identifier, method);
      method.declareSyntax(
         classtype, modifiers, typeParameterCount, parameterCount, hasVarargs, 0, identifier, false, false, true, exceptionCount, hasDocumentation
      );
      return method;
   }

   public Member declareIndexer(
           FileEntry file, int declarationNumber, ClassType classtype, int modifiers, int parameterCount, boolean hasVarargs, boolean hasDocumentation
   ) {
      Member method = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareIndexer(method);
      method.declareSyntax(classtype, modifiers, 0, parameterCount, hasVarargs, 0, this.identifiers.get("[]"), false, true, false, 0, hasDocumentation);
      return method;
   }

   public Member declareConstructor(
      FileEntry file,
      int declarationNumber,
      ClassType classtype,
      int modifiers,
      int typeParameterCount,
      int parameterCount,
      boolean hasVarargs,
      int exceptionCount,
      boolean hasDocumentation
   ) {
      Member constructor = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareConstructor(constructor);
      constructor.declareSyntax(
         classtype,
         modifiers,
         typeParameterCount,
         parameterCount,
         hasVarargs,
         0,
         classtype.getIdentifier(),
         true,
         false,
         false,
         exceptionCount,
         hasDocumentation
      );
      return constructor;
   }

   public Member declareOperator(
           FileEntry file, int declarationNumber, ClassType classtype, int modifiers, int operator, int parameterCount, boolean hasDocumentation
   ) {
      Member method = this.getFileInfo(file).declareMember(declarationNumber);
      classtype.declareOperator(operator, method);
      method.declareSyntax(classtype, modifiers, 0, parameterCount, false, operator, classtype.getIdentifier(), true, false, false, 0, hasDocumentation);
      return method;
   }

   protected void declareInheritanceDependence(FileEntry file, Entity usedEntity) {
      if (usedEntity.isClassType()) {
         FileEntry usedfile = usedEntity.getFile();
         if (usedfile.isArchiveEntry()) {
            usedfile = usedfile.getParentArchive();
         }

         if (file != usedfile) {
            this.fileinheritancedependences.put(file.getID(), usedfile.getID());
         }
      } else if (usedEntity.isParameterizedType()) {
         FileEntry usedfile = ((ParameterizedType)usedEntity).getClassType().getFile();
         if (usedfile.isArchiveEntry()) {
            usedfile = usedfile.getParentArchive();
         }

         if (file != usedfile) {
            this.fileinheritancedependences.put(file.getID(), usedfile.getID());
         }
      }
   }

   public ClassType getClasstype(FileEntry file, int declarationNumber) throws UnknownEntityException {
      return this.getFileInfo(file).getClasstype(declarationNumber);
   }

   public Member getMember(FileEntry file, int declarationNumber) throws UnknownEntityException {
      return this.getFileInfo(file).getMember(declarationNumber);
   }

   public ParameterType getParametertype(FileEntry file, int declarationNumber) throws UnknownEntityException {
      return this.getFileInfo(file).getParametertype(declarationNumber);
   }

   public MethodParameterType getMethodParametertype(FileEntry file, int declarationNumber) throws UnknownEntityException {
      return this.getFileInfo(file).getMethodParametertype(declarationNumber);
   }

   public Type getParameterizedtype(ClassType classtype, Type[] absoluteargumenttypes) {
      int[] absoluteargumentvariances = new int[absoluteargumenttypes.length];

      return this.getParameterizedtype(classtype, absoluteargumenttypes, absoluteargumentvariances);
   }

   public Type getParameterizedtype(ClassType classtype, Type[] absoluteargumenttypes, int[] absoluteargumentvariances) {
      ClassType classType2 = classtype;
      int argnum = classtype.getAbsoluteParametertypeCount();

      label75:
      while(true) {
         int count = classType2.getParametertypeCount();
         argnum -= count;
         if (!classType2.hasTypeparametersOfEnclosingClasstype()) {
            int i = 0;

            while(true) {
               if (i >= argnum) {
                  break label75;
               }

               try {
                  absoluteargumenttypes[i] = classtype.getAbsoluteParametertype(i);
               } catch (UnknownEntityException ignored) {
               }

               absoluteargumentvariances[i] = -1;
               ++i;
            }
         }

         ClassType enclclasstype = classType2.getEnclosingClassType();
         if (enclclasstype == classType2) {
            break;
         }

         classType2 = enclclasstype;
      }

      int absolutelen = classtype.getAbsoluteParametertypeCount();

      for(int i = 0; i < absolutelen; ++i) {
         if (absoluteargumentvariances[i] != -1) {
            for(int ix = 0; ix < absolutelen; ++ix) {
               if (absoluteargumentvariances[ix] == -1) {
                  absoluteargumentvariances[ix] = 0;

                  try {
                     absoluteargumenttypes[ix] = this.getRootClasstype(classtype.getFile());
                  } catch (UnknownEntityException ignored) {
                  }
               }
            }

            for(int ix = 0; ix < absolutelen; ++ix) {
               this.absoluteargumentids[ix] = absoluteargumenttypes[ix].getID();
            }

            if (this.parameterizedTypes.contains(classtype.getID(), this.absoluteargumentids, absoluteargumentvariances, 0, absolutelen)) {
               return (ParameterizedType)this.getEntity(
                  this.parameterizedTypes.get(classtype.getID(), this.absoluteargumentids, absoluteargumentvariances, 0, absolutelen)
               );
            }

            ParameterizedType p = new ParameterizedType(this, classtype, absoluteargumenttypes, absoluteargumentvariances);
            this.parameterizedTypes.put(classtype.getID(), this.absoluteargumentids, absoluteargumentvariances, 0, absolutelen, p.getID());
            return p;
         }
      }

      return classtype;
   }

   public MethodParameterTypeVariable getMethodParametertypeVariableFor(MethodParameterType methodparametertype) {
      if (!this.methodParametertypeVariables.contains(methodparametertype.getID())) {
         this.methodParametertypeVariables.put(methodparametertype.getID(), new MethodParameterTypeVariable(this, methodparametertype).getID());
      }

      return (MethodParameterTypeVariable)this.getEntity(this.methodParametertypeVariables.get(methodparametertype.getID()));
   }

   public PointerType getPointertype(Type type) {
      int typeentity = type.getID();
      if (this.pointerTypes.contains(typeentity)) {
         return (PointerType)this.getEntity(this.pointerTypes.get(typeentity));
      } else {
         PointerType p = new PointerType(this, type);
         this.pointerTypes.put(typeentity, p.getID());
         return p;
      }
   }

   public ArrayType getArraytype(Type type, int dimension) {
      long key = (long)type.getID() << 32 | (long)dimension;
      if (this.arrayTypes.contains(key)) {
         return (ArrayType)this.getEntity(this.arrayTypes.get(key));
      } else {
         ArrayType a = new ArrayType(this, type, dimension);
         this.arrayTypes.put(key, a.getID());
         return a;
      }
   }

   public PrimitiveType getPrimitivetype(Language language, int number) {
      long key = (long)this.filespace.getLanguageID(language) << 32 | (long)number;
      if (this.primitiveTypes.contains(key)) {
         return (PrimitiveType)this.getEntity(this.primitiveTypes.get(key));
      } else {
         PrimitiveType p = new PrimitiveType(this, this.filespace, language.getTypeSystem().getTypeSemanticForPrimitivetype(number), language);
         this.primitiveTypes.put(key, p.getID());
         return p;
      }
   }

   public ClassType getArraySuperClasstype(FileEntry file) throws UnknownEntityException {
      return file.getLanguage().getTypeSystem().getArraySuperClasstype(file);
   }

   public ClassType getRootClasstype(FileEntry file) throws UnknownEntityException {
      return file.getLanguage().getTypeSystem().getRootClasstype(file);
   }

   public long encodeBoolean(boolean b) {
      return b ? 1L : 0L;
   }

   public boolean decodeBoolean(long value) {
      return value == 1L;
   }

   public long encodeDouble(double d) {
      return Double.doubleToLongBits(d);
   }

   public double decodeDouble(long value) {
      return Double.longBitsToDouble(value);
   }

   public long encodeFloat(float f) {
      return Float.floatToIntBits(f);
   }

   public float decodeFloat(long value) {
      return Float.intBitsToFloat((int)value);
   }

   public long encodeInt(int i) {
      return i;
   }

   public int decodeInt(long value) {
      return (int)value;
   }

   public long encodeLong(long l) {
      return l;
   }

   public long decodeLong(long value) {
      return value;
   }

   public long foldConversion(FileEntry file, long value, Type fromtype, Type totype) throws UnknownEntityException {
      if (fromtype.isEqualTo(totype)) {
         return value;
      } else if (fromtype.isEnumType()) {
         return this.foldConversion(file, value, ((ClassType)fromtype.getErasedType()).getEnumBaseType(file), totype);
      } else if (totype.isEnumType()) {
         return this.foldConversion(file, value, fromtype, ((ClassType)totype.getErasedType()).getEnumBaseType(file));
      } else {
         switch(totype.getSemantic()) {
            case 3:
            case 4:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeInt((byte)this.decodeInt(value));
                  case 9:
                  case 10:
                     return this.encodeInt((byte)((int)this.decodeLong(value)));
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeInt((byte)((int)this.decodeFloat(value)));
                  case 14:
                     return this.encodeInt((byte)((int)this.decodeDouble(value)));
               }
            case 5:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeInt((short)this.decodeInt(value));
                  case 9:
                  case 10:
                     return this.encodeInt((short)((int)this.decodeLong(value)));
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeInt((short)((int)this.decodeFloat(value)));
                  case 14:
                     return this.encodeInt((short)((int)this.decodeDouble(value)));
               }
            case 6:
            case 12:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeInt((char)this.decodeInt(value));
                  case 9:
                  case 10:
                     return this.encodeInt((char)((int)this.decodeLong(value)));
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeInt((char)((int)this.decodeFloat(value)));
                  case 14:
                     return this.encodeInt((char)((int)this.decodeDouble(value)));
               }
            case 7:
            case 8:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return value;
                  case 9:
                  case 10:
                     return this.encodeInt((int)this.decodeLong(value));
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeInt((int)this.decodeFloat(value));
                  case 14:
                     return this.encodeInt((int)this.decodeDouble(value));
               }
            case 9:
            case 10:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeLong(this.decodeInt(value));
                  case 9:
                  case 10:
                  case 11:
                  default:
                     break;
                  case 13:
                     return this.encodeLong((long)this.decodeFloat(value));
                  case 14:
                     return this.encodeLong((long)this.decodeDouble(value));
               }
            case 11:
            default:
               break;
            case 13:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeFloat((float)this.decodeInt(value));
                  case 9:
                  case 10:
                     return this.encodeFloat((float)this.decodeLong(value));
                  case 11:
                  case 13:
                  default:
                     throw new UnknownEntityException();
                  case 14:
                     return this.encodeFloat((float)this.decodeDouble(value));
               }
            case 14:
               switch(fromtype.getSemantic()) {
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 12:
                     return this.encodeDouble(this.decodeInt(value));
                  case 9:
                  case 10:
                     return this.encodeDouble((double)this.decodeLong(value));
                  case 11:
                  default:
                     break;
                  case 13:
                     return this.encodeDouble(this.decodeFloat(value));
               }
         }

         throw new UnknownEntityException();
      }
   }

   public long foldUnaryOperation(FileEntry file, int operator, long value, Type type) throws UnknownEntityException {
      switch(operator) {
         case 1:
            return value;
         case 2:
            switch(type.getSemantic()) {
               case 3:
               case 5:
               case 7:
               case 12:
                  return this.encodeInt(-this.decodeInt(value));
               case 4:
               case 6:
               case 8:
               case 10:
               case 11:
               default:
                  throw new UnknownEntityException();
               case 9:
                  return this.encodeLong(-this.decodeLong(value));
               case 13:
                  return this.encodeFloat(-this.decodeFloat(value));
               case 14:
                  return this.encodeDouble(-this.decodeDouble(value));
            }
         case 20:
            switch(type.getSemantic()) {
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               case 8:
               case 12:
                  return this.encodeInt(~this.decodeInt(value));
               case 9:
                  return this.encodeLong(~this.decodeLong(value));
               case 10:
               case 11:
               default:
                  throw new UnknownEntityException();
            }
         case 21:
            return this.encodeBoolean(!this.decodeBoolean(value));
         default:
            throw new UnknownEntityException();
      }
   }

   public long foldBinaryOperation(FileEntry file, int operator, long value1, long value2, Type type1, Type type2) throws UnknownEntityException {
      try {
         switch(operator) {
            case 1: {
               if (type1.isEnumType()) {
                  return this.foldBinaryOperation(
                          file,
                          operator,
                          this.foldConversion(file, value1, type1, ((ClassType) type1.getErasedType()).getEnumBaseType(file)),
                          value2,
                          ((ClassType) type1.getErasedType()).getEnumBaseType(file),
                          type2
                  );
               }

               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) + this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) + this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeFloat(this.decodeFloat(pv1) + this.decodeFloat(pv2));
                  case 14:
                     return this.encodeDouble(this.decodeDouble(pv1) + this.decodeDouble(pv2));
               }
            }
            case 2: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) - this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) - this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeFloat(this.decodeFloat(pv1) - this.decodeFloat(pv2));
                  case 14:
                     return this.encodeDouble(this.decodeDouble(pv1) - this.decodeDouble(pv2));
               }
            }
            case 3: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) / this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) / this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeFloat(this.decodeFloat(pv1) / this.decodeFloat(pv2));
                  case 14:
                     return this.encodeDouble(this.decodeDouble(pv1) / this.decodeDouble(pv2));
               }
            }
            case 4: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) % this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) % this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeFloat(this.decodeFloat(pv1) % this.decodeFloat(pv2));
                  case 14:
                     return this.encodeDouble(this.decodeDouble(pv1) % this.decodeDouble(pv2));
               }
            }
            case 5: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) * this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) * this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeFloat(this.decodeFloat(pv1) * this.decodeFloat(pv2));
                  case 14:
                     return this.encodeDouble(this.decodeDouble(pv1) * this.decodeDouble(pv2));
               }
            }
            case 6: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) | this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) | this.decodeLong(pv2));
                  case 11:
                     return this.encodeBoolean(this.decodeBoolean(pv1) | this.decodeBoolean(pv2));
                  default:
                     throw new UnknownEntityException();
               }
            }
            case 7: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) & this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) & this.decodeLong(pv2));
                  case 11:
                     return this.encodeBoolean(this.decodeBoolean(pv1) & this.decodeBoolean(pv2));
                  default:
                     throw new UnknownEntityException();
               }
            }
            case 8: {
               Type resulttype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, resulttype);
               long pv2 = this.foldConversion(file, value2, type2, resulttype);
               switch (resulttype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeInt(this.decodeInt(pv1) ^ this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeLong(this.decodeLong(pv1) ^ this.decodeLong(pv2));
                  case 11:
                     return this.encodeBoolean(this.decodeBoolean(pv1) ^ this.decodeBoolean(pv2));
                  default:
                     throw new UnknownEntityException();
               }
            }
            case 9: {
               Type operationtype;
               if (type1 == type2) {
                  operationtype = type1;
               } else if (type1.isExplicitConvertibleTo(file, type2)) {
                  operationtype = type2;
               } else {
                  operationtype = type1;
               }

               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) == this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) == this.decodeLong(pv2));
                  case 11:
                     return this.encodeBoolean(this.decodeBoolean(pv1) == this.decodeBoolean(pv2));
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) == this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) == this.decodeDouble(pv2));
               }
            }
            case 10: {
               Type operationtype;
               if (type1 == type2) {
                  operationtype = type1;
               } else if (type1.isExplicitConvertibleTo(file, type2)) {
                  operationtype = type2;
               } else {
                  operationtype = type1;
               }

               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) != this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) != this.decodeLong(pv2));
                  case 11:
                     return this.encodeBoolean(this.decodeBoolean(pv1) != this.decodeBoolean(pv2));
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) != this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) != this.decodeDouble(pv2));
               }
            }
            case 11: {
               Type operationtype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) < this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) < this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) < this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) < this.decodeDouble(pv2));
               }
            }
            case 12: {
               Type operationtype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) <= this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) <= this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) <= this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) <= this.decodeDouble(pv2));
               }
            }
            case 13: {
               Type operationtype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) > this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) > this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) > this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) > this.decodeDouble(pv2));
               }
            }
            case 14: {
               Type operationtype = file.getLanguage().getTypeSystem().getBinaryNumericOperationType(file, operator, type1, type2);
               long pv1 = this.foldConversion(file, value1, type1, operationtype);
               long pv2 = this.foldConversion(file, value2, type2, operationtype);
               switch (operationtype.getSemantic()) {
                  case 7:
                  case 8:
                     return this.encodeBoolean(this.decodeInt(pv1) >= this.decodeInt(pv2));
                  case 9:
                  case 10:
                     return this.encodeBoolean(this.decodeLong(pv1) >= this.decodeLong(pv2));
                  case 11:
                  case 12:
                  default:
                     throw new UnknownEntityException();
                  case 13:
                     return this.encodeBoolean(this.decodeFloat(pv1) >= this.decodeFloat(pv2));
                  case 14:
                     return this.encodeBoolean(this.decodeDouble(pv1) >= this.decodeDouble(pv2));
               }
            }
            case 15:
               return this.encodeBoolean(this.decodeBoolean(value1) || this.decodeBoolean(value2));
            case 16:
               return this.encodeBoolean(this.decodeBoolean(value1) && this.decodeBoolean(value2));
            case 17:
               switch(type2.getSemantic()) {
                  case 4:
                  case 5:
                  case 7:
                  case 12:
                     switch(type1.getSemantic()) {
                        case 4:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) << this.decodeInt(value2));
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           throw new UnknownEntityException();
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) << this.decodeInt(value2));
                     }
                  case 6:
                  case 8:
                  case 10:
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 9:
                     switch(type1.getSemantic()) {
                        case 4:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) << (int)this.decodeLong(value2));
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           throw new UnknownEntityException();
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) << (int)this.decodeLong(value2));
                     }
               }
            case 18:
               switch(type2.getSemantic()) {
                  case 4:
                  case 5:
                  case 7:
                  case 12:
                     switch(type1.getSemantic()) {
                        case 4:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) >> this.decodeInt(value2));
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           throw new UnknownEntityException();
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) >> this.decodeInt(value2));
                     }
                  case 6:
                  case 8:
                  case 10:
                  case 11:
                  default:
                     throw new UnknownEntityException();
                  case 9:
                     switch(type1.getSemantic()) {
                        case 4:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) >> (int)this.decodeLong(value2));
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           throw new UnknownEntityException();
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) >> (int)this.decodeLong(value2));
                     }
               }
            case 19:
               switch(type2.getSemantic()) {
                  case 3:
                  case 5:
                  case 7:
                  case 12:
                     switch(type1.getSemantic()) {
                        case 3:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) >>> this.decodeInt(value2));
                        case 4:
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           break;
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) >>> this.decodeInt(value2));
                     }
                  case 4:
                  case 6:
                  case 8:
                  case 10:
                  case 11:
                  default:
                     break;
                  case 9:
                     switch(type1.getSemantic()) {
                        case 3:
                        case 5:
                        case 7:
                        case 12:
                           return this.encodeInt(this.decodeInt(value1) >>> (int)this.decodeLong(value2));
                        case 4:
                        case 6:
                        case 8:
                        case 10:
                        case 11:
                        default:
                           break;
                        case 9:
                           return this.encodeLong(this.decodeLong(value1) >>> (int)this.decodeLong(value2));
                     }
               }
         }
      } catch (Exception ignored) {
      }

      throw new UnknownEntityException();
   }

   public long foldConditionalOperation(FileEntry file, long valuecond, long value1, long value2, Type type1, Type type2) throws UnknownEntityException {
      Type type = file.getLanguage().getTypeSystem().getConditionalOperationType(file, type1, type2);
      return this.decodeBoolean(valuecond) ? this.foldConversion(file, value1, type1, type) : this.foldConversion(file, value2, type2, type);
   }

   protected boolean isExplicitConversion(FileEntry file, Type fromtype, Type totype) {
      return file.getLanguage().getTypeSystem().isExplicitConversion(file, fromtype, totype);
   }

   protected boolean isImplicitConversion(FileEntry file, Type fromtype, Type totype) {
      return file.getLanguage().getTypeSystem().isImplicitConversion(file, fromtype, totype);
   }

   protected void loadConstantValueOfField(FileEntry file, Member field) {
      if (file.getLanguage().getSignatureAnalyzer() != null) {
         file.getLanguage().getSignatureAnalyzer().doLoadConstantValueOfField(file, field);
      }
   }

   protected void loadSuperTypes(FileEntry file, ClassType classtype) {
      if (file.getLanguage().getSignatureAnalyzer() != null) {
         file.getLanguage().getSignatureAnalyzer().doLoadSuperTypes(file, classtype);
      }
   }

   protected void loadBoundTypes(FileEntry file, ClassType classtype) {
      if (file.getLanguage().getSignatureAnalyzer() != null) {
         file.getLanguage().getSignatureAnalyzer().doLoadBoundTypes(file, classtype);
      }
   }

   protected void loadSuperClasstypes(FileEntry file, ClassType classtype) {
      if (file.getLanguage().getSignatureAnalyzer() != null) {
         file.getLanguage().getSignatureAnalyzer().doLoadSuperClassTypes(file, classtype);
      }
   }

   protected void loadDefaultSuperClasstypes(FileEntry file, ClassType classtype) {
      if (file.getLanguage().getSignatureAnalyzer() != null) {
         file.getLanguage().getSignatureAnalyzer().doLoadDefaultSuperClassTypes(file, classtype);
      }
   }

   protected void loadPositions(FileEntry file) {
      this.getFileInfo(file).loadPositions();
   }

   protected void loadTypes(FileEntry file) {
      this.getFileInfo(file).loadTypes();
   }

   protected void loadSyntax(FileEntry file) {
      this.getFileInfo(file).loadSyntax();
   }

   protected void loadNamespaces() {
   }

   protected void preloadNamespaces() {
      if (!this.namespacesLoaded) {
         this.namespacesLoaded = true;
         SetOfFileEntry files = this.filespace.getSolutionFiles();
         files.DEFAULT_ITERATOR.init();

         while(files.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry file = files.DEFAULT_ITERATOR.nextKey();
            if (file.exists()) {
               this.getFileInfo(file).loadNamespaces();
               if (this.stopcallback != null && this.stopcallback.stopNamespacesLoad()) {
                  this.namespacesLoaded = false;
                  return;
               }
            }
         }

         files = this.filespace.getSolutionFiles();
         files.DEFAULT_ITERATOR.init();

         while(files.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry file = files.DEFAULT_ITERATOR.nextKey();
            if (file.exists()) {
               SetOf<Namespace> packages = this.getNamespacesOfFile(file);
               packages.DEFAULT_ITERATOR.init();

               while(packages.DEFAULT_ITERATOR.hasMoreElements()) {
                  this.declarePackage(packages.DEFAULT_ITERATOR.nextKey(), file);
               }

               MapOf<ClassType, Namespace> classtypes = this.getFileInfo(file).getTopLevelClasstypes();
               classtypes.DEFAULT_ITERATOR.init();

               while(classtypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  ClassType classtype = classtypes.DEFAULT_ITERATOR.nextKey();
                  Namespace namespace = classtypes.DEFAULT_ITERATOR.nextValue();
                  namespace.declareMemberClasstype(this.getFileInfo(file).getIdentifierOfClasstype(classtype), classtype);
               }
            }
         }
      }
   }

   protected String getDocumentationString(Entity entity) {
      FileEntry file = entity.getFile();
      if (file == null) {
         return "";
      } else {
         SyntaxTree ast = this.trees.getSyntaxTree(file);
         String doc = ast.getDocumentationString(entity);
         this.trees.releaseSyntaxTree(entity.getFile());
         return doc;
      }
   }

   protected String getHTMLDocumentationString(Entity entity) {
      FileEntry file = entity.getFile();
      if (file == null) {
         return "";
      } else {
         SyntaxTree ast = this.trees.getSyntaxTree(file);
         String doc = ast.getHTMLDocumentationString(entity);
         this.trees.releaseSyntaxTree(entity.getFile());
         return doc;
      }
   }

   private FileInfo getFileInfo(FileEntry file) {
      if (this.fileInfos.length <= file.getID()) {
         FileInfo[] newFileInfos = new FileInfo[Math.max(file.getID() + 1, this.fileInfos.length * 2 + 1)];
         System.arraycopy(this.fileInfos, 0, newFileInfos, 0, this.fileInfos.length);
         this.fileInfos = newFileInfos;
      }

      FileInfo f = this.fileInfos[file.getID()];
      if (f == null) {
         f = this.fileInfos[file.getID()] = new FileInfo(this, file);
      }

      return f;
   }

   private static final class FileInfo {
      private EntitySpace space;
      private FileEntry file;
      private long version;
      private long archiveVersion;
      private long classdecldigest;
      private long decldigest;
      private boolean syntaxloaded;
      private boolean typesLoaded;
      private boolean positionsLoaded;
      private boolean packageLoaded;
      private boolean corrupted;
      private FunctionOfIntInt members;
      private FunctionOfIntInt parametertypes;
      private FunctionOfIntInt methodparametertypes;
      private FunctionOfIntInt classtypes;
      private SetOfInt membersExist;
      private SetOfInt parametertypesExist;
      private SetOfInt methodparametertypesExist;
      private SetOfInt classtypesExist;
      private SetOf<Namespace> namespaces;
      private MapOf<ClassType, Namespace> toplevelclasstypes;
      private ClassType myMainClassType;
      private MapOfIntInt toplevelclasstypeIdentifiers;

      public FileInfo(EntitySpace space, FileEntry file) {
         this.space = space;
         this.file = file;
         this.classdecldigest = -1L;
         this.decldigest = -1L;
         this.version = -1L;
         this.namespaces = new SetOf<>(space);
         this.toplevelclasstypes = new MapOf<>(space);
         this.toplevelclasstypeIdentifiers = new MapOfIntInt();
      }

      public FileInfo(EntitySpace space, StoreInputStream stream) throws IOException {
         this.space = space;
         this.file = space.filespace.getFileEntry(stream.readInt());
         if (stream.readBoolean()) {
            this.members = new FunctionOfIntInt(stream);
         }

         if (stream.readBoolean()) {
            this.parametertypes = new FunctionOfIntInt(stream);
         }

         if (stream.readBoolean()) {
            this.methodparametertypes = new FunctionOfIntInt(stream);
         }

         if (stream.readBoolean()) {
            this.classtypes = new FunctionOfIntInt(stream);
         }

         this.packageLoaded = stream.readBoolean();
         this.namespaces = new SetOf<>(space, stream);
         this.toplevelclasstypes = new MapOf<>(space, stream);
         this.toplevelclasstypeIdentifiers = new MapOfIntInt(stream);
         int id = stream.readInt();
         if (id != 0) {
            this.myMainClassType = (ClassType)space.getEntity(id);
         }

         this.classdecldigest = stream.readLong();
         this.decldigest = stream.readLong();
         this.version = stream.readLong();
         this.archiveVersion = stream.readLong();
         this.syntaxloaded = stream.readBoolean();
         this.typesLoaded = stream.readBoolean();
         this.positionsLoaded = stream.readBoolean();
         this.corrupted = stream.readBoolean();
         if (stream.readBoolean()) {
            this.membersExist = new SetOfInt(stream);
         }

         if (stream.readBoolean()) {
            this.parametertypesExist = new SetOfInt(stream);
         }

         if (stream.readBoolean()) {
            this.methodparametertypesExist = new SetOfInt(stream);
         }

         if (stream.readBoolean()) {
            this.classtypesExist = new SetOfInt(stream);
         }
      }

      protected void store(StoreOutputStream stream) throws IOException {
         stream.writeInt(this.file.getID());
         stream.writeBoolean(this.members != null);
         if (this.members != null) {
            this.members.store(stream);
         }

         stream.writeBoolean(this.parametertypes != null);
         if (this.parametertypes != null) {
            this.parametertypes.store(stream);
         }

         stream.writeBoolean(this.methodparametertypes != null);
         if (this.methodparametertypes != null) {
            this.methodparametertypes.store(stream);
         }

         stream.writeBoolean(this.classtypes != null);
         if (this.classtypes != null) {
            this.classtypes.store(stream);
         }

         stream.writeBoolean(this.packageLoaded);
         this.namespaces.store(stream);
         this.toplevelclasstypes.store(stream);
         this.toplevelclasstypeIdentifiers.store(stream);
         if (this.myMainClassType == null) {
            stream.writeInt(0);
         } else {
            stream.writeInt(this.myMainClassType.getID());
         }

         stream.writeLong(this.classdecldigest);
         stream.writeLong(this.decldigest);
         stream.writeLong(this.version);
         stream.writeLong(this.archiveVersion);
         stream.writeBoolean(this.syntaxloaded);
         stream.writeBoolean(this.typesLoaded);
         stream.writeBoolean(this.positionsLoaded);
         stream.writeBoolean(this.corrupted);
         stream.writeBoolean(this.membersExist != null);
         if (this.membersExist != null) {
            this.membersExist.store(stream);
         }

         stream.writeBoolean(this.parametertypesExist != null);
         if (this.parametertypesExist != null) {
            this.parametertypesExist.store(stream);
         }

         stream.writeBoolean(this.methodparametertypesExist != null);
         if (this.methodparametertypesExist != null) {
            this.methodparametertypesExist.store(stream);
         }

         stream.writeBoolean(this.classtypesExist != null);
         if (this.classtypesExist != null) {
            this.classtypesExist.store(stream);
         }
      }

      public long lastAccessTime() {
         long time = 0L;
         if (this.classtypes != null) {
            this.classtypes.DEFAULT_ITERATOR.init();

            while(this.classtypes.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = (ClassType)this.space.getEntity(this.classtypes.DEFAULT_ITERATOR.nextValue());
               long accessedTime = classtype.lastAccessTime();
               if (accessedTime >= time) {
                  time = accessedTime;
               }
            }
         }

         return time;
      }

      public int memSize() {
         if (!this.syntaxloaded) {
            return 0;
         } else {
            int memSize = 0;
            if (this.membersExist != null) {
               memSize += this.membersExist.size() * 200;
            }

            if (this.classtypesExist != null) {
               memSize += this.classtypesExist.size() * 500;
            }

            return memSize;
         }
      }

      public int getIdentifierOfClasstype(ClassType classtype) {
         return this.toplevelclasstypeIdentifiers.get(classtype.getID());
      }

      public void setClassDeclarationDigest(long digest) {
         this.classdecldigest = digest;
      }

      public void setDeclarationDigest(long digest) {
         this.decldigest = digest;
      }

      public void setVersion(long syntaxVersion) {
         this.version = syntaxVersion;
      }

      public void setCorrupted() {
         this.corrupted = true;
      }

      public void invalidatePositions() {
         this.positionsLoaded = false;
      }

      public void invalidateNamespaces() {
         this.archiveVersion = 0L;
         this.packageLoaded = false;
         this.namespaces.clear();
         this.toplevelclasstypes.clear();
         this.myMainClassType = null;
         this.toplevelclasstypeIdentifiers.clear();
      }

      public void invalidate() {
         this.corrupted = false;
         this.typesLoaded = false;
         this.syntaxloaded = false;
         if (this.membersExist != null) {
            this.membersExist.clear();
         }

         if (this.parametertypesExist != null) {
            this.parametertypesExist.clear();
         }

         if (this.methodparametertypesExist != null) {
            this.methodparametertypesExist.clear();
         }

         if (this.classtypesExist != null) {
            this.classtypesExist.clear();
         }

         if (this.members != null) {
            this.members.DEFAULT_ITERATOR.init();

            while(this.members.DEFAULT_ITERATOR.hasMoreElements()) {
               ((Member)this.space.getEntity(this.members.DEFAULT_ITERATOR.nextValue())).invalidate();
            }
         }

         if (this.parametertypes != null) {
            this.parametertypes.DEFAULT_ITERATOR.init();

            while(this.parametertypes.DEFAULT_ITERATOR.hasMoreElements()) {
               ((ParameterType)this.space.getEntity(this.parametertypes.DEFAULT_ITERATOR.nextValue())).invalidate();
            }
         }

         if (this.methodparametertypes != null) {
            this.methodparametertypes.DEFAULT_ITERATOR.init();

            while(this.methodparametertypes.DEFAULT_ITERATOR.hasMoreElements()) {
               ((MethodParameterType)this.space.getEntity(this.methodparametertypes.DEFAULT_ITERATOR.nextValue())).invalidate();
            }
         }

         if (this.classtypes != null) {
            this.classtypes.DEFAULT_ITERATOR.init();

            while(this.classtypes.DEFAULT_ITERATOR.hasMoreElements()) {
               ((ClassType)this.space.getEntity(this.classtypes.DEFAULT_ITERATOR.nextValue())).invalidate();
            }
         }
      }

      public ClassType getMainClasstype() throws UnknownEntityException {
         this.space.loadNamespaces();
         if (this.myMainClassType == null) {
            throw new UnknownEntityException();
         } else {
            return this.myMainClassType;
         }
      }

      public MapOf<ClassType, Namespace> getTopLevelClasstypes() {
         this.space.loadNamespaces();
         return this.toplevelclasstypes;
      }

      public SetOf<Namespace> getNamespaces() {
         this.space.loadNamespaces();
         return this.namespaces;
      }

      public void declareNamespace(Namespace pakage) {
         this.namespaces.put(pakage);
      }

      public void declareToplevelClasstype(Namespace pakage, ClassType classtype, int identifier) {
         if (this.toplevelclasstypes.size() == 0) {
            this.myMainClassType = classtype;
         }

         this.toplevelclasstypes.put(classtype, pakage);
         this.toplevelclasstypeIdentifiers.put(classtype.getID(), identifier);
      }

      public boolean isCorrupted() {
         return this.corrupted;
      }

      public long getArchiveVersion() {
         return this.archiveVersion;
      }

      public long getVersion() {
         return this.version;
      }

      public long getClassDeclarationDigest() {
         return this.classdecldigest;
      }

      public long getDeclarationDigest() {
         return this.decldigest;
      }

      public SetOf<Entity> getAllDeclarations() {
         SetOf<Entity> declarations = new SetOf<>(this.space);
         this.loadSyntax();
         if (this.members != null) {
            this.members.DEFAULT_ITERATOR.init();

            while(this.members.DEFAULT_ITERATOR.hasMoreElements()) {
               if (this.membersExist.contains(this.members.DEFAULT_ITERATOR.nextKey())) {
                  declarations.put(this.space.getEntity(this.members.DEFAULT_ITERATOR.nextValue()));
               }
            }
         }

         if (this.classtypes != null) {
            this.classtypes.DEFAULT_ITERATOR.init();

            while(this.classtypes.DEFAULT_ITERATOR.hasMoreElements()) {
               if (this.classtypesExist.contains(this.classtypes.DEFAULT_ITERATOR.nextKey())) {
                  declarations.put(this.space.getEntity(this.classtypes.DEFAULT_ITERATOR.nextValue()));
               }
            }
         }

         return declarations;
      }

      public Member getMember(int declarationNumber) throws UnknownEntityException {
         this.loadSyntax();
         if (this.members == null) {
            throw new UnknownEntityException();
         } else if (!this.members.contains(declarationNumber)) {
            throw new UnknownEntityException();
         } else if (this.membersExist.contains(declarationNumber)) {
            return (Member)this.space.getEntity(this.members.get(declarationNumber));
         } else {
            throw new UnknownEntityException();
         }
      }

      public ClassType getClasstype(int declarationNumber, Namespace pakage, int identifier) throws UnknownEntityException {
         if (declarationNumber == 0) {
            return this.declareClasstype(0, pakage, identifier);
         } else {
            this.loadSyntax();
            if (this.classtypes == null) {
               throw new UnknownEntityException();
            } else if (!this.classtypes.contains(declarationNumber)) {
               throw new UnknownEntityException();
            } else if (this.classtypesExist.contains(declarationNumber)) {
               return (ClassType)this.space.getEntity(this.classtypes.get(declarationNumber));
            } else {
               throw new UnknownEntityException();
            }
         }
      }

      public ClassType getClasstype(int declarationNumber) throws UnknownEntityException {
         this.loadSyntax();
         if (this.classtypes == null) {
            throw new UnknownEntityException();
         } else if (!this.classtypes.contains(declarationNumber)) {
            throw new UnknownEntityException();
         } else if (this.classtypesExist.contains(declarationNumber)) {
            return (ClassType)this.space.getEntity(this.classtypes.get(declarationNumber));
         } else {
            throw new UnknownEntityException();
         }
      }

      public ParameterType getParametertype(int declarationNumber) throws UnknownEntityException {
         this.loadSyntax();
         if (this.parametertypes == null) {
            throw new UnknownEntityException();
         } else if (!this.parametertypes.contains(declarationNumber)) {
            throw new UnknownEntityException();
         } else if (this.parametertypesExist.contains(declarationNumber)) {
            return (ParameterType)this.space.getEntity(this.parametertypes.get(declarationNumber));
         } else {
            throw new UnknownEntityException();
         }
      }

      public MethodParameterType getMethodParametertype(int declarationNumber) throws UnknownEntityException {
         this.loadSyntax();
         if (this.methodparametertypes == null) {
            throw new UnknownEntityException();
         } else if (!this.methodparametertypes.contains(declarationNumber)) {
            throw new UnknownEntityException();
         } else if (this.methodparametertypesExist.contains(declarationNumber)) {
            return (MethodParameterType)this.space.getEntity(this.methodparametertypes.get(declarationNumber));
         } else {
            throw new UnknownEntityException();
         }
      }

      public MethodParameterType declareMethodParametertype(int declarationNumber) {
         if (this.methodparametertypes == null) {
            this.methodparametertypes = new FunctionOfIntInt();
         }

         if (this.methodparametertypesExist == null) {
            this.methodparametertypesExist = new SetOfInt();
         }

         this.methodparametertypesExist.put(declarationNumber);
         if (!this.methodparametertypes.contains(declarationNumber)) {
            MethodParameterType p = new MethodParameterType(this.space, this.space.identifiers, this.space.filespace, this.file, declarationNumber);
            this.methodparametertypes.put(declarationNumber, p.getID());
         }

         return (MethodParameterType)this.space.getEntity(this.methodparametertypes.get(declarationNumber));
      }

      public ParameterType declareParametertype(int declarationNumber) {
         if (this.parametertypes == null) {
            this.parametertypes = new FunctionOfIntInt();
         }

         if (this.parametertypesExist == null) {
            this.parametertypesExist = new SetOfInt();
         }

         this.parametertypesExist.put(declarationNumber);
         if (!this.parametertypes.contains(declarationNumber)) {
            ParameterType p = new ParameterType(this.space, this.space.identifiers, this.space.filespace, this.file, declarationNumber);
            this.parametertypes.put(declarationNumber, p.getID());
         }

         return (ParameterType)this.space.getEntity(this.parametertypes.get(declarationNumber));
      }

      public ClassType declareClasstype(int declarationNumber, Namespace pakage, int identifier) {
         if (this.classtypes == null) {
            this.classtypes = new FunctionOfIntInt();
         }

         if (this.classtypesExist == null) {
            this.classtypesExist = new SetOfInt();
         }

         this.classtypesExist.put(declarationNumber);
         if (!this.classtypes.contains(declarationNumber)) {
            ClassType c = new ClassType(
               this.space,
               this.space.filespace,
               this.space.identifiers,
               this.file,
               declarationNumber,
               this.file.getLanguage().getTypeSystem().getTypeSemanticForClasstype(pakage, identifier)
            );
            this.classtypes.put(declarationNumber, c.getID());
         }

         return (ClassType)this.space.getEntity(this.classtypes.get(declarationNumber));
      }

      public Member declareMember(int declarationNumber) {
         if (this.members == null) {
            this.members = new FunctionOfIntInt();
         }

         if (this.membersExist == null) {
            this.membersExist = new SetOfInt();
         }

         this.membersExist.put(declarationNumber);
         if (!this.members.contains(declarationNumber)) {
            Member m = new Member(this.space, this.space.identifiers, this.space.filespace, this.file, declarationNumber);
            this.members.put(declarationNumber, m.getID());
         }

         return (Member)this.space.getEntity(this.members.get(declarationNumber));
      }

      public boolean isLoaded() {
         return this.syntaxloaded;
      }

      public void loadPositions() {
         this.loadSyntax();
         if (!this.positionsLoaded) {
            this.positionsLoaded = true;
            if (this.file.getLanguage().getSignatureAnalyzer() != null) {
               this.file.getLanguage().getSignatureAnalyzer().doLoadPositions(this.file);
            }
         }
      }

      public boolean canDeclareSyntax() {
         return !this.syntaxloaded;
      }

      public void loadNamespaces() {
         if (!this.packageLoaded) {
            this.packageLoaded = true;
            this.version = this.file.getSyntaxVersion();
            this.archiveVersion = this.file.getArchiveVersion();
            this.classdecldigest = 0L;
            this.decldigest = 0L;
            if (this.file.getLanguage().getSignatureAnalyzer() != null) {
               this.file.getLanguage().getSignatureAnalyzer().doLoadNamespaces(this.file);
            }
         }
      }

      public void loadSyntax() {
         if (!this.syntaxloaded) {
            this.syntaxloaded = true;
            this.positionsLoaded = true;
            if (this.file.getLanguage().getSignatureAnalyzer() != null) {
               this.file.getLanguage().getSignatureAnalyzer().doLoadSyntax(this.file);
            }

            SyntaxTree ast = this.space.trees.getSyntaxTree(this.file);
            this.version = ast.getVersion();
            this.classdecldigest = ast.getClassDeclarationDigest();
            this.decldigest = ast.getDeclarationDigest();
            this.space.trees.releaseSyntaxTree(this.file);
         }
      }

      private void loadTypes() {
         if (!this.typesLoaded) {
            this.typesLoaded = true;
            if (this.file.getLanguage().getSignatureAnalyzer() != null) {
               this.file.getLanguage().getSignatureAnalyzer().doLoadTypes(this.file);
            }
         }
      }
   }
}
