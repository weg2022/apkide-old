package com.apkide.ui.util;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.ui.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public abstract class EntryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Entry> entries = new ArrayList<>();
    private EntryClickListener clickListener;
    private EntryLongPressListener longPressListener;
    private EntryHolderFactory holderFactory;

    public Context getContext() {
        return App.getContext();
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void updateEntries(@NonNull Collection<Entry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
        notifyDataSetChanged();
    }

    public void clearEntries() {
        this.entries.clear();
        notifyDataSetChanged();
    }

    public Entry entryAt(int position) {
        return entries.get(position);
    }

    public int getEntriesCount() {
        return entries.size();
    }

    @Override
    public long getItemId(int position) {
        return entries.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if (holderFactory != null) {
            int type = holderFactory.getEntryType(entries.get(position), position);
            if (type != -1)
                return type;
        }
        return getEntryType(entries.get(position), position);
    }


    public View foundView(@LayoutRes int layoutId){
        return LayoutInflater.from(getContext()).inflate(layoutId,null,false);
    }
    protected abstract int getEntryType(@NonNull Entry entry, int position);

    @NonNull
    protected abstract RecyclerView.ViewHolder holderCreated(@NonNull ViewGroup parent, int entryType);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int entryType) {
        if (holderFactory != null) {
            RecyclerView.ViewHolder holder = holderFactory.onEntryHolderCreated(parent, entryType);
            if (holder != null) return holder;
        }

        return holderCreated(parent, entryType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        Entry entry = entries.get(position);
        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.entryClicked(entry, v, position));
        }

        if (longPressListener != null) {
            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(v -> longPressListener.entryLongPressed(entry, v, position));
        }

        if (holderFactory != null)
            holderFactory.onEntryHolderBound(entry, holder, position);

        holderBound(entry,holder, position);
    }

    protected abstract void holderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position);

    public void setClickListener(EntryClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongPressListener(EntryLongPressListener longPressListener) {
        this.longPressListener = longPressListener;
    }

    public void setHolderFactory(EntryHolderFactory holderFactory) {
        this.holderFactory = holderFactory;
    }


    public interface Entry {
    }

    public interface EntryHolderFactory {

        RecyclerView.ViewHolder onEntryHolderCreated(@NonNull ViewGroup parent, int entryType);


        void onEntryHolderBound(@NonNull Entry entry, @NonNull RecyclerView.ViewHolder holder, int position);

        int getEntryType(@NonNull Entry entry, int position);
    }

    public interface EntryClickListener {
        void entryClicked(Entry entry, View view, int position);
    }

    public interface EntryLongPressListener {
        boolean entryLongPressed(Entry entry, View view, int position);
    }


}
