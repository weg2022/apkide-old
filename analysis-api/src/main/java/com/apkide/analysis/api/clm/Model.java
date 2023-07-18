package com.apkide.analysis.api.clm;


import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.cle.Language;
import com.apkide.analysis.api.clm.callback.APISearcherCallback;
import com.apkide.analysis.api.clm.callback.CodeCompleterCallback;
import com.apkide.analysis.api.clm.callback.FileReaderCallback;
import com.apkide.analysis.api.clm.callback.HighlighterCallback;
import com.apkide.analysis.api.clm.callback.OpenFileCallback;
import com.apkide.analysis.api.clm.callback.RefactoringCallback;
import com.apkide.analysis.api.clm.callback.StopCallback;
import com.apkide.analysis.api.clm.callback.StructureCallback;
import com.apkide.analysis.api.clm.callback.SymbolSearcherCallback;
import com.apkide.analysis.api.clm.callback.UsageSearcherCallback;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;
import java.util.List;

public class Model {
    private static int modelCount = 0;
    public final StructureCallback structurecallback;
    public final HighlighterCallback highlightercallback;
    public final SymbolSearcherCallback symbolcallback;
    public final CodeCompleterCallback completercallback;
    public final RefactoringCallback refactorcallback;
    public final UsageSearcherCallback usagesearchercallback;
    public final APISearcherCallback apisearchercallback;
    public final StopCallback stopcallback;
    public final OpenFileCallback openfilecallback;

    public final IdentifierSpace identifiers;
    public final FileSpace filespace;
    public final SyntaxTreeSpace trees;
    public final EntitySpace space;
    public final Compiler compiler;
    public ErrorTable errortable;
    private CodingStyle codingstyle;
    private String[] todos;
    private boolean[] todoCaseSensitive;

    public Model() {
        this(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public Model(
            StructureCallback structurecallback,
            HighlighterCallback highlightercallback,
            SymbolSearcherCallback symbolcallback,
            CodeCompleterCallback completercallback,
            RefactoringCallback refactorcallback,
            UsageSearcherCallback usagesearchercallback,
            APISearcherCallback apisearchercallback,
            StopCallback stopcallback,
            OpenFileCallback openfilecallback,
            FileReaderCallback fileReaderCallback
    ) {
        this.structurecallback = structurecallback;
        this.highlightercallback = highlightercallback;
        this.symbolcallback = symbolcallback;
        this.completercallback = completercallback;
        this.refactorcallback = refactorcallback;
        this.usagesearchercallback = usagesearchercallback;
        this.apisearchercallback = apisearchercallback;
        this.stopcallback = stopcallback;
        this.openfilecallback = openfilecallback;
        ++modelCount;
        this.identifiers = new IdentifierSpace();
        this.filespace = new FileSpace(this.identifiers, openfilecallback, stopcallback,fileReaderCallback);
        this.trees = new SyntaxTreeSpace(this.identifiers, this.filespace);
        this.space = new EntitySpace(this.identifiers, this.filespace, this.trees, stopcallback);
        this.errortable = new ErrorTable(this);
        this.compiler = new Compiler(this);
    }

    @Override
    protected void finalize() throws Throwable {
        --modelCount;
    }

    public void close() {
    }

    protected void load(StoreInputStream stream) throws IOException {
        this.filespace.loadEntries(stream);
        this.space.loadEntries(stream);
        this.identifiers.load(stream);
        this.filespace.load(stream);
        this.space.load(stream);
        this.errortable.load(stream);
        this.compiler.load(stream);
    }

    protected void store(StoreOutputStream stream) throws IOException {
        this.filespace.storeEntries(stream);
        this.space.storeEntries(stream);
        this.identifiers.store(stream);
        this.filespace.store(stream);
        this.space.store(stream);
        this.errortable.store(stream);
        this.compiler.store(stream);
    }

    public void begin(boolean updateFileSpace) {
        this.filespace.update(updateFileSpace);
        this.trees.update();
        this.space.update();
        this.space.preloadNamespaces();
        this.compiler.update();
    }

    public void shrink(boolean full) {
        this.trees.shrink(full);
        this.space.shrink(full);
        if (full) {
            for (CodeModel codeModel : filespace.getCodeModels()) {
                for (Language language : codeModel.getLanguages()) {
                    if (language != null)
                        language.shrink();
                }
            }
            SyntaxTreePool.shrink();
        }
    }

    public void done() {
        this.trees.releaseSyntaxTrees();
        this.filespace.close();
    }

    public CodeModel[] getCodeModels(){
       return filespace.getCodeModels();
    }

    public void configureCodeModelFilePatterns(CodeModel codeModel,List<String> filePatterns){
       filespace.configureCodeModelFilePatterns(codeModel,filePatterns);
    }

    public void configureCodeModels(CodeModel[] codeModels){
       filespace.configureCodeModels(codeModels);
    }

    public void reconfigure() {
        this.space.invalidateAll();
    }

    public void configure() {
        this.filespace.configure();
        this.space.invalidateNamespaces();
    }

    public void configureDestination(int assembly, String destinationPath,
                                     String targetVersion, String configuration) {
        this.filespace.configureDestination(assembly, destinationPath,
                targetVersion, configuration);
    }

    public void configureEncoding(String encoding) {
        this.filespace.configureEncoding(encoding);
    }

    public void configureAssembly(
            int assembly, String projectFilePath, String rootNamespace,
            List<String> defaultImports,
            List<String> definedSymbols,
            List<String> tagLibPaths) {
        this.filespace.configureAssembly(assembly, projectFilePath, rootNamespace,
                defaultImports, definedSymbols, tagLibPaths);
    }

    public void configureReference(int assembly1, int assembly2) {
        this.filespace.configureReference(assembly1, assembly2);
    }

    public void configureFile(FileEntry file, int assembly, CodeModel codeModel, boolean checked) {
        this.filespace.configureFile(file, assembly, codeModel, checked);
    }

    public void configureResourceFile(FileEntry file, int assembly, String packageName) {
        this.filespace.configureResourceFile(file, assembly, packageName);
    }

    public String[] getTodos() {
        return this.todos;
    }

    public boolean[] getTodoCasesensitive() {
        return this.todoCaseSensitive;
    }

    public CodingStyle getCodingstyle() {
        return this.codingstyle;
    }

    public void registerOptions(CodingStyle codingstyle, String[] todos, boolean[] todoCaseSensitive) {
        this.codingstyle = codingstyle;
        this.todos = todos;
        this.todoCaseSensitive = todoCaseSensitive;
    }
}
