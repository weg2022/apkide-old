package com.apkide.openapi.language.java;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Tools;
import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Model;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.SyntaxTree;

public class JavaTools implements Tools {
    private final Model myModel;
    private final JavaLanguage myLanguage;
    public JavaTools(Model model, JavaLanguage language) {
        myModel = model;
        myLanguage = language;
    }

    @Override
    public void requestCompletionList(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void requestClassesCompletionList(@NonNull FileEntry file) {

    }

    @Override
    public void requestSuperMethodsCompletionList(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void documentation(@NonNull FileEntry file) {

    }

    @Override
    public void documentation(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void safeDelete(@NonNull FileEntry file, @NonNull SyntaxTree element) {

    }

    @Override
    public void surroundWithTryCatch(FileEntry file, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void addThrows(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void createVariable(@NonNull FileEntry file, int line, int column, int identifier, @NonNull ParseTree type) {

    }

    @Override
    public void createField(@NonNull FileEntry file, int line, int column, int modifiers, int identifier, @NonNull ParseTree type) {

    }

    @Override
    public void createMethod(@NonNull FileEntry file, int line, int column, int modifiers, int identifier, @NonNull ParseTree type, int[] argModifiers, @NonNull ParseTree[] argTypes, int[] argNames) {

    }

    @Override
    public void inlineVariable(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void createSettersAndGetters(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void createConstructor(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void extractMethod(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn) {

    }

    @Override
    public void implementMembers(@NonNull FileEntry file, int line, int column) {

    }

    @Override
    public void overrideMember(@NonNull FileEntry file, int line, int column, @NonNull ParseTree member) {

    }

    @Override
    public void introduceVariable(@NonNull FileEntry file, int startLine, int startColumn, int endLine, int endColumn) {

    }
}
