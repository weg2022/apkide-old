package com.apkide.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.core.Logger;
import com.apkide.core.LoggerReceiver;
import com.apkide.ui.databinding.MainUiBinding;

public class MainUI extends StyledUI {

	private static final Logger LOGGER=Logger.getLogger("Main");
	private LoggerReceiver _loggerReceiver;
	private MainUiBinding _uiBinding;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_uiBinding=MainUiBinding.inflate(getLayoutInflater());
		setContentView(_uiBinding);
		_uiBinding.button.setOnClickListener(this);
		
		_loggerReceiver=new LoggerReceiver();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(LoggerReceiver.ACTION_LOGGER);
		registerReceiver(_loggerReceiver,intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (_uiBinding!=null)
			_uiBinding=null;
		
		if (_loggerReceiver!=null)
			unregisterReceiver(_loggerReceiver);
	}
	
	@Override
	public void onClick(@NonNull View v) {
	
	}
}
