package com.apkide.common.collection;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.util.Arrays;

public class IntSet implements Storeable {
	private static final int[] SIZES = new int[]{5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053,
			4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319,
			8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827,
			2147383649
	};

	public final Iterator ITERATOR = new Iterator();
	private int[] keys;
	private int[] oldKeys;
	private int slots;
	private int count;
	private int sizeExp;

	public IntSet(int size) {
		sizeExp = 1;

		while (SIZES[sizeExp] < size * 2) {
			sizeExp++;
		}

		keys = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public IntSet() {
		sizeExp = 1;
		keys = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		int size = in.readInt();
		sizeExp = in.readInt();
		keys = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;

		for (int i = 0; i < size; i++) {
			put(in.readInt());
		}
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(count);
		out.writeInt(sizeExp);

		for (int key : keys) {
			if (key != 0 && key != Integer.MIN_VALUE)
				out.writeInt(key);
		}
	}

	@NonNull
	public Iterator iterator() {
		return new Iterator();
	}

	public void clear() {
		if (slots > 0) {
			Arrays.fill(keys, 0);
			slots = 0;
			count = 0;
		}
	}

	public void put(@NonNull IntSet set) {
		for (int i = 0; i < set.keys.length; i++) {
			int key = set.keys[i];
			if (key == Integer.MAX_VALUE)
				put(0);
			else if (key != 0 && key != Integer.MIN_VALUE)
				put(key);
		}
	}

	public void put(@NonNull IntList list) {
		for (int i = 0; i < list.getSize(); i++) {
			put(list.get(i));
		}
	}

	public void put(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		int curKey = keys[index];

		int emptyIndex;
		for (emptyIndex = -1; curKey != 0; curKey = keys[index]) {
			if (curKey == key) return;

			if (curKey == Integer.MIN_VALUE)
				emptyIndex = index;

			index = (index + step) % keys.length;
		}

		if (emptyIndex != -1)
			index = emptyIndex;

		keys[index] = key;
		if (emptyIndex == -1)
			slots++;


		count++;
		if (slots * 2 > keys.length)
			rehash();
	}

	public void remove(@NonNull IntSet keys) {
		keys.ITERATOR.init();

		while (keys.ITERATOR.hasNext()) {
			remove(keys.ITERATOR.nextKey());
		}
	}

	public void remove(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;

		for (int curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return;

			index = (index + step) % keys.length;
		}

		keys[index] = Integer.MIN_VALUE;
		count--;
	}

	public int get() {
		int index = 0;

		while (index < keys.length) {
			int key = keys[index];
			index++;
			if (key != 0 && key != Integer.MIN_VALUE) {
				if (key == Integer.MAX_VALUE)
					key = 0;

				return key;
			}
		}

		return -1;
	}

	public boolean contains(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		int index = (key & Integer.MAX_VALUE) % keys.length;

		for (int curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	private void rehash() {
		int[] newKeys;
		if (count * 2 > keys.length) {
			sizeExp++;
			oldKeys = null;
			newKeys = new int[SIZES[sizeExp]];
		} else if (oldKeys != null && oldKeys.length == keys.length) {
			newKeys = oldKeys;
			Arrays.fill(newKeys, 0);
			oldKeys = keys;
		} else {
			oldKeys = keys;
			newKeys = new int[SIZES[sizeExp]];
		}

		int newSlots = 0;
		for (int key : keys) {
			if (key != 0 && key != Integer.MIN_VALUE) {
				int step = (key & Integer.MAX_VALUE) % (newKeys.length - 2) + 1;
				int index = (key & Integer.MAX_VALUE) % newKeys.length;

				while (newKeys[index] != 0) {
					index = (index + step) % newKeys.length;
				}

				newKeys[index] = key;
				newSlots++;
			}
		}

		keys = newKeys;
		slots = newSlots;
	}

	public int getSize() {
		return count;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(keys);
		result = 31 * result + Arrays.hashCode(oldKeys);
		result = 31 * result + slots;
		result = 31 * result + count;
		result = 31 * result + sizeExp;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntSet intSet = (IntSet) o;

		if (slots != intSet.slots) return false;
		if (count != intSet.count) return false;
		if (sizeExp != intSet.sizeExp) return false;
		if (!Arrays.equals(keys, intSet.keys)) return false;
		return Arrays.equals(oldKeys, intSet.oldKeys);
	}

	public class Iterator {
		private int index = 0;
		private int key;

		private Iterator() {
		}

		public void init() {
			index = 0;
		}

		public boolean hasNext() {
			while (index < keys.length) {
				key = keys[index];
				index++;
				if (key != 0 && key != Integer.MIN_VALUE) {
					if (key == Integer.MAX_VALUE)
						key = 0;

					return true;
				}
			}

			return false;
		}

		public int nextKey() {
			return key;
		}
	}
}
