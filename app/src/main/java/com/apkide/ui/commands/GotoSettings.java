package com.apkide.ui.commands;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.preferences.PreferencesUI;
import com.apkide.ui.util.MenuCommand;

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
        App.getUI().startActivity(new Intent(App.getUI(), PreferencesUI.class));
        return true;
    }
}
