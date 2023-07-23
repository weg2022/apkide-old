package com.apkide.analysis.clm.api.excpetions;

public class FileNotSynchronizedException extends RuntimeException {
   public FileNotSynchronizedException(String s) {
      super("File is not update " + s);
   }
}
