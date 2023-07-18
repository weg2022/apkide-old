package com.apkide.analysis.api.clm.callback;



import com.apkide.analysis.api.clm.FileEntry;
import com.apkide.analysis.api.clm.collections.SetOfFileEntry;

import java.io.Reader;

public interface OpenFileCallback {
   Reader getOpenFileReader(FileEntry fileEntry);

   long getOpenFileVersion(FileEntry fileEntry);

   long getOpenFileSize(FileEntry fileEntry);

   boolean areOpenFilesSynchronized();

   boolean isOpenFile(FileEntry fileEntry);

   SetOfFileEntry getOpenFiles();

   void update();
}
