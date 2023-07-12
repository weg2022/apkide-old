package com.apkide.engine.service;

import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.clm.Model;
import com.apkide.analysis.api.clm.common.FileNamePatternMatcher;
import com.apkide.analysis.language.cpp.CppCodeModel;
import com.apkide.common.FileUtils;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class CodeModels {

    public static CodeModel[] getCodeModels() {
        return createCodeModels(null);
    }

    public static TreeMap<String, List<String>> getFilePatternMap() {
        TreeMap<String, List<String>> map = new TreeMap<>();
        for (CodeModel codeModel : getCodeModels()) {
            map.put(codeModel.getName(), Arrays.asList(codeModel.getDefaultFilePatterns()));
        }
        return map;
    }

    public static CodeModel getCodeModel(String filePath) {
        String fileName = FileUtils.getFileName(filePath);
        for (CodeModel codeModel : getCodeModels()) {
            for (String pattern : codeModel.getDefaultFilePatterns()) {
                if (FileNamePatternMatcher.get() != null && FileNamePatternMatcher.get().match(fileName, pattern)) {
                    return codeModel;
                }
            }
        }
        return null;
    }

    public static CodeModel[] createCodeModels(Model model) {
        return new CodeModel[]{
                new CppCodeModel(model)
        };
    }
}
