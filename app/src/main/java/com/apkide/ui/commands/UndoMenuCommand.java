package com.apkide.ui.commands;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class UndoMenuCommand implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionUndo;
    }

    @NonNull
    @Override
    public String getName() {
        return "Undo";
    }

    @Override
    public boolean commandPerformed() {
        return false;
    }
}
