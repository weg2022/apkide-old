package com.apkide.ui.views.editor;

public interface ActionRunnable {
    void run();

    default void setEnabled(boolean enabled) {

    }
}
