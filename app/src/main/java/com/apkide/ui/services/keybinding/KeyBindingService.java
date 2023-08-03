package com.apkide.ui.services.keybinding;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.apkide.common.KeyStroke;
import com.apkide.ui.App;
import com.apkide.ui.views.KeyStrokeCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingService {
	private final Map<String, KeyStroke> myKeyMaps = new HashMap<>();
	private SharedPreferences myPreferences;


	public void bound(KeyStrokeCommand command, KeyStroke key) {
		if (key == null) {
			myKeyMaps.remove(command.getClass().getName() + command.getName());
		} else {
			myKeyMaps.put(command.getClass().getName() + command.getName(), key);
		}
		save();
	}

	public List<KeyStroke> getKeys(KeyStrokeCommand command) {
		ArrayList<KeyStroke> keys = new ArrayList<>();
		KeyStroke key = myKeyMaps.get(command.getClass().getName() + command.getName());
		if (key != null) {
			keys.add(key);
		} else {
			keys.add(command.getKey());
			if (command instanceof MultiKeyStrokeCommand) {
				keys.addAll(((MultiKeyStrokeCommand) command).getKeys());
			}
		}
		return keys;
	}

	public void clear() {
		myKeyMaps.clear();
		save();
	}

	@SuppressLint("CommitPrefEdits")
	private void save() {
		getPreferences().edit().clear();
		for (Map.Entry<String, KeyStroke> entry : myKeyMaps.entrySet()) {
			getPreferences().edit().putString(entry.getKey(), entry.getValue().store()).apply();
		}
	}

	public void restore() {
		Map<String, ?> map = myPreferences.getAll();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			if (entry.getValue() != null && entry.getValue() instanceof String) {
				myKeyMaps.put(entry.getKey(), KeyStroke.load((String) entry.getValue()));
			}
		}
	}

	private SharedPreferences getPreferences() {
		if (myPreferences == null) {
			myPreferences = App.getPreferences("KeyBindingService");
		}
		return myPreferences;
	}
}
