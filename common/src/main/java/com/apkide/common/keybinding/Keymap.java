package com.apkide.common.keybinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.action.Action;

public interface Keymap {

	void bound(@NonNull KeyStroke key, @NonNull Action action);

	void unbound(@NonNull KeyStroke key);

	@Nullable
	Action getAction(@NonNull KeyStroke key);

	@NonNull
	KeyStroke[] getKeys();

	@NonNull
	Action[] getActions();

	@Nullable
	Keymap getParent();

	boolean hasParent();

	@NonNull
	String getName();
}
