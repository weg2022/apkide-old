package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BrowserPager extends ViewPager {
    public BrowserPager(@NonNull Context context) {
        super(context);
    }

    public BrowserPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
