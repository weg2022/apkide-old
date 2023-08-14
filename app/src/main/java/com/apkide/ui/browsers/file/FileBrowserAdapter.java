package com.apkide.ui.browsers.file;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.common.EntryListAdapter;
import com.apkide.ui.R;

public class FileBrowserAdapter extends EntryListAdapter {

	public static final int TYPE_FILE = 0;

	@Override
	protected int getEntryType(@NonNull Entry entry, int position) {
		return TYPE_FILE;
	}

	@NonNull
	@Override
	protected RecyclerView.ViewHolder holderCreated(@NonNull ViewGroup parent, int entryType) {
		return new FileEntryViewHolder(foundView(R.layout.browser_file_entry));
	}

	@Override
	protected void holderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof FileEntryViewHolder) {

			if (entry instanceof FileEntry) {
				((FileEntryViewHolder) holder).entryIcon.setImageResource(((FileEntry) entry).getIcon());
				((FileEntryViewHolder) holder).entryLabel.setText(((FileEntry) entry).getLabel());
			}

		}

	}
}
