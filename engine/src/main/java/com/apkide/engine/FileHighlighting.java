package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

public class FileHighlighting implements Parcelable {
    
    public String filePath;
    public long version;
    public int[] styles;
    public int[] startLines;
    public int[] startColumns;
    public int[] endLines;
    public int[] endColumns;
    public int size;
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeLong(this.version);
        dest.writeIntArray(this.styles);
        dest.writeIntArray(this.startLines);
        dest.writeIntArray(this.startColumns);
        dest.writeIntArray(this.endLines);
        dest.writeIntArray(this.endColumns);
        dest.writeInt(this.size);
    }
    
    public void readFromParcel(Parcel source) {
        this.filePath = source.readString();
        this.version = source.readLong();
        this.styles = source.createIntArray();
        this.startLines = source.createIntArray();
        this.startColumns = source.createIntArray();
        this.endLines = source.createIntArray();
        this.endColumns = source.createIntArray();
        this.size = source.readInt();
    }
    
    public FileHighlighting() {
    }
    
    protected FileHighlighting(Parcel in) {
        this.filePath = in.readString();
        this.version = in.readLong();
        this.styles = in.createIntArray();
        this.startLines = in.createIntArray();
        this.startColumns = in.createIntArray();
        this.endLines = in.createIntArray();
        this.endColumns = in.createIntArray();
        this.size = in.readInt();
    }
    
    public static final Parcelable.Creator<FileHighlighting> CREATOR = new Parcelable.Creator<FileHighlighting>() {
        @Override
        public FileHighlighting createFromParcel(Parcel source) {
            return new FileHighlighting(source);
        }
        
        @Override
        public FileHighlighting[] newArray(int size) {
            return new FileHighlighting[size];
        }
    };
}
