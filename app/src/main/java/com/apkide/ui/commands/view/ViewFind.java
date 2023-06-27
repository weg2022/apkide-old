package com.apkide.ui.commands.view;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.util.MenuCommand;

public class ViewFind implements MenuCommand {
    @Override
    public int getId() {
        return R.id.mainActionViewFind;
    }

    @NonNull
    @Override
    public String getName() {
        return "Find";
    }

    @Override
    public boolean commandPerformed() {
        App.getUI().toggleFindBrowser();
        return true;
    }
}
