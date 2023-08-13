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

public class IntList implements Storeable, Serializable {


	private static final long serialVersionUID = -7157383907833659314L;
	private static final int[] sEmpty = new int[0];
	public final Iterator ITERATOR = new Iterator();
	private int[] values;
	private int size;

	public IntList() {
		values = sEmpty;
		size = 0;
	}

	public IntList(int capacity) {
		capacity = max(5, capacity);
		values = new int[capacity];
		size = capacity;
	}

	public IntList(@NonNull IntList list) {
		values = list.values;
		size = list.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void clear() {
		size = 0;
	}

	public void push(int value) {
		add(value);
	}

	public int pop() {
		return size == 0 ? -1 : values[size--];
	}

	public int peek() {
		return size == 0 ? -1 : get(size - 1);
	}

	public void addAll(@NonNull IntList list) {
		for (int i = 0; i < list.getSize(); i++) {
			add(list.get(i));
		}
	}

	public void add(int value) {
		resize();
		values[size++] = value;
	}

	public int set(int index, int value) {
		resize();
		if (index >= size)
			size = index + 1;
		int old = values[index];
		values[index] = value;
		return old;
	}

	public void remove(int index) {
		if (index < size) {
			arraycopy(values, index + 1, values, index, size - index - 1);
			size--;
		}
	}

	public int get(int index) {
		if (index >= values.length) return -1;
		return index >= size ? -1 : values[index];
	}

	public boolean contains(int value) {
		return indexOf(value) != -1;
	}


	public boolean isEmpty() {
		return size == 0;
	}

	public int getSize() {
		return size;
	}

	public int indexOf(int value) {
		return indexOf(value, 0);
	}

	public int indexOf(int value, int fromIndex) {
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
			int midValue = values[mid];
			while (fromIndex <= toIndex) {
				while (values[fromIndex] < midValue) {
					fromIndex++;
				}

				while (midValue < values[fromIndex]) {
					toIndex--;
				}

				if (fromIndex <= toIndex) {
					int value = values[toIndex];
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

	public void sort(@NonNull Comparator<Integer> comparator) {
		sort(0, size - 1, comparator);
	}

	public void sort(int fromIndex, int toIndex, @NonNull Comparator<Integer> comparator) {
		if (fromIndex < toIndex) {
			int from = fromIndex;
			int to = toIndex;
			int mid = fromIndex + (toIndex - fromIndex) / 2;
			int midValue = values[mid];
			while (fromIndex <= toIndex) {
				while (comparator.compare(values[fromIndex], midValue) < 0) {
					fromIndex++;
				}

				while (comparator.compare(midValue, values[fromIndex]) < 0) {
					toIndex--;
				}

				if (fromIndex <= toIndex) {
					int value = values[toIndex];
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
			int[] newValues = new int[(size + 1) * 5 / 4];
			arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}

	@NonNull
	public int[] toIntArray() {
		if (size == 0) return sEmpty;

		int[] array = new int[size];
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
			out.writeInt(values[i]);
		}
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		size = in.readInt();
		if (size > 0) {
			values = new int[size];
			for (int i = 0; i < values.length; i++) {
				values[i] = in.readInt();
			}
		} else {
			values = sEmpty;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntList intList = (IntList) o;

		if (size != intList.size) return false;
		return Arrays.equals(values, intList.values);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(values);
		result = 31 * result + size;
		return result;
	}

	public class Iterator {
		private int index = 0;
		private int value;

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

		public int next() {
			return value;
		}
	}
}
