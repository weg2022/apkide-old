package com.apkide.analysis.api.clm.callback;


import com.apkide.analysis.api.clm.FileEntry;

import java.util.Hashtable;

public interface RefactoringCallback {
   void pasteText(FileEntry fileEntry, int line, int column);

   void clipText(FileEntry fileEntry,  int startLine, int startColumn, int endLine, int endColumn);

   void selectText(FileEntry fileEntry, int startLine, int startColumn, int endLine, int endColumn);

   void replaceText(FileEntry fileEntry, int startLine, int startColumn, int endLine, int endColumn, String newText);

   void moveText(FileEntry fileEntry,  int startLine, int startColumn, int endLine, int endColumn, int line, int column);

   void copyText(FileEntry fileEntry,  int startLine, int startColumn, int endLine, int endColumn, int line, int column);

   void indentLines(FileEntry fileEntry, int startLine, int endLine);

   void renameFileOrDir(FileEntry fileEntry, String newName);

   void moveFileOrDir(FileEntry fileEntry, FileEntry newFileEntry);

   void deleteFile(FileEntry fileEntry);

   void warningFound(String warning);

   void refactoringNotSupported();

   void analysisStarted();

   void renameAnalysisFinished();

   void renameProblemFound(String problem);

   void moveTargetFound(String target, String qualifiedName);

   void moveProblemFound(String problem);

   void moveAnalysisFinished();

   void inlineMethodProblemFound(String problem);

   void inlineMethodAnalysisFinished();

   void fixAnalysisFinished();

   void documentationProblemFound(String problem);

   void documentationAnalysisFinished();

   void createProblemFound(String problem);

   void createAnalysisFinished();

   void implementProblemFound(String problem);

   void implementAnalysisFinished();

   void extractProblemFound(String problem);

   void extractAnalysisFinished();

   void introduceAnalysisFinished();

   void inlineVariableAnalysisFinished();

   void introduceProblemFound(String problem);

   void organizeAmbiguousTypesFound(Hashtable<String, String[]> types);

   void organizeAnalysisFinished();

   void surroundProblemFound(String problem);

   void surroundAnalysisFinished();
}
