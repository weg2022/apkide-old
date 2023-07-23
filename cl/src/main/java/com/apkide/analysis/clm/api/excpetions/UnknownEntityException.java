package com.apkide.analysis.clm.api.excpetions;

import androidx.annotation.NonNull;

public class UnknownEntityException extends Exception {
   public UnknownEntityException() {
   }

   @NonNull
   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
