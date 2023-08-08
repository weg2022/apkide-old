package com.apkide.common.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public  class DefaultAction implements Action {

	private final HashMap<Object, Object> myProperties = new HashMap<>();
	private boolean myEnabled;

	private final String myName;

	public DefaultAction(@NonNull String name){
		this(name,true);
	}
	public DefaultAction(@NonNull String name, boolean enabled) {
		myName = name;
		myEnabled= enabled;
	}

	@NonNull
	@Override
	public String getName() {
		return myName;
	}

	@Override
	public boolean isEnabled() {
		return myEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		myEnabled = enabled;
	}

	@Override
	public void setProperty(@NonNull Object key, @Nullable Object value) {
		myProperties.put(key, value);
	}

	@Nullable
	@Override
	public Object getProperty(@NonNull Object key) {
		if (myProperties.containsKey(key))
			return myProperties.get(key);
		return null;
	}

	@Nullable
	@Override
	public Object getProperty(@NonNull Object key, @Nullable Object def) {
		if (myProperties.containsKey(key)) {
			return myProperties.get(key);
		}
		return def;
	}

	@Override
	public void actionPerformed(@NonNull ActionEvent event) {

	}
}
