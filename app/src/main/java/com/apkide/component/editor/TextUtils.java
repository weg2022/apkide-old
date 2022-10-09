package com.apkide.component.editor;

public final class TextUtils {
	
	public static final char BS = '\b';
	
	public static final char CR = '\r';
	
	public static final char LF = '\n';
	
	public static final char TAB = '\t';
	
	public static final char SPACE = ' ';
	private static final Object sLock = new Object();
	
	private static char[] sTemp = null;
	
	static char[] obtain(int len) {
		char[] buf;
		
		synchronized (sLock) {
			buf = sTemp;
			sTemp = null;
		}
		
		if (buf == null || buf.length < len)
			buf = new char[len];
		
		return buf;
	}
	
	static void recycle(char[] temp) {
		if (temp.length > 1000)
			return;
		
		synchronized (sLock) {
			sTemp = temp;
		}
	}
	
	
	public static long of(int first, int second) {
		return (((long) first) << 32) | ((long) second & 0xffffffffL);
	}
	
	public static int first(long intPair) {
		return (int) (intPair >> 32);
	}
	
	public static int second(long intPair) {
		return (int) intPair;
	}
	
	// Returns true if the character's presence could affect RTL layout.
	//
	// In order to be fast, the code is intentionally rough and quite conservative in its
	// considering inclusion of any non-BMP or surrogate characters or anything in the bidi
	// blocks or any bidi formatting characters with a potential to affect RTL layout.
	/* package */
	public static boolean couldAffectRtl(char c) {
		return (0x0590 <= c && c <= 0x08FF) ||  // RTL scripts
				c == 0x200E ||  // Bidi format character
				c == 0x200F ||  // Bidi format character
				(0x202A <= c && c <= 0x202E) ||  // Bidi format characters
				(0x2066 <= c && c <= 0x2069) ||  // Bidi format characters
				(0xD800 <= c && c <= 0xDFFF) ||  // Surrogate pairs
				(0xFB1D <= c && c <= 0xFDFF) ||  // Hebrew and Arabic presentation forms
				(0xFE70 <= c && c <= 0xFEFE);  // Arabic presentation forms
	}
	
	// Returns true if there is no character present that may potentially affect RTL layout.
	// Since this calls couldAffectRtl() above, it's also quite conservative, in the way that
	// it may return 'false' (needs bidi) although careful consideration may tell us it should
	// return 'true' (does not need bidi).
	/* package */
	public static boolean doesNotNeedBidi(char[] text, int start, int len) {
		final int end = start + len;
		for (int i = start; i < end; i++) {
			if (couldAffectRtl(text[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean doesNotNeedBidi(TextContent content, int start, int len) {
		final int end = start + len;
		for (int i = start; i < end; i++) {
			if (couldAffectRtl(content.getChar(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static int getOffsetBefore(TextContent text, int offset) {
		if (offset == 0)
			return 0;
		if (offset == 1)
			return 0;
		
		char c = text.getChar(offset - 1);
		
		if (c >= '\uDC00' && c <= '\uDFFF') {
			char c1 = text.getChar(offset - 2);
			
			if (c1 >= '\uD800' && c1 <= '\uDBFF')
				offset -= 2;
			else
				offset -= 1;
		} else {
			offset -= 1;
		}
		
		return offset;
	}
	
	public static int getOffsetAfter(TextContent text, int offset) {
		int len = text.getCharCount();
		
		if (offset == len)
			return len;
		if (offset == len - 1)
			return len;
		
		char c = text.getChar(offset);
		
		if (c >= '\uD800' && c <= '\uDBFF') {
			char c1 = text.getChar(offset + 1);
			
			if (c1 >= '\uDC00' && c1 <= '\uDFFF')
				offset += 2;
			else
				offset += 1;
		} else {
			offset += 1;
		}
		return offset;
	}
	
	public static int getTrimmedLength(TextContent s) {
		int len = s.getCharCount();
		
		int start = 0;
		while (start < len && s.getChar(start) <= ' ') {
			start++;
		}
		
		int end = len;
		while (end > start && s.getChar(end - 1) <= ' ') {
			end--;
		}
		
		return end - start;
	}
	
	
	public static String truncateStringForUtf8Storage(String str, int maxbytes) {
		if (maxbytes < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		int bytes = 0;
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			if (c < 0x80) {
				bytes += 1;
			} else if (c < 0x800) {
				bytes += 2;
			} else if (c < Character.MIN_SURROGATE
					|| c > Character.MAX_SURROGATE
					|| str.codePointAt(i) < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
				bytes += 3;
			} else {
				bytes += 4;
				i += (bytes > maxbytes) ? 0 : 1;
			}
			if (bytes > maxbytes) {
				return str.substring(0, i);
			}
		}
		return str;
	}
	
	
}
