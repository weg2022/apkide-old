package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.MenuCommand;
import com.apkide.ui.R;

public class ViewProblem implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewProblems;
    }

    @NonNull
    @Override
    public String getName() {
        return "Problems";
    }

    @Override
    public boolean commandPerformed() {
        App.getMainUI().toggleProblemBrowser();
        return true;
    }
}
