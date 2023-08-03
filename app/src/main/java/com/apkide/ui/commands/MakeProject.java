package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class MakeProject implements MenuCommand {
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
    public boolean run() {
        return false;
    }
}
