package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class Redo implements MenuCommand {
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
