package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.util.MenuCommand;

public class ForwardMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return 0;
    }

    @NonNull
    @Override
    public String getName() {
        return "Forward";
    }

    @Override
    public boolean commandPerformed() {
        return false;
    }
}
