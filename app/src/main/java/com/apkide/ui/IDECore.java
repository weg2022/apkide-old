package com.apkide.ui;

import android.app.Activity;

import androidx.core.view.ViewCompat;

import java.util.Locale;

public final class IDECore {

    public static void applyThemeStyle(Activity activity, boolean preferencesUI) {
        var controller = ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
        if (IDEOptions.isDarkMode()) {
            if (controller != null) {
                controller.setAppearanceLightStatusBars(false);
                controller.setAppearanceLightNavigationBars(false);
            }
        } else {
            if (controller != null) {
                controller.setAppearanceLightStatusBars(true);
                controller.setAppearanceLightNavigationBars(true);
            }
        }
    }

    public static void updateConfiguration() {
        var configuration = ContentProvider.context.getResources().getConfiguration();
        configuration.setLocale(getLocale());
        ContentProvider.context.createConfigurationContext(configuration);
    }

    public static Locale getLocale() {
        var language = IDEOptions.getAppLanguage();
        if (language.equals("system") || language.equals("")) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(language);
    }

}
