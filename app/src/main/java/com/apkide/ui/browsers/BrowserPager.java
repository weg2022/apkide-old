package com.apkide.ui.browsers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.MainUI;

import java.util.ArrayList;
import java.util.List;

public class BrowserPager extends ViewPager {
	private List<View> _views;
	public static final int VIEW_FILES = 0;
	public static final int VIEW_EVENT = 1;
	public static final int VIEW_SEARCH = 2;
	public static final int VIEW_PROBLEMS = 3;
	public static final int VIEW_GIT = 4;
	public static final int VIEW_BUILD = 5;
	public static final int VIEW_LOGCAT = 6;
	
	public BrowserPager(@NonNull Context context) {
		this(context, null);
	}
	
	public BrowserPager(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private void initView() {
		_views = new ArrayList<>();
		_views.add(new FilesBrowser(getContext()));
		_views.add(new EventBrowser(getContext()));
		_views.add(new SearchBrowser(getContext()));
		_views.add(new ProblemsBrowser(getContext()));
		_views.add(new GitBrowser(getContext()));
		_views.add(new BuildBrowser(getContext()));
		_views.add(new LogcatBrowser(getContext()));
		setAdapter(new BrowserAdapter());
	}
	
	public MainUI getActivity() {
		return (MainUI) getContext();
	}
	
	public FilesBrowser getFileBrowser() {
		return (FilesBrowser) getView(VIEW_FILES);
	}
	
	public EventBrowser getEventBrowser() {
		return (EventBrowser) getView(VIEW_EVENT);
	}
	
	public SearchBrowser getSearchBrowser() {
		return (SearchBrowser) getView(VIEW_SEARCH);
	}
	
	public ProblemsBrowser getProblemsBrowser() {
		return (ProblemsBrowser) getView(VIEW_PROBLEMS);
	}
	
	public GitBrowser getGitBrowser() {
		return (GitBrowser) getView(VIEW_GIT);
	}
	
	public BuildBrowser getBuildBrowser() {
		return (BuildBrowser) getView(VIEW_BUILD);
	}
	
	public LogcatBrowser getLogcatBrowser() {
		return (LogcatBrowser) getView(VIEW_LOGCAT);
	}
	
	
	public int getCurrentBrowser() {
		return getCurrentItem();
	}
	
	public void setCurrentBrowser(int position, boolean scrollTo) {
		if (scrollTo) {
			setCurrentItem(position);
			return;
		}
		setCurrentItem(position == 0 ? 1 : 0, false);
		setCurrentItem(position, false);
	}
	
	private View getView(int position) {
		return _views.get(position);
	}
	
	private class BrowserAdapter extends PagerAdapter {
		private  int _position=-1;
		
		@Override
		public int getCount() {
			return _views.size();
		}
		
		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view.equals(object);
		}
		
		@Override
		public void startUpdate(@NonNull ViewGroup container) {
			super.startUpdate(container);
		}
		
		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			View view = _views.get(position);
			container.addView(view, 0);
			return view;
		}
		
		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			super.destroyItem(container, position, object);
			container.removeView((View) object);
		}
		
		@Override
		public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			super.setPrimaryItem(container, position, object);
			if(_position==position){
				return;
			}
			_position=position;
			postDelayed(() -> {
				Browser browser= (Browser) _views.get(position);
				browser.apply();
				getActivity().setCurrentBrowser(position);
				browser.unApply();
			}, 100L);
		}
		
		@Override
		public void finishUpdate(@NonNull ViewGroup container) {
			super.finishUpdate(container);
		}
		
		@Override
		public int getItemPosition(@NonNull Object object) {
			return super.getItemPosition(object);
		}
		
	}
	
}
