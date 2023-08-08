package com.apkide.ui.browsers.file;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.common.EntryListAdapter;
import com.apkide.ui.R;

public class FileEntryAdapter extends EntryListAdapter {
	@Override
	protected int getEntryType(@NonNull Entry entry, int position) {
		return 0;
	}

	@NonNull
	@Override
	protected RecyclerView.ViewHolder holderCreated(@NonNull ViewGroup parent, int entryType) {
		return new FileEntryViewHolder(foundView(R.layout.browser_file_entry));
	}

	@Override
	protected void holderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position) {
		if (entry instanceof FileEntry){
			FileEntryViewHolder viewHolder= (FileEntryViewHolder) holder;
			if (((FileEntry) entry).getIcon()!=0) {
				viewHolder.icon.setImageResource(((FileEntry) entry).getIcon());
			}else{
				viewHolder.icon.setVisibility(View.GONE);
			}
			viewHolder.label.setText(((FileEntry) entry).getLabel());
		}
	}
}
