package com.apkide.ui.commands.navigate;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class Backward implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionBackward;
    }

    @Override
    public boolean isEnabled() {
        return App.getNavigateService().canBackward();
    }

    @NonNull
    @Override
    public String getName() {
        return "Backward";
    }

    @Override
    public boolean commandPerformed() {
        App.getMainUI().gotoBackward();
        return false;
    }
}
