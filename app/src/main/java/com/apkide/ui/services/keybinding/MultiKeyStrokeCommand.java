package com.apkide.ui.services.keybinding;

import androidx.annotation.NonNull;

import com.apkide.common.KeyStroke;
import com.apkide.ui.views.KeyStrokeCommand;

import java.util.List;

public interface MultiKeyStrokeCommand extends KeyStrokeCommand {


	@NonNull
	List<KeyStroke> getKeys();
}
