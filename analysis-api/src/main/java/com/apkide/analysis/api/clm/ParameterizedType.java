package com.apkide.analysis.api.clm;



import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.analysis.api.clm.collections.SetOf;
import com.apkide.analysis.api.clm.excpetions.AmbiguousEntityException;
import com.apkide.analysis.api.clm.excpetions.UnknownEntityException;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class ParameterizedType extends Type {
   private EntitySpace space;
   private int id;
   private ClassType classtype;
   private Type[] absoluteargumenttypes;
   private int[] absoluteargumentvariances;
   private SetOf<Type> allSuperTypes;

   protected ParameterizedType(FileSpace fileSpace, EntitySpace space) {
      super(fileSpace,space, 16);
      this.space = space;
   }

   protected ParameterizedType(FileSpace fileSpace, EntitySpace space, ClassType classtype, Type[] absoluteargumenttypes, int[] absoluteargumentvariances) {
      super(fileSpace,space, 16);
      this.space = space;
      this.id = space.declareEntity(this);
      this.classtype = classtype;
      this.absoluteargumenttypes = absoluteargumenttypes;
      this.absoluteargumentvariances = absoluteargumentvariances;
      this.allSuperTypes = null;
   }

   @Override
   protected void load(@NonNull StoreInputStream stream) throws IOException {
      super.load(stream);
      this.id = stream.readInt();
      this.classtype = (ClassType)this.space.getEntity(stream.readInt());
      int count = stream.readInt();
      this.absoluteargumenttypes = new Type[count];
      this.absoluteargumentvariances = new int[count];

      for(int i = 0; i < this.absoluteargumenttypes.length; ++i) {
         this.absoluteargumenttypes[i] = (Type)this.space.getEntity(stream.readInt());
         this.absoluteargumentvariances[i] = stream.readInt();
      }

      if (stream.readBoolean()) {
         this.allSuperTypes = new SetOf<>(this.space, stream);
      }
   }

   @Override
   protected void store(@NonNull StoreOutputStream stream) throws IOException {
      super.store(stream);
      stream.writeInt(this.id);
      stream.writeInt(this.space.getID(this.classtype));
      stream.writeInt(this.absoluteargumenttypes.length);

      for(int i = 0; i < this.absoluteargumenttypes.length; ++i) {
         stream.writeInt(this.space.getID(this.absoluteargumenttypes[i]));
         stream.writeInt(this.absoluteargumentvariances[i]);
      }

      stream.writeBoolean(this.allSuperTypes != null);
      if (this.allSuperTypes != null) {
         this.allSuperTypes.store(stream);
      }
   }

   public void invalidate() {
      this.allSuperTypes = null;
   }

   public Type[] getAbsoluteArgumentTypes() {
      return this.absoluteargumenttypes;
   }

   public int[] getAbsoluteArgumentVariances() {
      return this.absoluteargumentvariances;
   }

   @Override
   public boolean isInterfaceType() {
      return this.getClassType().isInterfaceType();
   }

   @Override
   public FileEntry getFile() {
      return this.getClassType().getFile();
   }

   @Override
   public boolean isSealedType() {
      return this.getClassType().isSealedType();
   }

   @Override
   public boolean isDelegateType() {
      return this.getClassType().isDelegateType();
   }

   @Override
   public boolean isStructType() {
      return this.getClassType().isStructType();
   }

   @Override
   public boolean isEnumType() {
      return this.getClassType().isEnumType();
   }

   @Override
   public Type accessMemberType(int identifier, boolean caseSensitive, int parameterCount, Entity referingClasstypeOrPackage, FileEntry file) throws UnknownEntityException, AmbiguousEntityException {
      ClassType classtype = this.getClassType();
      Type member = classtype.accessMemberType(identifier, caseSensitive, parameterCount, referingClasstypeOrPackage, file);
      if (member.isClassType()) {
         member = ((ClassType)member).parameterizeCanonical();
      }

      return this.replaceType(member);
   }

   public Type parameterize(Type[] argumenttypes) {
      int[] variances = new int[argumenttypes.length];

      return this.parameterize(argumenttypes, variances);
   }

   public Type parameterize(Type[] argumenttypes, int[] variances) {
      ClassType classtype = this.getClassType();
      int len = classtype.getParametertypeCount();
      int absolutelen = classtype.getAbsoluteParametertypeCount();
      if (len < absolutelen) {
         Type[] oldargtypes = this.getAbsoluteArgumentTypes();
         int[] oldvariances = this.getAbsoluteArgumentVariances();
         Type[] absoluteargumenttypes = new Type[absolutelen];
         int[] absolutevariances = new int[absolutelen];

         for(int i = 0; i < absolutelen - len; ++i) {
            absoluteargumenttypes[i] = oldargtypes[i];
            absolutevariances[i] = oldvariances[i];
         }

         for(int i = absolutelen - len; i < absolutelen; ++i) {
            absoluteargumenttypes[i] = argumenttypes[i - absolutelen + len];
            absolutevariances[i] = variances[i - absolutelen + len];
         }

         argumenttypes = absoluteargumenttypes;
         variances = this.absoluteargumentvariances;
      }

      return this.space.getParameterizedtype(classtype, argumenttypes, variances);
   }

   public SetOf<Type> getAllSuperTypes() {
      if (this.allSuperTypes == null) {
         this.allSuperTypes = new SetOf<>(this.space);
         SetOf<Type> allSuperTypesOfClasstype = this.classtype.getAllSuperTypes();
         allSuperTypesOfClasstype.DEFAULT_ITERATOR.init();

         while(allSuperTypesOfClasstype.DEFAULT_ITERATOR.hasMoreElements()) {
            try {
               this.allSuperTypes.put(this.replaceType((Type)allSuperTypesOfClasstype.DEFAULT_ITERATOR.nextKey()));
            } catch (UnknownEntityException var3) {
            }
         }
      }

      return this.allSuperTypes;
   }

   public Type replaceType(Type type) throws UnknownEntityException {
      return this.replaceType(type, true);
   }

   public Type replaceType(Type type, boolean readaccess) throws UnknownEntityException {
      if (type.isArrayType()) {
         return this.space.getArraytype(this.replaceType(((ArrayType)type).getElementType()), ((ArrayType)type).getDimension());
      } else if (type.isParameterType()) {
         for(int i = 0; i < this.absoluteargumenttypes.length; ++i) {
            try {
               if (this.classtype.getAbsoluteParametertype(i) == type) {
                  return this.absoluteargumenttypes[i];
               }
            } catch (UnknownEntityException var11) {
            }
         }

         return type;
      } else if (!type.isParameterizedType()) {
         return type;
      } else {
         Type[] memberargumenttypes = ((ParameterizedType)type).getAbsoluteArgumentTypes();
         int[] membervariances = ((ParameterizedType)type).getAbsoluteArgumentVariances();
         int[] replacedvariances = new int[memberargumenttypes.length];
         Type[] replacedtypes = new Type[memberargumenttypes.length];

         label100:
         for(int i = 0; i < memberargumenttypes.length; ++i) {
            Type membertype = memberargumenttypes[i];

            for(int j = 0; j < this.absoluteargumenttypes.length; ++j) {
               if (this.classtype.getAbsoluteParametertype(j) == membertype) {
                  Type argumentboundtype = this.classtype.getAbsoluteParametertype(j).getErasedType();
                  replacedtypes[i] = this.absoluteargumenttypes[j];
                  switch(membervariances[i]) {
                     case 0:
                        if (readaccess) {
                           replacedvariances[i] = this.absoluteargumentvariances[j];
                        } else {
                           switch(this.absoluteargumentvariances[j]) {
                              case 0:
                                 replacedvariances[i] = this.absoluteargumentvariances[j];
                                 continue label100;
                              case 1:
                              case 2:
                              case 3:
                                 throw new UnknownEntityException();
                           }
                        }
                        continue label100;
                     case 1:
                     case 2:
                        if (readaccess) {
                           switch(this.absoluteargumentvariances[j]) {
                              case 0:
                              case 1:
                              case 2:
                                 replacedvariances[i] = 2;
                                 if (argumentboundtype == this.absoluteargumenttypes[j]) {
                                    replacedvariances[i] = 1;
                                 }
                                 continue label100;
                              case 3:
                                 replacedvariances[i] = 1;
                                 replacedtypes[i] = argumentboundtype;
                           }
                        } else {
                           switch(this.absoluteargumentvariances[j]) {
                              case 0:
                              case 3:
                                 replacedvariances[i] = 2;
                                 if (argumentboundtype == this.absoluteargumenttypes[j]) {
                                    replacedvariances[i] = 1;
                                 }
                                 continue label100;
                              case 1:
                              case 2:
                                 throw new UnknownEntityException();
                           }
                        }
                        continue label100;
                     case 3:
                        if (readaccess) {
                           switch(this.absoluteargumentvariances[j]) {
                              case 0:
                              case 3:
                                 replacedvariances[i] = 3;
                                 if (argumentboundtype == this.absoluteargumenttypes[j]) {
                                    replacedvariances[i] = 0;
                                 }
                                 continue label100;
                              case 1:
                              case 2:
                                 replacedvariances[i] = 1;
                                 replacedtypes[i] = argumentboundtype;
                           }
                        } else {
                           switch(this.absoluteargumentvariances[j]) {
                              case 0:
                              case 1:
                              case 2:
                                 replacedvariances[i] = 3;
                                 if (argumentboundtype == this.absoluteargumenttypes[j]) {
                                    replacedvariances[i] = 0;
                                 }
                                 continue label100;
                              case 3:
                                 replacedvariances[i] = 0;
                                 replacedtypes[i] = argumentboundtype;
                           }
                        }
                     default:
                        continue label100;
                  }
               }
            }

            replacedtypes[i] = this.replaceType(membertype, readaccess);
            replacedvariances[i] = membervariances[i];
         }

         return this.space.getParameterizedtype(((ParameterizedType)type).getClassType(), replacedtypes, replacedvariances);
      }
   }

   public ClassType getClassType() {
      return this.classtype;
   }

   @Override
   public int getID() {
      return this.id;
   }

   @Override
   public Language getLanguage() {
      return this.getClassType().getLanguage();
   }
}
