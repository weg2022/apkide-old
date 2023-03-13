package com.apkide.common;

import static java.lang.System.arraycopy;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class SafeListenerList<E> implements Iterable<E> {
	
	private static final Object[] EmptyArray = new Object[0];
	
	private volatile Object[] listeners = EmptyArray;
	
	public synchronized void add(E listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		
		final int oldSize = listeners.length;
		for (Object listener2 : listeners) {
			if (listener.equals(listener2))
				return;
		}
		Object[] newListeners = new Object[oldSize + 1];
		arraycopy(listeners, 0, newListeners, 0, oldSize);
		newListeners[oldSize] = listener;
		this.listeners = newListeners;
	}
	
	@NonNull
	public Object[] getListeners() {
		return listeners;
	}
	
	@NonNull
	@Override
	public Iterator<E> iterator() {
		return new ListenerListIterator<>(listeners);
	}
	
	public boolean isEmpty() {
		return listeners.length == 0;
	}
	
	public synchronized void remove(Object listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		int oldSize = listeners.length;
		for (int i = 0; i < oldSize; ++i) {
			Object listener2 = listeners[i];
			if (listener.equals(listener2)) {
				if (oldSize == 1) {
					listeners = EmptyArray;
				} else {
					Object[] newListeners = new Object[oldSize - 1];
					arraycopy(listeners, 0, newListeners, 0, i);
					arraycopy(listeners, i + 1, newListeners, i, oldSize - i - 1);
					this.listeners = newListeners;
				}
				return;
			}
		}
	}
	
	public int size() {
		return listeners.length;
	}
	
	public synchronized void clear() {
		listeners = EmptyArray;
	}
	
	private static class ListenerListIterator<E> implements Iterator<E> {
		private final Object[] listeners;
		private int i;
		
		public ListenerListIterator(Object[] listeners) {
			this.listeners = listeners;
		}
		
		@Override
		public boolean hasNext() {
			return i < listeners.length;
		}
		
		@Override
		public E next() {
			if (i >= listeners.length)
				throw new NoSuchElementException();
			
			return (E) listeners[i++];
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
