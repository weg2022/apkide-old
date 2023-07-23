package com.apkide.analysis.clm.api.excpetions;

import androidx.annotation.NonNull;

public class AmbiguousEntityException extends Exception {
   public AmbiguousEntityException() {
   }

   @NonNull
   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
