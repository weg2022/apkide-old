package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

public class FileHighlights implements Parcelable {

    public String filePath;
    public int[] kinds;
    public int[] startLines;
    public int[] startColumns;
    public int[] endLines;
    public int[] endColumns;
    public int size;
    public FileHighlights(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeIntArray(this.kinds);
        dest.writeIntArray(this.startLines);
        dest.writeIntArray(this.startColumns);
        dest.writeIntArray(this.endLines);
        dest.writeIntArray(this.endColumns);
        dest.writeInt(this.size);
    }

    public void readFromParcel(Parcel source) {
        this.filePath = source.readString();
        this.kinds = source.createIntArray();
        this.startLines = source.createIntArray();
        this.startColumns = source.createIntArray();
        this.endLines = source.createIntArray();
        this.endColumns = source.createIntArray();
        this.size = source.readInt();
    }

    protected FileHighlights(Parcel in) {
        this.filePath = in.readString();
        this.kinds = in.createIntArray();
        this.startLines = in.createIntArray();
        this.startColumns = in.createIntArray();
        this.endLines = in.createIntArray();
        this.endColumns = in.createIntArray();
        this.size = in.readInt();
    }

    public static final Parcelable.Creator<FileHighlights> CREATOR = new Parcelable.Creator<>() {
        @Override
        public FileHighlights createFromParcel(Parcel source) {
            return new FileHighlights(source);
        }

        @Override
        public FileHighlights[] newArray(int size) {
            return new FileHighlights[size];
        }
    };
}
