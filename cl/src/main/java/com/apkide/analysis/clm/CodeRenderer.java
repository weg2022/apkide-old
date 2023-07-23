package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.ClassType;
import com.apkide.analysis.clm.api.Entity;
import com.apkide.analysis.clm.api.Namespace;
import com.apkide.analysis.clm.api.SyntaxTree;
import com.apkide.analysis.clm.api.Type;
import com.apkide.analysis.clm.api.Variable;
import com.apkide.analysis.clm.api.collections.MapOf;
import com.apkide.analysis.clm.api.collections.SetOf;

public interface CodeRenderer {
   String renderParameter(String typeName, String identifier);

   String renderTypeName(SyntaxTree ast, int line, int column, Type type);

   String renderDefaultValueString(Type type);

   String renderStaticImports(SyntaxTree ast, MapOf<ClassType, Entity> imports);

   String renderFileNamespace(Namespace namespace);

   String renderClassNamespaceStart(Namespace namespace);

   String renderClassNamespaceEnd(Namespace namespace);

   String renderImports(SyntaxTree ast, MapOf<ClassType, Entity> imports);

   String renderImports(SyntaxTree ast, int line, int column, SetOf<? extends Type> imports);

   String renderImports(SyntaxTree ast, int line, int column, SetOf<? extends Type> imports, SetOf<Entity> set);

   String getModifierString(int modifier);

   String getHTMLString(Variable variable);

   String getNameString(Entity entity);

   String getFullyQualifiedNameString(Entity entity);

   String getHTMLString(Entity entity);
}
