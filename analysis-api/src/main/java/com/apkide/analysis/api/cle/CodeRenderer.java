package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.ClassType;
import com.apkide.analysis.api.clm.Entity;
import com.apkide.analysis.api.clm.Namespace;
import com.apkide.analysis.api.clm.SyntaxTree;
import com.apkide.analysis.api.clm.Type;
import com.apkide.analysis.api.clm.Variable;
import com.apkide.analysis.api.clm.collections.MapOf;
import com.apkide.analysis.api.clm.collections.SetOf;

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
