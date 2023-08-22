package com.apkide.ui.editors;

import androidx.annotation.NonNull;


import com.apkide.common.FileSystem;
import com.apkide.common.io.IoUtils;
import com.apkide.common.io.iterator.LineIterator;
import com.apkide.common.text.TextModel;
import com.apkide.language.FileHighlighting;
import com.apkide.ui.services.file.FileModel;
import com.apkide.ui.views.CodeEditTextModel;
import com.apkide.ui.views.editor.StyleSpan;

import java.io.IOException;

public class IDEEditorModel extends CodeEditTextModel implements FileModel {
    private final String myFilePath;
    private final Object myHighlightLock = new Object();
    private StyleSpan myHighlight = new StyleSpan();
    private StyleSpan myHighlightGUI = new StyleSpan();
    private final Object mySyntaxHighlightLock = new Object();
    private StyleSpan mySyntaxHighlight = new StyleSpan();
    private StyleSpan mySyntaxHighlightGUI = new StyleSpan();
    
    public IDEEditorModel(@NonNull String filePath) {
        super();
        myFilePath = filePath;
        setReadOnly(!FileSystem.isNormalFile(myFilePath));
        addTextModelListener(new TextChangeListener() {
            @Override
            public void prepareInsert(@NonNull TextModel model, int line, int column, @NonNull String newText) {
            
            }
            
            @Override
            public void insertUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
                synchronized (myHighlightLock) {
                    myHighlightGUI.insert(startLine, startColumn, endLine, endColumn);
                }
                synchronized (mySyntaxHighlightLock) {
                    mySyntaxHighlightGUI.insert(startLine, startColumn, endLine, endColumn);
                }
            }
            
            @Override
            public void prepareRemove(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
            
            }
            
            @Override
            public void removeUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
                synchronized (myHighlightLock) {
                    myHighlightGUI.remove(startLine, startColumn, endLine, endColumn);
                }
                synchronized (mySyntaxHighlightLock) {
                    mySyntaxHighlightGUI.remove(startLine, startColumn, endLine, endColumn);
                }
            }
        });
    }
    
    @NonNull
    @Override
    public String getFileContent() {
        return getText();
    }
    
    @NonNull
    @Override
    public LineIterator getFileContents() {
        return new LineIterator(getReader());
    }
    
    @Override
    public void sync() throws IOException {
        String text=IoUtils.readStringAndClose(FileSystem.readFile(myFilePath));
        setText(text);
    }
    
    @Override
    public void save() throws IOException {
        FileSystem.writeFile(myFilePath, getReader());
    }
    
    @Override
    public void highlighting(@NonNull FileHighlighting highlighting) {
        myHighlight.set(highlighting.styles,
                highlighting.startLines,
                highlighting.startColumns,
                highlighting.endLines,
                highlighting.endColumns,
                highlighting.length);
        synchronized (myHighlightLock) {
            StyleSpan span = myHighlightGUI;
            myHighlightGUI = myHighlight;
            myHighlight = span;
        }
    }
    
    @Override
    public void semanticHighlighting(@NonNull FileHighlighting highlighting) {
        mySyntaxHighlight.set(highlighting.styles,
                highlighting.startLines,
                highlighting.startColumns,
                highlighting.endLines,
                highlighting.endColumns,
                highlighting.length);
        synchronized (myHighlightLock) {
            StyleSpan span = mySyntaxHighlightGUI;
            mySyntaxHighlightGUI = mySyntaxHighlight;
            mySyntaxHighlight = span;
        }
    }
    
    @Override
    public boolean isBinary() {
        return FileSystem.isArchiveEntry(myFilePath);
    }
    
    @NonNull
    @Override
    public String getFilePath() {
        return myFilePath;
    }
    
    @Override
    public long getLastModified() {
        if (FileSystem.exists(myFilePath)) {
            return FileSystem.getLastModified(myFilePath);
        }
        return lastEditTimestamps();
    }
    
    @Override
    public long getFileSize() {
        return 0;
    }
    
    @Override
    public int getStyle(int line, int column) {
        int style = mySyntaxHighlightGUI.getStyle(line, column);
        if (style == 0) {
            return myHighlightGUI.getStyle(line, column);
        }
        return style;
    }
}
