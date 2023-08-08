package com.apkide.ui.browsers.file;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.ui.R;


public class FileEntryViewHolder extends RecyclerView.ViewHolder {
	public TextView label;
	public ImageView icon;
	public FileEntryViewHolder(@NonNull View itemView) {
		super(itemView);
		label=itemView.findViewById(R.id.fileEntryLabel);
		icon=itemView.findViewById(R.id.fileEntryIcon);
	}
}
