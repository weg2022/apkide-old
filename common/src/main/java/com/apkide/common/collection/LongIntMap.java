package com.apkide.common.collection;

import static java.util.Arrays.fill;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class LongIntMap implements Storeable, Serializable {


	private static final long serialVersionUID = -3746064746121528092L;
	private static final int[] SIZES = new int[]{5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053,
			4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319,
			8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827,
			2147383649
	};

	public final Iterator ITERATOR=new Iterator();
	private long[] keys;
	private long[] oldKeys;
	private int[] values;
	private int[] oldValues;
	private int slots;
	private int count;
	private int sizeExp;

	public LongIntMap() {
		sizeExp = 0;
		keys = new long[SIZES[sizeExp]];
		values = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public LongIntMap(int capacity){
		sizeExp=0;
		while (SIZES[sizeExp] < capacity *2){
			sizeExp++;
		}
		keys = new long[SIZES[sizeExp]];
		values = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public void clear() {
		if (slots > 0) {
			fill(keys, 0);
			slots = 0;
			count = 0;
		}
	}

	public void clear(int size) {
		if (slots > 0) {
			if (keys.length < size) {
				fill(keys, 0);
			} else {
				sizeExp=0;
				while(SIZES[sizeExp] < size * 2) {
					sizeExp++;
				}

				keys = new long[SIZES[sizeExp]];
				values = new int[SIZES[sizeExp]];
			}

			slots = 0;
			count = 0;
		}
	}

	public void insert(@NonNull LongIntMap map) {
		for(int i = 0; i < map.keys.length; i++) {
			long key = map.keys[i];
			int value = map.values[i];
			if (key == Long.MAX_VALUE)
				insert(0, value);
			else if (key != 0 && key != Long.MIN_VALUE)
				insert(key, value);
		}
	}

	public void put(long key, int value) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		boolean inserted = false;
		int emptyIndex = -1;

		for(long curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {

				if (values[index] != value) {
					keys[index] = Long.MIN_VALUE;
					count--;
				}else
					inserted = true;

			} else if (curKey == Long.MIN_VALUE)
				emptyIndex = index;

			index = (index + step) % keys.length;
		}

		if (!inserted) {
			if (emptyIndex != -1)
				index = emptyIndex;

			keys[index] = key;
			values[index] = value;
			count++;
			if (emptyIndex == -1)
				slots++;

			if (slots * 2 > keys.length)
				rehash();
		}
	}

	public void insert(long key, int value) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		boolean inserted = false;
		int emptyIndex = -1;

		for(long curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {
				if (values[index] == value) return;

			} else if (curKey == Long.MIN_VALUE)
				emptyIndex = index;

			index = (index + step) % keys.length;
		}

		if (!inserted) {
			if (emptyIndex != -1)
				index = emptyIndex;

			keys[index] = key;
			values[index] = value;
			count++;
			if (emptyIndex == -1)
				slots++;

			if (slots * 2 > keys.length)
				rehash();
		}
	}

	public boolean contains(long key, int value) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);

		for(long curKey = keys[index]; curKey != key ||
				values[index] != value; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public void remove(long key, int value) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);

		for(long curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key && values[index] == value) {
				keys[index] = Long.MIN_VALUE;
				count--;
			}

			index = (index + step) % keys.length;
		}
	}

	public void remove(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int index = (int) ((key & Long.MAX_VALUE) % keys.length);
		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);

		for(long curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {
				keys[index] = Long.MIN_VALUE;
				count--;
			}

			index = (index + step) %keys.length;
		}
	}

	public boolean contains(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		int index = (int) ((key & Long.MAX_VALUE) % keys.length);

		for(long curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public int count(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int count = 0;
		int step = (int) ((key & Long.MAX_VALUE) % (this.keys.length - 2) + 1);
		int index = (int) ((key & Long.MAX_VALUE) % this.keys.length);

		for(long curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) count++;

			index = (index + step) % this.keys.length;
		}

		return count;
	}

	public int get(long key) {
		if (key == 0) key = Long.MAX_VALUE;

		int step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
		int index = (int) ((key & Long.MAX_VALUE) % keys.length);

		for(long curKey =keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return -1;

			index = (index + step) % keys.length;
		}

		return values[index];
	}

	private void rehash() {
		long[] newKeys;
		int[] newValues;
		if (count * 2 > keys.length) {
			sizeExp++;
			oldKeys = null;
			oldValues = null;
			newKeys = new long[SIZES[sizeExp]];
			newValues = new int[SIZES[sizeExp]];
		} else if (oldKeys != null && oldKeys.length == keys.length) {
			newKeys = oldKeys;

			fill(newKeys, 0);

			newValues = oldValues;
			oldKeys = keys;
			oldValues = values;
		} else {
			oldKeys = keys;
			oldValues = values;
			newKeys = new long[SIZES[sizeExp]];
			newValues = new int[SIZES[sizeExp]];
		}

		int newSlots = 0;

		for(int i = 0; i < keys.length; i++) {
			long key = keys[i];
			if (key != 0 && key != Long.MIN_VALUE) {
				int step = (int) ((key & Long.MAX_VALUE) % (newKeys.length - 2) + 1);
				int index = (int) ((key & Long.MAX_VALUE) % newKeys.length);

				while(newKeys[index] != 0) {
					index = (index + step) % newKeys.length;
				}

				newKeys[index] = key;
				newValues[index] = values[i];
				newSlots++;
			}
		}

		keys = newKeys;
		values = newValues;
		slots = newSlots;
	}

	public int getSize() {
		return count;
	}

	@NonNull
	public Iterator iterator(){
		return new Iterator();
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(count);
		out.writeInt(sizeExp);

		for(int i = 0; i < keys.length; ++i) {
			long key = keys[i];
			if (key != 0 && key != Long.MIN_VALUE) {
				out.writeLong(keys[i]);
				out.writeInt(values[i]);
			}
		}
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		int size = in.readInt();
		sizeExp = in.readInt();
		keys = new long[SIZES[sizeExp]];
		values = new int[SIZES[sizeExp]];
		slots = 0;
		count = 0;

		for(int i = 0; i < size; i++) {
			insert(in.readLong(), in.readInt());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LongIntMap intIntMap = (LongIntMap) o;

		if (slots != intIntMap.slots) return false;
		if (count != intIntMap.count) return false;
		if (sizeExp != intIntMap.sizeExp) return false;
		if (!Arrays.equals(keys, intIntMap.keys)) return false;
		if (!Arrays.equals(oldKeys, intIntMap.oldKeys)) return false;
		if (!Arrays.equals(values, intIntMap.values)) return false;
		return Arrays.equals(oldValues, intIntMap.oldValues);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(keys);
		result = 31 * result + Arrays.hashCode(oldKeys);
		result = 31 * result + Arrays.hashCode(values);
		result = 31 * result + Arrays.hashCode(oldValues);
		result = 31 * result + slots;
		result = 31 * result + count;
		result = 31 * result + sizeExp;
		return result;
	}

	public class Iterator {
		private int index = 0;
		private int step = 0;
		private long key;
		private int value;
		private long theKey;

		private Iterator() {
		}

		public void init() {
			index = 0;
			step = 0;
		}

		public void init(long key) {
			if (key == 0) key = Long.MAX_VALUE;

			theKey = key;
			step = (int) ((key & Long.MAX_VALUE) % (keys.length - 2) + 1);
			index = (int) ((key & Long.MAX_VALUE) % keys.length);
		}

		public boolean hasNext() {
			if (step == 0) {
				while(index < keys.length) {
					key = keys[index];
					if (key != 0 && key != Long.MIN_VALUE) {
						if (key == Long.MAX_VALUE)
							key = 0;

						value = values[index];
						index++;
						return true;
					}

					index++;
				}
				return false;
			} else {
				while(true) {
					key = keys[index];
					if (key == 0) return false;

					if (key == theKey) {
						if (key == Long.MAX_VALUE)
							key = 0;

						value = values[index];
						index = (index + step) % keys.length;
						return true;
					}

					index = (index + step) % keys.length;
				}
			}
		}

		public long nextKey() {
			return key;
		}

		public int nextValue() {
			return value;
		}
	}
}
