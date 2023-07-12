package com.apkide.analysis.api.cle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

public interface Language {

    @NonNull
    String getName();

    boolean isParenChar(char c);

    void shrink();

    @Nullable
    Set<?> getDefaultOptions();

    @Nullable
    Syntax getSyntax();

    @Nullable
    Tools getTools();

    @Nullable
    SignatureAnalyzer getSignatureAnalyzer();

    @Nullable
    TypeSystem getTypeSystem();

    @Nullable
    CodeRenderer getCodeRenderer();

    @Nullable
    CodeAnalyzer getCodeAnalyzer();
}
