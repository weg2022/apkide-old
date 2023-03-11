package com.apkide.common.collection;

import static java.lang.System.*;

import java.util.EmptyStackException;

public class IntStack {
	private int[] data;
	private int size;
	public IntStack(int initialCapacity) {
		data = new int[initialCapacity];
		size = 0;
	}
	
	public IntStack() {
		this(5);
	}
	
	public void push(int t) {
		if (size >= data.length) {
			int[] newData = new int[data.length * 3 / 2];
			arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		data[size++] = t;
	}
	
	public int peek() {
		if (size == 0) throw new EmptyStackException();
		return data[size - 1];
	}
	
	public int pop() {
		if (size == 0) throw new EmptyStackException();
		return data[--size];
	}
	
	public boolean empty() {
		return size == 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof IntStack) {
			IntStack otherStack = (IntStack)o;
			if (size != otherStack.size) return false;
			for (int i = 0; i < otherStack.size; i++) {
				if (data[i] != otherStack.data[i]) return false;
			}
			return true;
		}
		
		return false;
	}
	
	public void clear() {
		size = 0;
	}
}
