package com.apkide.openapi.language.api.runtime;

import com.apkide.common.collections.FunctionOfIntLong;
import com.apkide.common.collections.SetOfInt;
import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Model;

import org.antlr.v4.runtime.tree.SyntaxTree;

import java.io.IOException;
import java.io.Reader;

public class Highlighter {
    private final Model myModel;
    private final FunctionOfIntLong myVersions = new FunctionOfIntLong();
    private final FunctionOfIntLong mySyntaxVersions = new FunctionOfIntLong();

    public Highlighter(Model model) {
        myModel = model;
    }


    public void begin() {
        SetOfInt set = new SetOfInt();
        myVersions.DEFAULT_ITERATOR.init();
        while (myVersions.DEFAULT_ITERATOR.hasMoreElements()) {
            int id = myVersions.DEFAULT_ITERATOR.nextKey();
            if (myModel.getFileSpace().getFileEntry(id).isOpen()) {
                set.put(id);
            }
        }

        mySyntaxVersions.DEFAULT_ITERATOR.init();
        while (mySyntaxVersions.DEFAULT_ITERATOR.hasMoreElements()) {
            int id = mySyntaxVersions.DEFAULT_ITERATOR.nextKey();
            if (myModel.getFileSpace().getFileEntry(id).isOpen()) {
                set.put(id);
            }
        }

        set.DEFAULT_ITERATOR.init();
        while (set.DEFAULT_ITERATOR.hasMoreElements()) {
            myVersions.remove(set.DEFAULT_ITERATOR.nextKey());
            mySyntaxVersions.remove(set.DEFAULT_ITERATOR.nextKey());
        }
    }


    public void highlighting(FileEntry file, Reader reader) throws IOException {
        if (myModel.getLanguages() != null && myVersions.get(file.getID()) != file.getVersion()) {
            myVersions.put(file.getID(), file.getVersion());
            myModel.getHighlighterCallback().fileStarted(file);

            if (reader == null)
                reader = file.getReader();
            file.getLanguage().fileHighlighting(file, reader, result -> myModel.getHighlighterCallback().fileHighlightingFinished(file, file.getLanguage(), result));
            reader.close();
        }
    }

    public void semanticHighlighting(FileEntry file, boolean analyzeErrors) throws IOException {
        if (file.getLanguage() != null && mySyntaxVersions.get(file.getID()) != file.getVersion()) {
            mySyntaxVersions.put(file.getID(), file.getVersion());

            SyntaxTree ast = myModel.getFileSpace().getSyntaxTree(file, file.getLanguage());
            if (file.getLanguage().getAnalyzer() != null && analyzeErrors)
                file.getLanguage().getAnalyzer().analyzeErrors(file, ast);

            file.getLanguage().semanticHighlighting(file,file.getLanguage(),ast);

            myModel.getHighlighterCallback().fileFinished(file);
        }
    }
}
