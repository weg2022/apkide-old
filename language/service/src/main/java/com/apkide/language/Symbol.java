package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Symbol implements Parcelable {
    public static final Parcelable.Creator<Symbol> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Symbol createFromParcel(Parcel source) {
            return new Symbol(source);
        }
        
        @Override
        public Symbol[] newArray(int size) {
            return new Symbol[size];
        }
    };
    private int kind;
    @NonNull
    private String name;
    private boolean deprecated;
    private @NonNull String filePath;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    
    
    public Symbol(int kind, @NonNull String name, boolean deprecated, @NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        this.kind = kind;
        this.name = name;
        this.deprecated = deprecated;
        this.filePath = filePath;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }
    
    
    public int getKind() {
        return kind;
    }
    
    @NonNull
    public String getName() {
        return name;
    }
    
    public boolean isDeprecated() {
        return deprecated;
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
    
    protected Symbol(Parcel in) {
        this.kind = in.readInt();
        this.name = Objects.requireNonNull(in.readString());
        this.deprecated = in.readByte() != 0;
        this.filePath = Objects.requireNonNull(in.readString());
        this.startLine = in.readInt();
        this.startColumn = in.readInt();
        this.endLine = in.readInt();
        this.endColumn = in.readInt();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.kind);
        dest.writeString(this.name);
        dest.writeByte(this.deprecated ? (byte) 1 : (byte) 0);
        dest.writeString(this.filePath);
        dest.writeInt(this.startLine);
        dest.writeInt(this.startColumn);
        dest.writeInt(this.endLine);
        dest.writeInt(this.endColumn);
    }
    
    public void readFromParcel(Parcel source) {
        this.kind = source.readInt();
        this.name = Objects.requireNonNull(source.readString());
        this.deprecated = source.readByte() != 0;
        this.filePath = Objects.requireNonNull(source.readString());
        this.startLine = source.readInt();
        this.startColumn = source.readInt();
        this.endLine = source.readInt();
        this.endColumn = source.readInt();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(kind, name, deprecated, filePath, startLine, startColumn, endLine, endColumn);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return kind == symbol.kind && deprecated == symbol.deprecated && startLine == symbol.startLine && startColumn == symbol.startColumn && endLine == symbol.endLine && endColumn == symbol.endColumn && name.equals(symbol.name) && filePath.equals(symbol.filePath);
    }
}
