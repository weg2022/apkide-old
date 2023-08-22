package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Objects;

public class FileHighlighting implements Parcelable {
    public static final Parcelable.Creator<FileHighlighting> CREATOR = new Parcelable.Creator<>() {
        @Override
        public FileHighlighting createFromParcel(Parcel source) {
            return new FileHighlighting(source);
        }
        
        @Override
        public FileHighlighting[] newArray(int size) {
            return new FileHighlighting[size];
        }
    };
    public String filePath;
    public int[] styles;
    public int[] startLines;
    public int[] startColumns;
    public int[] endLines;
    public int[] endColumns;
    public int length;
    
    public FileHighlighting() {
    }
    
    protected FileHighlighting(Parcel in) {
        this.filePath = in.readString();
        this.styles = in.createIntArray();
        this.startLines = in.createIntArray();
        this.startColumns = in.createIntArray();
        this.endLines = in.createIntArray();
        this.endColumns = in.createIntArray();
        this.length = in.readInt();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeIntArray(this.styles);
        dest.writeIntArray(this.startLines);
        dest.writeIntArray(this.startColumns);
        dest.writeIntArray(this.endLines);
        dest.writeIntArray(this.endColumns);
        dest.writeInt(this.length);
    }
    
    public void readFromParcel(Parcel source) {
        this.filePath = source.readString();
        this.styles = source.createIntArray();
        this.startLines = source.createIntArray();
        this.startColumns = source.createIntArray();
        this.endLines = source.createIntArray();
        this.endColumns = source.createIntArray();
        this.length = source.readInt();
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(filePath, length);
        result = 31 * result + Arrays.hashCode(styles);
        result = 31 * result + Arrays.hashCode(startLines);
        result = 31 * result + Arrays.hashCode(startColumns);
        result = 31 * result + Arrays.hashCode(endLines);
        result = 31 * result + Arrays.hashCode(endColumns);
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileHighlighting that = (FileHighlighting) o;
        return length == that.length && Objects.equals(filePath, that.filePath) && Arrays.equals(styles, that.styles) && Arrays.equals(startLines, that.startLines) && Arrays.equals(startColumns, that.startColumns) && Arrays.equals(endLines, that.endLines) && Arrays.equals(endColumns, that.endColumns);
    }
}
