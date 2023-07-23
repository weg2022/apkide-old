package com.apkide.analysis.clm.api.excpetions;

import androidx.annotation.NonNull;

public class DuplicateEntityException extends Exception {
   public DuplicateEntityException() {
   }

   @NonNull
   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
