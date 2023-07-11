package com.apkide.ui.browsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.apkide.ui.App;

public abstract class BrowserLayout extends LinearLayout implements Browser {
    private SharedPreferences preferences;

    public BrowserLayout(Context context) {
        super(context);
    }

    public abstract String getPreferencesName();

    @NonNull
    public SharedPreferences getPreferences() {
        if (preferences == null)
            preferences = App.getPreferences(getPreferencesName());
        return preferences;
    }

    @Override
    public void onSyncing() {

    }
}
