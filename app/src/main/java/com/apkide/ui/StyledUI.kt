package com.apkide.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class StyledUI extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        IDECore.updateConfiguration();
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IDECore.applyThemeStyle(this,isPreferencesUI());
        onUICreated(savedInstanceState);
    }


    protected abstract void onUICreated(@Nullable Bundle savedInstanceState);


    protected boolean isPreferencesUI(){
        return false;
    }


}
