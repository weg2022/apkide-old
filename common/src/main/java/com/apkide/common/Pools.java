package com.apkide.common;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Pools {

	private Pools() {
	}

	public interface Pool<T> {

		@Nullable
		T acquire();

		boolean release(@NonNull T instance);
	}

	public static class SimplePool<T> implements Pool<T> {
		private final Object[] mPool;

		private int mPoolSize;

		public SimplePool(int maxPoolSize) {
			if (maxPoolSize <= 0)
				throw new IllegalArgumentException("The max pool size must be > 0");

			mPool = new Object[maxPoolSize];
		}

		@Nullable
		@Override
		@SuppressWarnings("unchecked")
		public T acquire() {
			if (mPoolSize > 0) {
				final int lastPooledIndex = mPoolSize - 1;
				T instance = (T) mPool[lastPooledIndex];
				mPool[lastPooledIndex] = null;
				mPoolSize--;
				return instance;
			}
			return null;
		}

		@Override
		public boolean release(@NonNull T instance) {
			if (isInPool(instance))
				throw new IllegalStateException("Already in the pool!");

			if (mPoolSize < mPool.length) {
				mPool[mPoolSize] = instance;
				mPoolSize++;
				return true;
			}
			return false;
		}

		private boolean isInPool(T instance) {
			for (int i = 0; i < mPoolSize; i++) {
				if (mPool[i] == instance)
					return true;
			}
			return false;
		}
	}

	public static class SynchronizedPool<T> extends SimplePool<T> {
		private final Object mLock;

		public SynchronizedPool(int maxPoolSize, Object lock) {
			super(maxPoolSize);
			mLock = lock;
		}

		public SynchronizedPool(int maxPoolSize) {
			this(maxPoolSize, new Object());
		}

		@Nullable
		@Override
		public T acquire() {
			synchronized (mLock) {
				return super.acquire();
			}
		}

		@Override
		public boolean release(@NonNull T element) {
			synchronized (mLock) {
				return super.release(element);
			}
		}
	}
}

