package com.apkide.codemodel.api.excpetions;

import androidx.annotation.NonNull;

public class DuplicateEntityException extends Exception {
   @NonNull
   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
