package com.apkide.ui.browsers.file;

import static android.view.LayoutInflater.from;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.apkide.ui.R;
import com.apkide.ui.browsers.Browser;
import com.apkide.ui.databinding.BrowserFileBinding;
import com.apkide.ui.databinding.BrowserHeaderBinding;
import com.apkide.ui.util.EntryAdapter;


@SuppressLint("InflateParams")
public class FileBrowser extends LinearLayout implements Browser, PopupMenu.OnMenuItemClickListener, EntryAdapter.EntryClickListener, EntryAdapter.EntryLongPressListener {

    private BrowserHeaderBinding myHeaderBinding;
    private BrowserFileBinding myBinding;
    private final FileEntryAdapter myAdapter=new FileEntryAdapter();

    @SuppressLint("ClickableViewAccessibility")
    public FileBrowser(Context context) {
        super(context);
        removeAllViews();
        setOrientation(VERTICAL);
        LayoutInflater inflater = from(getContext());
        myHeaderBinding = BrowserHeaderBinding.inflate(inflater, this, false);
        myBinding = BrowserFileBinding.inflate(inflater, this, false);
        addView(myHeaderBinding.getRoot());
        addView(myBinding.getRoot());

        myHeaderBinding.browserHeaderLabel.setText("Files");
        myHeaderBinding.browserHeaderIcon.setImageResource(R.mipmap.folder);
        myHeaderBinding.browserHeaderHelp.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.inflate(R.menu.filebrowser_options);
            menu.setOnMenuItemClickListener(FileBrowser.this);
            menu.show();
        });

        myBinding.filebrowserListView.setNestedScrollingEnabled(false);
        myBinding.filebrowserListView.setLayoutManager(new LinearLayoutManager(getContext()));
        myBinding.filebrowserListView.setAdapter(myAdapter);
        myAdapter.setClickListener(this);
        myAdapter.setLongPressListener(this);
    }


    @Override
    public void onSyncing() {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void entryClicked(EntryAdapter.Entry entry, View view, int position) {

    }

    @Override
    public boolean entryLongPressed(EntryAdapter.Entry entry, View view, int position) {
        return false;
    }
}
