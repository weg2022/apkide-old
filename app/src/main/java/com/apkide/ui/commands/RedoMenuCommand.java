package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class RedoMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionRedo;
    }

    @NonNull
    @Override
    public String getName() {
        return "Redo";
    }

    @Override
    public boolean commandPerformed() {
        return false;
    }
}
