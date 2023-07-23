package com.apkide.analysis;

import com.apkide.analysis.cle.BinaryArchiveSupport;
import com.apkide.analysis.cle.EditorMode;
import com.apkide.analysis.cle.ProjectSupport;
import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.api.Model;

public interface CLPlugin {
   void init();

   String getPluginName();

   String getPluginVersion();

   EditorMode createEditorMode();

   ProjectSupport createProjectSupport();

   BinaryArchiveSupport getBinaryArchiveSupport();

   Language createLanguageProvider(Model model);
}
