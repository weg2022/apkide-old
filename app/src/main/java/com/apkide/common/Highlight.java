package com.apkide.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Highlight implements Comparable<Highlight>, Parcelable {
    public int id;// registered ColorScheme attribute id
    public int flags;// warning error...
    public int start;
    public int end;

    public Highlight(int id, int start, int end) {
       this(id,0,start, end);
    }

    public Highlight(int id, int flagId, int start, int end) {
        this.id = id;
        this.flags = flagId;
        this.start = start;
        this.end = end;
    }


    @Override
    public int compareTo(Highlight o) {
        if (start==o.start){
            return Integer.compare(end,o.end);
        }
        return Integer.compare(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Highlight highlight = (Highlight) o;
        return id == highlight.id && flags == highlight.flags && start == highlight.start && end == highlight.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, flags, start, end);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.flags);
        dest.writeInt(this.start);
        dest.writeInt(this.end);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.flags = source.readInt();
        this.start = source.readInt();
        this.end = source.readInt();
    }

    protected Highlight(Parcel in) {
        this.id = in.readInt();
        this.flags = in.readInt();
        this.start = in.readInt();
        this.end = in.readInt();
    }

    public static final Parcelable.Creator<Highlight> CREATOR = new Parcelable.Creator<Highlight>() {
        @Override
        public Highlight createFromParcel(Parcel source) {
            return new Highlight(source);
        }

        @Override
        public Highlight[] newArray(int size) {
            return new Highlight[size];
        }
    };
}
