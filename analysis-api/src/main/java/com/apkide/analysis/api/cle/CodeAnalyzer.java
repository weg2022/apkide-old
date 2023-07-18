package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.SyntaxTree;
import com.apkide.analysis.api.clm.Type;
import com.apkide.analysis.api.clm.excpetions.UnknownEntityException;
import com.apkide.common.collections.SetOfInt;

public interface CodeAnalyzer {
    Type analyzeTypeName(SyntaxTree ast, int var2, int var3, String name) throws UnknownEntityException;

    void analyzeImports(SyntaxTree ast);

    void analyzeEveryIdentifiers(SyntaxTree ast, SetOfInt identifiers);

    void analyzeEveryIdentifier(SyntaxTree ast, int identifier);

    void analyzeNode(SyntaxTree ast, int node);

    void analyzeNodeWithValues(SyntaxTree ast, int node);

    void analyzeNamesAndTypes(SyntaxTree ast);

    void analyzeErrors(SyntaxTree ast);
}
