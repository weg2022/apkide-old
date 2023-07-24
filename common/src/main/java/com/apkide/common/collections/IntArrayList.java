package com.apkide.common.collections;

import static java.lang.System.arraycopy;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class IntArrayList implements Cloneable {
	private int[] data;
	private int size;
	
	public IntArrayList(int initialCapacity) {
		data = new int[initialCapacity];
	}
	
	public IntArrayList() {
		this(10);
	}
	
	public void trimToSize() {
		int oldCapacity = data.length;
		if (size < oldCapacity){
			int[] oldData = data;
			data = new int[size];
			arraycopy(oldData, 0, data, 0, size);
		}
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity){
			int[] oldData = data;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity){
				newCapacity = minCapacity;
			}
			data = new int[newCapacity];
			arraycopy(oldData, 0, data, 0, size);
		}
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean contains(int elem) {
		return indexOf(elem) >= 0;
	}
	
	public int indexOf(int elem) {
		for(int i = 0; i < size; i++){
			if (elem == data[i]) return i;
		}
		return -1;
	}
	
	public int lastIndexOf(int elem) {
		for(int i = size - 1; i >= 0; i--){
			if (elem == data[i]) return i;
		}
		return -1;
	}
	
	@NonNull
	public IntArrayList clone() {
		try{
			IntArrayList v = (IntArrayList)super.clone();
			v.data = new int[size];
			arraycopy(data, 0, v.data, 0, size);
			return v;
		} catch(CloneNotSupportedException e){
			throw new InternalError();
		}
	}
	
	@NonNull
	public int[] toArray() {
		int[] result = new int[size];
		arraycopy(data, 0, result, 0, size);
		return result;
	}
	
	@NonNull
	public int[] toArray(@NonNull int[] a) {
		if (a.length < size)
			a = new int[size];
		arraycopy(data, 0, a, 0, size);
		return a;
	}
	
	public int get(int index) {
		checkRange(index);
		return data[index];
	}
	
	public int set(int index, int element) {
		checkRange(index);
		
		int oldValue = data[index];
		data[index] = element;
		return oldValue;
	}
	
	public void add(int o) {
		ensureCapacity(size + 1);
		data[size++] = o;
	}
	
	public void add(int index, int element) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		
		ensureCapacity(size + 1);
		arraycopy(data, index, data, index + 1, size - index);
		data[index] = element;
		size++;
	}
	
	public int remove(int index) {
		checkRange(index);
		
		int oldValue = data[index];
		int numMoved = size - index - 1;
		if (numMoved > 0)
			arraycopy(data, index + 1, data, index,numMoved);
		
		size--;
		return oldValue;
	}
	
	public void clear() {
		size = 0;
	}
	
	protected void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		arraycopy(data, toIndex, data, fromIndex, numMoved);
		size -= (toIndex - fromIndex);
	}
	
	private void checkRange(int index) {
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}
	
	@NonNull
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
}
