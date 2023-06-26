package com.apkide.ui.util;

import com.apkide.common.Command;

public interface MenuCommand extends Command {

    int getId();

    @Override
    default boolean isEnabled() {
        return true;
    }

    @Override
    default void setEnabled(boolean enabled) {

    }

    default boolean isVisible() {
        return true;
    }
}
