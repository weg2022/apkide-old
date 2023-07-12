package com.apkide.analysis.language.cpp;

import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.cle.Language;
import com.apkide.analysis.api.clm.Model;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CppCodeModel implements CodeModel {
    private final Model myModel;
    private final CppLanguage myCppLanguage;

    public CppCodeModel(Model model) {
        myModel = model;
        myCppLanguage=new CppLanguage(model);
    }

    @NonNull
    @Override
    public String getName() {
        return "C/C++";
    }

    @NonNull
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.c", "*.h", "*.cpp", "*.hpp"};
    }

    @NonNull
    @Override
    public List<Language> getLanguages() {
        return List.of(myCppLanguage);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isArchiveReader() {
        return false;
    }

    @Override
    public long getArchiveVersion(String filePath) {
        return 0;
    }

    @Override
    public Reader getArchiveEntryReader(String filePath, String entryName, String encoding) throws IOException {
        return null;
    }

    @Override
    public String[] getArchiveEntries(String filePath) {
        return new String[0];
    }

    @Override
    public void close() {

    }
}
