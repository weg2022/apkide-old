package com.apktool.cl.api;


import com.apktool.cl.api.engine.BinaryArchiveSupport;
import com.apktool.cl.api.engine.EditorMode;
import com.apktool.cl.api.engine.ProjectSupport;
import com.apktool.cl.api.model.Language;
import com.apktool.cl.api.model.Model;

public interface CLPlugin {
    void init(ApplicationContext context);

    String getPluginName();

    String getPluginVersion();

    EditorMode createEditorMode();

    ProjectSupport createProjectSupport();

    BinaryArchiveSupport getBinaryArchiveSupport();

    Language createLanguageProvider(Model model);
}
