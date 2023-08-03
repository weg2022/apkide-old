package com.apkide.ui.views;

import androidx.annotation.NonNull;

import com.apkide.common.Command;
import com.apkide.common.KeyStroke;

public interface KeyStrokeCommand extends Command {

    @NonNull
    String getName();

    boolean isEnabled();

    boolean run();

    @NonNull
    KeyStroke getKey();
}
