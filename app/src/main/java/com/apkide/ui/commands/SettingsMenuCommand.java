package com.apkide.ui.commands;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MainUI;
import com.apkide.ui.R;
import com.apkide.ui.preferences.PreferencesUI;
import com.apkide.ui.util.MenuCommand;

public class SettingsMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionSettings;
    }

    @NonNull
    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public boolean commandPerformed() {
        if (App.getContext() instanceof MainUI) {
            MainUI ui = (MainUI) App.getContext();
            ui.startActivity(new Intent(ui, PreferencesUI.class));
            return true;
        }
        return false;
    }
}
