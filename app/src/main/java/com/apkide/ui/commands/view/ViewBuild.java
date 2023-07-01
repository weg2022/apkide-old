package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class ViewBuild implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewBuild;
    }

    @NonNull
    @Override
    public String getName() {
        return "Build";
    }

    @Override
    public boolean commandPerformed() {
        App.getMainUI().toggleBuildBrowser();
        return true;
    }
}
