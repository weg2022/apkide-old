package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.common.MessageBox;
import com.apkide.ui.App;

import java.io.File;

//for testing only
public class ApkDecompileDialog extends MessageBox {
    private final String filePath;
    
    public ApkDecompileDialog(String filePath) {
        this.filePath = filePath;
    }
    
    @NonNull
    @Override
    protected Dialog buildDialog(@NonNull Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String name = FileSystem.getName(filePath);
        String parent = FileSystem.getParentDirPath(filePath);
        builder.setTitle("APK Decompile");
        builder.setMessage("Are you sure you want to decompile this apk file?");
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            if (parent == null) return;
            File destDir = new File(parent, name.replace(FileSystem.getExtensionName(name), ""));
            App.getApkEngineService().decodeApk(filePath, destDir.getAbsolutePath());
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(false);
        return builder.create();
    }
}
