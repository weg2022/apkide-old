package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ViewProject implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewProject;
    }

    @NonNull
    @Override
    public String getName() {
        return "Project";
    }

    @Override
    public boolean commandPerformed() {
        App.getUI().toggleProjectBrowser();
        return true;
    }
}
