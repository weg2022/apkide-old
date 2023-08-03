package com.apkide.ui;

import androidx.annotation.NonNull;

import com.apkide.common.FileHighlights;
import com.apkide.common.SyntaxKind;
import com.apkide.ui.views.editor.Model;
import com.apkide.ui.views.editor.ModelListener;
import com.apkide.ui.views.editor.SyncRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaHighlighting {

    private static final FileHighlights sHighlights = new FileHighlights();
    private static final JavaLexer sLexer = new JavaLexer();
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();
    private static IDEEditor sEditor;
    private static final SyncRunner sLexerRunner = new SyncRunner(new Runnable() {
        @Override
        public void run() {
            try {
                sHighlights.clear();
                sLexer.reset(sEditor.getEditorModel().getReader());
                boolean inDocComment = false;
                int firstLine = -1;
                int firstColumn = -1;
                while (true) {
                    SyntaxKind kind = sLexer.nextToken();
                    int line = sLexer.getLine();
                    int column = sLexer.getColumn();
                    if (kind == SyntaxKind.DocComment) {
                        firstLine = line;
                        firstColumn = column;
                        inDocComment = true;
                    } else {
                        if (inDocComment) {
                            sHighlights.highlight(SyntaxKind.DocComment, firstLine, firstColumn, line, column);
                            inDocComment = false;
                        }
                    }

                    int endColumn = column + sLexer.yylength();
                    if (kind == null) break;
                    sHighlights.highlight(kind, line, column, line, endColumn);
                }
                sEditor.getEditorModel().highlighting(sHighlights);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    public static void setEditor(IDEEditor editor) {
        sEditor = editor;
        sExecutor.execute(sLexerRunner::run);

        sEditor.getEditorModel().addModelListener(new ModelListener() {
            @Override
            public void insertUpdate(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {
                sExecutor.execute(sLexerRunner::run);
            }

            @Override
            public void removeUpdate(@NonNull Model model, int startLine, int startColumn, int endLine, int endColumn) {
                sExecutor.execute(sLexerRunner::run);
            }
        });
    }
}
