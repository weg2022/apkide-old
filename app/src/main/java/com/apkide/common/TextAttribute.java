package com.apkide.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class TextAttribute implements Parcelable {
    public int foreground;
    public int background;
    public int fontStyle;
    public int flags;

    public TextAttribute(int foreground){
        this(foreground,0);
    }
    public TextAttribute(int foreground,int background){
        this(foreground,background,0);
    }

    public TextAttribute(int foreground,int background,int fontStyle){
        this(foreground,background,fontStyle,0);
    }

    public TextAttribute(int foreground, int background, int fontStyle, int flags) {
        this.foreground = foreground;
        this.background = background;
        this.fontStyle = fontStyle;
        this.flags = flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextAttribute that = (TextAttribute) o;
        return foreground == that.foreground && background == that.background && fontStyle == that.fontStyle && flags == that.flags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(foreground, background, fontStyle, flags);
    }

    @NonNull
    @Override
    public String toString() {
        return "TextAttribute{" +
                "foreground=" + foreground +
                ", background=" + background +
                ", fontStyle=" + fontStyle +
                ", flags=" + flags +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.foreground);
        dest.writeInt(this.background);
        dest.writeInt(this.fontStyle);
        dest.writeInt(this.flags);
    }

    public void readFromParcel(Parcel source) {
        this.foreground = source.readInt();
        this.background = source.readInt();
        this.fontStyle = source.readInt();
        this.flags = source.readInt();
    }

    protected TextAttribute(Parcel in) {
        this.foreground = in.readInt();
        this.background = in.readInt();
        this.fontStyle = in.readInt();
        this.flags = in.readInt();
    }

    public static final Creator<TextAttribute> CREATOR = new Creator<TextAttribute>() {
        @Override
        public TextAttribute createFromParcel(Parcel source) {
            return new TextAttribute(source);
        }

        @Override
        public TextAttribute[] newArray(int size) {
            return new TextAttribute[size];
        }
    };
}
