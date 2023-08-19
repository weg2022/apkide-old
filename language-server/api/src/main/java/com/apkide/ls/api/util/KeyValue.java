package com.apkide.ls.api.util;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class KeyValue<K,V> implements Serializable {

	private static final long serialVersionUID = 8388992473343540991L;
	@NonNull
	public K key;
	@NonNull
	public V value;

	public KeyValue(@NonNull K key, @NonNull V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		KeyValue<?, ?> keyValue = (KeyValue<?, ?>) o;

		if (!key.equals(keyValue.key)) return false;
		return value.equals(keyValue.value);
	}

	@Override
	public int hashCode() {
		int result = key.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}
}
