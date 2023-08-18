package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;

import com.apkide.apktool.androlib.ApkDecoder;
import com.apkide.apktool.directory.ExtFile;
import com.apkide.common.FileSystem;
import com.apkide.common.MessageBox;
import com.apkide.common.SafeRunner;
import com.apkide.ui.App;

import java.io.File;
import java.io.IOException;
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
            MessageBox.showDialog(App.getMainUI(),new ProgressDialog("APK Decompile", "Decompiling..."));
            App.runOnBackground(() -> {
                ApkDecoder decoder = new ApkDecoder(new ExtFile(filePath));
                File destDir = new File(parent, name.replace(FileSystem.getExtensionName(name), ""));
                SafeRunner.run(new SafeRunner.SafeRunnable() {
                    @Override
                    public void run() throws Exception {
                        if (destDir.exists())
                            FileSystem.delete(destDir.getAbsolutePath());
    
                    }
    
                    @Override
                    public void handleException(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    
    
                SafeRunner.run(new SafeRunner.SafeRunnable() {
                    @Override
                    public void run() throws Exception {
                        decoder.decode(destDir);
                    }
        
                    @Override
                    public void handleException(@NonNull Throwable e) {
                        try {
                            FileSystem.delete(destDir.getAbsolutePath());
                        } catch (IOException ignored) {
    
                        }
                        e.printStackTrace();
                    }
                });
            }, new Runnable() {
                @Override
                public void run() {
                    MessageBox.hide();
                    App.getFileBrowserService().sync();
                }
            });
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(false);
        return builder.create();
    }
}
