package com.apkide.common.text;

public interface TextChangeListener {
    void textSet();

    void textChanging(TextChangeEvent event);

    void textChanged(TextChangeEvent event);
}
