package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Problem implements Parcelable {
    public static final Parcelable.Creator<Problem> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Problem createFromParcel(Parcel source) {
            return new Problem(source);
        }
        
        @Override
        public Problem[] newArray(int size) {
            return new Problem[size];
        }
    };
    @NonNull
    private String source;
    @Nullable
    private String filePath;
    @NonNull
    private Level level;
    @NonNull
    private String message;
    @NonNull
    private String code;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    
    public Problem(@NonNull String source, @NonNull Level level, @NonNull String message, @NonNull String code) {
        this.source = source;
        this.filePath = null;
        this.level = level;
        this.message = message;
        this.code = code;
        this.startLine = -1;
        this.startColumn = -1;
        this.endLine = -1;
        this.endColumn = -1;
    }
    
    public Problem(@NonNull String filePath, @NonNull Level level, @NonNull String message, @NonNull String code, int startLine, int startColumn, int endLine, int endColumn) {
        this.source = filePath;
        this.filePath = filePath;
        this.level = level;
        this.message = message;
        this.code = code;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }
    
    protected Problem(Parcel in) {
        this.source = Objects.requireNonNull(in.readString());
        this.filePath = in.readString();
        this.level = Level.values()[in.readInt()];
        this.message = Objects.requireNonNull(in.readString());
        this.code = Objects.requireNonNull(in.readString());
        this.startLine = in.readInt();
        this.startColumn = in.readInt();
        this.endLine = in.readInt();
        this.endColumn = in.readInt();
    }
    
    @NonNull
    public String getSource() {
        return source;
    }
    
    @Nullable
    public String getFilePath() {
        return filePath;
    }
    
    public boolean isFile() {
        return filePath != null;
    }
    
    @NonNull
    public String getMessage() {
        return message;
    }
    
    @NonNull
    public String getCode() {
        return code;
    }
    
    @NonNull
    public Level getLevel() {
        return level;
    }
    
    public boolean isPosition() {
        return startLine != -1 && startColumn != -1
                && endLine != -1 && endColumn != -1;
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
    public int hashCode() {
        return Objects.hash(source, filePath, level, message, code, startLine, startColumn, endLine, endColumn);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return startLine == problem.startLine && startColumn == problem.startColumn && endLine == problem.endLine && endColumn == problem.endColumn && source.equals(problem.source) && Objects.equals(filePath, problem.filePath) && level == problem.level && message.equals(problem.message) && code.equals(problem.code);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.source);
        dest.writeString(this.filePath);
        dest.writeInt(this.level.ordinal());
        dest.writeString(this.message);
        dest.writeString(this.code);
        dest.writeInt(this.startLine);
        dest.writeInt(this.startColumn);
        dest.writeInt(this.endLine);
        dest.writeInt(this.endColumn);
    }
    
    public void readFromParcel(Parcel source) {
        this.source = Objects.requireNonNull(source.readString());
        this.filePath = source.readString();
        this.level = Level.values()[source.readInt()];
        this.message = Objects.requireNonNull(source.readString());
        this.code = Objects.requireNonNull(source.readString());
        this.startLine = source.readInt();
        this.startColumn = source.readInt();
        this.endLine = source.readInt();
        this.endColumn = source.readInt();
    }
    
    public enum Level {
        Warning,
        Error
    }
}
