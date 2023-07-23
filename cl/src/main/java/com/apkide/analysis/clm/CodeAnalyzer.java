package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.SyntaxTree;
import com.apkide.analysis.clm.api.Type;
import com.apkide.analysis.clm.api.collections.SetOfInt;
import com.apkide.analysis.clm.api.excpetions.UnknownEntityException;

public interface CodeAnalyzer {
   Type analyzeTypeName(SyntaxTree ast, int line, int column, String name) throws UnknownEntityException;

   void analyzeImports(SyntaxTree ast);

   void analyzeEveryIdentifiers(SyntaxTree ast, SetOfInt identifiers);

   void analyzeEveryIdentifier(SyntaxTree ast, int identifier);

   void analyzeNode(SyntaxTree ast, int node);

   void analyzeNodeWithValues(SyntaxTree ast, int node);

   void analyzeNamesAndTypes(SyntaxTree ast);

   void analyzeErrors(SyntaxTree ast);
}
