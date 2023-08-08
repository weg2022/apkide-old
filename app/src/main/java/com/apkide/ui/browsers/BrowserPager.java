package com.apkide.ui.browsers;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.MainUI;
import com.apkide.ui.R;
import com.apkide.ui.browsers.build.BuildBrowser;
import com.apkide.ui.browsers.file.FileBrowser;
import com.apkide.ui.browsers.problem.ProblemBrowser;
import com.apkide.ui.browsers.search.SearchBrowser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BrowserPager extends ViewPager {
    public BrowserPager(@NonNull Context context) {
        super(context);
        initView();
    }

    public BrowserPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private static final String PREF_NAME = "browserPager";
    private SharedPreferences preferences;
    public static final int FILE_BROWSER = 0;
    public static final int SEARCH_BROWSER = 1;
    public static final int PROBLEM_BROWSER = 2;
    public static final int BUILD_BROWSER = 3;
    private final List<View> browsers = new ArrayList<>();

    private void initView() {
        browsers.add(new FileBrowser(getContext()));
        browsers.add(new SearchBrowser(getContext()));
        browsers.add(new ProblemBrowser(getContext()));
        browsers.add(new BuildBrowser(getContext()));

        try {
            Field declaredField = requireNonNull(getClass().getSuperclass()).getDeclaredField("mTouchSlop");
            declaredField.setAccessible(true);
            declaredField.setInt(this, 15);
        } catch (Exception ignored) {

        }

        setPageMargin((int) getContext().getResources().getDisplayMetrics().density);
        setPageMarginDrawable(new ColorDrawable(getContext().getColor(R.color.colorOnSecondary)));
        setAdapter(new PagerAdapter() {
            private int index = -1;

            @Override
            public int getCount() {
                return browsers.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view.equals(object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = browsers.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                if (index != position) {
                    index = position;
                    postDelayed(() -> {
                        Browser browser = (Browser) browsers.get(index);
                        browser.onApply();
                        getPreferences().edit().putInt("currentIndex", index).apply();
                    }, 100L);
                }
            }
        });
    }

    public int getBrowserCount() {
        return browsers.size();
    }

    public MainUI getUI() {
        return (MainUI) getContext();
    }

    public FileBrowser getFileBrowser() {
        return (FileBrowser) browserAt(FILE_BROWSER);
    }

    public ProblemBrowser getProblemBrowser() {
        return (ProblemBrowser) browserAt(PROBLEM_BROWSER);
    }

    public BuildBrowser getBuildBrowser() {
        return (BuildBrowser) browserAt(BUILD_BROWSER);
    }

    public SearchBrowser getFindBrowser() {
        return (SearchBrowser) browserAt(SEARCH_BROWSER);
    }


    public int getCurrentBrowser() {
        return getCurrentItem();
    }

    private View browserAt(int index) {
        return browsers.get(index);
    }

    public void refresh() {
        toggle(getCurrentBrowser(), true);
    }

    public void toggle(int index, boolean refresh) {
        if (refresh) {
            setCurrentItem(index == 0 ? 1 : 0, false);
            setCurrentItem(index, false);
        } else {
            setCurrentItem(index);
        }
    }

    @NonNull
    public SharedPreferences getPreferences() {
        if (preferences == null)
            preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences;
    }

    public int getLastBrowser() {
        return getPreferences().getInt("currentIndex", -1);
    }


}
