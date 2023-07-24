package com.apkide.ui.views.editor;

import java.util.LinkedList;

public class Model {

    private LinkedList<TextLine> myLines=new LinkedList<>();



    public TextLine getLine(int line){
        return myLines.get(line);
    }
}
