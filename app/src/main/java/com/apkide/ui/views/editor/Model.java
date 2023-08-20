package com.apkide.ui.views.editor;

import com.apkide.common.color.Color;
import com.apkide.common.text.TextModelImpl;
import com.apkide.ls.api.diagnostic.Diagnostic;

public class Model extends TextModelImpl {
    public Model() {
        super();
    }
    
    
    
    
    
    
    public int getStyle(int line,int column){
        return 0;
    }
    
    public boolean isColor(int line,int column){
        return false;
    }
    
    public Color getColor(int line,int column){
        return null;
    }
    
    public boolean isDiagnostic(int line,int column){
        return false;
    }
    
    public Diagnostic getDiagnostic(int line,int column){
        return null;
    }
}
