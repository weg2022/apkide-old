package com.apkide.ui.browsers.error;

import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.ui.util.Entry;
import com.apkide.ui.util.ListAdapter;

public class ErrorEntryListAdapter extends ListAdapter {
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
