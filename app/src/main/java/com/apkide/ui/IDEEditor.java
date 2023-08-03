package com.apkide.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.FileHighlights;
import com.apkide.common.IOUtils;
import com.apkide.common.KeyStroke;
import com.apkide.common.KeyStrokeDetector;
import com.apkide.ui.services.file.OpenFileModel;
import com.apkide.ui.views.CodeEditText;
import com.apkide.ui.views.KeyStrokeCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class IDEEditor extends CodeEditText {
    public IDEEditor(Context context) {
        super(context);
        initView();
    }

    public IDEEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IDEEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

    }

    public void makeCaretVisible() {
        getScrollView().scrollToCaretVisible();
    }

    @Override
    protected void onKeyStroke() {
        getScrollView().updateCaret();
    }

    @Override
    protected List<KeyStroke> foundKeys(KeyStrokeCommand command) {
        return App.getKeyBindingService().getKeys(command);
    }

    @Override
    public KeyStrokeDetector getKeyStrokeDetector() {
        return App.getMainUI().getKeyStrokeDetector();
    }

    public IDEEditorModel getEditorModel() {
        return (IDEEditorModel) getEditorView().getCodeEditTextEditorModel();
    }

    public IDEEditorPager getEditorPager() {
        return App.getMainUI().getEditorPager();
    }

    public int getIndentation(int line) {
        return getEditorModel().getIndentation(line);
    }

    public OpenFileModel openFile(String filePath) {
        OpenFileModel openFileModel = null;//TODO:// Cache opened file;
        if (openFileModel == null) {
            openFileModel = createFileModel(filePath);
        }
        getEditorView().setModel((IDEEditorModel) openFileModel);
        getEditorView().setEditable(true);
        return openFileModel;
    }

    public OpenFileModel createFileModel(String filePath) {
        try {
            return new IDEEditorModel(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public class IDEEditorModel extends CodeEditTextEditorModel implements OpenFileModel {

        private final String myFilePath;
        private long myLastModified;


        public IDEEditorModel(String filePath) throws IOException {
            super(FileSystem.readFile(filePath));
            myFilePath = filePath;
            myLastModified = FileSystem.getLastModified(filePath);
        }

        @Override
        public void highlighting(FileHighlights highlights) {
            highlighting(highlights.kinds,
                    highlights.startLines,
                    highlights.startColumns,
                    highlights.endLines,
                    highlights.endColumns,
                    highlights.size);

        }

        @Override
        public void semanticHighlighting(FileHighlights highlights) {
            semanticHighlighting(highlights.kinds,
                    highlights.startLines,
                    highlights.startColumns,
                    highlights.endLines,
                    highlights.endColumns,
                    highlights.size);
        }

        @NonNull
        @Override
        public List<String> getContents() {
            List<String> list = new ArrayList<>(getLineCount());
            try {
                BufferedReader reader = new BufferedReader(getReader());
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
            } catch (IOException e) {
                AppLog.e(e);
            }
            return list;
        }

        @NonNull
        public String getFilePath() {
            return myFilePath;
        }

        @Override
        public void close() {

        }

        private String getText(int startLine, int startColumn, int endLine, int endColumn) {
            Reader reader = getReader(startLine, startColumn, endLine, endColumn);
            String text = "";
            try {
                text = IOUtils.readString(reader, true);
            } catch (IOException e) {
                AppLog.e(e);
            }
            return text;
        }

        public int getIndentation(int line) {
            line--;
            if (line < 0) {
                return 0;
            }
            int indent = 0;
            int length = getLineLength(line);
            for (int column = 0; column < length; column++) {
                char c = getChar(line, column);
                if (c == '\t') {
                    int tabSize = getTabSize();
                    indent = ((indent + tabSize) / tabSize) * tabSize;
                } else if (c != ' ') {
                    return indent;
                } else {
                    indent++;
                }
            }
            return indent;
        }


    }
}
