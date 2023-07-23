package com.apkide.analysis.clm.api;

import static com.apkide.analysis.clm.api.TypeSemantic.UNKNOWN_SEMANTIC;
import static com.apkide.analysis.clm.api.Variance.BIVARIANCE;
import static com.apkide.analysis.clm.api.Variance.CONTRAVARIANCE;
import static com.apkide.analysis.clm.api.Variance.COVARIANCE;
import static com.apkide.analysis.clm.api.Variance.INVARIANCE;

import com.apkide.analysis.clm.api.collections.MapOfInt;
import com.apkide.analysis.clm.api.collections.SetOf;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;
import com.apkide.analysis.clm.api.excpetions.AmbiguousEntityException;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;

import java.io.IOException;

public class Type extends Entity {
    private final EntitySpace mySpace;
    private int mySemantic;

    public Type(EntitySpace space, int semantic) {
        super(space);
        this.mySpace = space;
        this.mySemantic = semantic;
    }

    public Type(EntitySpace space) {
        super(space);
        this.mySpace = space;
    }

    public boolean isReferable(FileEntry file) {
        return this.getFile() == null || this.getFile().isReferable(file);
    }

    public Type accessSuperFields(FileEntry file, int identifier, boolean caseSensitive, ClassType enclosingClassType, SetOf<Member> fields) {
        try {
            Type superType = this.getSuperType();
            superType.accessFields(file, identifier, caseSensitive, (ClassType) superType.getErasedType(), fields);
        } catch (UnknownEntityException ignored) {
        }

        return this;
    }

    public Type accessSuperMethods(FileEntry file, int identifier, boolean caseSensitive, ClassType enclosingClassType, SetOf<Member> methods) {
        try {
            Type superType = this.getSuperType();
            superType.accessMethods(file, identifier, caseSensitive, (ClassType) superType.getErasedType(), methods);
        } catch (UnknownEntityException ignored) {
        }

        return this;
    }

    public Type accessConstructors(ClassType referingClassType, SetOf<Member> constructors) {
        if (this.isParameterizedType()) {
            return ((ParameterizedType) this).getClassType().accessConstructors(referingClassType, constructors) == null ? null : this;
        } else {
            return null;
        }
    }

    public Type accessMethods(FileEntry file, int identifier, boolean caseSensitive, ClassType referingClassType, SetOf<Member> methods) {
        methods.clear();
        if (this.isParameterizedType()) {
            return ((ParameterizedType) this).getClassType().accessMethods(file, identifier, caseSensitive, referingClassType, methods) == null ? null : this;
        } else {
            try {
                if (this.isArrayType()) {
                    ClassType arraySuperClassType = this.mySpace.getArraySuperClasstype(file);
                    return arraySuperClassType.accessMethods(file, identifier, caseSensitive, arraySuperClassType, methods);
                } else if (this.isParameterType()) {
                    if (((ParameterType) this).getBoundTypeCount() == 1) {
                        return this.getErasedType().accessMethods(file, identifier, caseSensitive, referingClassType, methods);
                    } else {
                        return this.mySpace.getRootClasstype(file).accessMethods(file, identifier, caseSensitive, referingClassType, methods);
                    }
                } else if (this.isMethodParameterType()) {
                    if (((MethodParameterType) this).getBoundTypeCount() == 1) {
                        return this.getErasedType().accessMethods(file, identifier, caseSensitive, referingClassType, methods);
                    } else {
                        return this.mySpace.getRootClasstype(file).accessMethods(file, identifier, caseSensitive, referingClassType, methods);
                    }
                }
            } catch (UnknownEntityException ignored) {
            }
            return null;
        }
    }

    public Type accessFields(FileEntry file, int identifier, boolean caseSensitive, ClassType referingClassType, SetOf<Member> fields) {
        fields.clear();
        if (this.isParameterizedType()) {
            return ((ParameterizedType) this).getClassType().accessFields(file, identifier, caseSensitive, referingClassType, fields) == null ? null : this;
        } else {
            try {
                if (this.isArrayType()) {
                    ClassType arraySuperClassType = this.mySpace.getArraySuperClasstype(file);
                    return arraySuperClassType.accessFields(file, identifier, caseSensitive, arraySuperClassType, fields);
                } else if (this.isParameterType()) {
                    if (((ParameterType) this).getBoundTypeCount() == 1)
                        return this.getErasedType().accessFields(file, identifier, caseSensitive, referingClassType, fields);
                    else
                        return this.mySpace.getRootClasstype(file).accessFields(file, identifier, caseSensitive, referingClassType, fields);

                } else if (this.isMethodParameterType()) {
                    if (((MethodParameterType) this).getBoundTypeCount() == 1)
                        return this.getErasedType().accessFields(file, identifier, caseSensitive, referingClassType, fields);
                    else
                        return this.mySpace.getRootClasstype(file).accessFields(file, identifier, caseSensitive, referingClassType, fields);

                }
            } catch (UnknownEntityException ignored) {
            }

            return null;
        }
    }

    public Type accessMemberType(int identifier, boolean caseSensitive, int parameterTypeCount, Entity referingClasstypeOrPackage, FileEntry file) throws UnknownEntityException, AmbiguousEntityException {
        if (this.isClassType()) {
            return ((ClassType) this).accessMemberClasstype(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file);
        } else if (this.isParameterizedType()) {
            return this.accessMemberType(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file);
        } else if (this.isParameterType()) {
            return ((ParameterType) this).getBoundTypeCount() == 1
                    ? this.getErasedType().accessMemberType(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file)
                    : this.mySpace.getRootClasstype(file).accessMemberType(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file);
        } else if (this.isMethodParameterType()) {
            return ((MethodParameterType) this).getBoundTypeCount() == 1
                    ? this.getErasedType().accessMemberType(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file)
                    : this.mySpace.getRootClasstype(file).accessMemberType(identifier, caseSensitive, parameterTypeCount, referingClasstypeOrPackage, file);
        } else {
            throw new UnknownEntityException();
        }
    }

    public Type getSuperType() throws UnknownEntityException {
        if (this.isParameterizedType()) {
            return ((ParameterizedType) this).replaceType(((ParameterizedType) this).getClassType().getSuperType());
        } else {
            throw new UnknownEntityException();
        }
    }

    public MapOfInt<Member> getAllOperators() {
        return this.isParameterizedType() ? ((ParameterizedType) this).getClassType().getAllOperators() : null;
    }

    public boolean isEqualTo(Type type) {
        return this == type || this.getSemantic() != UNKNOWN_SEMANTIC && this.getSemantic() == type.getSemantic();
    }

    public boolean isRawType() {
        return this.isClassType() && ((ClassType) this).getParametertypeCount() > 0;
    }

    public boolean isSubTypeOf(Type type2, Member method1, Member method2) {
        if (this == type2) {
            return true;
        } else {
            if (this.isMethodParameterType() && type2.isMethodParameterType()) {
                return ((MethodParameterType) this).getMethod() == method1
                        && ((MethodParameterType) type2).getMethod() == method2
                        && ((MethodParameterType) this).getNumber() == ((MethodParameterType) type2).getNumber();
            } else {
                if (this.isArrayType() && type2.isArrayType()) {
                    if (((ArrayType) this).getDimension() != ((ArrayType) type2).getDimension())
                        return false;

                    return ((ArrayType) this).getElementType().isSubTypeOf(((ArrayType) type2).getElementType(), method1, method2);
                }

                if (this.isParameterizedType() && type2.isParameterizedType()) {
                    if (!((ParameterizedType) this).getClassType().isSubClassTypeOf(((ParameterizedType) type2).getClassType())) {
                        return false;
                    }

                    Type[] argumentTypes = ((ParameterizedType) this).getAbsoluteArgumentTypes();
                    Type[] types = ((ParameterizedType) type2).getAbsoluteArgumentTypes();

                    for (int i = 0; i < argumentTypes.length; ++i) {
                        if (!argumentTypes[i].isEqualTo(types[i], method1, method2)) {
                            return false;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public boolean isEqualTo(Type type2, Member method1, Member method2) {
        if (this.isEqualTo(type2)) {
            return true;
        } else {
            if (this.isMethodParameterType() && type2.isMethodParameterType()) {
                return ((MethodParameterType) this).getMethod() == method1
                        && ((MethodParameterType) type2).getMethod() == method2
                        && ((MethodParameterType) this).getNumber() == ((MethodParameterType) type2).getNumber();
            } else {
                if (this.isArrayType() && type2.isArrayType()) {
                    return ((ArrayType) this).getElementType().isEqualTo(((ArrayType) type2).getElementType(), method1, method2);
                }

                if (this.isParameterizedType() && type2.isParameterizedType()) {
                    if (((ParameterizedType) this).getClassType() != ((ParameterizedType) type2).getClassType()) {
                        return false;
                    }

                    Type[] argumentTypes = ((ParameterizedType) this).getAbsoluteArgumentTypes();
                    Type[] types = ((ParameterizedType) type2).getAbsoluteArgumentTypes();

                    for (int i = 0; i < argumentTypes.length; ++i) {
                        if (!argumentTypes[i].isEqualTo(types[i], method1, method2)) {
                            return false;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public boolean isSubTypeOf(FileEntry file, Type superType) {
        SetOf<Type> superTypes;
        if (this.isClassType())
            superTypes = ((ClassType) this).getAllSuperTypes();
        else
            superTypes = ((ParameterizedType) this).getAllSuperTypes();

        if (superTypes.contains(superType)) {
            return true;
        } else {
            superTypes.DEFAULT_ITERATOR.init();

            try {
                while (superTypes.DEFAULT_ITERATOR.hasMoreElements()) {
                    Type t = superTypes.DEFAULT_ITERATOR.nextKey();
                    if (t.isParameterizedType()
                            && superType.isParameterizedType()
                            && ((ParameterizedType) t).getClassType() == ((ParameterizedType) superType).getClassType()) {
                        int[] variancesFrom = ((ParameterizedType) t).getAbsoluteArgumentVariances();
                        int[] variancesTo = ((ParameterizedType) superType).getAbsoluteArgumentVariances();
                        Type[] argumentTypesFrom = ((ParameterizedType) t).getAbsoluteArgumentTypes();
                        Type[] argumentTypesTo = ((ParameterizedType) superType).getAbsoluteArgumentTypes();

                        for (int i = 0; i < variancesTo.length; ++i) {
                            switch (variancesFrom[i]) {
                                case INVARIANCE:
                                    switch (variancesTo[i]) {
                                        case INVARIANCE:
                                            if (argumentTypesFrom[i] != argumentTypesTo[i])
                                                return false;
                                            continue;
                                        case BIVARIANCE:
                                        case COVARIANCE:
                                            if (!argumentTypesFrom[i].isImplicitConvertibleTo(file, argumentTypesTo[i]))
                                                return false;
                                            continue;
                                        case CONTRAVARIANCE:
                                            if (!argumentTypesTo[i].isImplicitConvertibleTo(file, argumentTypesFrom[i]))
                                                return false;
                                        default:
                                            continue;
                                    }
                                case BIVARIANCE:
                                case COVARIANCE:
                                    switch (variancesTo[i]) {
                                        case INVARIANCE:
                                        case CONTRAVARIANCE:
                                            return false;
                                        case BIVARIANCE:
                                        case COVARIANCE:
                                            if (!argumentTypesFrom[i].isImplicitConvertibleTo(file, argumentTypesTo[i]))
                                                return false;
                                            continue;
                                        default:
                                            continue;
                                    }
                                case CONTRAVARIANCE:
                                    switch (variancesTo[i]) {
                                        case INVARIANCE:
                                            return false;
                                        case BIVARIANCE:
                                        case COVARIANCE:
                                            return argumentTypesTo[i] == this.mySpace.getRootClasstype(file);
                                        case CONTRAVARIANCE:
                                            if (!argumentTypesTo[i].isImplicitConvertibleTo(file, argumentTypesFrom[i]))
                                                return false;
                                    }
                            }
                        }

                        return true;
                    }
                }
            } catch (UnknownEntityException ignored) {
            }

            return false;
        }
    }

    public boolean isExplicitConvertibleTo(FileEntry file, Type totype) {
        return this.mySpace.isExplicitConversion(file, this, totype);
    }

    public boolean isImplicitConvertibleTo(FileEntry file, Type totype) {
        return this.mySpace.isImplicitConversion(file, this, totype);
    }

    public Type getCommonSuperTypeWith(FileEntry file, Type type2) throws UnknownEntityException {
        if ((this.isClassType() || this.isParameterizedType()) && (type2.isClassType() || type2.isParameterizedType())) {
            Type type = this.mySpace.getRootClasstype(file);
            SetOf<ClassType> superClassTypes1 = ((ClassType) this.getErasedType()).getAllSuperClasstypes();
            SetOf<ClassType> superClassTypes2 = ((ClassType) type2.getErasedType()).getAllSuperClasstypes();
            superClassTypes1.DEFAULT_ITERATOR.init();

            while (superClassTypes1.DEFAULT_ITERATOR.hasMoreElements()) {
                ClassType t = superClassTypes1.DEFAULT_ITERATOR.nextKey();
                if (superClassTypes2.contains(t) && (t.getAllSuperClasstypes().contains(type) || type.isInterfaceType() && !t.isInterfaceType())) {
                    type = t;
                }
            }

            SetOf<Type> superTypes1;
            if (this.isParameterizedType())
                superTypes1 = ((ParameterizedType) this).getAllSuperTypes();
             else
                superTypes1 = ((ClassType) this).getAllSuperTypes();

            SetOf<Type> superTypes2;
            if (this.isParameterizedType()) {
                superTypes2 = ((ParameterizedType) this).getAllSuperTypes();
            } else
                superTypes2 = ((ClassType) this).getAllSuperTypes();

            superTypes1.DEFAULT_ITERATOR.init();

            while (superTypes1.DEFAULT_ITERATOR.hasMoreElements()) {
                Type t = superTypes1.DEFAULT_ITERATOR.nextKey();
                if (t.getErasedType() == type && type2.isImplicitConvertibleTo(file, t)) {
                    type = t;
                }
            }

            superTypes2.DEFAULT_ITERATOR.init();

            while (superTypes2.DEFAULT_ITERATOR.hasMoreElements()) {
                Type t = superTypes2.DEFAULT_ITERATOR.nextKey();
                if (t.getErasedType() == type && this.isImplicitConvertibleTo(file, t)) {
                    type = t;
                }
            }

            if (this.isParameterizedType() && type2.isParameterizedType() && type.isClassType() && ((ClassType) type).getParametertypeCount() > 0) {
                type = this.mySpace.getRootClasstype(file);
            }

            return type;
        } else {
            return this.mySpace.getRootClasstype(file);
        }
    }

    public boolean containsType(Type partType) {
        if (this == partType) {
            return true;
        } else if (this.isArrayType()) {
            return ((ArrayType) this).getElementType().containsType(partType);
        } else {
            if (this.isParameterizedType()) {
                Type[] argTypes = ((ParameterizedType) this).getAbsoluteArgumentTypes();

                for (Type argtype : argTypes) {
                    if (argtype.containsType(partType))
                        return true;
                }
            }

            return false;
        }
    }

    public boolean containsVariable() {
        if (this.isMethodParameterTypeVariable()) {
            return true;
        } else if (this.isArrayType()) {
            return ((ArrayType) this).getElementType().containsVariable();
        } else {
            if (this.isParameterizedType()) {
                Type[] argumentTypes = ((ParameterizedType) this).getAbsoluteArgumentTypes();

                for (Type absoluteargumenttype : argumentTypes) {
                    if (absoluteargumenttype.containsVariable())
                        return true;
                }
            }

            return false;
        }
    }

    public Type getNonVariableContainigType() throws UnknownEntityException {
        if (!this.containsVariable()) {
            return this;
        } else if (this.isMethodParameterTypeVariable()) {
            MethodParameterType methodparametertype = ((MethodParameterTypeVariable) this).getMethodParameterType();
            return methodparametertype.getErasedType();
        } else if (this.isArrayType()) {
            return this.mySpace.getArraytype(((ArrayType) this).getElementType().getNonVariableContainigType(), ((ArrayType) this).getDimension());
        } else if (!this.isParameterizedType()) {
            return this;
        } else {
            Type[] argumentTypes = ((ParameterizedType) this).getAbsoluteArgumentTypes();
            int[] variances = ((ParameterizedType) this).getAbsoluteArgumentVariances();
            Type[] replacementTypes = new Type[argumentTypes.length];

            for (int i = 0; i < argumentTypes.length; ++i) {
                replacementTypes[i] = argumentTypes[i].getNonVariableContainigType();
            }

            return this.mySpace.getParameterizedtype(((ParameterizedType) this).getClassType(), replacementTypes, variances);
        }
    }

    public boolean isNonInvariantType() {
        if (this.isParameterizedType()) {
            int[] variances = ((ParameterizedType) this).getAbsoluteArgumentVariances();

            for (int variance : variances) {
                if (variance != INVARIANCE)
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void load(StoreInputStream stream) throws IOException {
        super.load(stream);
        this.mySemantic = stream.readInt();
    }

    @Override
    protected void store(StoreOutputStream stream) throws IOException {
        super.store(stream);
        stream.writeInt(this.mySemantic);
    }

    public int getSemantic() {
        return this.mySemantic;
    }

    public boolean isSealedType() {
        return false;
    }

    public boolean isInterfaceType() {
        return false;
    }

    public boolean isDelegateType() {
        return false;
    }

    public boolean isStructType() {
        return false;
    }

    public boolean isEnumType() {
        return false;
    }

    public Type getErasedType() throws UnknownEntityException {
        if (this.isParameterizedType()) {
            return ((ParameterizedType) this).getClassType();
        } else if (this.isParameterType()) {
            return this.getErasedType().getErasedType();
        } else if (this.isMethodParameterType()) {
            return this.getErasedType().getErasedType();
        } else {
            return this.isArrayType()
                    ? this.mySpace.getArraytype(((ArrayType) this).getElementType().getErasedType(), ((ArrayType) this).getDimension())
                    : this;
        }
    }
}
