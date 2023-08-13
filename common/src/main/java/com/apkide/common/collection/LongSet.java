package com.apkide.common.collection;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class LongSet implements Storeable, Serializable {

	private static final long serialVersionUID = -3439606139293584889L;
	private static final int[] SIZES = new int[]{5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053,
			4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319,
			8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827,
			2147383649
	};

	public final Iterator ITERATOR = new Iterator();
	private long[] keys;
	private long[] oldKeys;
	private int slots;
	private int count;
	private int sizeExp;

	public LongSet(int size) {
		sizeExp = 1;

		while (SIZES[sizeExp] < size * 2) {
			sizeExp++;
		}

		keys = new long[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public LongSet() {
		sizeExp = 1;
		keys = new long[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		int size = in.readInt();
		sizeExp = in.readInt();
		keys = new long[SIZES[sizeExp]];
		slots = 0;
		count = 0;

		for (int i = 0; i < size; i++) {
			put(in.readLong());
		}
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(count);
		out.writeInt(sizeExp);

		for (long key : keys) {
			if (key != 0 && key != Long.MIN_VALUE)
				out.writeLong(key);
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

	public void put(@NonNull LongSet set) {
		for (int i = 0; i < set.keys.length; i++) {
			long key = set.keys[i];
			if (key == Long.MAX_VALUE)
				put(0);
			else if (key != 0 && key != Long.MIN_VALUE)
				put(key);
		}
	}

	public void put(@NonNull LongList list) {
		for (int i = 0; i < list.getSize(); i++) {
			put(list.get(i));
		}
	}

	public void put(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		long curKey = keys[index];

		int emptyIndex;
		for (emptyIndex = -1; curKey != 0; curKey = keys[index]) {
			if (curKey == key) return;

			if (curKey == Long.MIN_VALUE)
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

	public void remove(@NonNull LongSet keys) {
		keys.ITERATOR.init();

		while (keys.ITERATOR.hasNext()) {
			remove(keys.ITERATOR.nextKey());
		}
	}

	public void remove(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);

		for (long curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return;

			index = (index + step) % keys.length;
		}

		keys[index] = Long.MIN_VALUE;
		count--;
	}

	public long get() {
		int index = 0;

		while (index < keys.length) {
			long key = keys[index];
			index++;
			if (key != 0 && key != Long.MIN_VALUE) {
				if (key == Long.MAX_VALUE)
					key = 0;

				return key;
			}
		}

		return -1;
	}

	public boolean contains(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		int index = (int) ((key & Long.MAX_VALUE) % keys.length);

		for (long curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	private void rehash() {
		long[] newKeys;
		if (count * 2 > keys.length) {
			sizeExp++;
			oldKeys = null;
			newKeys = new long[SIZES[sizeExp]];
		} else if (oldKeys != null && oldKeys.length == keys.length) {
			newKeys = oldKeys;
			Arrays.fill(newKeys, 0);
			oldKeys = keys;
		} else {
			oldKeys = keys;
			newKeys = new long[SIZES[sizeExp]];
		}

		int newSlots = 0;
		for (long key : keys) {
			if (key != 0 && key != Long.MIN_VALUE) {
				int step = (int) ((key & Long.MAX_VALUE) % (newKeys.length - 2) + 1);
				int index = (int) ((key & Long.MAX_VALUE) % newKeys.length);

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

		LongSet intSet = (LongSet) o;

		if (slots != intSet.slots) return false;
		if (count != intSet.count) return false;
		if (sizeExp != intSet.sizeExp) return false;
		if (!Arrays.equals(keys, intSet.keys)) return false;
		return Arrays.equals(oldKeys, intSet.oldKeys);
	}

	public class Iterator {
		private int index = 0;

		private Iterator() {
		}

		public void init() {
			index = 0;
		}

		public boolean hasNext() {
			return index < keys.length;
		}

		public long nextKey() {
			long key = keys[index];
			index++;
			if (key != 0 && key != Long.MIN_VALUE) {
				if (key == Long.MAX_VALUE)
					key = 0;
			}
			return key;
		}
	}
}
