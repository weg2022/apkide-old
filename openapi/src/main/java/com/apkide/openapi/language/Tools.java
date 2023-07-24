package com.apkide.openapi.language;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.api.FileEntry;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.SyntaxTree;

public interface Tools {

    void requestCompletionList(@NonNull FileEntry file, int line, int column);

    void requestClassesCompletionList(@NonNull FileEntry file);

    void requestSuperMethodsCompletionList(@NonNull FileEntry file, int line, int column);

    void documentation(@NonNull FileEntry file);

    void documentation(@NonNull FileEntry file, int line, int column);

    void safeDelete(@NonNull FileEntry file, @NonNull SyntaxTree element);

    void surroundWithTryCatch(FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void addThrows(@NonNull FileEntry file, int line, int column);

    void createVariable(@NonNull FileEntry file, int line, int column, int identifier, @NonNull ParseTree type);

    void createField(@NonNull FileEntry file, int line, int column, int modifiers, int identifier, @NonNull ParseTree type);

    void createMethod(@NonNull FileEntry file, int line, int column, int modifiers, int identifier, @NonNull ParseTree type, int[] argModifiers, @NonNull ParseTree[] argTypes, int[] argNames);

    void inlineVariable(@NonNull FileEntry file, int line, int column);

    void createSettersAndGetters(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void createConstructor(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void extractMethod(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

    void implementMembers(@NonNull FileEntry file, int line, int column);

    void overrideMember(@NonNull FileEntry file, int line, int column, @NonNull ParseTree member);

    void introduceVariable(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn);

}
