package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ViewGit implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewGit;
    }

    @NonNull
    @Override
    public String getName() {
        return "Git";
    }

    @Override
    public boolean commandPerformed() {
        App.getUI().toggleGitBrowser();
        return true;
    }
}
