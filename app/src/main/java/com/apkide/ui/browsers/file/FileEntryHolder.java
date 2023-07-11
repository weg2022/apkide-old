package com.apkide.ui.browsers.file;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.ui.R;

public class FileEntryHolder extends RecyclerView.ViewHolder {
    public TextView myLabel;
    public ImageView myIcon;

    public FileEntryHolder(@NonNull View itemView) {
        super(itemView);
        myLabel = itemView.findViewById(R.id.fileEntryLabel);
        myIcon = itemView.findViewById(R.id.fileEntryIcon);
    }
}
