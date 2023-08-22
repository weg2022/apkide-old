package com.apkide.language.service;

import com.apkide.language.Modification;

interface ICodeRefactoringListener {

     void optimizeImportsFinished(in String filePath,in List<Modification> list);

     void prepareRenameFinished(in String filePath,in int line,in int column,
                           in String newName,in List<Modification> list);


     void renameFinished(in String filePath,in int line,in int column, in String newName,
                         in List<Modification> list);


     void prepareInlineFinished(in String filePath,in int line,in int column,
                                in List<Modification> list);


     void inlineFinished(in String filePath,in int line,in int column,
                         in List<Modification> list);

     void commentFinished(in String filePath,in int startLine,in int startColumn,
                          in int endLine,in int endColumn,in List<Modification> list);


     void removeCommentFinished(in String filePath,in int startLine,in int startColumn,
                                in int endLine,in int endColumn,in List<Modification> list);


     void safeDeleteFinisehd(in String filePath,in int line,in int column,
                             in List<Modification> list);


     void surroundWithFinished(in String filePath,in int line,in int column,
                               in List<Modification> list);

}