package com.apkide.common;

import static java.lang.System.arraycopy;

import android.os.Parcel;
import android.os.Parcelable;

public class FileHighlights implements Parcelable {
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
    public SyntaxKind[] kinds=new SyntaxKind[1000];
    public int[] startLines=new int[1000];
    public int[] startColumns=new int[1000];
    public int[] endLines=new int[1000];
    public int[] endColumns=new int[1000];
    public int size=0;

    public FileHighlights() {
    }

    protected FileHighlights(Parcel in) {
        this.size = in.readInt();
        this.kinds = new SyntaxKind[this.size * 5 / 4];
        for (int i = 0; i < size; i++) {
            kinds[i] = SyntaxKind.values()[in.readByte()];
        }
        this.startLines = in.createIntArray();
        this.startColumns = in.createIntArray();
        this.endLines = in.createIntArray();
        this.endColumns = in.createIntArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.size);
        for (SyntaxKind kind : kinds) {
            dest.writeByte((byte) kind.ordinal());
        }
        dest.writeIntArray(this.startLines);
        dest.writeIntArray(this.startColumns);
        dest.writeIntArray(this.endLines);
        dest.writeIntArray(this.endColumns);
    }

    public void readFromParcel(Parcel source) {
        this.size = source.readInt();
        this.kinds = new SyntaxKind[this.size * 5 / 4];
        for (int i = 0; i < size; i++) {
            kinds[i] = SyntaxKind.values()[source.readByte()];
        }
        this.startLines = source.createIntArray();
        this.startColumns = source.createIntArray();
        this.endLines = source.createIntArray();
        this.endColumns = source.createIntArray();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////

    public void clear() {
        size = 0;
    }

    public void highlight(SyntaxKind kind, int startLine, int startColumn, int endLine, int endColumn) {
        resize();
        kinds[size] = kind;
        startLines[size] = startLine;
        startColumns[size] = startColumn;
        endLines[size] = endLine;
        endColumns[size] = endColumn;
        size++;
    }

    private void resize() {
        if (kinds.length <= size) {
            int newSize = size * 5 / 4;
            SyntaxKind[] temp = new SyntaxKind[newSize];
            arraycopy(kinds, 0, temp, 0, kinds.length);
            kinds = temp;

            int[] temp2 = new int[newSize];
            arraycopy(startLines, 0, temp2, 0, startLines.length);
            startLines = temp2;

            int[] temp3 = new int[newSize];
            arraycopy(startColumns, 0, temp3, 0, startColumns.length);
            startColumns = temp3;


            int[] temp4 = new int[newSize];
            arraycopy(endLines, 0, temp4, 0, endLines.length);
            endLines = temp4;

            int[] temp5 = new int[newSize];
            arraycopy(endColumns, 0, temp5, 0, endColumns.length);
            endColumns = temp5;
        }
    }
}
