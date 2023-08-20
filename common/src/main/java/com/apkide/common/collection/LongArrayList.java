package com.apkide.common.collection;


import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;

public class LongArrayList implements Serializable {
	private static final long serialVersionUID = -919454589528703039L;
	
	private long[] data;
	private int size;
	
	public LongArrayList() {
		this(8);
	}
	
	public LongArrayList(int capacity) {
		data = new long[capacity];
		size = 0;
	}
	
	public LongArrayList(@NonNull long[] array) {
		data = array;
		size = array.length;
	}
	
	public int size() {
		return this.size;
	}
	
	public long get(int idx) {
		return this.data[idx];
	}
	
	public void set(int idx, long v) {
		assert idx < this.size;
		this.data[idx] = v;
	}
	
	public void add(long v) {
		if (this.data.length == this.size)
			this.data = copyOf(this.data, this.data.length * 2);
		
		this.data[this.size] = v;
		this.size += 1;
	}
	
	public void add(int idx, long v) {
		assert idx <= this.size;
		
		if (this.data.length == this.size) {
			long[] newBuffer = new long[this.size * 5/4];
			arraycopy(this.data, 0, newBuffer, 0, idx);
			arraycopy(this.data, idx, newBuffer, idx + 1, this.size - idx);
			data = newBuffer;
		} else
			arraycopy(this.data, idx, this.data, idx + 1, this.size - idx);
	
		this.data[idx] = v;
		this.size += 1;
	}
	
	public void removeRange(int from, int to) {
		if (to > this.size)
			throw new IndexOutOfBoundsException();
		
		if (to < this.size)
			arraycopy(this.data, to, this.data, from, this.size - to);
		size -= to - from;
	}
	
	public void remove(int idx) {
		removeRange(idx, idx + 1);
	}
	
	@NonNull
	@Override
	public String toString() {
		return Arrays.toString(copyOfRange(this.data, 0, size));
	}
	
	@NonNull
	public LongArrayList copy(int capacity) {
		LongArrayList r = new LongArrayList();
		r.data = copyOf(this.data, capacity);
		r.size = this.size;
		return r;
	}
	
	@NonNull
	public LongArrayList copy() {
		return this.copy(this.size);
	}
	
	public int binarySearch(long key) {
		return Arrays.binarySearch(this.data, 0, this.size, key);
	}
	
	public int binarySearch(int idx, long key) {
		return Arrays.binarySearch(this.data, idx, this.size, key);
	}
	
	@NonNull
	public LongArrayList subList(int from, int to) {
		LongArrayList r = new LongArrayList(to - from);
		arraycopy(this.data, from, r.data, 0, to - from);
		r.size = to - from;
		return r;
	}
	
	public int indexOf(long key) {
		for (int i = 0; i < this.size; i++) {
			if (this.data[i] == key)
				return i;
		}
		return -1;
	}
}
