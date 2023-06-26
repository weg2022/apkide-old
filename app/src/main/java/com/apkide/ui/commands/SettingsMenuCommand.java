package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

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
        App.gotoSettings();
        return true;
    }
}
