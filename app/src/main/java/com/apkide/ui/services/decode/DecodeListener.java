package com.apkide.ui.services.decode;

import androidx.annotation.NonNull;

public interface DecodeListener {
    void decodeStarted(@NonNull String rootPath);
    
    void decodeOutput(@NonNull String message);
    
    void decodeFailed(@NonNull Throwable err);
    
    void decodeFinished(@NonNull String outputPath);
}
