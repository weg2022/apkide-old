package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.FileEntry;
import com.apkide.analysis.clm.api.SyntaxTree;

import java.io.Reader;

public interface Parser {
   void fillSyntaxTree(Reader reader, FileEntry fileEntry, long syntaxVersion, boolean errors, SyntaxTree ast, boolean parseCode, boolean parseComments);
}
