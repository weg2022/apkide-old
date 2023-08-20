package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.apkide.ui.util.MessageBox;

public class ProgressDialog extends MessageBox {
    
    private final String label;
    private final String message;
    
    public ProgressDialog(String label, String message) {
        this.label = label;
        this.message = message;
    }
    
    @NonNull
    @Override
    protected Dialog buildDialog(@NonNull Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        builder.setTitle(label);
        builder.setMessage(message);
        ProgressBar progressBar=new ProgressBar(activity);
        progressBar.setIndeterminate(true);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(-1,-2));
        builder.setView(progressBar);
        builder.setPositiveButton("Hidden", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(false);
        return builder.create();
    }
}
