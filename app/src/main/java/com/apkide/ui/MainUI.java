package com.apkide.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.apkide.core.logging.Level;
import com.apkide.core.logging.LoggerReceiver;
import com.apkide.ui.databinding.MainUiBinding;
import com.apkide.ui.preferences.PreferencesUI;
import com.apkide.ui.marketing.WhatsNew;

public class MainUI extends StyledUI implements LoggerReceiver.LoggerListener {
	
	private LoggerReceiver _loggerReceiver;
	private MainUiBinding _uiBinding;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		IDEPreferences.init(this);
		super.onCreate(savedInstanceState);
		_uiBinding = MainUiBinding.inflate(getLayoutInflater());
		setContentView(_uiBinding);
		registerWhatsnew();
		registerLogger();
	}
	
	private void registerWhatsnew() {
		WhatsNew.register(this);
	}
	
	private void unregisterWhatsnew() {
		WhatsNew.unregister(this);
	}
	
	private void registerLogger() {
		_loggerReceiver = new LoggerReceiver();
		_loggerReceiver.setLoggerListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LoggerReceiver.ACTION_LOGGER);
		registerReceiver(_loggerReceiver, intentFilter);
	}
	
	private void unregisterLogger() {
		if (_loggerReceiver != null) {
			unregisterReceiver(_loggerReceiver);
			_loggerReceiver = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterWhatsnew();
		unregisterLogger();
		if (_uiBinding != null)
			_uiBinding = null;
	}
	
	@Override
	public void onLogging(String logName, Level level, String msg, Throwable thrown) {
	
	}
	
	public void setCurrentBrowser(int position) {
		commitCurrentBrowser(position);
	}
	
	public void commitCurrentBrowser(int position) {
		SharedPreferences.Editor edit = getSharedPreferences("BrowserLayout", 0).edit();
		edit.putInt("CurrentBrowser", position);
		edit.apply();
	}
	
	
	public void exitApp() {
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_context_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mainExitCommand:
				exitApp();
				break;
			case R.id.mainGotoSettingsCommand:
				startActivity(new Intent(this, PreferencesUI.class));
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
