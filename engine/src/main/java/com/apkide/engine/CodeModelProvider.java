package com.apkide.engine;

import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.clm.Model;

public interface CodeModelProvider {
    long getVersion();

    CodeModel[] createCodeModels(Model model);
}
