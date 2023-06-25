package com.apktool.cl.api.engine;

import java.util.Locale;

public interface ProjectSupport {

    String[] getSupportedProjectFileExtensions();

    String getLanguageNameForItemTemplates();

    boolean shouldBeCompiledByDefault(String filePath);
}
