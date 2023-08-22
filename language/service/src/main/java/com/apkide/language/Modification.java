package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Modification implements Parcelable {
    public static final Parcelable.Creator<Modification> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Modification createFromParcel(Parcel source) {
            return new Modification(source);
        }
        
        @Override
        public Modification[] newArray(int size) {
            return new Modification[size];
        }
    };
    
    public enum Mode {
        CopyText, ReplaceText, MoveText,
        RenameFile, MoveFile, CreateFile, DeleteFile,
    }
    @NonNull
    private Mode mode;
    @NonNull
    private String filePath;
    @Nullable
    private String toFilePath;
    private int startLine, startColumn;
    private int endLine, endColumn;
    private int toLine, toColumn;
    @Nullable
    private String newText;
    
    private Modification(@NonNull Mode mode, @NonNull String filePath, @Nullable String toFilePath, int startLine, int startColumn, int endLine, int endColumn, int toLine, int toColumn, @Nullable String newText) {
        this.mode = mode;
        this.filePath = filePath;
        this.toFilePath = toFilePath;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.toLine = toLine;
        this.toColumn = toColumn;
        this.newText = newText;
    }
    
    protected Modification(Parcel in) {
        this.mode = Mode.values()[in.readInt()];
        this.filePath = Objects.requireNonNull(in.readString());
        this.toFilePath = in.readString();
        this.startLine = in.readInt();
        this.startColumn = in.readInt();
        this.endLine = in.readInt();
        this.endColumn = in.readInt();
        this.toLine = in.readInt();
        this.toColumn = in.readInt();
        this.newText = in.readString();
    }
    
    public static Modification copyText(@NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn, int line, int column) {
        return new Modification(Mode.CopyText, filePath, null, startLine, startColumn, endLine, endColumn, line, column, null);
    }
    
    public static Modification replaceText(@NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn, @NonNull String newText) {
        return new Modification(Mode.ReplaceText, filePath, null, startLine, startColumn, endLine, endColumn, -1, -1, newText);
    }
    
    public static Modification moveText(@NonNull String filePath, int startLine, int startColumn, int endLine, int endColumn, int line, int column) {
        return new Modification(Mode.MoveText, filePath, null, startLine, startColumn, endLine, endColumn, line, column, null);
    }
    
    public static Modification renameFile(@NonNull String filePath, @NonNull String newName) {
        
        return new Modification(Mode.RenameFile, filePath, newName, -1, -1, -1, -1, -1, -1, null);
    }
    
    public static Modification moveFile(@NonNull String filePath, @NonNull String newFilePath) {
        return new Modification(Mode.MoveFile, filePath, newFilePath, -1, -1, -1, -1, -1, -1, null);
    }
    
    public static Modification createFile(@NonNull String filePath) {
        return new Modification(Mode.CreateFile, filePath, null, -1, -1, -1, -1, -1, -1, null);
    }
    
    public static Modification deleteFile(@NonNull String filePath) {
        return new Modification(Mode.DeleteFile, filePath, null, -1, -1, -1, -1, -1, -1, null);
    }
    
    @NonNull
    public Mode getMode() {
        return mode;
    }
    
    @NonNull
    public String getFilePath() {
        return filePath;
    }
    
    @Nullable
    public String getNewFilePath() {
        return toFilePath;
    }
    
    @Nullable
    public String getNewName() {
        return toFilePath;
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
    
    public int getToLine() {
        return toLine;
    }
    
    public int getToColumn() {
        return toColumn;
    }
    
    @Nullable
    public String getNewText() {
        return newText;
    }
    
    public boolean isTextEdit() {
        return mode == Mode.ReplaceText || mode == Mode.MoveText || mode == Mode.CopyText;
    }
    
    public boolean isFileOperation() {
        return mode == Mode.RenameFile || mode == Mode.MoveFile || mode == Mode.CreateFile || mode == Mode.DeleteFile;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mode, filePath, toFilePath, startLine, startColumn, endLine, endColumn, toLine, toColumn, newText);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modification that = (Modification) o;
        return startLine == that.startLine && startColumn == that.startColumn && endLine == that.endLine && endColumn == that.endColumn && toLine == that.toLine && toColumn == that.toColumn && mode == that.mode && filePath.equals(that.filePath) && Objects.equals(toFilePath, that.toFilePath) && Objects.equals(newText, that.newText);
    }
    
    @NonNull
    @Override
    public String toString() {
        switch (mode) {
            case CopyText:
                return "Copying text in " + filePath + " form " + startLine + "," + startColumn + "," + endLine + "," + endColumn + " to " + toLine + "," + toColumn;
            case ReplaceText:
                return "Replace text in " + filePath + " form " + startLine + "," + startColumn + "," + endLine + "," + endColumn + " to " + newText;
            case MoveText:
                return "Moving text in " + filePath + " form " + startLine + "," + startColumn + "," + endLine + "," + endColumn + " to " + toLine + "," + toColumn;
            case RenameFile:
                return "Rename file " + filePath + " to " + filePath;
            case MoveFile:
                return "Moving file " + filePath + " to " + toFilePath;
            case CreateFile:
                return "Creation file " + filePath;
            case DeleteFile:
                return "Deletion file " + filePath;
            default:
                return "Unknown";
        }
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mode.ordinal());
        dest.writeString(this.filePath);
        dest.writeString(this.toFilePath);
        dest.writeInt(this.startLine);
        dest.writeInt(this.startColumn);
        dest.writeInt(this.endLine);
        dest.writeInt(this.endColumn);
        dest.writeInt(this.toLine);
        dest.writeInt(this.toColumn);
        dest.writeString(this.newText);
    }
    
    public void readFromParcel(Parcel source) {
        this.mode = Mode.values()[source.readInt()];
        this.filePath = Objects.requireNonNull(source.readString());
        this.toFilePath = source.readString();
        this.startLine = source.readInt();
        this.startColumn = source.readInt();
        this.endLine = source.readInt();
        this.endColumn = source.readInt();
        this.toLine = source.readInt();
        this.toColumn = source.readInt();
        this.newText = source.readString();
    }

}
