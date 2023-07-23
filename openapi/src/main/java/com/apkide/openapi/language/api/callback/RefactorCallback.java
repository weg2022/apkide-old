package com.apkide.openapi.language.api.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

public interface RefactorCallback {

    void pasteText(@NonNull FileEntry file, int line, int column);

    void cutText(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void replaceText(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn, @NonNull String newText);

    void moveText(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn, int line, int column);

    void copyText(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn, int line, int column);

    void selectText(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void indentLines(@NonNull FileEntry file, int startLine, int endLine);

    void renameFile(@NonNull FileEntry file, @NonNull String name);

    void moveFile(@NonNull FileEntry file, @NonNull FileEntry newFile);

    void deleteFile(@NonNull FileEntry file);

    void reportUnSupportedOfRefactoring(@NonNull FileEntry file);

    void reportWarning(@NonNull FileEntry file, @NonNull String msg);

    void analysisStarted(@NonNull FileEntry file);

    void renameAnalysisFinished(@NonNull FileEntry file);

    void reportRenameProblem(@NonNull FileEntry file, @NonNull String msg);

    void moveAnalysisFinished(@NonNull FileEntry file);

    void reportMoveProblem(@NonNull FileEntry file, @NonNull String msg);

    void fixAnalysisFinished(@NonNull FileEntry file);

    void reportFixProblem(@NonNull FileEntry file, @NonNull String msg);

    void documentationAnalysisFinished(@NonNull FileEntry file);

    void reportDocumentationProblem(@NonNull FileEntry file, @NonNull String msg);

    void createAnalysisFinished(@NonNull FileEntry file);

    void reportCreateProblem(@NonNull FileEntry file, @NonNull String msg);

    void implementationAnalysisFinished(@NonNull FileEntry file);

    void reportImplementationProblem(@NonNull FileEntry file, @NonNull String msg);

    void introduceAnalysisFinished(@NonNull FileEntry file);

    void reportIntroduceProblem(@NonNull FileEntry file, @NonNull String msg);

    void extractAnalysisFinished(@NonNull FileEntry file);

    void reportExtractProblem(@NonNull FileEntry file, @NonNull String msg);

    void inlineVariableAnalysisFinished(@NonNull FileEntry file);

    void reportInlineVariableProblem(@NonNull FileEntry file, @NonNull String msg);

    void optimizeImportAnalysisFinished(@NonNull FileEntry file);

    void reportOptimizeImportProblem(@NonNull FileEntry file, @NonNull String msg);

    void surroundAnalysisFinished(@NonNull FileEntry file);

    void reportSurroundProblem(@NonNull FileEntry file, @NonNull String msg);


}
