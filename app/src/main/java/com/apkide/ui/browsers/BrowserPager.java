package com.apkide.ui.browsers;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.build.BuildBrowser;
import com.apkide.ui.browsers.error.ErrorBrowser;
import com.apkide.ui.browsers.file.FileBrowser;
import com.apkide.ui.browsers.project.ProjectBrowser;
import com.apkide.ui.browsers.search.SearchBrowser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BrowserPager extends ViewPager {
	public BrowserPager(Context context) {
		super(context);
		initView();
	}

	public BrowserPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private final List<View> myViews = new ArrayList<>();

	public static final int FILE_BROWSER=0;
	public static final int PROJECT_BROWSER=1;
	public static final int ERROR_BROWSER=2;
	public static final int SEARCH_BROWSER=3;
	public static final int BUILD_BROWSER=4;
	private SharedPreferences myPreferences;

	private void initView() {
		removeAllViews();
		myViews.add(new FileBrowser(getContext()));
		myViews.add(new ProjectBrowser(getContext()));
		myViews.add(new ErrorBrowser(getContext()));
		myViews.add(new SearchBrowser(getContext()));
		myViews.add(new BuildBrowser(getContext()));

		try {
			Field declaredField = requireNonNull(getClass().getSuperclass()).getDeclaredField("mTouchSlop");
			declaredField.setAccessible(true);
			declaredField.setInt(this, 20);
		} catch (Exception ignored) {

		}

		setPageMargin((int) getContext().getResources().getDisplayMetrics().density);
		setPageMarginDrawable(new ColorDrawable(getContext().getColor(R.color.colorSecondaryVariant)));
		setAdapter(new PagerAdapter() {
			private int lastIndex;

			@Override
			public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
				super.setPrimaryItem(container, position, object);
				if (lastIndex != position) {
					lastIndex = position;
					postDelayed(() -> {
						Browser browser = (Browser) myViews.get(position);
						browser.refresh();
						getPreferences().edit().putInt("index", lastIndex).apply();
					}, 50L);
				}
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

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view.equals(object);
			}

			@Override
			public int getCount() {
				return myViews.size();
			}

		});

		int index = getPreferences().getInt("index", -1);
		if (index != -1)
			toggle(index, false);
	}


	public FileBrowser getFileBrowser(){
		return viewAt(FILE_BROWSER);
	}
	public ProjectBrowser getProjectBrowser(){
		return viewAt(PROJECT_BROWSER);
	}

	public ErrorBrowser getErrorBrowser(){
		return viewAt(ERROR_BROWSER);
	}

	public SearchBrowser getSearchBrowser(){
		return viewAt(SEARCH_BROWSER);
	}

	public BuildBrowser getBuildBrowser(){
		return viewAt(BUILD_BROWSER);
	}

	private <T extends View>T viewAt(int index){
		return (T) myViews.get(index);
	}

	public int getIndex() {
		return getCurrentItem();
	}

	public int getCount(){
		return myViews.size();
	}

	public void refresh() {
		toggle(getIndex(), true);
	}

	public void toggle(int index){
		toggle(index,true);
	}

	public void toggle(int index, boolean refresh) {
		if (refresh) {
			setCurrentItem(index == 0 ? 1 : 0, false);
			setCurrentItem(index, false);
		} else {
			setCurrentItem(index);
		}
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null)
			myPreferences = App.getPreferences("BrowserPager");
		return myPreferences;
	}
}
