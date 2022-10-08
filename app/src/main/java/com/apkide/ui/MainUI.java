package com.apkide.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.core.logging.Level;
import com.apkide.core.logging.LoggerReceiver;
import com.apkide.ui.databinding.MainUiBinding;
import com.apkide.ui.preferences.PreferencesUI;
import com.apkide.ui.whatsnew.WhatsNew;

public class MainUI extends StyledUI implements LoggerReceiver.LoggerListener{
	
	private LoggerReceiver _loggerReceiver;
	private MainUiBinding _uiBinding;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_uiBinding = MainUiBinding.inflate(getLayoutInflater());
		setContentView(_uiBinding);
		WhatsNew.register(this);
		_uiBinding.button.setOnClickListener(this);
		
		_loggerReceiver = new LoggerReceiver();
		_loggerReceiver.setLoggerListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LoggerReceiver.ACTION_LOGGER);
		registerReceiver(_loggerReceiver, intentFilter);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		WhatsNew.unregister(this);
		
		if (_uiBinding != null)
			_uiBinding = null;
		
		if (_loggerReceiver != null)
			unregisterReceiver(_loggerReceiver);
	}
	
	@Override
	public void onClick(@NonNull View v) {
		startActivity(new Intent(this, PreferencesUI.class));
	}
	
	@Override
	public void onLogging(String logName, Level level, String msg, Throwable thrown) {
	
	}
	
	public void setCurrentBrowser(int position){
		commitCurrentBrowser(position);
	}
	public void commitCurrentBrowser(int position){
		SharedPreferences.Editor edit = getSharedPreferences("BrowserLayout", 0).edit();
		edit.putInt("CurrentBrowser", position);
		edit.apply();
	}
}
