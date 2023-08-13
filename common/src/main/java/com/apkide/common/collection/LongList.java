package com.apkide.common.collection;


import static java.lang.Math.max;
import static java.lang.System.arraycopy;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class LongList implements Storeable, Serializable {


	private static final long serialVersionUID = -4630205134592535477L;
	private static final long[] sEmpty = new long[0];
	public final Iterator ITERATOR = new Iterator();
	private long[] values;
	private int size;

	public LongList() {
		values = sEmpty;
		size = 0;
	}

	public LongList(int capacity) {
		capacity = max(5, capacity);
		values = new long[capacity];
		size = capacity;
	}

	public LongList(@NonNull LongList list) {
		values = list.values;
		size = list.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void clear() {
		size = 0;
	}

	public void push(long value) {
		add(value);
	}

	public long pop() {
		return size == 0 ? -1 : values[size--];
	}

	public long peek() {
		return size == 0 ? -1 : get(size - 1);
	}

	public void addAll(@NonNull LongList list) {
		for (int i = 0; i < list.getSize(); i++) {
			add(list.get(i));
		}
	}

	public void add(long value) {
		resize();
		values[size++] = value;
	}

	public long set(int index, long value) {
		resize();
		if (index >= size)
			size = index + 1;
		long old = values[index];
		values[index] = value;
		return old;
	}

	public void remove(int index) {
		if (index < size) {
			arraycopy(values, index + 1, values, index, size - index - 1);
			size--;
		}
	}

	public long get(int index) {
		if (index >= values.length) return -1;

		return index >= size ? -1 : values[index];
	}

	public boolean contains(long value) {
		return indexOf(value) != -1;
	}


	public boolean isEmpty() {
		return size == 0;
	}

	public int getSize() {
		return size;
	}

	public int indexOf(long value) {
		return indexOf(value, 0);
	}

	public int indexOf(long value, int fromIndex) {
		for (int i = fromIndex; i < size; i++) {
			if (values[i] == value)
				return i;
		}
		return -1;
	}


	public void sort() {
		sort(0, size - 1);
	}

	public void sort(int fromIndex, int toIndex) {
		if (fromIndex < toIndex) {
			int from = fromIndex;
			int to = toIndex;
			int mid = fromIndex + (toIndex - fromIndex) / 2;
			long midValue = values[mid];
			while (fromIndex <= toIndex) {
				while (values[fromIndex] < midValue) {
					fromIndex++;
				}

				while (midValue < values[fromIndex]) {
					toIndex--;
				}

				if (fromIndex <= toIndex) {
					long value = values[toIndex];
					values[toIndex] = values[fromIndex];
					values[fromIndex] = value;
					fromIndex++;
					toIndex--;
				}
			}
			sort(from, toIndex);
			sort(fromIndex, to);
		}
	}

	public void sort(@NonNull Comparator<Long> comparator) {
		sort(0, size - 1, comparator);
	}

	public void sort(int fromIndex, int toIndex, @NonNull Comparator<Long> comparator) {
		if (fromIndex < toIndex) {
			int from = fromIndex;
			int to = toIndex;
			int mid = fromIndex + (toIndex - fromIndex) / 2;
			long midValue = values[mid];
			while (fromIndex <= toIndex) {
				while (comparator.compare(values[fromIndex], midValue) < 0) {
					fromIndex++;
				}

				while (comparator.compare(midValue, values[fromIndex]) < 0) {
					toIndex--;
				}

				if (fromIndex <= toIndex) {
					long value = values[toIndex];
					values[toIndex] = values[fromIndex];
					values[fromIndex] = value;
					fromIndex++;
					toIndex--;
				}
			}
			sort(from, toIndex, comparator);
			sort(fromIndex, to, comparator);
		}
	}

	private void resize() {
		if (size >= values.length) {
			long[] newValues = new long[(size + 1) * 5 / 4];
			arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}

	@NonNull
	public long[] toIntArray() {
		if (size == 0) return sEmpty;

		long[] array = new long[size];
		arraycopy(values, 0, array, 0, size);
		return array;
	}

	@NonNull
	public Iterator iterator() {
		return new Iterator();
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			out.writeLong(values[i]);
		}
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		size = in.readInt();
		if (size > 0) {
			values = new long[size];
			for (int i = 0; i < values.length; i++) {
				values[i] = in.readLong();
			}
		} else {
			values = sEmpty;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LongList longList = (LongList) o;

		if (size != longList.size) return false;
		return Arrays.equals(values, longList.values);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(values);
		result = 31 * result + size;
		return result;
	}

	public class Iterator {
		private int index = 0;
		private long value;

		private Iterator() {
		}

		public void init() {
			index = 0;
		}

		public boolean hasNext() {
			if (index < size) {
				value = get(index);
				index++;
				return true;
			}
			return false;
		}

		public long next() {
			return value;
		}
	}
}
