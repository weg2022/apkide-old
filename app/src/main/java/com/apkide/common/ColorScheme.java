package com.apkide.common;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ColorScheme extends Parcelable {

    void restoreDefaults();

    void register(int attrId, @NonNull TextAttribute attribute);

    void unregister(int attrId);

    boolean isRegister(int attrId);

    @Nullable
    TextAttribute getAttribute(int attrId);

    @NonNull
    String getName();

    boolean isDark();
}
