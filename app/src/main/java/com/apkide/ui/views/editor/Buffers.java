package com.apkide.ui.views.editor;

class Buffers {
	 static char[] chars(int len) {
		char[] buf;

		synchronized (Buffers.class) {
			buf = sChars;
			sChars = null;
		}

		if (buf == null || buf.length < len)
			buf = new char[len];
		return buf;
	}

	 static float[] floats(int len) {
		float[] buf;

		synchronized (Buffers.class) {
			buf = sFloats;
			sFloats = null;
		}

		if (buf == null || buf.length < len)
			buf = new float[len];
		return buf;
	}

	public static int[] ints(int len) {
		int[] buf;

		synchronized (Buffers.class) {
			buf = sInts;
			sInts = null;
		}

		if (buf == null || buf.length < len)
			buf = new int[len];
		return buf;
	}

	 static void recycle(int[] temp) {
		if (temp.length > 8192) return;

		synchronized (Buffers.class) {
			sInts = temp;
		}
	}

	 static void recycle(float[] temp) {
		if (temp.length > 8192) return;

		synchronized (Buffers.class) {
			sFloats = temp;
		}
	}


	 static void recycle(char[] temp) {
		if (temp.length > 8192) return;

		synchronized (Buffers.class) {
			sChars = temp;
		}
	}

	private static int[] sInts = null;
	private static char[] sChars = null;
	private static float[] sFloats = null;

}
