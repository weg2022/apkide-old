package com.apkide.analysis.cle;


public interface ProjectSupport {
   int getFileIcon();

   String getPackageGuid();

   String[] getProjectFilePatterns();

   boolean isJavaProjectType();

   String getLanguageNameForItemTemplates();

   boolean shouldBeAdded(String filePath, boolean isNormalFile);

   boolean shouldBeCompiledByDefault(String filePath);
}
