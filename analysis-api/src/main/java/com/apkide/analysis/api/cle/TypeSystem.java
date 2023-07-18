package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.ClassType;
import com.apkide.analysis.api.clm.FileEntry;
import com.apkide.analysis.api.clm.Namespace;
import com.apkide.analysis.api.clm.Type;
import com.apkide.analysis.api.clm.excpetions.UnknownEntityException;

public interface TypeSystem {
    boolean hasTypeparametersOfEnclosingClasstype();

    boolean inheritsBoundTypes();

    boolean supportsGenericTypeNameResolving();

    boolean supportsMethodParametertypeVariables();

    Type getConditionalOperationType(FileEntry fileEntry, Type type1, Type type2) throws UnknownEntityException;

    Type getBinaryNumericOperationType(FileEntry fileEntry, int operator, Type type1, Type type2) throws UnknownEntityException;

    boolean isImplicitConversion(FileEntry fileEntry, Type type1, Type type2);

    boolean isExplicitConversion(FileEntry fileEntry, Type type1, Type type2);

    int getTypeSemanticForClasstype(Namespace namespace, int identifier);

    int getTypeSemanticForPrimitivetype(int number);

    ClassType getRootClasstype(FileEntry fileEntry) throws UnknownEntityException;

    ClassType getArraySuperClasstype(FileEntry fileEntry) throws UnknownEntityException;
}
