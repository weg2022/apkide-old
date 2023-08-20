package com.apkide.ui.util;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ListAdapter extends RecyclerView.Adapter<ListAdapter.EntryViewHolder> {
    
    private final List<Entry> myEntries = new ArrayList<>();
    private ListEntryClickListener myClickListener;
    private ListEntryLongPressListener myLongPressListener;
    
    public void setClickListener(ListEntryClickListener clickListener) {
        myClickListener = clickListener;
    }
    
    public void setLongPressListener(ListEntryLongPressListener longPressListener) {
        myLongPressListener = longPressListener;
    }
    
    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getLayoutId(viewType), parent, false);
        return createEntryViewHolder(view,viewType);
    }
    
    protected abstract int getLayoutId(int viewType);
    
    protected abstract EntryViewHolder createEntryViewHolder(View view, int viewType);
    
    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = myEntries.get(position);
        if (myClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                myClickListener.listEntryClick(v, entry, position);
            });
        }
        if (myLongPressListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                return myLongPressListener.listEntryLongPress(v, entry, position);
            });
        }
        boundEntryViewHolder(holder, entry, position);
    }
    
    protected abstract void boundEntryViewHolder(@NonNull EntryViewHolder holder, Entry entry, int position);
    
    public void clear() {
        myEntries.clear();
    }
    
    public void update(@NonNull Collection<? extends Entry> entries) {
        myEntries.clear();
        myEntries.addAll(entries);
    }
    
    @SuppressLint("NotifyDataSetChanged")
    public void sync() {
        notifyDataSetChanged();
    }
    
    public Entry getEntry(int position) {
        return myEntries.get(position);
    }
    
    public List<Entry> getEntries() {
        return Collections.unmodifiableList(myEntries);
    }
    
    @Override
    public int getItemCount() {
        return myEntries.size();
    }
    
    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    
    public interface ListEntryClickListener {
        void listEntryClick(View view, Entry entry, int position);
    }
    
    public interface ListEntryLongPressListener {
        boolean listEntryLongPress(View view, Entry entry, int position);
    }
}
