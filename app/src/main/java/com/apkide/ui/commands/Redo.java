package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

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
    public boolean run() {
        return false;
    }
}
