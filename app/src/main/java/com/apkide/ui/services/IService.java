package com.apkide.ui.services;

import androidx.annotation.NonNull;

public interface IService {

    void restart();

    void destroy();
    
    @NonNull
    String getName();
}
