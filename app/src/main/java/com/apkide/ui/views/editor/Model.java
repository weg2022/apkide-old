package com.apkide.ui.views.editor;

import com.apkide.common.color.Color;
import com.apkide.common.text.TextModelImpl;

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
    

    public boolean isLogging(){
        return false;
    }
    
    public boolean isLogErrorLine(int line){
        return false;
    }
    
    public boolean isLogWarningLine(int line){
        return false;
    }
    
    public boolean isLogInfo(int line){
        return false;
    }
    
    public boolean isLogDebugLine(int line){
        return false;
    }
    
    public boolean isLogVerboseLine(int line){
        return false;
    }
    
}
