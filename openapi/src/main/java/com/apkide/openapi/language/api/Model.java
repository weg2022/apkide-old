package com.apkide.openapi.language.api;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Language;
import com.apkide.openapi.language.api.callback.CodeCompleterCallback;
import com.apkide.openapi.language.api.callback.HighlighterCallback;
import com.apkide.openapi.language.api.callback.OpenFileCallback;
import com.apkide.openapi.language.api.callback.RefactorCallback;
import com.apkide.openapi.language.api.callback.StopCallback;
import com.apkide.openapi.language.api.callback.StructureCallback;
import com.apkide.openapi.language.api.callback.SymbolSearcherCallback;
import com.apkide.openapi.language.api.callback.UsageSearcherCallback;

import java.util.List;

public class Model {
    protected static int modelCount = 0;
    private final StructureCallback myStructureCallback;
    private final HighlighterCallback myHighlighterCallback;
    private final SymbolSearcherCallback mySymbolSearcherCallback;
    private final CodeCompleterCallback myCodeCompleterCallback;
    private final RefactorCallback myRefactoringCallback;
    private final UsageSearcherCallback myUsageSearcherCallback;
    private final StopCallback myStopCallback;
    private final OpenFileCallback myOpenFileCallback;
    private final IdentifierSpace myIdentifiers;
    private final FileSpace myFileSpace;
    private final ErrorTable myErrorTable;

    public Model(
            @NonNull
            StructureCallback structureCallback,
            @NonNull
            HighlighterCallback highlighterCallback,
            @NonNull
            SymbolSearcherCallback symbolSearcherCallback,
            @NonNull
            CodeCompleterCallback codeCompleterCallback,
            @NonNull
            RefactorCallback refactoringCallback,
            @NonNull
            UsageSearcherCallback usageSearcherCallback,
            @NonNull
            StopCallback stopCallback,
            @NonNull
            OpenFileCallback openFileCallback
    ) {
        myStructureCallback = structureCallback;
        myHighlighterCallback = highlighterCallback;
        mySymbolSearcherCallback = symbolSearcherCallback;
        myCodeCompleterCallback = codeCompleterCallback;
        myRefactoringCallback = refactoringCallback;
        myUsageSearcherCallback = usageSearcherCallback;
        myStopCallback = stopCallback;
        myOpenFileCallback = openFileCallback;
        ++modelCount;
        myIdentifiers = new IdentifierSpace();
        myFileSpace = new FileSpace(myIdentifiers, openFileCallback, stopCallback);
        myErrorTable = new ErrorTable(this);
    }

    @Override
    protected void finalize() {
        --modelCount;
    }

    public void close() {
    }


    public void begin(boolean updateFileSpace) {
        myFileSpace.update(updateFileSpace);
    }

    public void done() {
        myFileSpace.close();
    }

    public void configure() {
        myFileSpace.configure();
    }

    public void configureLanguages(@NonNull Language[] languages) {
        myFileSpace.configureLanguages(languages);
    }

    public Language[] getLanguages() {
        return myFileSpace.getLanguages();
    }

    public void configureEncoding(@NonNull String encoding) {
        myFileSpace.configureEncoding(encoding);
    }

    public void configureAssembly(int assembly,
                                  @NonNull String projectAssembly,
                                  @NonNull String projectFilePath,
                                  @NonNull String rootNamespace,
                                  @NonNull String configuration,
                                  @NonNull List<String> defaultImports,
                                  @NonNull List<String> defaultSymbols,
                                  @NonNull List<String> targetLibs,
                                  @NonNull String destinationPath,
                                  @NonNull String targetVersion,
                                  boolean isExternal,
                                  boolean isDebug,
                                  boolean isRelease
    ) {
        myFileSpace.configureAssembly(assembly,
                projectAssembly,
                projectFilePath,
                rootNamespace,
                configuration,
                defaultImports,
                defaultSymbols,
                targetLibs,
                destinationPath,
                targetVersion,
                isExternal,
                isDebug,
                isRelease);
    }

    public void configureReference(int assembly1, int assembly2) {
        myFileSpace.configureReference(assembly1, assembly2);
    }

    public void configureFile(@NonNull FileEntry file, int assembly, @NonNull Language language, boolean checked) {
        myFileSpace.configureFile(file, assembly, language, checked);
    }

    public void configureResourceFile(@NonNull FileEntry file, int assembly, @NonNull String packageName) {
        myFileSpace.configureResourceFile(file, assembly, packageName);
    }


    @NonNull
    public StructureCallback getStructureCallback() {
        return myStructureCallback;
    }

    @NonNull
    public HighlighterCallback getHighlighterCallback() {
        return myHighlighterCallback;
    }

    @NonNull
    public SymbolSearcherCallback getSymbolSearcherCallback() {
        return mySymbolSearcherCallback;
    }

    @NonNull
    public CodeCompleterCallback getCodeCompleterCallback() {
        return myCodeCompleterCallback;
    }

    @NonNull
    public RefactorCallback getRefactoringCallback() {
        return myRefactoringCallback;
    }

    @NonNull
    public UsageSearcherCallback getUsageSearcherCallback() {
        return myUsageSearcherCallback;
    }

    @NonNull
    public StopCallback getStopCallback() {
        return myStopCallback;
    }

    @NonNull
    public OpenFileCallback getOpenFileCallback() {
        return myOpenFileCallback;
    }

    @NonNull
    public IdentifierSpace getIdentifiers() {
        return myIdentifiers;
    }

    @NonNull
    public FileSpace getFileSpace() {
        return myFileSpace;
    }

    @NonNull
    public ErrorTable getErrorTable() {
        return myErrorTable;
    }
}

