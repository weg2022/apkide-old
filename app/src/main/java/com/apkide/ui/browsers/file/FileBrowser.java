package com.apkide.ui.browsers.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

import com.apkide.ui.browsers.Browser;


@SuppressLint("InflateParams")
public class FileBrowser extends LinearLayout implements Browser {
    private final SharedPreferences preferences;

    @SuppressLint("ClickableViewAccessibility")
    public FileBrowser(Context context) {
        super(context);
        preferences = context.getSharedPreferences("file_browser", Context.MODE_PRIVATE);
        setLayoutParams(new AbsListView.LayoutParams(-1, -1));
        ViewCompat.requestApplyInsets(this);
        setOrientation(VERTICAL);
        removeAllViews();
    }


    @Override
    public void reload() {

    }

}
