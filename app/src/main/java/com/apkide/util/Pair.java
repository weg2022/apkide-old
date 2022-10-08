package com.apkide.util;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Pair<U, V> implements Serializable {
	public U first;
	public V second;
	
	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
	}
	
	@NonNull
	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}