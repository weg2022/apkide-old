package com.apkide.ui.commands.navigate;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

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
        App.getUI().gotoForward();
        return true;
    }
}
