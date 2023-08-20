package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.util.MessageBox;
import com.apkide.ui.App;

import java.io.File;
import java.io.IOException;

public class RenameFileDialog extends MessageBox {
    private String filePath;
    
    public RenameFileDialog(String filePath) {
        this.filePath = filePath;
    }
    
    @NonNull
    @Override
    protected Dialog buildDialog(@NonNull Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Rename");
        EditText editText = new EditText(activity);
        editText.setHint("New Name:");
        editText.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            if (editText.getText().length() != 0) {
               try {
                    String parent = FileSystem.getParentPath(filePath);
                    if (parent != null) {
                        String destPath = parent + File.separator + editText.getText().toString();
                        FileSystem.rename(filePath, destPath);
                    }
                } catch (IOException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            App.getFileBrowserService().sync();
        });
        builder.setNegativeButton(
                android.R.string.cancel,
                (dialog, which) -> dialog.dismiss());
        
        builder.setCancelable(false);
        return builder.create();
    }
}
