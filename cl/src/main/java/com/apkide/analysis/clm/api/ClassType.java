package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.api.collections.ListOf;
import com.apkide.analysis.clm.api.collections.MapOf;
import com.apkide.analysis.clm.api.collections.MapOfInt;
import com.apkide.analysis.clm.api.collections.SetOf;
import com.apkide.analysis.clm.api.collections.SetOfInt;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;
import com.apkide.analysis.clm.api.excpetions.AmbiguousEntityException;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;
import com.apkide.analysis.clm.api.util.TimeUtils;

import java.io.IOException;

public final class ClassType extends Type {
   private final FileSpace filespace;
   private final EntitySpace space;
   private final IdentifierSpace identifiers;
   private int id;
   private FileEntry file;
   private int declarationNumber;
   private Namespace namespace;
   private ClassType myToplevelclasstype;
   private ClassType myEnclosingclasstype;
   private int identifier;
   private boolean isModule;
   private int typeParameterCount;
   private Data data;
   private long accessTime;

   protected long lastAccessTime() {
      return this.accessTime;
   }

   private Data data() {
      this.accessTime = TimeUtils.currentTimeMillis();
      if (this.data == null) {
         this.data = new Data();
         this.data.inheritedMethodPairs = new ListOf<>(this.space);
         this.data.superClasstypes = new ListOf<>(this.space);
         this.data.superTypes = new ListOf<>(this.space);
         this.data.absoluteTypeParameters = new ListOf<>(this.space);
         this.data.typeParameters = new ListOf<>(this.space);
         this.data.memberParametertypes = new MapOfInt<>(this.space);
         this.data.memberFields = new MapOfInt<>(this.space);
         this.data.memberOperators = new MapOfInt<>(this.space);
         this.data.memberClasstypes = new MapOfInt<>(this.space);
         this.data.memberConstructors = new SetOf<>(this.space);
         this.data.memberMethods = new MapOfInt<>(this.space);
         this.data.containingTypesOfMembers = new MapOf<>(this.space);
         this.data.allMemberOperators = new MapOfInt<>(this.space);
         this.data.allMemberFields = new MapOfInt<>(this.space);
         this.data.allMemberMethods = new MapOfInt<>(this.space);
         this.data.allExplicitMemberFields = new MapOfInt<>(this.space);
         this.data.allExplicitMemberMethods = new MapOfInt<>(this.space);
      }
      return this.data;
   }

   protected ClassType(EntitySpace space, FileSpace filespace, IdentifierSpace identifiers) {
      super(space);
      this.filespace = filespace;
      this.space = space;
      this.identifiers = identifiers;
   }

   protected ClassType(EntitySpace space, FileSpace filespace, IdentifierSpace identifiers, FileEntry file, int declarationNumber, int semantic) {
      super(space, semantic);
      this.filespace = filespace;
      this.space = space;
      this.identifiers = identifiers;
      this.id = space.declareEntity(this);
      this.file = file;
      this.declarationNumber = declarationNumber;
   }

   @Override
   protected void load(StoreInputStream stream) throws IOException {
      super.load(stream);
      this.namespace = (Namespace)this.space.getEntity(stream.readInt());
      this.id = stream.readInt();
      this.file = this.filespace.getFileEntry(stream.readInt());
      this.declarationNumber = stream.readInt();
      this.myToplevelclasstype = (ClassType)this.space.getEntity(stream.readInt());
      this.myEnclosingclasstype = (ClassType)this.space.getEntity(stream.readInt());
      this.identifier = stream.readInt();
      this.isModule = stream.readBoolean();
      this.typeParameterCount = stream.readInt();
      if (stream.readBoolean()) {
         this.data = new Data();
         this.data.syntaxLoaded = stream.readBoolean();
         this.data.typesLoaded = stream.readBoolean();
         this.data.modifiersstartline = stream.readInt();
         this.data.modifiersstartcolumn = stream.readInt();
         this.data.modifiersendline = stream.readInt();
         this.data.modifiersendcolumn = stream.readInt();
         this.data.startline = stream.readInt();
         this.data.startcolumn = stream.readInt();
         this.data.endline = stream.readInt();
         this.data.endcolumn = stream.readInt();
         this.data.idline = stream.readInt();
         this.data.idstartcolumn = stream.readInt();
         this.data.idendcolumn = stream.readInt();
         this.data.modifiers = stream.readInt();
         this.data.hasDocumentation = stream.readBoolean();
         this.data.hasSuperClasstypes = stream.readBoolean();
         this.data.isanonymous = stream.readBoolean();
         this.data.islocal = stream.readBoolean();
         this.data.inStaticMethod = stream.readBoolean();
         this.data.superClasstypesLoaded = stream.readBoolean();
         this.data.superClasstypesLoading = stream.readBoolean();
         this.data.superTypesLoaded = stream.readBoolean();
         this.data.superTypesLoading = stream.readBoolean();
         this.data.cycliclyDefined = stream.readBoolean();
         this.data.corruptedSupertypes = stream.readBoolean();
         this.data.absoluteTypeParameterCount = stream.readInt();
         this.data.delegateOrEnumtype = (Type)this.space.getEntity(stream.readInt());
         if (stream.readBoolean()) {
            this.data.superClasstypes = new ListOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.superTypes = new ListOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allSuperClasstypes = new SetOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allSuperTypes = new SetOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allMemberClasstypes = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allMemberTypes = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberClasstypes = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberParametertypes = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.typeParameters = new ListOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.absoluteTypeParameters = new ListOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberOperators = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberFields = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberMethods = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.memberConstructors = new SetOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.containingTypesOfMembers = new MapOf<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allMemberFields = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allMemberMethods = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allExplicitMemberFields = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allExplicitMemberMethods = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.allMemberOperators = new MapOfInt<>(this.space, stream);
         }

         if (stream.readBoolean()) {
            this.data.inheritedMethodPairs = new ListOf<>(this.space, stream);
         }
      }
   }

   @Override
   protected void store(StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.space.getID(this.namespace));
      stream.writeInt(this.id);
      stream.writeInt(this.file.getID());
      stream.writeInt(this.declarationNumber);
      stream.writeInt(this.space.getID(this.myToplevelclasstype));
      stream.writeInt(this.space.getID(this.myEnclosingclasstype));
      stream.writeInt(this.identifier);
      stream.writeBoolean(this.isModule);
      stream.writeInt(this.typeParameterCount);
      stream.writeBoolean(this.data != null);
      if (this.data != null) {
         stream.writeBoolean(this.data.syntaxLoaded);
         stream.writeBoolean(this.data.typesLoaded);
         stream.writeInt(this.data.modifiersstartline);
         stream.writeInt(this.data.modifiersstartcolumn);
         stream.writeInt(this.data.modifiersendline);
         stream.writeInt(this.data.modifiersendcolumn);
         stream.writeInt(this.data.startline);
         stream.writeInt(this.data.startcolumn);
         stream.writeInt(this.data.endline);
         stream.writeInt(this.data.endcolumn);
         stream.writeInt(this.data.idline);
         stream.writeInt(this.data.idstartcolumn);
         stream.writeInt(this.data.idendcolumn);
         stream.writeInt(this.data.modifiers);
         stream.writeBoolean(this.data.hasDocumentation);
         stream.writeBoolean(this.data.hasSuperClasstypes);
         stream.writeBoolean(this.data.isanonymous);
         stream.writeBoolean(this.data.islocal);
         stream.writeBoolean(this.data.inStaticMethod);
         stream.writeBoolean(this.data.superClasstypesLoaded);
         stream.writeBoolean(this.data.superClasstypesLoading);
         stream.writeBoolean(this.data.superTypesLoaded);
         stream.writeBoolean(this.data.superTypesLoading);
         stream.writeBoolean(this.data.cycliclyDefined);
         stream.writeBoolean(this.data.corruptedSupertypes);
         stream.writeInt(this.data.absoluteTypeParameterCount);
         stream.writeInt(this.space.getID(this.data.delegateOrEnumtype));
         stream.writeBoolean(this.data.superClasstypes != null);
         if (this.data.superClasstypes != null) {
            this.data.superClasstypes.store(stream);
         }

         stream.writeBoolean(this.data.superTypes != null);
         if (this.data.superTypes != null) {
            this.data.superTypes.store(stream);
         }

         stream.writeBoolean(this.data.allSuperClasstypes != null);
         if (this.data.allSuperClasstypes != null) {
            this.data.allSuperClasstypes.store(stream);
         }

         stream.writeBoolean(this.data.allSuperTypes != null);
         if (this.data.allSuperTypes != null) {
            this.data.allSuperTypes.store(stream);
         }

         stream.writeBoolean(this.data.allMemberClasstypes != null);
         if (this.data.allMemberClasstypes != null) {
            this.data.allMemberClasstypes.store(stream);
         }

         stream.writeBoolean(this.data.allMemberTypes != null);
         if (this.data.allMemberTypes != null) {
            this.data.allMemberTypes.store(stream);
         }

         stream.writeBoolean(this.data.memberClasstypes != null);
         if (this.data.memberClasstypes != null) {
            this.data.memberClasstypes.store(stream);
         }

         stream.writeBoolean(this.data.memberParametertypes != null);
         if (this.data.memberParametertypes != null) {
            this.data.memberParametertypes.store(stream);
         }

         stream.writeBoolean(this.data.typeParameters != null);
         if (this.data.typeParameters != null) {
            this.data.typeParameters.store(stream);
         }

         stream.writeBoolean(this.data.absoluteTypeParameters != null);
         if (this.data.absoluteTypeParameters != null) {
            this.data.absoluteTypeParameters.store(stream);
         }

         stream.writeBoolean(this.data.memberOperators != null);
         if (this.data.memberOperators != null) {
            this.data.memberOperators.store(stream);
         }

         stream.writeBoolean(this.data.memberFields != null);
         if (this.data.memberFields != null) {
            this.data.memberFields.store(stream);
         }

         stream.writeBoolean(this.data.memberMethods != null);
         if (this.data.memberMethods != null) {
            this.data.memberMethods.store(stream);
         }

         stream.writeBoolean(this.data.memberConstructors != null);
         if (this.data.memberConstructors != null) {
            this.data.memberConstructors.store(stream);
         }

         stream.writeBoolean(this.data.containingTypesOfMembers != null);
         if (this.data.containingTypesOfMembers != null) {
            this.data.containingTypesOfMembers.store(stream);
         }

         stream.writeBoolean(this.data.allMemberFields != null);
         if (this.data.allMemberFields != null) {
            this.data.allMemberFields.store(stream);
         }

         stream.writeBoolean(this.data.allMemberMethods != null);
         if (this.data.allMemberMethods != null) {
            this.data.allMemberMethods.store(stream);
         }

         stream.writeBoolean(this.data.allExplicitMemberFields != null);
         if (this.data.allExplicitMemberFields != null) {
            this.data.allExplicitMemberFields.store(stream);
         }

         stream.writeBoolean(this.data.allExplicitMemberMethods != null);
         if (this.data.allExplicitMemberMethods != null) {
            this.data.allExplicitMemberMethods.store(stream);
         }

         stream.writeBoolean(this.data.allMemberOperators != null);
         if (this.data.allMemberOperators != null) {
            this.data.allMemberOperators.store(stream);
         }

         stream.writeBoolean(this.data.inheritedMethodPairs != null);
         if (this.data.inheritedMethodPairs != null) {
            this.data.inheritedMethodPairs.store(stream);
         }
      }
   }

   protected void invalidate() {
      this.data = null;
   }

   protected void declareSyntax(
      int identifier,
      int modifiers,
      Namespace pakage,
      int typeParameterCount,
      ClassType toplevelclasstype,
      ClassType enclosingclasstype,
      boolean hasSupertypes,
      boolean isanonymous,
      boolean islocal,
      boolean inStaticMethod,
      boolean isModule,
      boolean hasDocumentation
   ) {
      this.namespace = pakage;
      this.identifier = identifier;
      this.myToplevelclasstype = toplevelclasstype;
      this.myEnclosingclasstype = enclosingclasstype;
      this.isModule = isModule;
      this.typeParameterCount = typeParameterCount;
      this.data().isanonymous = isanonymous;
      this.data().islocal = islocal;
      this.data().inStaticMethod = inStaticMethod;
      this.data().hasSuperClasstypes = hasSupertypes;
      this.data().modifiers = modifiers;
      this.data().hasDocumentation = hasDocumentation;
      if (enclosingclasstype == this) {
         this.data().absoluteTypeParameterCount = typeParameterCount;
      } else {
         this.data().absoluteTypeParameterCount = typeParameterCount + enclosingclasstype.getAbsoluteParametertypeCount();
         int enclcount = enclosingclasstype.getAbsoluteParametertypeCount();
         this.data().absoluteTypeParameters.setSize(this.data().absoluteTypeParameterCount);

         for(int i = 0; i < enclcount; ++i) {
            try {
               this.data().absoluteTypeParameters.set(i, enclosingclasstype.getAbsoluteParametertype(i));
            } catch (UnknownEntityException ignored) {
            }
         }
      }

      this.data().memberOperators.clear();
      this.data().memberConstructors.clear();
      this.data().memberFields.clear();
      this.data().memberMethods.clear();
      this.data().memberClasstypes.clear();
      this.data().memberParametertypes.clear();
   }

   protected void declarePositions(
      int idline,
      int idstartcolumn,
      int idendcolumn,
      int modifiersstartline,
      int modifiersstartcolumn,
      int modifiersendline,
      int modifiersendcolumn,
      int startline,
      int startcolumn,
      int endline,
      int endcolumn
   ) {
      this.data().idline = idline;
      this.data().idstartcolumn = idstartcolumn;
      this.data().idendcolumn = idendcolumn;
      this.data().modifiersstartline = modifiersstartline;
      this.data().modifiersstartcolumn = modifiersstartcolumn;
      this.data().modifiersendline = modifiersendline;
      this.data().modifiersendcolumn = modifiersendcolumn;
      this.data().startline = startline;
      this.data().startcolumn = startcolumn;
      this.data().endline = endline;
      this.data().endcolumn = endcolumn;
   }

   protected void declareCorruptedSupertypes() {
      this.data().corruptedSupertypes = true;
   }

   protected void declareSuperClasstypesLoaded() {
      this.data().superClasstypesLoaded = true;
      this.data().superClasstypesLoading = false;
      this.packSuperClasstypes();
   }

   protected void declareSuperClasstypesLoading() {
      this.data().superClasstypesLoaded = false;
      this.data().superClasstypesLoading = true;
   }

   protected void declareCycliclyDefined() {
      this.data().cycliclyDefined = true;
   }

   protected void declareDelegateType(Type delegatetype) {
      this.data().delegateOrEnumtype = delegatetype;
   }

   protected void declareEnumType(Type enumtype) {
      this.data().delegateOrEnumtype = enumtype;
   }

   protected void declareSyntax(Namespace pakage, int identifier, boolean module, int typeParameterCount) {
      this.identifier = identifier;
      this.namespace = pakage;
      this.myToplevelclasstype = this;
      this.myEnclosingclasstype = this;
      this.isModule = module;
      this.typeParameterCount = typeParameterCount;
   }

   protected void declareMethod(int identifier, Member method) {
      this.data().memberMethods.insert(identifier, method);
   }

   protected void declareField(int identifier, Member field) {
      this.data().memberFields.insert(identifier, field);
   }

   protected void declareOperator(int operator, Member method) {
      this.data().memberOperators.insert(operator, method);
   }

   protected void declareSuperClasstype(ClassType superClassType) {
      this.data().superClasstypes.add(superClassType);
   }

   protected void declareIndexer(Member method) {
      this.data().memberMethods.insert(this.identifiers.get("[]"), method);
   }

   protected void declareConstructor(Member constructor) {
      this.data().memberConstructors.put(constructor);
   }

   protected void declareParametertype(int number, int identifier, ParameterType parametertype) {
      this.data().memberParametertypes.put(identifier, parametertype);
      this.data().typeParameters.set(number, parametertype);
      this.data().absoluteTypeParameters.set(this.data().absoluteTypeParameterCount - this.getParametertypeCount() + number, parametertype);
   }

   protected void declareSuperType(Type supertype) {
      ClassType classtype;
      if (supertype.isParameterizedType()) {
         classtype = ((ParameterizedType)supertype).getClassType();
      } else {
         classtype = (ClassType)supertype;
      }

      if (this.data().allSuperClasstypes.contains(classtype)) {
         this.data().superTypes.add(supertype);
      }
   }

   protected void declareMemberClasstype(int identifier, ClassType classtype) {
      this.data().memberClasstypes.insert(identifier, classtype);
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
   public int getID() {
      return this.id;
   }

   public boolean hasTypeparametersOfEnclosingClasstype() {
      return this.file.getLanguage().getTypeSystem().hasTypeparametersOfEnclosingClasstype() || this.hasInstanceOfEnclosingClasstype();
   }

   public boolean hasInstanceOfEnclosingClasstype() {
      return !this.isToplevel() && !this.isStatic() && !this.data().inStaticMethod;
   }

   public Type parameterizeBivariant() {
      int absolutelen = this.getAbsoluteParametertypeCount();
      if (absolutelen == 0) {
         return this;
      } else {
         try {
            int[] absolutevariances = new int[absolutelen];
            Type[] absoluteargumenttypes = new Type[absolutelen];

            for(int i = 0; i < absolutelen; ++i) {
               absolutevariances[i] = 1;
               absoluteargumenttypes[i] = this.getAbsoluteParametertype(i).getErasedType();
            }

            return this.space.getParameterizedtype(this, absoluteargumenttypes, absolutevariances);
         } catch (UnknownEntityException var5) {
            return this;
         }
      }
   }

   public boolean isModule() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.isModule;
   }

   public boolean isAnonymous() {
      return this.data().isanonymous;
   }

   public boolean isLocal() {
      return this.data().islocal;
   }

   public boolean isStatic() {
      return (this.data().modifiers & 64) != 0;
   }

   public boolean isAbstract() {
      return Modifiers.isAbstract(this.getModifiers());
   }

   public boolean isSealed() {
      return (this.data().modifiers & 128) != 0;
   }

   public boolean isApplicable(FileEntry file, int argumentTypeCount) {
      if (!file.getLanguage().getTypeSystem().supportsGenericTypeNameResolving()) {
         return true;
      } else {
         return argumentTypeCount == this.getParametertypeCount();
      }
   }

   public SetOf<ClassType> getAllPartialClasstypes() {
      return this.getNamespace().getAllPartialClasstypes(this.getFile(), this.getIdentifier());
   }

   public Type parameterizeCanonical() {
      int absolutelen = this.getAbsoluteParametertypeCount();
      if (absolutelen == 0) {
         return this;
      } else {
         try {
            Type[] absoluteargumenttypes = new Type[absolutelen];
            int[] absolutevariances = new int[absolutelen];

            for(int i = 0; i < absolutelen; ++i) {
               absoluteargumenttypes[i] = this.getAbsoluteParametertype(i);
               absolutevariances[i] = 0;
            }

            return this.space.getParameterizedtype(this, absoluteargumenttypes, absolutevariances);
         } catch (UnknownEntityException var5) {
            return this;
         }
      }
   }

   public boolean isDuplicate() {
      if (!this.isToplevel()) {
         MapOfInt<ClassType> classtypes = this.getEnclosingClassType().getDeclaredMemberClasstypes();
         classtypes.DEFAULT_ITERATOR.init(this.getIdentifier());

         while(classtypes.DEFAULT_ITERATOR.hasMoreElements()) {
            if (classtypes.DEFAULT_ITERATOR.nextValue() != this) {
               return true;
            }
         }
      }

      return false;
   }

   public Type getEnumBaseType(FileEntry file) throws UnknownEntityException {
      this.space.loadTypes(file);
      if (this.data().delegateOrEnumtype == null) {
         throw new UnknownEntityException();
      } else {
         return this.data().delegateOrEnumtype;
      }
   }

   public Type getDelegateOrEnumtype() throws UnknownEntityException {
      this.space.loadTypes(this.file);
      if (this.data().delegateOrEnumtype == null) {
         throw new UnknownEntityException();
      } else {
         return this.data().delegateOrEnumtype;
      }
   }

   public boolean existsMemberParametertype(int identifier) {
      this.loadSyntax();
      return this.data().memberParametertypes.contains(identifier);
   }

   public ParameterType accessParametertype(int identifier) throws UnknownEntityException {
      this.loadSyntax();
      ParameterType type = this.data().memberParametertypes.get(identifier);
      if (type == null) {
         throw new UnknownEntityException();
      } else {
         return type;
      }
   }

   public int getAssembly() {
      return this.getFile().getAssembly();
   }

   public Entity getContainer() {
      return this.isToplevel() ? this.getNamespace() : this.getEnclosingClassType();
   }

   public Namespace getNamespace() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.namespace;
   }

   @Override
   public int getIdentifierLine() {
      this.space.loadPositions(this.file);
      return this.data().idline;
   }

   @Override
   public int getIdentifierEndColumn() {
      this.space.loadPositions(this.file);
      return this.data().idendcolumn;
   }

   @Override
   public int getIdentifierStartColumn() {
      this.space.loadPositions(this.file);
      return this.data().idstartcolumn;
   }

   @Override
   public int getModifiersStartLine() {
      this.space.loadPositions(this.file);
      return this.data().modifiersstartline;
   }

   @Override
   public int getModifiersEndLine() {
      this.space.loadPositions(this.file);
      return this.data().modifiersendline;
   }

   @Override
   public int getModifiersEndColumn() {
      this.space.loadPositions(this.file);
      return this.data().modifiersendcolumn;
   }

   @Override
   public int getModifiersStartColumn() {
      this.space.loadPositions(this.file);
      return this.data().modifiersstartcolumn;
   }

   @Override
   public int getEndColumn() {
      this.space.loadPositions(this.file);
      return this.data().endcolumn;
   }

   @Override
   public int getEndLine() {
      this.space.loadPositions(this.file);
      return this.data().endline;
   }

   @Override
   public int getStartColumn() {
      this.space.loadPositions(this.file);
      return this.data().startcolumn;
   }

   @Override
   public int getStartLine() {
      this.space.loadPositions(this.file);
      return this.data().startline;
   }

   @Override
   public MapOfInt<Member> getAllOperators() {
      this.loadTypes();
      return this.data().allMemberOperators;
   }

   public MapOfInt<Member> getDeclaredMemberMethods() {
      this.loadSyntax();
      return this.data().memberMethods;
   }

   public MapOfInt<Member> getDeclaredMemberFields() {
      this.loadSyntax();
      return this.data().memberFields;
   }

   public SetOf<Member> getDeclaredConstructors() {
      this.loadSyntax();
      return this.data().memberConstructors;
   }

   public SetOf<Member> getConstructors() {
      this.loadTypes();
      return this.data().memberConstructors;
   }

   @Override
   public Type accessFields(FileEntry file, int identifier, boolean caseSensitive, ClassType referingClassType, SetOf<Member> fields) {
      this.loadTypes();
      fields.clear();
      MapOfInt<Member> lookupFields = this.data().allMemberFields;
      int lookupIdentifier = identifier;
      if (!caseSensitive) {
         if (this.data().allMemberFieldsCaseInsensitive == null) {
            this.data().allMemberFieldsCaseInsensitive = new MapOfInt<>(this.space);
            this.data().allMemberFields.DEFAULT_ITERATOR.init();

            while(this.data().allMemberFields.DEFAULT_ITERATOR.hasMoreElements()) {
               int ident = this.identifiers.toUpperCase(this.data().allMemberFields.DEFAULT_ITERATOR.nextKey());
               Member field = this.data().allMemberFields.DEFAULT_ITERATOR.nextValue();
               this.data().allMemberFieldsCaseInsensitive.insert(ident, field);
            }
         }

         lookupIdentifier = this.identifiers.toUpperCase(identifier);
         lookupFields = this.data().allMemberFieldsCaseInsensitive;
      }

      lookupFields.DEFAULT_ITERATOR.init(lookupIdentifier);

      while(lookupFields.DEFAULT_ITERATOR.hasMoreElements()) {
         Member field = lookupFields.DEFAULT_ITERATOR.nextValue();
         if (field.isVisible(this, referingClassType)) {
            fields.put(field);
         }
      }

      return fields.empty() ? null : this;
   }

   @Override
   public Type accessMethods(FileEntry file, int identifier, boolean caseSensitive, ClassType referingClassType, SetOf<Member> methods) {
      this.loadTypes();
      methods.clear();
      MapOfInt<Member> lookupMethods = this.data().allMemberMethods;
      int lookupIdentifier = identifier;
      if (!caseSensitive) {
         if (this.data().allMemberMethodsCaseInsensitive == null) {
            this.data().allMemberMethodsCaseInsensitive = new MapOfInt<>(this.space);
            this.data().allMemberMethods.DEFAULT_ITERATOR.init();

            while(this.data().allMemberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
               int ident = this.identifiers.toUpperCase(this.data().allMemberMethods.DEFAULT_ITERATOR.nextKey());
               Member method = this.data().allMemberMethods.DEFAULT_ITERATOR.nextValue();
               this.data().allMemberMethodsCaseInsensitive.insert(ident, method);
            }
         }

         lookupIdentifier = this.identifiers.toUpperCase(identifier);
         lookupMethods = this.data().allMemberMethodsCaseInsensitive;
      }

      lookupMethods.DEFAULT_ITERATOR.init(lookupIdentifier);

      while(lookupMethods.DEFAULT_ITERATOR.hasMoreElements()) {
         Member method = lookupMethods.DEFAULT_ITERATOR.nextValue();
         if (method.isVisible(this, referingClassType)) {
            methods.put(method);
         }
      }

      return methods.empty() ? null : this;
   }

   @Override
   public Type accessConstructors(ClassType referingClassType, SetOf<Member> constructors) {
      this.loadTypes();
      constructors.clear();
      this.data().memberConstructors.DEFAULT_ITERATOR.init();

      while(this.data().memberConstructors.DEFAULT_ITERATOR.hasMoreElements()) {
         Member constructor = this.data().memberConstructors.DEFAULT_ITERATOR.nextKey();
         if (constructor.isVisible(referingClassType, referingClassType)) {
            constructors.put(constructor);
         }
      }

      return constructors.empty() ? null : this;
   }

   public Type parameterize(Type[] argumenttypes) {
      int[] variances = new int[argumenttypes.length];

      return this.parameterize(argumenttypes, variances);
   }

   public Type parameterize(Type[] argumenttypes, int[] variances) {
      int len = this.getParametertypeCount();
      int absolutelen = this.getAbsoluteParametertypeCount();
      if (absolutelen == 0) {
         return this;
      } else {
         if (len < absolutelen) {
            try {
               Type[] absoluteargumenttypes = new Type[absolutelen];
               int[] absolutevariances = new int[absolutelen];

               for(int i = 0; i < absolutelen - len; ++i) {
                  absoluteargumenttypes[i] = this.getAbsoluteParametertype(i).getErasedType().getErasedType();
                  absolutevariances[i] = 0;
               }

               for(int i = absolutelen - len; i < absolutelen; ++i) {
                  absoluteargumenttypes[i] = argumenttypes[i - absolutelen + len];
                  absolutevariances[i] = variances[i - absolutelen + len];
               }

               argumenttypes = absoluteargumenttypes;
               variances = absolutevariances;
            } catch (UnknownEntityException var8) {
               return this;
            }
         }

         return this.space.getParameterizedtype(this, argumenttypes, variances);
      }
   }

   @Override
   public Type getSuperType() throws UnknownEntityException {
      if (this.data().superTypesLoading) {
         throw new UnknownEntityException();
      } else {
         this.loadSupertypes();
         if (this.isInterfaceType()) {
            throw new UnknownEntityException();
         } else if (this.data().superTypes.size() == 0) {
            throw new UnknownEntityException();
         } else {
            return this.data().superTypes.get(0);
         }
      }
   }

   public boolean isPartial() {
      this.loadSyntax();
      return (this.data().modifiers & 2097152) != 0;
   }

   @Override
   public boolean isSealedType() {
      this.loadSyntax();
      return (this.data().modifiers & 128) != 0;
   }

   @Override
   public boolean isInterfaceType() {
      this.loadSyntax();
      return (this.data().modifiers & 134217728) != 0;
   }

   @Override
   public boolean isEnumType() {
      this.loadSyntax();
      return (this.data().modifiers & 268435456) != 0;
   }

   @Override
   public boolean isDelegateType() {
      this.loadSyntax();
      return (this.data().modifiers & 33554432) != 0;
   }

   @Override
   public boolean isStructType() {
      this.loadSyntax();
      return (this.data().modifiers & 67108864) != 0;
   }

   @Override
   public String getHTMLDocumentationString() {
      this.loadSyntax();
      return this.data().hasDocumentation ? super.getHTMLDocumentationString() : "";
   }

   public ClassType getSuperClasstype() throws UnknownEntityException {
      this.loadSuperClasstypes();
      if (this.isInterfaceType()) {
         throw new UnknownEntityException();
      } else if (this.data().superClasstypes.size() == 0) {
         throw new UnknownEntityException();
      } else {
         return this.data().superClasstypes.get(0);
      }
   }

   public ClassType getAnonymousSuperClasstype() throws UnknownEntityException {
      this.loadSuperClasstypes();
      if (this.isInterfaceType()) {
         throw new UnknownEntityException();
      } else if (this.data().superClasstypes.size() == 0) {
         throw new UnknownEntityException();
      } else {
         return this.data().superClasstypes.size() == 1 ? this.data().superClasstypes.get(0) : this.data().superClasstypes.get(1);
      }
   }

   public MapOfInt<Member> getAllMemberFields() {
      this.loadTypes();
      return this.data().allMemberFields;
   }

   public MapOfInt<Member> getAllExplicitMemberFields() {
      this.loadTypes();
      return this.data().allExplicitMemberFields;
   }

   public MapOfInt<Member> getAllMemberMethods() {
      this.loadTypes();
      return this.data().allMemberMethods;
   }

   public MapOfInt<Member> getAllExplicitMemberMethods() {
      this.loadTypes();
      return this.data().allExplicitMemberMethods;
   }

   @Override
   public int getIdentifier() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.identifier;
   }

   public String getIdentifierString() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.isAnonymous() ? "(anonymous)" : this.identifiers.getString(this.identifier);
   }

   public int getParametertypeCount() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      if (this.typeParameterCount == -1) {
         this.loadSyntax();
      }

      return this.typeParameterCount;
   }

   public ParameterType getParametertype(int number) throws UnknownEntityException {
      this.loadSyntax();
      if (this.typeParameterCount <= number) {
         throw new UnknownEntityException();
      } else {
         return this.data().typeParameters.get(number);
      }
   }

   public int getAbsoluteParametertypeCount() {
      this.loadSyntax();
      return this.data().absoluteTypeParameterCount;
   }

   public ParameterType getAbsoluteParametertype(int number) throws UnknownEntityException {
      this.loadSyntax();
      if (this.data().absoluteTypeParameterCount <= number) {
         throw new UnknownEntityException();
      } else {
         return this.data().absoluteTypeParameters.get(number);
      }
   }

   public boolean areSuperClasstypesLoaded() {
      return this.data().superClasstypesLoaded;
   }

   public boolean isCycliclyDefined() {
      this.loadSuperClasstypes();
      return this.data().cycliclyDefined;
   }

   public boolean hasCorruptedSupertypes() {
      this.loadSuperClasstypes();
      return this.data().corruptedSupertypes;
   }

   public int getInheritedMethodPairCount() {
      this.loadTypes();
      return this.data().inheritedMethodPairs.size() / 2;
   }

   public Member getInheritedMethod1(int n) {
      return this.data().inheritedMethodPairs.get(n * 2);
   }

   public Member getInheritedMethod2(int n) {
      return this.data().inheritedMethodPairs.get(n * 2 + 1);
   }

   public MapOfInt<ClassType> getDeclaredMemberClasstypes() {
      this.loadSyntax();
      return this.data().memberClasstypes;
   }

   public MapOfInt<Member> getDeclaredMemberOperators() {
      this.loadSyntax();
      return this.data().memberOperators;
   }

   public Type getContainingTypeOfMember(Member method) {
      return this.data().containingTypesOfMembers.contains(method) ? this.data().containingTypesOfMembers.get(method) : method.getEnclosingClassType();
   }

   public boolean isEnclosedBy(ClassType enclosingClassType) {
      if (this == enclosingClassType) {
         return true;
      } else {
         return !this.isToplevel() && this.myEnclosingclasstype.isEnclosedBy(enclosingClassType);
      }
   }

   @Override
   public ClassType getEnclosingTopLevelClassType() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.myToplevelclasstype;
   }

   @Override
   public ClassType getEnclosingClassType() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.myEnclosingclasstype;
   }

   public boolean isToplevel() {
      if (this.declarationNumber == 0) {
         this.space.loadNamespaces();
      } else {
         this.loadSyntax();
      }

      return this.myEnclosingclasstype == this;
   }

   public SetOf<Type> getAllSuperTypes() {
      if (this.data().superTypesLoading) {
         return new SetOf<>(this.space);
      } else {
         this.loadSupertypes();
         return this.data().allSuperTypes;
      }
   }

   public SetOf<ClassType> getAllSuperClasstypes() {
      this.loadSuperClasstypes();
      return this.data().allSuperClasstypes;
   }

   public MapOfInt<Type> getAllMemberTypes() {
      this.loadSupertypes();
      return this.data().allMemberTypes;
   }

   public MapOfInt<ClassType> getAllMemberClasstypes() {
      this.loadSuperClasstypes();
      return this.data().allMemberClasstypes;
   }

   @Override
   public Type accessMemberType(int identifier, boolean caseSensitive, int parameterTypeCount, Entity referingClasstypeOrPackage, FileEntry file) throws UnknownEntityException, AmbiguousEntityException {
      if (this.data().superTypesLoading) {
         return this.accessMemberClasstype(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file);
      } else {
         this.loadSupertypes();
         MapOfInt<Type> lookupTypes = this.data().allMemberTypes;
         int lookupIdentifier = identifier;
         if (!caseSensitive) {
            if (this.data().allMemberTypesCaseInsensitive == null) {
               this.data().allMemberTypesCaseInsensitive = new MapOfInt<>(this.space);
               this.data().allMemberTypes.DEFAULT_ITERATOR.init();

               while(this.data().allMemberTypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  int ident = this.identifiers.toUpperCase(this.data().allMemberTypes.DEFAULT_ITERATOR.nextKey());
                  Type type = this.data().allMemberTypes.DEFAULT_ITERATOR.nextValue();
                  this.data().allMemberTypesCaseInsensitive.insert(ident, type);
               }
            }

            lookupIdentifier = this.identifiers.toUpperCase(identifier);
            lookupTypes = this.data().allMemberTypesCaseInsensitive;
         }

         Type foundtype = null;
         lookupTypes.DEFAULT_ITERATOR.init(lookupIdentifier);

         while(lookupTypes.DEFAULT_ITERATOR.hasMoreElements()) {
            Type type = lookupTypes.DEFAULT_ITERATOR.nextValue();
            ClassType classtype;
            if (type.isParameterizedType()) {
               classtype = ((ParameterizedType)type).getClassType();
            } else {
               classtype = (ClassType)type;
            }

            if (classtype.isApplicable(file, parameterTypeCount) && classtype.isVisible(referingClasstypeOrPackage)) {
               if (foundtype != null) {
                  throw new AmbiguousEntityException();
               }

               foundtype = type;
            }
         }

         if (foundtype != null) {
            return foundtype;
         } else {
            throw new UnknownEntityException();
         }
      }
   }

   public boolean existsMemberClasstype(int identifier, boolean caseSensitive) {
      if (this.data().superClasstypesLoading) {
         MapOfInt<ClassType> lookupTypes = this.data().memberClasstypes;
         int lookupIdentifier = identifier;
         if (!caseSensitive) {
            if (this.data().memberClasstypesCaseInsensitive == null) {
               this.data().memberClasstypesCaseInsensitive = new MapOfInt<>(this.space);
               this.data().memberClasstypes.DEFAULT_ITERATOR.init();

               while(this.data().memberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  int ident = this.identifiers.toUpperCase(this.data().memberClasstypes.DEFAULT_ITERATOR.nextKey());
                  ClassType type = this.data().memberClasstypes.DEFAULT_ITERATOR.nextValue();
                  this.data().memberClasstypesCaseInsensitive.insert(ident, type);
               }
            }

            lookupIdentifier = this.identifiers.toUpperCase(identifier);
            lookupTypes = this.data().memberClasstypesCaseInsensitive;
         }

         return lookupTypes.contains(lookupIdentifier);
      } else {
         this.loadSuperClasstypes();
         MapOfInt<ClassType> lookupTypes = this.data().allMemberClasstypes;
         int lookupIdentifier = identifier;
         if (!caseSensitive) {
            if (this.data().allMemberClasstypesCaseInsensitive == null) {
               this.data().allMemberClasstypesCaseInsensitive = new MapOfInt<>(this.space);
               this.data().allMemberClasstypes.DEFAULT_ITERATOR.init();

               while(this.data().allMemberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  int ident = this.identifiers.toUpperCase(this.data().allMemberClasstypes.DEFAULT_ITERATOR.nextKey());
                  ClassType type = this.data().allMemberClasstypes.DEFAULT_ITERATOR.nextValue();
                  this.data().allMemberClasstypesCaseInsensitive.insert(ident, type);
               }
            }

            lookupIdentifier = this.identifiers.toUpperCase(identifier);
            lookupTypes = this.data().allMemberClasstypesCaseInsensitive;
         }

         return lookupTypes.contains(lookupIdentifier);
      }
   }

   public ClassType accessMemberClasstype(int identifier, boolean caseSensitive, int parameterTypeCount, Entity referingClasstypeOrPackage, FileEntry file) throws UnknownEntityException, AmbiguousEntityException {
      if (this.data().superClasstypesLoading) {
         MapOfInt<ClassType> lookupTypes = this.data().memberClasstypes;
         int lookupIdentifier = identifier;
         if (!caseSensitive) {
            if (this.data().memberClasstypesCaseInsensitive == null) {
               this.data().memberClasstypesCaseInsensitive = new MapOfInt<>(this.space);
               this.data().memberClasstypes.DEFAULT_ITERATOR.init();

               while(this.data().memberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  int ident = this.identifiers.toUpperCase(this.data().memberClasstypes.DEFAULT_ITERATOR.nextKey());
                  ClassType type = this.data().memberClasstypes.DEFAULT_ITERATOR.nextValue();
                  this.data().memberClasstypesCaseInsensitive.insert(ident, type);
               }
            }

            lookupIdentifier = this.identifiers.toUpperCase(identifier);
            lookupTypes = this.data().memberClasstypesCaseInsensitive;
         }

         ClassType foundtype = null;
         lookupTypes.DEFAULT_ITERATOR.init(lookupIdentifier);

         while(lookupTypes.DEFAULT_ITERATOR.hasMoreElements()) {
            ClassType classtype = lookupTypes.DEFAULT_ITERATOR.nextValue();
            if (classtype.isApplicable(file, parameterTypeCount) && classtype.isVisible(referingClasstypeOrPackage)) {
               if (foundtype != null) {
                  throw new AmbiguousEntityException();
               }

               foundtype = classtype;
            }
         }

         if (foundtype != null) {
            return foundtype;
         } else {
            throw new UnknownEntityException();
         }
      } else {
         this.loadSuperClasstypes();
         MapOfInt<ClassType> lookupTypes = this.data().allMemberClasstypes;
         int lookupIdentifier = identifier;
         if (!caseSensitive) {
            if (this.data().allMemberClasstypesCaseInsensitive == null) {
               this.data().allMemberClasstypesCaseInsensitive = new MapOfInt<>(this.space);
               this.data().allMemberClasstypes.DEFAULT_ITERATOR.init();

               while(this.data().allMemberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
                  int ident = this.identifiers.toUpperCase(this.data().allMemberClasstypes.DEFAULT_ITERATOR.nextKey());
                  ClassType type = this.data().allMemberClasstypes.DEFAULT_ITERATOR.nextValue();
                  this.data().allMemberClasstypesCaseInsensitive.insert(ident, type);
               }
            }

            lookupIdentifier = this.identifiers.toUpperCase(identifier);
            lookupTypes = this.data().allMemberClasstypesCaseInsensitive;
         }

         ClassType foundtype = null;
         lookupTypes.DEFAULT_ITERATOR.init(lookupIdentifier);

         while(lookupTypes.DEFAULT_ITERATOR.hasMoreElements()) {
            ClassType classtype = lookupTypes.DEFAULT_ITERATOR.nextValue();
            if (classtype.isApplicable(file, parameterTypeCount) && classtype.isVisible(referingClasstypeOrPackage)) {
               if (foundtype != null) {
                  throw new AmbiguousEntityException();
               }

               foundtype = classtype;
            }
         }

         if (foundtype != null) {
            return foundtype;
         } else {
            throw new UnknownEntityException();
         }
      }
   }

   @Override
   public int getModifiers() {
      this.loadSyntax();
      return this.data().modifiers;
   }

   public boolean isVisible(Entity referingClasstypeOrPackage) {
      if (this.declarationNumber == 0) {
         return true;
      } else {
         this.loadSyntax();
         int modifiers = this.getModifiers();
         ClassType enclosingclasstype = this.getEnclosingClassType();
         if (Modifiers.isPublic(modifiers)) {
            return true;
         } else {
            if (Modifiers.isCSPrivate(modifiers) && referingClasstypeOrPackage.isClassType()) {
               if (((ClassType)referingClasstypeOrPackage).isEnclosedBy(enclosingclasstype)) {
                  return true;
               }

               if (enclosingclasstype.getNamespace() == ((ClassType)referingClasstypeOrPackage).getNamespace()
                  && enclosingclasstype.getIdentifier() == referingClasstypeOrPackage.getIdentifier()
                  && enclosingclasstype.isPartial()
                  && ((ClassType)referingClasstypeOrPackage).isPartial()) {
                  return true;
               }
            }

            if (Modifiers.isJavaPrivate(modifiers)
               && referingClasstypeOrPackage.isClassType()
               && this.getEnclosingTopLevelClassType() == referingClasstypeOrPackage.getEnclosingTopLevelClassType()) {
               return true;
            } else {
               if (Modifiers.isInternal(modifiers)) {
                  if (referingClasstypeOrPackage.isClassType()) {
                     if (((ClassType)referingClasstypeOrPackage).getAssembly() == this.getAssembly()) {
                        return true;
                     }
                  } else {
                     if (referingClasstypeOrPackage == this.space.getRootNamespace()) {
                        return true;
                     }

                     SetOfInt assemblies = ((Namespace)referingClasstypeOrPackage).getAllAssemblies();
                     assemblies.DEFAULT_ITERATOR.init();

                     while(assemblies.DEFAULT_ITERATOR.hasMoreElements()) {
                        int assembly = assemblies.DEFAULT_ITERATOR.nextKey();
                        if (assembly == this.getAssembly()) {
                           return true;
                        }
                     }
                  }
               }

               if (Modifiers.isPackagePrivate(modifiers)) {
                  if (referingClasstypeOrPackage.isClassType()) {
                     if (((ClassType)referingClasstypeOrPackage).getNamespace() == this.getNamespace()) {
                        return true;
                     }
                  } else if (referingClasstypeOrPackage == this.getNamespace()) {
                     return true;
                  }
               }

               if (Modifiers.isProtected(modifiers)) {
                  if (referingClasstypeOrPackage.isNamespace()) {
                     return this.getNamespace() == referingClasstypeOrPackage;
                  } else {
                     if (this == referingClasstypeOrPackage) {
                        return true;
                     }

                     ClassType referingClassType = (ClassType)referingClasstypeOrPackage;
                     ClassType enclclasstype = this.getEnclosingClassType();
                     if (referingClassType.isSubClassTypeOf(enclclasstype)) {
                        return true;
                     }

                     while(!referingClassType.isToplevel()) {
                        referingClassType = referingClassType.getEnclosingClassType();
                        if (referingClassType.isSubClassTypeOf(enclclasstype)) {
                           return true;
                        }
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   public boolean isPackagePrivate() {
      this.loadSyntax();
      return Modifiers.isPackagePrivate(this.data().modifiers);
   }

   public boolean isSubClassTypeOf(Type superClasstype) {
      if (this.data().superClasstypesLoading) {
         return false;
      } else {
         this.loadSuperClasstypes();
         return this.data().allSuperClasstypes.contains(superClasstype);
      }
   }

   public int getSupertypeCount() {
      if (this.data().superTypesLoading) {
         return 0;
      } else {
         this.loadSupertypes();
         return this.data().superTypes.size();
      }
   }

   public Type getSupertype(int i) {
      this.loadSupertypes();
      return this.data().superTypes.get(i);
   }

   private void loadTypes() {
      this.loadSyntax();
      if (!this.data().typesLoaded) {
         this.data().typesLoaded = true;
         this.loadSupertypes();
         this.packMembers();
      }
   }

   private void packMembers() {
      Type canonicEntity = this.parameterizeCanonical();
      this.data().allMemberOperators.clear();
      this.data().memberOperators.DEFAULT_ITERATOR.init();

      while(this.data().memberOperators.DEFAULT_ITERATOR.hasMoreElements()) {
         this.data()
            .allMemberOperators
            .insert(this.data().memberOperators.DEFAULT_ITERATOR.nextKey(), this.data().memberOperators.DEFAULT_ITERATOR.nextValue());
      }

      for(int i = 0; i < this.data().superTypes.size(); ++i) {
         Type supertype = this.data().superTypes.get(i);
         ClassType superclasstype;
         if (supertype.isParameterizedType()) {
            superclasstype = ((ParameterizedType)supertype).getClassType();
         } else {
            superclasstype = (ClassType)supertype;
         }

         MapOfInt<Member> operators = superclasstype.getAllOperators();
         operators.DEFAULT_ITERATOR.init();

         while(operators.DEFAULT_ITERATOR.hasMoreElements()) {
            this.data().allMemberOperators.insert(operators.DEFAULT_ITERATOR.nextKey(), operators.DEFAULT_ITERATOR.nextValue());
         }
      }

      this.data().allMemberFields.clear();
      this.data().memberFields.DEFAULT_ITERATOR.init();

      while(this.data().memberFields.DEFAULT_ITERATOR.hasMoreElements()) {
         int identifier = this.data().memberFields.DEFAULT_ITERATOR.nextKey();
         Member field = this.data().memberFields.DEFAULT_ITERATOR.nextValue();
         if (field.isExplicitOverriding()) {
            this.data().allExplicitMemberFields.insert(identifier, field);
         } else {
            this.data().allMemberFields.insert(identifier, field);
         }

         if (canonicEntity != this) {
            this.data().containingTypesOfMembers.put(field, canonicEntity);
         }
      }

      if (this.isPartial()) {
         SetOf<ClassType> types = this.getAllPartialClasstypes();
         if (types != null) {
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
               if (classtype != this) {
                  this.space.declareInheritanceDependence(this.file, classtype);
                  MapOfInt<Member> memberFields = classtype.getDeclaredMemberFields();
                  memberFields.DEFAULT_ITERATOR.init();

                  while(memberFields.DEFAULT_ITERATOR.hasMoreElements()) {
                     int identifier = memberFields.DEFAULT_ITERATOR.nextKey();
                     Member field = memberFields.DEFAULT_ITERATOR.nextValue();
                     if (!field.isExplicitOverriding()) {
                        this.data().allMemberFields.insert(identifier, field);
                     }

                     if (field.getEnclosingClassType() != canonicEntity) {
                        this.data().containingTypesOfMembers.put(field, canonicEntity);
                     }
                  }
               }
            }
         }
      }

      label527:
      for(int i = 0; i < this.data().superTypes.size(); ++i) {
         Type supertype = this.data().superTypes.get(i);
         ClassType superclasstype;
         if (supertype.isParameterizedType()) {
            superclasstype = ((ParameterizedType)supertype).getClassType();
         } else {
            superclasstype = (ClassType)supertype;
         }

         if (superclasstype.isInterfaceType()) {
            for(int j = 0; j < this.data().superTypes.size(); ++j) {
               if (i != j) {
                  Type supertype2 = this.data().superTypes.get(j);
                  ClassType superclasstype2;
                  if (supertype2.isParameterizedType()) {
                     superclasstype2 = ((ParameterizedType)supertype2).getClassType();
                  } else {
                     superclasstype2 = (ClassType)supertype2;
                  }

                  if (superclasstype2.getAllSuperClasstypes().contains(superclasstype)) {
                     continue label527;
                  }
               }
            }
         }

         MapOfInt<Member> expFields = superclasstype.getAllExplicitMemberFields();
         expFields.DEFAULT_ITERATOR.init();

         while(expFields.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = expFields.DEFAULT_ITERATOR.nextKey();
            Member field = expFields.DEFAULT_ITERATOR.nextValue();
            if (!Modifiers.isJavaPrivate(field.getModifiers())) {
               Type containingType = superclasstype.getContainingTypeOfMember(field);
               if (containingType.isClassType()) {
                  if (field.getEnclosingClassType() != containingType) {
                     this.data().containingTypesOfMembers.put(field, containingType);
                  }
               } else if (supertype.isClassType()) {
                  Type e;
                  if (supertype.isRawType()) {
                     e = ((ParameterizedType)containingType).getClassType();
                  } else {
                     e = containingType;
                  }

                  if (field.getEnclosingClassType() != e) {
                     this.data().containingTypesOfMembers.put(field, e);
                  }
               } else {
                  try {
                     Type type = ((ParameterizedType)supertype).replaceType(containingType);
                     if (field.getEnclosingClassType() != type) {
                        this.data().containingTypesOfMembers.put(field, type);
                     }
                  } catch (UnknownEntityException ignored) {
                  }
               }

               this.data().allExplicitMemberFields.insert(identifier, field);
            }
         }

         MapOfInt<Member> fields = superclasstype.getAllMemberFields();
         fields.DEFAULT_ITERATOR.init();

         label505:
         while(fields.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = fields.DEFAULT_ITERATOR.nextKey();
            Member field = fields.DEFAULT_ITERATOR.nextValue();
            if (!Modifiers.isJavaPrivate(field.getModifiers())) {
               Type containingType = superclasstype.getContainingTypeOfMember(field);
               if (containingType.isClassType()) {
                  if (field.getEnclosingClassType() != containingType) {
                     this.data().containingTypesOfMembers.put(field, containingType);
                  }
               } else if (supertype.isClassType()) {
                  Type method2;
                  if (supertype.isRawType()) {
                     method2 = ((ParameterizedType)containingType).getClassType();
                  } else {
                     method2 = containingType;
                  }

                  if (field.getEnclosingClassType() != method2) {
                     this.data().containingTypesOfMembers.put(field, method2);
                  }
               } else {
                  try {
                     Type type = ((ParameterizedType)supertype).replaceType(containingType);
                     if (field.getEnclosingClassType() != type) {
                        this.data().containingTypesOfMembers.put(field, type);
                     }
                  } catch (UnknownEntityException ignored) {
                  }
               }

               if (this.data().memberFields.contains(identifier)) {
                  this.data().memberFields.DEFAULT_ITERATOR.init(identifier);

                  while(this.data().memberFields.DEFAULT_ITERATOR.hasMoreElements()) {
                     Member memberfield = this.data().memberFields.DEFAULT_ITERATOR.nextValue();
                     if (memberfield.isExplicitOverriding() && memberfield.getExplicitOverridingType() == this.getContainingTypeOfMember(field)) {
                        memberfield.declareOverriddenMember(field);
                        continue label505;
                     }
                  }

                  this.data().memberFields.DEFAULT_ITERATOR.init(identifier);

                  while(this.data().memberFields.DEFAULT_ITERATOR.hasMoreElements()) {
                     Member memberfield = this.data().memberFields.DEFAULT_ITERATOR.nextValue();
                     if (!memberfield.isExplicitOverriding()) {
                        if (this.haveOverridingFieldModifiers(memberfield, field)) {
                           memberfield.declareOverriddenMember(field);
                           continue label505;
                        }

                        if (this.haveHidingFieldModifiers(memberfield, field)) {
                           continue label505;
                        }
                     }
                  }
               }

               if (this.data().allMemberFields.contains(identifier)) {
                  this.data().allMemberFields.DEFAULT_ITERATOR.init(identifier);

                  while(this.data().allMemberFields.DEFAULT_ITERATOR.hasMoreElements()) {
                     Member field2 = this.data().allMemberFields.DEFAULT_ITERATOR.nextValue();
                     if (field != field2) {
                        continue label505;
                     }
                  }
               }

               if (field.getEnclosingClassType().isInterfaceType() && this.data().allExplicitMemberFields.contains(identifier)) {
                  this.data().allExplicitMemberFields.DEFAULT_ITERATOR.init(identifier);

                  while(this.data().allExplicitMemberFields.DEFAULT_ITERATOR.hasMoreElements()) {
                     Member field2 = this.data().allExplicitMemberFields.DEFAULT_ITERATOR.nextValue();
                     if (field != field2) {
                        continue label505;
                     }
                  }
               }

               this.data().allMemberFields.insert(identifier, field);
            }
         }
      }

      this.data().memberConstructors.DEFAULT_ITERATOR.init();

      while(this.data().memberConstructors.DEFAULT_ITERATOR.hasMoreElements()) {
         Member constructor = this.data().memberConstructors.DEFAULT_ITERATOR.nextKey();
         if (canonicEntity != this) {
            this.data().containingTypesOfMembers.put(constructor, canonicEntity);
         }
      }

      if (this.isPartial()) {
         SetOf<ClassType> types = this.getAllPartialClasstypes();
         if (types != null) {
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
               if (classtype != this) {
                  this.space.declareInheritanceDependence(this.file, classtype);
                  SetOf<Member> memberConstructors = classtype.getDeclaredConstructors();
                  memberConstructors.DEFAULT_ITERATOR.init();

                  while(memberConstructors.DEFAULT_ITERATOR.hasMoreElements()) {
                     Member constructor = memberConstructors.DEFAULT_ITERATOR.nextKey();
                     if (constructor.getEnclosingClassType() != canonicEntity) {
                        this.data().containingTypesOfMembers.put(constructor, canonicEntity);
                     }
                  }
               }
            }
         }
      }

      this.data().allMemberMethods.clear();
      this.data().memberMethods.DEFAULT_ITERATOR.init();

      while(this.data().memberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
         int identifier = this.data().memberMethods.DEFAULT_ITERATOR.nextKey();
         Member method = this.data().memberMethods.DEFAULT_ITERATOR.nextValue();
         if (method.isExplicitOverriding()) {
            this.data().allExplicitMemberMethods.insert(identifier, method);
         } else {
            this.data().allMemberMethods.insert(identifier, method);
         }

         if (canonicEntity != this) {
            this.data().containingTypesOfMembers.put(method, canonicEntity);
         }
      }

      if (this.isPartial()) {
         SetOf<ClassType> types = this.getAllPartialClasstypes();
         if (types != null) {
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
               if (classtype != this) {
                  this.space.declareInheritanceDependence(this.file, classtype);
                  MapOfInt<Member> memberMethods = classtype.getDeclaredMemberMethods();
                  memberMethods.DEFAULT_ITERATOR.init();

                  while(memberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
                     int identifier = memberMethods.DEFAULT_ITERATOR.nextKey();
                     Member method = memberMethods.DEFAULT_ITERATOR.nextValue();
                     if (!method.isExplicitOverriding()) {
                        this.data().allMemberMethods.insert(identifier, method);
                     }

                     if (method.getEnclosingClassType() != canonicEntity) {
                        this.data().containingTypesOfMembers.put(method, canonicEntity);
                     }
                  }
               }
            }
         }
      }

      label407:
      for(int i = 0; i < this.data().superTypes.size(); ++i) {
         Type supertype = this.data().superTypes.get(i);
         ClassType superclasstype;
         if (supertype.isParameterizedType()) {
            superclasstype = ((ParameterizedType)supertype).getClassType();
         } else {
            superclasstype = (ClassType)supertype;
         }

         if (superclasstype.isInterfaceType()) {
            for(int j = 0; j < this.data().superTypes.size(); ++j) {
               if (i != j) {
                  Type supertype2 = this.data().superTypes.get(j);
                  ClassType superclasstype2;
                  if (supertype2.isParameterizedType()) {
                     superclasstype2 = ((ParameterizedType)supertype2).getClassType();
                  } else {
                     superclasstype2 = (ClassType)supertype2;
                  }

                  if (superclasstype2.getAllSuperClasstypes().contains(superclasstype)) {
                     continue label407;
                  }
               }
            }
         }

         MapOfInt<Member> explMethods = superclasstype.getAllExplicitMemberMethods();
         explMethods.DEFAULT_ITERATOR.init();

         while(explMethods.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = explMethods.DEFAULT_ITERATOR.nextKey();
            Member method = explMethods.DEFAULT_ITERATOR.nextValue();

            try {
               if ((
                     !this.isInterfaceType()
                        || i == this.data().superTypes.size() - 1
                        || method.getEnclosingClassType() != this.space.getRootClasstype(this.file)
                  )
                  && !Modifiers.isJavaPrivate(method.getModifiers())) {
                  Type containingType = superclasstype.getContainingTypeOfMember(method);
                  if (containingType.isClassType()) {
                     if (method.getEnclosingClassType() != containingType) {
                        this.data().containingTypesOfMembers.put(method, containingType);
                     }
                  } else if (supertype.isClassType()) {
                     Type var61;
                     if (supertype.isRawType()) {
                        var61 = ((ParameterizedType)containingType).getClassType();
                     } else {
                        var61 = containingType;
                     }

                     if (method.getEnclosingClassType() != var61) {
                        this.data().containingTypesOfMembers.put(method, var61);
                     }
                  } else {
                     try {
                        Type type = ((ParameterizedType)supertype).replaceType(containingType);
                        if (method.getEnclosingClassType() != type) {
                           this.data().containingTypesOfMembers.put(method, type);
                        }
                     } catch (UnknownEntityException ignored) {
                     }
                  }

                  this.data().allExplicitMemberMethods.insert(identifier, method);
               }
            } catch (UnknownEntityException ignored) {
            }
         }

         MapOfInt<Member> methods = superclasstype.getAllMemberMethods();
         methods.DEFAULT_ITERATOR.init();

         label384:
         while(methods.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = methods.DEFAULT_ITERATOR.nextKey();
            Member method = methods.DEFAULT_ITERATOR.nextValue();

            try {
               if ((
                     !this.isInterfaceType()
                        || i == this.data().superTypes.size() - 1
                        || method.getEnclosingClassType() != this.space.getRootClasstype(this.file)
                  )
                  && !Modifiers.isJavaPrivate(method.getModifiers())) {
                  Type containingType = superclasstype.getContainingTypeOfMember(method);
                  if (containingType.isClassType()) {
                     if (method.getEnclosingClassType() != containingType) {
                        this.data().containingTypesOfMembers.put(method, containingType);
                     }
                  } else if (supertype.isClassType()) {
                     Type var69;
                     if (supertype.isRawType()) {
                        var69 = ((ParameterizedType)containingType).getClassType();
                     } else {
                        var69 = containingType;
                     }

                     if (method.getEnclosingClassType() != var69) {
                        this.data().containingTypesOfMembers.put(method, var69);
                     }
                  } else {
                     try {
                        Type type = ((ParameterizedType)supertype).replaceType(containingType);
                        if (method.getEnclosingClassType() != type) {
                           this.data().containingTypesOfMembers.put(method, type);
                        }
                     } catch (UnknownEntityException ignored) {
                     }
                  }

                  if (this.data().memberMethods.contains(identifier)) {
                     this.data().memberMethods.DEFAULT_ITERATOR.init(identifier);

                     while(this.data().memberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
                        Member membermethod = this.data().memberMethods.DEFAULT_ITERATOR.nextValue();
                        if (this.haveMatchingOverrideMethodSignatures(canonicEntity, membermethod, method)
                           && membermethod.isExplicitOverriding()
                           && membermethod.getExplicitOverridingType() == this.getContainingTypeOfMember(method)) {
                           membermethod.declareOverriddenMember(method);
                           continue label384;
                        }
                     }

                     this.data().memberMethods.DEFAULT_ITERATOR.init(identifier);

                     while(this.data().memberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
                        Member membermethod = this.data().memberMethods.DEFAULT_ITERATOR.nextValue();
                        if (this.haveMatchingOverrideMethodSignatures(canonicEntity, membermethod, method) && !membermethod.isExplicitOverriding()) {
                           if (this.haveOverridingMethodModifiers(membermethod, method)) {
                              membermethod.declareOverriddenMember(method);
                              continue label384;
                           }

                           if (this.haveHidingMethodModifiers(membermethod, method)) {
                              continue label384;
                           }
                        }
                     }
                  }

                  if (this.data().allMemberMethods.contains(identifier)) {
                     this.data().allMemberMethods.DEFAULT_ITERATOR.init(identifier);

                     while(this.data().allMemberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
                        Member method2 = this.data().allMemberMethods.DEFAULT_ITERATOR.nextValue();
                        if (method != method2 && this.haveMatchingOverrideMethodSignatures(canonicEntity, method2, method)) {
                           continue label384;
                        }
                     }
                  }

                  if (method.getEnclosingClassType().isInterfaceType() && this.data().allExplicitMemberMethods.contains(identifier)) {
                     this.data().allExplicitMemberMethods.DEFAULT_ITERATOR.init(identifier);

                     while(this.data().allExplicitMemberMethods.DEFAULT_ITERATOR.hasMoreElements()) {
                        Member method2 = this.data().allExplicitMemberMethods.DEFAULT_ITERATOR.nextValue();
                        if (method != method2 && this.haveMatchingOverrideMethodSignatures(canonicEntity, method2, method)) {
                           continue label384;
                        }
                     }
                  }

                  this.data().allMemberMethods.insert(identifier, method);
               }
            } catch (UnknownEntityException ignored) {
            }
         }
      }
   }

   private boolean haveMatchingOverrideMethodSignatures(Type type, Member method, Member overriddenmethod) {
      try {
         if (method.getParameterCount() != overriddenmethod.getParameterCount()) {
            return false;
         } else if (method.getMethodParametertypeCount(type) != overriddenmethod.getMethodParametertypeCount(type)) {
            return false;
         } else {
            if (method.getMethodParametertypeCount(type) > 0) {
               if (!this.file.getLanguage().getTypeSystem().inheritsBoundTypes()) {
                  int count = method.getMethodParametertypeCount(type);

                  for(int i = 0; i < count; ++i) {
                     MethodParameterType methodparametertype1 = method.getMethodParameterType(i);
                     MethodParameterType methodparametertype2 = overriddenmethod.getMethodParameterType(i);
                     int c = methodparametertype1.getBoundTypeCount();
                     if (c != methodparametertype2.getBoundTypeCount()) {
                        return false;
                     }

                     for(int j = 0; j < c; ++j) {
                        Type boundtype1 = methodparametertype1.getBoundType(type, j);
                        Type boundtype2 = methodparametertype2.getBoundType(type, j);
                        if (!boundtype1.isEqualTo(boundtype2, method, overriddenmethod)) {
                           return false;
                        }
                     }
                  }
               }

               int c = method.getParameterCount();

               for(int i = 0; i < c; ++i) {
                  if (method.getModifiersOfParameter(i) != overriddenmethod.getModifiersOfParameter(i)) {
                     return false;
                  }

                  if (!method.getTypeOfParameter(type, i).isEqualTo(overriddenmethod.getTypeOfParameter(type, i), method, overriddenmethod)) {
                     return false;
                  }
               }
            } else {
               int c = method.getParameterCount();

               for(int i = 0; i < c; ++i) {
                  if (method.getModifiersOfParameter(i) != overriddenmethod.getModifiersOfParameter(i)) {
                     return false;
                  }

                  Type typeOfParameter = method.getTypeOfParameter(type, i);
                  Type typeOfOveriddenParameter = overriddenmethod.getTypeOfParameter(type, i);
                  if (!typeOfParameter.isEqualTo(typeOfOveriddenParameter, method, overriddenmethod)
                     && (
                        typeOfParameter.isParameterizedType() && typeOfOveriddenParameter.isParameterizedType()
                           || !typeOfParameter.getErasedType().isEqualTo(typeOfOveriddenParameter.getErasedType())
                              && !typeOfOveriddenParameter.getErasedType().isEqualTo(typeOfParameter.getErasedType())
                     )) {
                     return false;
                  }
               }
            }

            return true;
         }
      } catch (UnknownEntityException var12) {
         return false;
      }
   }

   private boolean haveHidingMethodModifiers(Member method, Member overriddenmethod) {
      return !this.haveOverridingMethodModifiers(method, overriddenmethod);
   }

   private boolean haveOverridingMethodModifiers(Member method, Member overriddenmethod) {
      return !Modifiers.isNew(method.getModifiers())
         && (
            Modifiers.isAbstract(overriddenmethod.getModifiers())
               || Modifiers.isOverride(overriddenmethod.getModifiers())
               || Modifiers.isVirtual(overriddenmethod.getModifiers())
         )
         && (
            Modifiers.isAbstract(method.getModifiers())
               || overriddenmethod.getEnclosingClassType().isInterfaceType()
               || Modifiers.isOverride(method.getModifiers())
         );
   }

   private boolean haveHidingFieldModifiers(Member field, Member overriddenfield) {
      return !this.haveOverridingFieldModifiers(field, overriddenfield);
   }

   private boolean haveOverridingFieldModifiers(Member field, Member overriddenfield) {
      return !Modifiers.isNew(field.getModifiers())
         && (
            Modifiers.isAbstract(overriddenfield.getModifiers())
               || Modifiers.isOverride(overriddenfield.getModifiers())
               || Modifiers.isVirtual(overriddenfield.getModifiers())
         )
         && (
            Modifiers.isAbstract(field.getModifiers())
               || overriddenfield.getEnclosingClassType().isInterfaceType()
               || Modifiers.isOverride(field.getModifiers())
         );
   }

   private void loadSuperClasstypes() {
      this.loadSyntax();
      if (!this.data().superClasstypesLoaded) {
         if (this.data().hasSuperClasstypes) {
            this.data().superClasstypesLoading = true;
            this.space.loadSuperClasstypes(this.file, this);
            this.data().superClasstypesLoading = false;
         } else {
            this.space.loadDefaultSuperClasstypes(this.file, this);
         }
      }
   }

   private void loadSupertypes() {
      this.loadSuperClasstypes();
      if (!this.data().superTypesLoaded) {
         boolean mustAnalyze = false;
         this.data().allSuperClasstypes.DEFAULT_ITERATOR.init();

         while(this.data().allSuperClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
            if (this.data().allSuperClasstypes.DEFAULT_ITERATOR.nextKey().getAbsoluteParametertypeCount() > 0) {
               mustAnalyze = true;
               break;
            }
         }

         if (mustAnalyze) {
            this.data().superTypesLoading = true;
            this.space.loadSuperTypes(this.file, this);
            this.data().superTypesLoading = false;
            this.data().superTypesLoaded = true;
         } else {
            this.data().superTypesLoaded = true;

            for(int i = 0; i < this.data().superClasstypes.size(); ++i) {
               this.data().superTypes.set(i, this.data().superClasstypes.get(i));
            }
         }

         this.packSuperTypes();
      }
   }

   private void packSuperClasstypes() {
      if (this.isPartial()) {
         SetOf<ClassType> types = this.getAllPartialClasstypes();
         if (types != null) {
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
               if (classtype != this) {
                  classtype.loadSuperClasstypes();
                  ListOf<ClassType> superClasstypes = classtype.data().superClasstypes;
                  if (superClasstypes != null && superClasstypes.size() > 0) {
                     try {
                        if (!superClasstypes.get(0).isInterfaceType() && this.data().superClasstypes.get(0) == this.space.getRootClasstype(this.file)) {
                           this.data().superClasstypes.set(0, superClasstypes.get(0));
                        }
                     } catch (UnknownEntityException ignored) {
                     }

                     for(int i = 0; i < superClasstypes.size(); ++i) {
                        if (!this.data().superClasstypes.contains(superClasstypes.get(i))) {
                           this.data().superClasstypes.add(superClasstypes.get(i));
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.data().allSuperClasstypes == null) {
         this.data().allSuperClasstypes = new SetOf<>(this.space);
      }

      for(int i = 0; i < this.data().superClasstypes.size(); ++i) {
         this.data().allSuperClasstypes.put(this.data().superClasstypes.get(i).getAllSuperClasstypes());
      }

      this.data().allSuperClasstypes.put(this);
      if (this.data().allMemberClasstypes == null) {
         this.data().allMemberClasstypes = new MapOfInt<>(this.space);
      } else {
         this.data().allMemberClasstypes.clear();
      }

      for(int i = 0; i < this.data().superClasstypes.size(); ++i) {
         MapOfInt<ClassType> members = this.data().superClasstypes.get(i).getAllMemberClasstypes();
         members.DEFAULT_ITERATOR.init();

         while(members.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = members.DEFAULT_ITERATOR.nextKey();
            ClassType classtype = members.DEFAULT_ITERATOR.nextValue();
            if (!Modifiers.isJavaPrivate(classtype.getModifiers())) {
               this.data().allMemberClasstypes.insert(identifier, classtype);
            }
         }
      }

      this.data().memberClasstypes.DEFAULT_ITERATOR.init();

      while(this.data().memberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         int identifier = this.data().memberClasstypes.DEFAULT_ITERATOR.nextKey();
         ClassType classtype = this.data().memberClasstypes.DEFAULT_ITERATOR.nextValue();
         this.data().allMemberClasstypes.put(identifier, classtype);
      }

      this.data().allSuperClasstypes.DEFAULT_ITERATOR.init();

      while(this.data().allSuperClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         this.space.declareInheritanceDependence(this.file, this.data().allSuperClasstypes.DEFAULT_ITERATOR.nextKey());
      }
   }

   private void loadSyntax() {
      if (!this.data().syntaxLoaded) {
         this.data().syntaxLoaded = true;
         this.space.loadSyntax(this.file);
      }
   }

   private void packSuperTypes() {
      if (this.isPartial()) {
         SetOf<ClassType> types = this.getAllPartialClasstypes();
         if (types != null) {
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               ClassType classtype = types.DEFAULT_ITERATOR.nextKey();
               if (classtype != this) {
                  classtype.loadSupertypes();
                  ListOf<Type> superTypes = classtype.data().superTypes;
                  if (superTypes != null) {
                     try {
                        if (!superTypes.get(0).isInterfaceType()
                           && !this.data().superTypes.get(0).isInterfaceType()
                           && this.data().superTypes.get(0) == this.space.getRootClasstype(this.file)) {
                           this.data().superTypes.set(0, superTypes.get(0));
                        }
                     } catch (UnknownEntityException ignored) {
                     }

                     for(int i = 0; i < superTypes.size(); ++i) {
                        if (!this.data().superClasstypes.contains(superTypes.get(i))) {
                           this.data().superTypes.add(superTypes.get(i));
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.data().allSuperTypes == null) {
         this.data().allSuperTypes = new SetOf<>(this.space);
      }

      for(int i = 0; i < this.data().superTypes.size(); ++i) {
         Type supertype = this.data().superTypes.get(i);
         if (supertype.isParameterizedType()) {
            ClassType classtype = ((ParameterizedType)supertype).getClassType();
            SetOf<Type> types = classtype.getAllSuperTypes();
            types.DEFAULT_ITERATOR.init();

            while(types.DEFAULT_ITERATOR.hasMoreElements()) {
               Type type = types.DEFAULT_ITERATOR.nextKey();

               try {
                  this.data().allSuperTypes.put(((ParameterizedType)supertype).replaceType(type));
               } catch (UnknownEntityException ignored) {
               }
            }
         } else if (supertype.isRawType()) {
            this.data().allSuperTypes.put(((ClassType)supertype).getAllSuperClasstypes());
         } else {
            this.data().allSuperTypes.put(((ClassType)supertype).getAllSuperTypes());
         }
      }

      this.data().allSuperTypes.put(this.parameterizeCanonical());
      if (this.data().allMemberTypes == null) {
         this.data().allMemberTypes = new MapOfInt<>(this.space);
      } else {
         this.data().allMemberTypes.clear();
      }

      for(int i = 0; i < this.data().superTypes.size(); ++i) {
         Type supertype = this.data().superTypes.get(i);
         ClassType superclasstype;
         if (supertype.isParameterizedType()) {
            superclasstype = ((ParameterizedType)supertype).getClassType();
         } else {
            superclasstype = (ClassType)supertype;
         }

         MapOfInt<Type> members = superclasstype.getAllMemberTypes();
         members.DEFAULT_ITERATOR.init();

         while(members.DEFAULT_ITERATOR.hasMoreElements()) {
            int identifier = members.DEFAULT_ITERATOR.nextKey();
            Type membertype = members.DEFAULT_ITERATOR.nextValue();
            ClassType memberclasstype;
            if (membertype.isParameterizedType()) {
               memberclasstype = ((ParameterizedType)membertype).getClassType();
            } else {
               memberclasstype = (ClassType)membertype;
            }

            if (!Modifiers.isJavaPrivate(superclasstype.getModifiers())) {
               if (supertype.isParameterizedType()) {
                  try {
                     this.data().allMemberTypes.insert(identifier, ((ParameterizedType)supertype).replaceType(membertype));
                  } catch (UnknownEntityException ignored) {
                  }
               } else if (membertype.isRawType()) {
                  this.data().allMemberTypes.insert(identifier, membertype);
               } else {
                  this.data().allMemberTypes.insert(identifier, memberclasstype);
               }
            }
         }
      }

      this.data().memberClasstypes.DEFAULT_ITERATOR.init();

      while(this.data().memberClasstypes.DEFAULT_ITERATOR.hasMoreElements()) {
         int identifier = this.data().memberClasstypes.DEFAULT_ITERATOR.nextKey();
         ClassType classtype = this.data().memberClasstypes.DEFAULT_ITERATOR.nextValue();
         this.data().allMemberTypes.put(identifier, classtype.parameterizeCanonical());
      }
   }

   private static class Data {
      public boolean syntaxLoaded;
      public boolean typesLoaded;
      public int idline;
      public int idstartcolumn;
      public int idendcolumn;
      public int modifiersstartline;
      public int modifiersendline;
      public int modifiersstartcolumn;
      public int modifiersendcolumn;
      public int startline;
      public int startcolumn;
      public int endline;
      public int endcolumn;
      public int modifiers;
      public boolean hasSuperClasstypes;
      public boolean isanonymous;
      public boolean islocal;
      public boolean inStaticMethod;
      public boolean hasDocumentation;
      public boolean superClasstypesLoaded;
      public boolean superClasstypesLoading;
      public ListOf<ClassType> superClasstypes;
      public boolean superTypesLoaded;
      public boolean superTypesLoading;
      public ListOf<Type> superTypes;
      public SetOf<ClassType> allSuperClasstypes;
      public SetOf<Type> allSuperTypes;
      public boolean cycliclyDefined;
      public boolean corruptedSupertypes;
      public MapOfInt<ClassType> allMemberClasstypes;
      public MapOfInt<Type> allMemberTypes;
      public MapOfInt<ClassType> memberClasstypes;
      public MapOfInt<ParameterType> memberParametertypes;
      public int absoluteTypeParameterCount;
      public ListOf<ParameterType> typeParameters;
      public ListOf<ParameterType> absoluteTypeParameters;
      public ListOf<Member> inheritedMethodPairs;
      public MapOfInt<Member> memberFields;
      public MapOfInt<Member> memberMethods;
      public SetOf<Member> memberConstructors;
      public MapOfInt<Member> allMemberFields;
      public MapOfInt<Member> allMemberMethods;
      public MapOfInt<Member> allExplicitMemberFields;
      public MapOfInt<Member> allExplicitMemberMethods;
      public MapOf<Member, Type> containingTypesOfMembers;
      public MapOfInt<Member> memberOperators;
      public MapOfInt<Member> allMemberOperators;
      public Type delegateOrEnumtype;
      public MapOfInt<Member> allMemberFieldsCaseInsensitive;
      public MapOfInt<Member> allMemberMethodsCaseInsensitive;
      public MapOfInt<Type> allMemberTypesCaseInsensitive;
      public MapOfInt<ClassType> memberClasstypesCaseInsensitive;
      public MapOfInt<ClassType> allMemberClasstypesCaseInsensitive;

      private Data() {
      }
   }
}
