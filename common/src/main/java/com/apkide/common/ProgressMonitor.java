package com.apkide.common;

public interface ProgressMonitor {

    void done();

    boolean isCanceled();

    void setCanceled(boolean value);

}
