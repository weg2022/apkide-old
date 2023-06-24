package com.apkide.ui;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

public class ThemeUI extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean darkMode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            darkMode = getResources().getConfiguration().isNightModeActive();
        else
            darkMode = isNightModeActive(getResources().getConfiguration().uiMode);

        applyStatusBar(darkMode);
    }

    private void applyStatusBar(boolean dark) {
        WindowInsetsControllerCompat compat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        compat.setAppearanceLightStatusBars(!dark);
        compat.setAppearanceLightStatusBars(!dark);
    }

    public boolean isNightModeActive(int uiMode) {
        return (uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
    }

}
