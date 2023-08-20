package com.apkide.common.collection;


import static java.lang.System.arraycopy;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;

public class EventListenerList implements Serializable {
	
	private static final long serialVersionUID = 7854660335615510019L;
	private final static Object[] NULL_ARRAY = new Object[0];
	protected transient Object[] listenerList = NULL_ARRAY;
	
	@NonNull
	public Object[] getListenerList() {
		return listenerList;
	}
	
	@NonNull
	public <T extends EventListener> T[] getListeners(@NonNull Class<T> type) {
		Object[] list = listenerList;
		int n = getListenerCount(list, type);
		T[] result = (T[]) Array.newInstance(type, n);
		int j = 0;
		for (int i = list.length - 2; i >= 0; i -= 2) {
			if (list[i] == type) {
				result[j++] = (T) list[i + 1];
			}
		}
		return result;
	}
	
	private int getListenerCount(@NonNull Object[] list, @NonNull Class<?> type) {
		int count = 0;
		for (int i = 0; i < list.length; i += 2) {
			if (type == list[i])
				count++;
		}
		return count;
	}
	
	public int getListenerCount() {
		return listenerList.length / 2;
	}
	
	public int getListenerCount(@NonNull Class<?> type) {
		Object[] lList = listenerList;
		return getListenerCount(lList, type);
	}
	
	public synchronized <T extends EventListener> void remove(@NonNull Class<T> type, T listener) {
		if (listener == null) return;
		
		if (!type.isInstance(listener))
			throw new IllegalArgumentException("Listener " + listener + " is not of type " + type);
		
		int index = -1;
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if ((listenerList[i] == type) && (listenerList[i + 1].equals(listener))) {
				index = i;
				break;
			}
		}
		
		if (index != -1) {
			Object[] tmp = new Object[listenerList.length - 2];
			arraycopy(listenerList, 0, tmp, 0, index);
			if (index < tmp.length)
				arraycopy(listenerList, index + 2, tmp, index,
						tmp.length - index);
			listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
		}
	}
	
	private void writeObject(@NonNull ObjectOutputStream stream) throws IOException {
		Object[] lList = listenerList;
		stream.defaultWriteObject();
		
		for (int i = 0; i < lList.length; i += 2) {
			Class<?> t = (Class<?>) lList[i];
			EventListener l = (EventListener) lList[i + 1];
			if ((l instanceof Serializable)) {
				stream.writeObject(t.getName());
				stream.writeObject(l);
			}
		}
		
		stream.writeObject(null);
	}
	
	private void readObject(@NonNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		listenerList = NULL_ARRAY;
		stream.defaultReadObject();
		Object listenerTypeOrNull;
		
		while (null != (listenerTypeOrNull = stream.readObject())) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			EventListener l = (EventListener) stream.readObject();
			String name = (String) listenerTypeOrNull;
			add((Class<EventListener>) Class.forName(name, true, cl), l);
		}
	}
	
	public synchronized <T extends EventListener> void add(@NonNull Class<T> type, T listener) {
		if (listener == null) return;
		
		if (!type.isInstance(listener))
			throw new IllegalArgumentException("Listener " + listener + " is not of type " + type);
		
		if (listenerList == NULL_ARRAY) {
			listenerList = new Object[]{type, listener};
		} else {
			int i = listenerList.length;
			Object[] tmp = new Object[i + 2];
			arraycopy(listenerList, 0, tmp, 0, i);
			
			tmp[i] = type;
			tmp[i + 1] = listener;
			
			listenerList = tmp;
		}
	}
}
