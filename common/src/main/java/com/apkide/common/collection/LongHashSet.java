package com.apkide.common.collection;

import java.util.Arrays;

public class LongHashSet {

    protected static final int DEFAULT_CAPACITY = 16;

    final static class Entry {
        final long key;
        Entry next;

        Entry(long key, Entry next) {
            this.key = key;
            this.next = next;
        }
    }

    public static LongHashSet createSynchronized() {
        return new Synchronized(DEFAULT_CAPACITY);
    }

    public static LongHashSet createSynchronized(int capacity) {
        return new Synchronized(capacity);
    }

    private Entry[] table;
    private int capacity;
    private int threshold;
    private volatile int size;
    private volatile float loadFactor = 1.3f;

    public LongHashSet() {
        this(DEFAULT_CAPACITY);
    }

    public LongHashSet(int capacity) {
        this.capacity = capacity;
        this.threshold = (int) (capacity * loadFactor + 0.5f);
        this.table = new Entry[capacity];
    }

    public boolean contains(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;

        for (Entry entry = table[index]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return true;
            }
        }
        return false;
    }

    public boolean add(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        final Entry entryOriginal = table[index];
        for (Entry entry = entryOriginal; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return false;
            }
        }
        table[index] = new Entry(key, entryOriginal);
        size++;
        if (size > threshold) {
            setCapacity(2 * capacity);
        }
        return true;
    }

    public boolean remove(long key) {
        int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        Entry previous = null;
        Entry entry = table[index];
        while (entry != null) {
            Entry next = entry.next;
            if (entry.key == key) {
                if (previous == null) {
                    table[index] = next;
                } else {
                    previous.next = next;
                }
                size--;
                return true;
            }
            previous = entry;
            entry = next;
        }
        return false;
    }

    public long[] keys() {
        long[] values = new long[size];
        int idx = 0;
        for (Entry entry : table) {
            while (entry != null) {
                values[idx++] = entry.key;
                entry = entry.next;
            }
        }
        return values;
    }

    public void clear() {
        size = 0;
        Arrays.fill(table, null);
    }

    public int size() {
        return size;
    }

    public void setCapacity(int newCapacity) {
        Entry[] newTable = new Entry[newCapacity];
        int length = table.length;
        for (int i = 0; i < length; i++) {
            Entry entry = table[i];
            while (entry != null) {
                long key = entry.key;
                int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % newCapacity;

                Entry originalNext = entry.next;
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = originalNext;
            }
        }
        table = newTable;
        capacity = newCapacity;
        threshold = (int) (newCapacity * loadFactor + 0.5f);
    }

    public void setLoadFactor(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    public void reserveRoom(int entryCount) {
        setCapacity((int) (entryCount * loadFactor * 1.3f + 0.5f));
    }

    protected static class Synchronized extends LongHashSet {
        public Synchronized(int capacity) {
            super(capacity);
        }

        @Override
        public synchronized boolean contains(long key) {
            return super.contains(key);
        }

        @Override
        public synchronized boolean add(long key) {
            return super.add(key);
        }

        @Override
        public synchronized boolean remove(long key) {
            return super.remove(key);
        }

        @Override
        public synchronized long[] keys() {
            return super.keys();
        }

        @Override
        public synchronized void clear() {
            super.clear();
        }

        @Override
        public synchronized void setCapacity(int newCapacity) {
            super.setCapacity(newCapacity);
        }

        @Override
        public synchronized void reserveRoom(int entryCount) {
            super.reserveRoom(entryCount);
        }

    }

}
