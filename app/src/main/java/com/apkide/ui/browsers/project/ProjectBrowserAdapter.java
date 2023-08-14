package com.apkide.ui.browsers.project;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.common.EntryListAdapter;

public class ProjectBrowserAdapter extends EntryListAdapter {
	@Override
	protected int getEntryType(@NonNull Entry entry, int position) {
		return 0;
	}

	@NonNull
	@Override
	protected RecyclerView.ViewHolder holderCreated(@NonNull ViewGroup parent, int entryType) {
		return null;
	}

	@Override
	protected void holderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position) {

	}
}
