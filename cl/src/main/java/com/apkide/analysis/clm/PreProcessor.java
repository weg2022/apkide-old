package com.apkide.analysis.clm;

import com.apkide.analysis.clm.api.FileEntry;
import com.apkide.analysis.clm.api.SyntaxTree;

import java.io.Reader;

public interface PreProcessor {
   char POS_ESCAPE_CHAR = '\uFFEE';

   void processVersion(FileEntry fileEntry);

   Reader process(FileEntry fileEntry, Reader reader, SyntaxTree ast);
}
