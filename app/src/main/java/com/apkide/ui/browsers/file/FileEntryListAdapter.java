package com.apkide.ui.browsers.file;

import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

public class FileEntryListAdapter extends ListAdapter {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.browser_file_entry;
    }
    
    @Override
    protected EntryViewHolder createEntryViewHolder(View view, int viewType) {
        return new FileEntryViewHolder(view);
    }
    
    @Override
    protected void boundEntryViewHolder(@NonNull EntryViewHolder holder, Entry entry, int position) {
        if (entry == null) return;
        
        if (holder instanceof FileEntryViewHolder) {
            FileEntryViewHolder viewHolder = (FileEntryViewHolder) holder;
            if (entry instanceof FileEntry) {
                FileEntry fileEntry = (FileEntry) entry;
                if (fileEntry.getIconDrawable() != null) {
                    viewHolder.entryIcon.setImageDrawable(fileEntry.getIconDrawable());
                } else
                    viewHolder.entryIcon.setImageResource(fileEntry.getIcon());
                viewHolder.entryLabel.setText(fileEntry.getLabel());
            }
        }
    }
}
