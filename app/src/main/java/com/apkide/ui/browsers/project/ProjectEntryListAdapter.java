package com.apkide.ui.browsers.project;

import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

public class ProjectEntryListAdapter extends ListAdapter {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.browser_project_entry;
    }
    
    @Override
    protected EntryViewHolder createEntryViewHolder(View view, int viewType) {
        return new ProjectEntryViewHolder(view);
    }
    
    @Override
    protected void boundEntryViewHolder(@NonNull EntryViewHolder holder, Entry entry, int position) {
        if (entry == null) return;
        if (holder instanceof ProjectEntryViewHolder) {
            ProjectEntryViewHolder viewHolder = (ProjectEntryViewHolder) holder;
            if (entry instanceof ProjectEntry) {
                ProjectEntry projectEntry = (ProjectEntry) entry;
                if (projectEntry.getIconDrawable() != null) {
                    viewHolder.entryIcon.setImageDrawable(projectEntry.getIconDrawable());
                } else
                    viewHolder.entryIcon.setImageResource(projectEntry.getIcon());
                viewHolder.entryLabel.setText(projectEntry.getLabel());
            }
        }
    }
}
