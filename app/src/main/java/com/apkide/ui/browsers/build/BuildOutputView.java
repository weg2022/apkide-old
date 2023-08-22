package com.apkide.ui.browsers.build;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.apkide.ui.AppPreferences;
import com.apkide.ui.views.CodeEditText;
import com.apkide.ui.views.editor.Theme;

public class BuildOutputView extends CodeEditText {
    public BuildOutputView(Context context) {
        super(context);
        initView();
    }
    
    public BuildOutputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public BuildOutputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    private void initView() {
        getEditorView().applyColorScheme(new Theme.DefaultColorScheme(AppPreferences.isNightTheme()));
        getEditorView().setCaretVisibility(true);
        setModel(new BuildOutputModel());
    }
    
    public void setModel(@NonNull BuildOutputModel model) {
        super.setModel(model);
    }
    
    @NonNull
    public BuildOutputModel getBuildOutputModel() {
        return (BuildOutputModel) super.getCodeEditTextModel();
    }
    
    public void append(@NonNull String msg){
        getBuildOutputModel().insert(getBuildOutputModel().getLineCount()-1,getBuildOutputModel().getLineLength(getLineCount()-1),msg);
        getBuildOutputModel().insertLineBreak(getBuildOutputModel().getLineCount()-1,getBuildOutputModel().getLineLength(getLineCount()-1));
        
    }
    
    
}
