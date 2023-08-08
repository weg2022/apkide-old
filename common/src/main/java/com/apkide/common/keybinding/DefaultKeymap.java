package com.apkide.common.keybinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.action.Action;

import java.util.HashMap;

public class DefaultKeymap implements Keymap {
	private final HashMap<KeyStroke, Action> myBoundMap = new HashMap<>();
	private final Keymap myParent;
	private final String myName;

	public DefaultKeymap(@NonNull String name) {
		this(null, name);
	}

	public DefaultKeymap(@Nullable Keymap keymap, @NonNull String name) {
		myName = name;
		myParent = keymap;
		if (myParent != null) {
			for (KeyStroke key : myParent.getKeys()) {
				if (key != null) {
					Action action = myParent.getAction(key);
					if (action != null) {
						myBoundMap.put(key, action);
					}
				}
			}
		}
	}

	@Override
	public void bound(@NonNull KeyStroke key, @NonNull Action action) {
		myBoundMap.put(key, action);
	}

	@Override
	public void unbound(@NonNull KeyStroke key) {
		myBoundMap.remove(key);
	}

	@Nullable
	@Override
	public Action getAction(@NonNull KeyStroke key) {
		if (myBoundMap.containsKey(key)) {
			return myBoundMap.get(key);
		}
		return null;
	}

	@NonNull
	@Override
	public KeyStroke[] getKeys() {
		KeyStroke[] keys = new KeyStroke[myBoundMap.size()];
		myBoundMap.keySet().toArray(keys);
		return keys;
	}

	@NonNull
	@Override
	public Action[] getActions() {
		Action[] actions = new Action[myBoundMap.size()];
		myBoundMap.values().toArray(actions);
		return actions;
	}

	@Nullable
	@Override
	public Keymap getParent() {
		return myParent;
	}

	@Override
	public boolean hasParent() {
		return myParent != null;
	}

	@NonNull
	@Override
	public String getName() {
		return myName;
	}
}
