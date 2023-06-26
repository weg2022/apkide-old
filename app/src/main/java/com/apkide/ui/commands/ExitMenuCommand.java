package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class ExitMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionExit;
    }

    @NonNull
    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public boolean commandPerformed() {
        App.exitApp();
        return true;
    }
}
