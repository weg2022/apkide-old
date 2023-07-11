package com.apkide.ui.browsers.find;

import android.content.Context;
import android.widget.LinearLayout;

import com.apkide.ui.browsers.Browser;

public class FindBrowser extends LinearLayout implements Browser {
    public FindBrowser(Context context) {
        super(context);
    }

    @Override
    public void onSyncing() {

    }
}
