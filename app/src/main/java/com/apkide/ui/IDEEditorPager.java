package com.apkide.ui;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.services.file.OpenFileModel;
import com.apkide.ui.services.file.OpenFileProvider;

import java.lang.reflect.Field;
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
        try {
            Field declaredField = requireNonNull(getClass().getSuperclass()).getDeclaredField("mTouchSlop");
            declaredField.setAccessible(true);
            declaredField.setInt(this, 15);
        } catch (Exception ignored) {
        }

        setPageMargin((int) getContext().getResources().getDisplayMetrics().density);
        setPageMarginDrawable(new ColorDrawable(getContext().getColor(R.color.colorSecondaryVariant)));

        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return myViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view.equals(object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = myViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            private int index = -1;

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                if (this.index != position) {
                    this.index = position;
                }
            }

        });
    }

    public int getEditorCount() {
        return myViews.size();
    }

    public IDEEditor getCurrentEditor() {
        int currentItem = getCurrentItem();
        if (currentItem < 0) {
            return null;
        }
        return getEditor(currentItem);
    }

    public IDEEditor getEditor(int index) {
        if (index < 0 || index >= myViews.size()) {
            return null;
        }
        return myViews.get(index).findViewById(R.id.editorComponent);
    }

    public List<IDEEditor> getEditors() {
        List<IDEEditor> editors = new ArrayList<>(myViews.size());
        for (int i = 0; i < myViews.size(); i++) {
            editors.add(getEditor(i));
        }
        return editors;
    }

    @Override
    public OpenFileModel openFile(String filePath) {

        return null;
    }

    @Override
    public OpenFileModel closeFile(String filePath) {

        return null;
    }
}
