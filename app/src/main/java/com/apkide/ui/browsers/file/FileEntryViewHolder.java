package com.apkide.ui.browsers.file;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.ListAdapter;

public class FileEntryViewHolder extends ListAdapter.EntryViewHolder {
	public ImageView entryIcon;
	public TextView entryLabel;

	public FileEntryViewHolder(@NonNull View itemView) {
		super(itemView);
		entryIcon = itemView.findViewById(R.id.fileEntryIcon);
		entryLabel = itemView.findViewById(R.id.fileEntryLabel);
	}
}
