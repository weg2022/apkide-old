package com.apkide.ui.commands.navigate;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class Forward implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionForward;
    }

    @Override
    public boolean isEnabled() {
        return App.getNavigateService().canForward();
    }

    @NonNull
    @Override
    public String getName() {
        return "Forward";
    }

    @Override
    public boolean commandPerformed() {
        App.getMainUI().gotoForward();
        return true;
    }
}
