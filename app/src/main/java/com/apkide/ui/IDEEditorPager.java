package com.apkide.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.services.file.OpenFileModel;
import com.apkide.ui.services.file.OpenFileProvider;

import java.util.ArrayList;
import java.util.List;

public class IDEEditorPager extends ViewPager implements OpenFileProvider {
    public IDEEditorPager(@NonNull Context context) {
        super(context);
        initView();
    }

    public IDEEditorPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private final List<View> myViews = new ArrayList<>();

    private void initView() {
        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return myViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view.equals(object);
            }

        });
    }


    @Override
    public OpenFileModel getFileModel(String filePath) {
        return null;
    }

    @Override
    public String getFilePath() {
        return null;
    }

    @Override
    public void openFile(String filePath) {

    }

    @Override
    public void closeFile(String filePath) {

    }
}
