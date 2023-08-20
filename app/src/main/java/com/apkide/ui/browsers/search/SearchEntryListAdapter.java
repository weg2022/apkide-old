package com.apkide.ui.browsers.search;

import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

public class SearchEntryListAdapter extends ListAdapter {
    @Override
    protected int getLayoutId(int viewType) {
        return 0;
    }
    
    @Override
    protected EntryViewHolder createEntryViewHolder(View view, int viewType) {
        return null;
    }
    
    @Override
    protected void boundEntryViewHolder(@NonNull EntryViewHolder holder, Entry entry, int position) {
    
    }
}
