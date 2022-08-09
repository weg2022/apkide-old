package com.apkide.common;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public abstract class BaseColorScheme implements ColorScheme {

    private TextAttribute[] attributes = new TextAttribute[50];
    private String name;
    private boolean dark;

    public BaseColorScheme(@NonNull String name, boolean dark) {
        this.name = name;
        this.dark = dark;
        restoreDefaults();
    }

    @Override
    public void restoreDefaults() {
        Arrays.fill(attributes, null);
        setDefaults();
    }


    protected abstract void setDefaults();

    @Override
    public void register(int attrId, @NonNull TextAttribute attribute) {
        if (attrId >= attributes.length)
            attributes = Arrays.copyOf(attributes, attrId + 50);
        attributes[attrId] = attribute;
    }

    @Override
    public void unregister(int attrId) {
        if (attrId < 0 || attrId > attributes.length) return;
        attributes[attrId] = null;
    }

    @Override
    public boolean isRegister(int attrId) {
        if (attrId < 0 || attrId > attributes.length) return false;
        return attributes[attrId] != null;
    }

    @Nullable
    @Override
    public TextAttribute getAttribute(int attrId) {
        if (attrId < 0 || attrId > attributes.length) return null;
        return attributes[attrId];
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDark() {
        return dark;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.attributes, flags);
        dest.writeString(this.name);
        dest.writeByte(this.dark ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.attributes = source.createTypedArray(TextAttribute.CREATOR);
        this.name = source.readString();
        this.dark = source.readByte() != 0;
    }

    protected BaseColorScheme(Parcel in) {
        this.attributes = in.createTypedArray(TextAttribute.CREATOR);
        this.name = in.readString();
        this.dark = in.readByte() != 0;
    }

}
