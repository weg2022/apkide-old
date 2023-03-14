package com.apkide.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.Vector;

public class IDEEditorPager extends ViewPager {
	public IDEEditorPager(Context context) {
		super(context);
		init();
	}
	
	public IDEEditorPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private final List<IDEEditor> editors = new Vector<>();
	
	private void init() {
		setAdapter(new PagerAdapter() {
			@Nullable
			@Override
			public Parcelable saveState() {
				return super.saveState();
			}
			
			@Override
			public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
				super.restoreState(state, loader);
			}
			
			
			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, int position) {
				return super.instantiateItem(container, position);
			}
			
			@Override
			public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
				super.destroyItem(container, position, object);
			}
			
			@Override
			public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
				super.setPrimaryItem(container, position, object);
			}
			
			@Override
			public void finishUpdate(@NonNull ViewGroup container) {
				super.finishUpdate(container);
			}
			
			@Override
			public int getItemPosition(@NonNull Object object) {
				return super.getItemPosition(object);
			}
			
			@Nullable
			@Override
			public CharSequence getPageTitle(int position) {
				return super.getPageTitle(position);
			}
			
			@Override
			public int getCount() {
				return editors.size();
			}
			
			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view.equals(object);
			}
		});
	}
	
	public void addEditor(@NonNull IDEEditor editor) {
		if (editors.contains(editor)) return;
		editors.add(editor);
		notifyEditorsChanged();
	}
	
	public IDEEditor getCurrentEditor() {
		return getEditorAt(getCurrentItem());
	}
	
	public IDEEditor getEditorAt(int index) {
		return editors.get(index);
	}
	
	public void removeEditorAt(int index) {
		editors.remove(index);
		notifyEditorsChanged();
	}
	
	public void removeAllEditors() {
		editors.clear();
		notifyEditorsChanged();
	}
	
	public int getEditorCount() {
		return editors.size();
	}
	
	private void notifyEditorsChanged() {
		if (getCurrentItem() >= editors.size())
			setCurrentItem(editors.size() - 1);
		assert getAdapter() != null;
		getAdapter().notifyDataSetChanged();
	}
}
