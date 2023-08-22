package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class FileSpan implements Parcelable {
    public static final Parcelable.Creator<FileSpan> CREATOR = new Parcelable.Creator<>() {
        @Override
        public FileSpan createFromParcel(Parcel source) {
            return new FileSpan(source);
        }
        
        @Override
        public FileSpan[] newArray(int size) {
            return new FileSpan[size];
        }
    };
    @NonNull
    private String filePath;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    
    
    public FileSpan(@NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }
    
    protected FileSpan(Parcel in) {
        this.filePath = Objects.requireNonNull(in.readString());
        this.startLine = in.readInt();
        this.startColumn = in.readInt();
        this.endLine = in.readInt();
        this.endColumn = in.readInt();
    }
    
    @NonNull
    public String getFilePath() {
        return filePath;
    }
    
    public int getStartLine() {
        return startLine;
    }
    
    public int getStartColumn() {
        return startColumn;
    }
    
    public int getEndLine() {
        return endLine;
    }
    
    public int getEndColumn() {
        return endColumn;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeInt(this.startLine);
        dest.writeInt(this.startColumn);
        dest.writeInt(this.endLine);
        dest.writeInt(this.endColumn);
    }
    
    public void readFromParcel(Parcel source) {
        this.filePath = Objects.requireNonNull(source.readString());
        this.startLine = source.readInt();
        this.startColumn = source.readInt();
        this.endLine = source.readInt();
        this.endColumn = source.readInt();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(filePath, startLine, startColumn, endLine, endColumn);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSpan fileSpan = (FileSpan) o;
        return startLine == fileSpan.startLine && startColumn == fileSpan.startColumn && endLine == fileSpan.endLine && endColumn == fileSpan.endColumn && filePath.equals(fileSpan.filePath);
    }
}
