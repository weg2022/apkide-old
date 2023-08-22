package com.apkide.language.xml;

import androidx.annotation.NonNull;

import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeCompleterCallback;

public class XmlCodeCompleter implements CodeCompleter {
    private final XmlLanguage myLanguage;
    
    public XmlCodeCompleter(XmlLanguage language) {
        myLanguage = language;
    }
    
    @Override
    public void completion(@NonNull String filePath, int line, int column, boolean allowTypes,
                           @NonNull CodeCompleterCallback callback) {
    
    }
}
