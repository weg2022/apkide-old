package com.apkide.analysis.clm;

public interface Language {
   String getName();

   void shrink();

   ArchiveReader getArchiveReader();

   PreProcessor getPreProcessor();

   Parser getParser();

   Syntax getSyntax();

   SignatureAnalyzer getSignatureAnalyzer();

   TypeSystem getTypeSystem();

   CodeRenderer getRenderer();

   CodeAnalyzer getCodeAnalyzer();

   ToolsAbstraction getTools();

   Compiler getCompiler();

}
