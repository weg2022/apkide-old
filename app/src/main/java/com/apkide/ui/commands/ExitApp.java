package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ExitApp implements MenuCommand {
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
        App.getMainUI().exitApp();
        return true;
    }
}
