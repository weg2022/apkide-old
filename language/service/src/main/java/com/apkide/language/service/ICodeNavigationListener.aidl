package com.apkide.language.service;

import com.apkide.language.FileSpan;
import com.apkide.language.Symbol;

interface ICodeNavigationListener {

   void searchUsagesFinished(in String filePath,in int line,in int column,
                             in List<FileSpan> list);

   void searchSymbolFinished(in String filePath,in int line,in int column,
                             in boolean includeDeclaration,
                             in List<Symbol> list);
}