package com.apkide.ui.browsers.file;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.ui.R;
import com.apkide.ui.util.EntryAdapter;

public class FileEntryAdapter extends EntryAdapter {
    @Override
    protected int getEntryType(@NonNull Entry entry, int position) {
        return 0;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder holderCreated(@NonNull ViewGroup parent, int entryType) {
        return new FileEntryHolder(foundView(R.layout.browser_file_entry));
    }

    @Override
    protected void holderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position) {
        if (entry instanceof FileEntry) {
            FileEntry fileEntry = (FileEntry) entry;
            FileEntryHolder entryHolder = (FileEntryHolder) holder;

            entryHolder.myIcon.setImageResource(fileEntry.getIcon());
            entryHolder.myLabel.setText(fileEntry.getName());
        }
    }
}
