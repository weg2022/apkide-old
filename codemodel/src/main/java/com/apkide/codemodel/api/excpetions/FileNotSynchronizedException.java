package com.apkide.codemodel.api.excpetions;

public class FileNotSynchronizedException extends RuntimeException {
   public FileNotSynchronizedException(String s) {
      super("File is not uptodate " + s);
   }
}
