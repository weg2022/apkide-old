package com.apkide.ui.views.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.common.TextModel;


public class EditorView extends View implements TextModel.TextModelListener {
    public EditorView(Context context) {
        super(context);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private EditorModel myModel;
    private EditorTheme myTheme;
    
    private void initView() {
        
        setModel(new EditorModel());
    }
    
    public void setModel(@NonNull EditorModel model) {
        myModel = model;
    }
    
    @NonNull
    public EditorModel getModel() {
        return myModel;
    }
    
    @Override
    public void prepareInsert(@NonNull TextModel model, int line, int column, @NonNull String newText) {
    
    }
    
    @Override
    public void insertUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
    
    }
    
    @Override
    public void prepareRemove(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
    
    }
    
    @Override
    public void removeUpdate(@NonNull TextModel model, int startLine, int startColumn, int endLine, int endColumn) {
    
    }
}
