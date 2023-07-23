package com.apkide.analysis.cle.api.formatter;

public interface CodeFormatterOption {
   String getGroupName();

   String getName();

   String getPreview(boolean preview);

   @Override
   String toString();
}
