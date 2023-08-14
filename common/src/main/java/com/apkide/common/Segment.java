package com.apkide.common;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.CharacterIterator;

public class Segment implements Cloneable, CharacterIterator, CharSequence, Serializable {
    private static final long serialVersionUID = 7946315141337071656L;

    public char[] array;

    public int offset;

    public int count;

    public boolean copy;

    private int pos;

    public Segment() {
        this(null, 0, 0);
    }

    public Segment(char[] array, int offset, int count) {
        this.array = array;
        this.offset = offset;
        this.count = count;
    }

    @NonNull
    public String toString() {
        if (array != null) {
            return new String(array, offset, count);
        }
        return "";
    }

    @Override
    public char first() {
        pos = offset;
        if (count != 0) {
            return array[pos];
        }
        return DONE;
    }

    @Override
    public char last() {
        pos = offset + count;
        if (count != 0) {
            pos -= 1;
            return array[pos];
        }
        return DONE;
    }

    @Override
    public char current() {
        if (count != 0 && pos < offset + count) {
            return array[pos];
        }
        return DONE;
    }

    @Override
    public char next() {
        pos += 1;
        int end = offset + count;
        if (pos >= end) {
            pos = end;
            return DONE;
        }
        return current();
    }

    @Override
    public char previous() {
        if (pos == offset) {
            return DONE;
        }
        pos -= 1;
        return current();
    }

    @Override
    public char setIndex(int position) {
        int end = offset + count;
        if ((position < offset) || (position > end)) {
            throw new IllegalArgumentException("bad position: " + position);
        }
        pos = position;
        if ((pos != end) && (count != 0)) {
            return array[pos];
        }
        return DONE;
    }

    @Override
    public int getBeginIndex() {
        return offset;
    }

    @Override
    public int getEndIndex() {
        return offset + count;
    }

    @Override
    public int getIndex() {
        return pos;
    }

    @Override
    public char charAt(int index) {
        if (index < 0
                || index >= count) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return array[offset + index];
    }

    @Override
    public int length() {
        return count;
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }
        Segment segment = new Segment();
        segment.array = this.array;
        segment.offset = this.offset + start;
        segment.count = end - start;
        return segment;
    }

    @NonNull
    @Override
    public Object clone() {
        Object o;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException cnse) {
            o = null;
        }
        assert o != null;
        return o;
    }


}
