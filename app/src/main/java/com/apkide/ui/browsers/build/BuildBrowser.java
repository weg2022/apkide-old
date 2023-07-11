package com.apkide.ui.browsers.build;

import android.content.Context;
import android.widget.LinearLayout;

import com.apkide.ui.browsers.Browser;

public class BuildBrowser extends LinearLayout implements Browser {
    public BuildBrowser(Context context) {
        super(context);
    }

    @Override
    public void onSyncing() {

    }
}
