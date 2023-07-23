package com.apkide.analysis.clm.api.callback;

import com.apkide.analysis.clm.api.FileEntry;
import com.apkide.analysis.clm.api.collections.SetOfFileEntry;

import java.io.Reader;

public interface OpenFileCallback {
   Reader getOpenFileReader(FileEntry file);

   long getOpenFileVersion(FileEntry file);

   long getOpenFileSize(FileEntry file);

   boolean areOpenFilesSynchronized();

   boolean isOpenFile(FileEntry file);

   SetOfFileEntry getOpenFiles();

   void update();
}
