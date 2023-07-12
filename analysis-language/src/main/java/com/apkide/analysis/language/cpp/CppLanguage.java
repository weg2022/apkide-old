package com.apkide.analysis.language.cpp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.analysis.api.cle.CodeAnalyzer;
import com.apkide.analysis.api.cle.CodeRenderer;
import com.apkide.analysis.api.cle.Language;
import com.apkide.analysis.api.cle.SignatureAnalyzer;
import com.apkide.analysis.api.cle.Syntax;
import com.apkide.analysis.api.cle.Tools;
import com.apkide.analysis.api.cle.TypeSystem;
import com.apkide.analysis.api.clm.Model;

import java.util.Set;

public class CppLanguage implements Language {

    private final Model myModel;

    public CppLanguage(Model model) {
        myModel = model;
    }

    @NonNull
    @Override
    public String getName() {
        return "C++";
    }

    @Override
    public boolean isParenChar(char c) {
        switch (c) {
            case '(':
            case ':':
            case ';':
            case '[':
            case '{':
            case '}':
                return true;
            default:
                return false;
        }
    }

    @Override
    public void shrink() {

    }

    @Nullable
    @Override
    public Set<?> getDefaultOptions() {
        return null;
    }

    @Nullable
    @Override
    public Syntax getSyntax() {
        return null;
    }

    @Nullable
    @Override
    public Tools getTools() {
        return null;
    }

    @Nullable
    @Override
    public SignatureAnalyzer getSignatureAnalyzer() {
        return null;
    }

    @Nullable
    @Override
    public TypeSystem getTypeSystem() {
        return null;
    }

    @Nullable
    @Override
    public CodeRenderer getCodeRenderer() {
        return null;
    }

    @Nullable
    @Override
    public CodeAnalyzer getCodeAnalyzer() {
        return null;
    }
}
