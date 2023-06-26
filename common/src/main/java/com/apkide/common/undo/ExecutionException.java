package com.apkide.common.undo;

import androidx.annotation.Keep;

@Keep
public class ExecutionException extends Exception{
    public ExecutionException() {
        super();
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

}
