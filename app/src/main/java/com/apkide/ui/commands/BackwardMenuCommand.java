package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.util.MenuCommand;

public class BackwardMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return 0;
    }

    @NonNull
    @Override
    public String getName() {
        return "Backward";
    }

    @Override
    public boolean commandPerformed() {
        return false;
    }
}
