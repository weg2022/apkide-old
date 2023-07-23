package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.ClassType;
import com.apkide.analysis.clm.api.FileEntry;
import com.apkide.analysis.clm.api.Namespace;
import com.apkide.analysis.clm.api.Type;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;

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
