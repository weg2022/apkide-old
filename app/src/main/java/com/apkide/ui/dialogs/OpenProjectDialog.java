package com.apkide.ui.dialogs;

import static com.apkide.common.FileSystem.getName;
import static com.apkide.common.FileSystem.getParentPath;
import static java.io.File.separator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;

import com.apkide.ui.util.MessageBox;
import com.apkide.ui.App;

public class OpenProjectDialog extends MessageBox {
    
    private final String filePath;
    
    public OpenProjectDialog(String filePath) {
        this.filePath = filePath;
    }
    
    @NonNull
    @Override
    protected Dialog buildDialog(@NonNull Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Open Project");
        String simpleName ="~"+ separator+ getName(getParentPath(filePath))+ separator+ getName(filePath);
        builder.setMessage("Confirm that you want to open project\n '"+simpleName+"' ?");
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            App.getProjectService().openProject(filePath);
            App.getFileBrowserService().sync();
        });
        builder.setNegativeButton(
                android.R.string.cancel,
                (dialog, which) -> dialog.dismiss());
    
        builder.setCancelable(false);
        return builder.create();
    }
}
