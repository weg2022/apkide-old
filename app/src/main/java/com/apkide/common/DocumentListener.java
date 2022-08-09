package com.apkide.common;

public interface DocumentListener {

    void documentAboutToBeChanged(DocumentEvent event);

    void documentChanged(DocumentEvent event);

}
