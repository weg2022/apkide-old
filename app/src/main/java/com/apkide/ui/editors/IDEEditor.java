package com.apkide.ui.editors;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.apkide.ui.AppPreferences;
import com.apkide.ui.views.CodeEditText;
import com.apkide.ui.views.editor.Theme;

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
        getEditorView().applyColorScheme(new Theme.DefaultColorScheme(AppPreferences.isNightTheme()));
    }
    
    public void setModel(@NonNull IDEEditorModel model) {
        super.setModel(model);
    }
    
    @NonNull
    public IDEEditorModel getIDEEditorModel() {
        return (IDEEditorModel) super.getCodeEditTextModel();
    }
}
