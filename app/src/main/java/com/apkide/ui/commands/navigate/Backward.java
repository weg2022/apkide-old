package com.apkide.ui.commands.navigate;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

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
        App.getUI().gotoBackward();
        return false;
    }
}
