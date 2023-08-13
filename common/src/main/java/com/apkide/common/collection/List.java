package com.apkide.common.collection;

import static com.apkide.common.SerializeHelper.deserialize;
import static com.apkide.common.SerializeHelper.serialize;
import static java.lang.Math.max;
import static java.lang.System.arraycopy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class List<E extends Serializable> implements Storeable, Serializable {


	private static final long serialVersionUID = 990174039747444370L;
	private static final Object[] sEmpty = new Object[0];
	public final Iterator ITERATOR = new Iterator();
	private Object[] values;
	private int size;

	public List() {
		values = sEmpty;
		size = 0;
	}

	public List(int capacity) {
		capacity = max(5, capacity);
		values = new Object[capacity];
		size = capacity;
	}

	public List(@NonNull List<E> list) {
		values = list.values;
		size = list.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void clear() {
		size = 0;
	}

	public void push(@Nullable E value) {
		add(value);
	}

	@Nullable
	public E pop() {
		return size == 0 ? null : (E) values[size--];
	}

	@Nullable
	public E peek() {
		return size == 0 ? null : get(size - 1);
	}

	public void addAll(@NonNull List<E> list) {
		for (int i = 0; i < list.getSize(); i++) {
			add(list.get(i));
		}
	}

	public void add(@Nullable E value) {
		resize();
		values[size++] = value;
	}

	@Nullable
	public E set(int index, @Nullable E value) {
		resize();
		if (index >= size)
			size = index + 1;
		Object old = values[index];
		values[index] = value;
		return (E) old;
	}

	public void remove(int index) {
		if (index < size) {
			arraycopy(values, index + 1, values, index, size - index - 1);
			size--;
		}
	}

	@Nullable
	public E get(int index) {
		if (index >= values.length) return null;

		return index >= size ? null : (E) values[index];
	}

	public boolean contains(@NonNull E value) {
		return indexOf(value) != -1;
	}


	public boolean isEmpty() {
		return size == 0;
	}

	public int getSize() {
		return size;
	}

	public int indexOf(@NonNull E value) {
		return indexOf(value, 0);
	}

	public int indexOf(@NonNull E value, int fromIndex) {
		for (int i = fromIndex; i < size; i++) {
			if (values[i] == value)
				return i;
		}
		return -1;
	}


	public void sort(@NonNull Comparator<E> comparator) {
		sort(0, size - 1, comparator);
	}

	public void sort(int fromIndex, int toIndex, @NonNull Comparator<E> comparator) {
		if (fromIndex < toIndex) {
			int from = fromIndex;
			int to = toIndex;
			int mid = fromIndex + (toIndex - fromIndex) / 2;
			E midValue = (E) values[mid];
			while (fromIndex <= toIndex) {
				while (comparator.compare((E) values[fromIndex], midValue) < 0) {
					fromIndex++;
				}

				while (comparator.compare(midValue, (E) values[fromIndex]) < 0) {
					toIndex--;
				}

				if (fromIndex <= toIndex) {
					Object value = values[toIndex];
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
			Object[] newValues = new Object[(size + 1) * 5 / 4];
			arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}

	@NonNull
	public E[] toIntArray() {
		if (size == 0) return (E[]) sEmpty;

		Object[] array = new Object[size];
		arraycopy(values, 0, array, 0, size);
		return (E[]) array;
	}

	@NonNull
	public Iterator iterator() {
		return new Iterator();
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			byte[] serialize = serialize(values[i]);
			out.writeInt(serialize == null ? 0 : serialize.length);
			if (serialize != null)
				out.write(serialize);
		}
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		size = in.readInt();
		if (size > 0) {
			values = new Object[size];
			for (int i = 0; i < values.length; i++) {
				int length = in.readInt();
				byte[] bytes = new byte[length];
				if (length != 0) {
					in.read(bytes);
					values[i] = deserialize(bytes, null);
				} else
					values[i] = null;
			}
		} else {
			values = sEmpty;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		List<E> intList = (List<E>) o;

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
		private E value;

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

		public E next() {
			return value;
		}
	}
}
