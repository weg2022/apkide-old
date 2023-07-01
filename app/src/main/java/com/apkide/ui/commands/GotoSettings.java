package com.apkide.ui.commands;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;
import com.apkide.ui.preferences.PreferencesUI;

public class GotoSettings implements MenuCommand {
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
        App.getMainUI().startActivity(new Intent(App.getMainUI(), PreferencesUI.class));
        return true;
    }
}
