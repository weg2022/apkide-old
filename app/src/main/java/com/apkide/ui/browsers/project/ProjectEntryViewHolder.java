package com.apkide.ui.browsers.project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.ListAdapter;

public class ProjectEntryViewHolder extends ListAdapter.EntryViewHolder {
    
    public ImageView entryIcon;
    public TextView entryLabel;
    
    public ProjectEntryViewHolder(@NonNull View itemView) {
        super(itemView);
        entryIcon = itemView.findViewById(R.id.projectEntryIcon);
        entryLabel = itemView.findViewById(R.id.projectEntryLabel);
    }
}
