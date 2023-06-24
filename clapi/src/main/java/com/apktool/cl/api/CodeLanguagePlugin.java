package com.apktool.cl.api;

public interface CodeLanguagePlugin {
    void init(ApplicationContext context);

    String getPluginName();

    String getPluginVersion();
}
