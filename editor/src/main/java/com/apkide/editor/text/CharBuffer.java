package com.apkide.editor.text;

public interface CharBuffer {

    void set(char[] buffer);

    //void insert(int index,char[] buffer);

    //void delete(int index,int length);

    void replace(int index,int length,char[] buffer);

    char charAt(int index);

    char[] get(int index,int length);

    char[] get();

    int getLength();
}
