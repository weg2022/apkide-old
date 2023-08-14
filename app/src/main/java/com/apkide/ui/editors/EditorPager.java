package com.apkide.ui.editors;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.apkide.common.AppLog;
import com.apkide.ui.App;
import com.apkide.ui.services.openfile.OpenFileModel;
import com.apkide.ui.services.openfile.OpenFileServiceListener;

public class EditorPager extends ViewPager implements OpenFileServiceListener {
	public EditorPager(Context context) {
		super(context);
		initView();
	}

	public EditorPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {

		App.getOpenFileService().addListener(this);
	}

	@Override
	public void fileOpened(@NonNull String filePath, @NonNull OpenFileModel fileModel) {
		AppLog.s(this,"fileOpened: "+filePath);
	}

	@Override
	public void fileClosed(@NonNull String filePath, @NonNull OpenFileModel fileModel) {
		AppLog.s(this,"fileClosed: "+filePath);
	}
}
