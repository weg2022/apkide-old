package com.apktool.cl.api.model.excpetions;

import androidx.annotation.NonNull;

public class DuplicateEntityException extends Exception {
    public DuplicateEntityException() {
        super();
    }

    @NonNull
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

