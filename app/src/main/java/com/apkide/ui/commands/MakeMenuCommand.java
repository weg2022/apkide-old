package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class MakeMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionRun;
    }

    @NonNull
    @Override
    public String getName() {
        return "Make";
    }

    @Override
    public boolean commandPerformed() {
        return false;
    }
}
