package com.apkide.component.editor;

import static com.apkide.component.editor.TextUtils.CR;
import static com.apkide.component.editor.TextUtils.LF;
import static java.lang.System.arraycopy;
import static java.lang.System.lineSeparator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.GetChars;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.apkide.component.editor.graphics.GraphicsOperations;

import java.util.ArrayList;
import java.util.List;

public class TextContentImpl implements TextContent, GetChars, GraphicsOperations {
	
	private final static String LineDelimiter = lineSeparator();
	
	private final List<TextChangeListener> textListeners = new ArrayList<>(1);
	private final int highWatermark = 300;
	private final int lowWatermark = 50;
	private char[] textStore = new char[0];
	private int gapStart = -1;
	private int gapEnd = -1;
	private int gapLine = -1;
	private int[][] lines = new int[50][2];
	private int lineCount = 0;
	private int expandExp = 1;
	private int replaceExpandExp = 1;
	
	
	public TextContentImpl() {
		super();
		indexLines();
	}
	
	private void addLineIndex(int start, int length) {
		int size = lines.length;
		if (lineCount == size) {
			// expand the lines by powers of 2
			int[][] newLines = new int[size + pow2(expandExp)][2];
			arraycopy(lines, 0, newLines, 0, size);
			lines = newLines;
			expandExp++;
		}
		int[] range = new int[]{start, length};
		lines[lineCount] = range;
		lineCount++;
	}
	
	private int pow2(int n) {
		if (n >= 1 && n <= 30)
			return 2 << (n - 1);
		else if (n != 0) {
			throw new IndexOutOfBoundsException();
		}
		return 1;
	}
	
	private int[][] addLineIndex(int start, int length, int[][] linesArray, int count) {
		int size = linesArray.length;
		int[][] newLines = linesArray;
		if (count == size) {
			newLines = new int[size + pow2(replaceExpandExp)][2];
			replaceExpandExp++;
			arraycopy(linesArray, 0, newLines, 0, size);
		}
		int[] range = new int[]{start, length};
		newLines[count] = range;
		return newLines;
	}
	
	@Override
	public void addTextChangeListener(TextChangeListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		if (!textListeners.contains(listener))
			textListeners.add(listener);
	}
	
	private void adjustGap(int position, int sizeHint, int line) {
		if (position == gapStart) {
			int size = (gapEnd - gapStart) - sizeHint;
			if (lowWatermark <= size && size <= highWatermark)
				return;
		} else if ((position + sizeHint == gapStart) && (sizeHint < 0)) {
			int size = (gapEnd - gapStart) - sizeHint;
			if (lowWatermark <= size && size <= highWatermark)
				return;
		}
		moveAndResizeGap(position, sizeHint, line);
	}
	
	private void indexLines() {
		int start = 0;
		lineCount = 0;
		int textLength = textStore.length;
		int i;
		for (i = start; i < textLength; i++) {
			char ch = textStore[i];
			if (ch == CR) {
				if (i + 1 < textLength) {
					ch = textStore[i + 1];
					if (ch == LF) {
						i++;
					}
				}
				addLineIndex(start, i - start + 1);
				start = i + 1;
			} else if (ch == LF) {
				addLineIndex(start, i - start + 1);
				start = i + 1;
			}
		}
		addLineIndex(start, i - start);
	}
	
	private boolean isDelimiter(char ch) {
		if (ch == CR) return true;
		return ch == LF;
	}
	
	private boolean isInsideCRLF(int pos) {
		if (pos == 0) return false;
		if (pos == getCharCount()) return false;
		
		char charBefore = getChar(pos - 1);
		if (charBefore != '\r') return false;
		
		char charAfter = getChar(pos);
		if (charAfter != '\n') return false;
		
		return getLineAtOffset(pos - 1) == getLineAtOffset(pos);
	}
	
	
	private void validateReplace(int start, int replaceLength) {
		if (replaceLength == 0) {
			if (isInsideCRLF(start)) {
				String message = " [0: start=" + start + " len=" + replaceLength + "]";
				throw new IllegalArgumentException(message);
			}
		} else {
			if (isInsideCRLF(start)) {
				String message = " [1: start=" + start + " len=" + replaceLength + "]";
				throw new IllegalArgumentException(message);
			}
			
			if (isInsideCRLF(start + replaceLength)) {
				String message = " [2: start=" + start + " len=" + replaceLength + "]";
				throw new IllegalArgumentException(message);
			}
		}
	}
	
	private int[][] indexLines(int offset, int length, int numLines) {
		int[][] indexedLines = new int[numLines][2];
		int start = 0;
		int lineCount = 0;
		int i;
		replaceExpandExp = 1;
		for (i = start; i < length; i++) {
			int location = i + offset;
			if ((location >= gapStart) && (location < gapEnd)) {
				// ignore the gap
			} else {
				char ch = textStore[location];
				if (ch == CR) {
					if (location + 1 < textStore.length) {
						ch = textStore[location + 1];
						if (ch == LF) {
							i++;
						}
					}
					indexedLines = addLineIndex(start, i - start + 1, indexedLines, lineCount);
					lineCount++;
					start = i + 1;
				} else if (ch == LF) {
					indexedLines = addLineIndex(start, i - start + 1, indexedLines, lineCount);
					lineCount++;
					start = i + 1;
				}
			}
		}
		int[][] newLines = new int[lineCount + 1][2];
		arraycopy(indexedLines, 0, newLines, 0, lineCount);
		int[] range = new int[]{start, i - start};
		newLines[lineCount] = range;
		return newLines;
	}
	
	private void insert(int position, String text) {
		if (text.length() == 0) return;
		
		int startLine = getLineAtOffset(position);
		int change = text.length();
		boolean endInsert = position == getCharCount();
		adjustGap(position, change, startLine);
		
		int startLineOffset = getOffsetAtLine(startLine);
		int startLineLength = getPhysicalLine(startLine).length();
		
		// shrink gap
		gapStart += (change);
		for (int i = 0; i < text.length(); i++) {
			textStore[position + i] = text.charAt(i);
		}
		
		int[][] newLines = indexLines(startLineOffset, startLineLength, 10);
		int numNewLines = newLines.length - 1;
		if (newLines[numNewLines][1] == 0) {
			if (endInsert) {
				numNewLines += 1;
			} else {
				numNewLines -= 1;
			}
		}
		
		expandLinesBy(numNewLines);
		for (int i = lineCount - 1; i > startLine; i--) {
			lines[i + numNewLines] = lines[i];
		}
		for (int i = 0; i < numNewLines; i++) {
			newLines[i][0] += startLineOffset;
			lines[startLine + i] = newLines[i];
		}
		if (numNewLines < newLines.length) {
			newLines[numNewLines][0] += startLineOffset;
			lines[startLine + numNewLines] = newLines[numNewLines];
		}
		
		lineCount += numNewLines;
		gapLine = getLineAtPhysicalOffset(gapStart);
	}
	
	private void moveAndResizeGap(int position, int size, int newGapLine) {
		char[] content = null;
		int oldSize = gapEnd - gapStart;
		int newSize;
		if (size > 0) {
			newSize = highWatermark + size;
		} else {
			newSize = lowWatermark - size;
		}
		
		if (gapExists()) {
			lines[gapLine][1] = lines[gapLine][1] - oldSize;
			for (int i = gapLine + 1; i < lineCount; i++) {
				lines[i][0] = lines[i][0] - oldSize;
			}
		}
		
		if (newSize < 0) {
			if (oldSize > 0) {
				content = new char[textStore.length - oldSize];
				arraycopy(textStore, 0, content, 0, gapStart);
				arraycopy(textStore, gapEnd, content, gapStart, content.length - gapStart);
				textStore = content;
			}
			gapStart = gapEnd = position;
			return;
		}
		content = new char[textStore.length + (newSize - oldSize)];
		int newGapEnd = position + newSize;
		if (oldSize == 0) {
			arraycopy(textStore, 0, content, 0, position);
			arraycopy(textStore, position, content, newGapEnd, content.length - newGapEnd);
		} else if (position < gapStart) {
			int delta = gapStart - position;
			arraycopy(textStore, 0, content, 0, position);
			arraycopy(textStore, position, content, newGapEnd, delta);
			arraycopy(textStore, gapEnd, content, newGapEnd + delta, textStore.length - gapEnd);
		} else {
			int delta = position - gapStart;
			arraycopy(textStore, 0, content, 0, gapStart);
			arraycopy(textStore, gapEnd, content, gapStart, delta);
			arraycopy(textStore, gapEnd + delta, content, newGapEnd, content.length - newGapEnd);
		}
		textStore = content;
		gapStart = position;
		gapEnd = newGapEnd;
		
		if (gapExists()) {
			gapLine = newGapLine;
			int gapLength = gapEnd - gapStart;
			lines[gapLine][1] = lines[gapLine][1] + (gapLength);
			for (int i = gapLine + 1; i < lineCount; i++) {
				lines[i][0] = lines[i][0] + gapLength;
			}
		}
	}
	
	private int lineCount(int startOffset, int length) {
		if (length == 0) {
			return 0;
		}
		int lineCount = 0;
		int count = 0;
		int i = startOffset;
		if (i >= gapStart) {
			i += gapEnd - gapStart;
		}
		while (count < length) {
			if ((i >= gapStart) && (i < gapEnd)) {
				// ignore the gap
			} else {
				char ch = textStore[i];
				if (ch == CR) {
					if (i + 1 < textStore.length) {
						ch = textStore[i + 1];
						if (ch == LF) {
							i++;
							count++;
						}
					}
					lineCount++;
				} else if (ch == LF) {
					lineCount++;
				}
				count++;
			}
			i++;
		}
		return lineCount;
	}
	
	private int lineCount(String text) {
		int lineCount = 0;
		int length = text.length();
		for (int i = 0; i < length; i++) {
			char ch = text.charAt(i);
			if (ch == CR) {
				if (i + 1 < length && text.charAt(i + 1) == LF) {
					i++;
				}
				lineCount++;
			} else if (ch == LF) {
				lineCount++;
			}
		}
		return lineCount;
	}
	
	@Override
	public int getCharCount() {
		int length = gapEnd - gapStart;
		return (textStore.length - length);
	}
	
	@Override
	public int getLineLength(int lineIndex) {
		if ((lineIndex >= lineCount) || (lineIndex < 0)) throw new IllegalArgumentException();
		int start = lines[lineIndex][0];
		int length = lines[lineIndex][1];
		int end = start + length - 1;
		if (!gapExists() || (end < gapStart) || (start >= gapEnd)) {
			while ((length - 1 >= 0) && isDelimiter(textStore[start + length - 1])) {
				length--;
			}
			return length;
		} else {
			StringBuilder buf = new StringBuilder();
			int gapLength = gapEnd - gapStart;
			buf.append(textStore, start, gapStart - start);
			buf.append(textStore, gapEnd, length - gapLength - (gapStart - start));
			length = buf.length();
			while ((length - 1 >= 0) && isDelimiter(buf.charAt(length - 1))) {
				length--;
			}
			return length;
		}
	}
	
	@Override
	public int getLinePhysicalLength(int lineIndex) {
		if ((lineIndex >= lineCount) || (lineIndex < 0)) throw new IllegalArgumentException();
		return lines[lineIndex][1];
	}
	
	@Override
	public String getLine(int index) {
		if ((index >= lineCount) || (index < 0)) throw new IllegalArgumentException();
		int start = lines[index][0];
		int length = lines[index][1];
		int end = start + length - 1;
		if (!gapExists() || (end < gapStart) || (start >= gapEnd)) {
			while ((length - 1 >= 0) && isDelimiter(textStore[start + length - 1])) {
				length--;
			}
			return new String(textStore, start, length);
		} else {
			StringBuilder buf = new StringBuilder();
			int gapLength = gapEnd - gapStart;
			buf.append(textStore, start, gapStart - start);
			buf.append(textStore, gapEnd, length - gapLength - (gapStart - start));
			length = buf.length();
			while ((length - 1 >= 0) && isDelimiter(buf.charAt(length - 1))) {
				length--;
			}
			return buf.substring(0, length);
		}
	}
	
	@Override
	public String getPlatformLineDelimiter() {
		return LineDelimiter;
	}
	
	@Override
	public String getLineDelimiter() {
		return getLineDelimiter(0);
	}
	
	@Override
	public String getLineDelimiter(int lineIndex) {
		if ((lineIndex >= lineCount) || (lineIndex < 0)) throw new IllegalArgumentException();
		int start = lines[lineIndex][0];
		int length = lines[lineIndex][1];
		int end = start + length;
		while (end > start) {
			if (getChar(end) == LF) {
				if (end - 1 >= start && getChar(end - 1) == CR) {
					return "\r\n";
				}
				return "\n";
			} else if (getChar(end) == CR) {
				return "\r";
			}
			end--;
		}
		return LineDelimiter;
	}
	
	@Override
	public String getLinePhysical(int lineIndex) {
		if ((lineIndex >= lineCount) || (lineIndex < 0)) throw new IllegalArgumentException();
		
		int start = lines[lineIndex][0];
		int length = lines[lineIndex][1];
		int end = start + length - 1;
		if (!gapExists() || (end < gapStart) || (start >= gapEnd)) {
			return new String(textStore, start, length);
		} else {
			int gapLength = gapEnd - gapStart;
			return String.valueOf(textStore, start, gapStart - start) +
					String.valueOf(textStore, gapEnd, length - gapLength - (gapStart - start));
		}
	}
	
	private String getPhysicalLine(int index) {
		int start = lines[index][0];
		int length = lines[index][1];
		return getPhysicalText(start, length);
	}
	
	@Override
	public int getLineCount() {
		return lineCount;
	}
	
	@Override
	public int getLineAtOffset(int charPosition) {
		if ((charPosition > getCharCount()) || (charPosition < 0))
			throw new IllegalArgumentException();
		int position;
		if (charPosition < gapStart) {
			position = charPosition;
		} else {
			position = charPosition + (gapEnd - gapStart);
		}
		
		if (lineCount > 0) {
			int lastLine = lineCount - 1;
			if (position == lines[lastLine][0] + lines[lastLine][1])
				return lastLine;
		}
		
		int high = lineCount;
		int low = -1;
		int index;
		while (high - low > 1) {
			index = (high + low) / 2;
			int lineStart = lines[index][0];
			int lineEnd = lineStart + lines[index][1] - 1;
			if (position <= lineStart) {
				high = index;
			} else if (position <= lineEnd) {
				high = index;
				break;
			} else {
				low = index;
			}
		}
		return high;
	}
	
	private int getLineAtPhysicalOffset(int position) {
		int high = lineCount;
		int low = -1;
		int index;
		while (high - low > 1) {
			index = (high + low) / 2;
			int lineStart = lines[index][0];
			int lineEnd = lineStart + lines[index][1] - 1;
			if (position <= lineStart) {
				high = index;
			} else if (position <= lineEnd) {
				high = index;
				break;
			} else {
				low = index;
			}
		}
		return high;
	}
	
	@Override
	public int getOffsetAtLine(int lineIndex) {
		if (lineIndex == 0) return 0;
		if ((lineIndex >= lineCount) || (lineIndex < 0)) throw new IllegalArgumentException();
		int start = lines[lineIndex][0];
		if (start > gapEnd) {
			return start - (gapEnd - gapStart);
		} else {
			return start;
		}
	}
	
	@Override
	public char getChar(int offset) {
		int len = getCharCount();
		if (offset < 0) {
			throw new IndexOutOfBoundsException("getChar: " + offset + " < 0");
		} else if (offset >= len) {
			throw new IndexOutOfBoundsException("getChar: " + offset + " >= length " + len);
		}
		
		if (offset >= gapStart)
			return textStore[offset + (gapEnd - gapStart)];
		else
			return textStore[offset];
	}
	
	private void expandLinesBy(int numLines) {
		int size = lines.length;
		if (size - lineCount >= numLines) {
			return;
		}
		int[][] newLines = new int[size + Math.max(10, numLines)][2];
		arraycopy(lines, 0, newLines, 0, size);
		lines = newLines;
	}
	
	private boolean gapExists() {
		return gapStart != gapEnd;
	}
	
	private String getPhysicalText(int start, int length) {
		return new String(textStore, start, length);
	}
	
	@Override
	public String getTextRange(int start, int length) {
		if (textStore == null)
			return "";
		if (length == 0)
			return "";
		int end = start + length;
		if (!gapExists() || (end < gapStart))
			return new String(textStore, start, length);
		if (gapStart < start) {
			int gapLength = gapEnd - gapStart;
			return new String(textStore, start + gapLength, length);
		}
		return String.valueOf(textStore, start, gapStart - start) +
				String.valueOf(textStore, gapEnd, end - gapStart);
	}
	
	@Override
	public String getText() {
		return getTextRange(0, getCharCount());
	}
	
	@Override
	public void setText(String text) {
		textStore = text.toCharArray();
		gapStart = -1;
		gapEnd = -1;
		expandExp = 1;
		indexLines();
		TextChangedEvent event = new TextChangedEvent(this);
		event.newText = "";
		for (TextChangeListener listener : textListeners) {
			listener.textSet(event);
		}
	}
	
	@Override
	public void removeTextChangeListener(TextChangeListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		textListeners.remove(listener);
	}
	
	@Override
	public void replaceTextRange(int start, int replaceLength, String newText) {
		validateReplace(start, replaceLength);
		
		TextChangingEvent event = new TextChangingEvent(this);
		event.start = start;
		event.replaceLineCount = lineCount(start, replaceLength);
		event.newText = newText;
		event.newLineCount = lineCount(newText);
		event.replaceCharCount = replaceLength;
		event.newCharCount = newText.length();
		for (TextChangeListener listener : textListeners) {
			listener.textChanging(event);
		}
		
		delete(start, replaceLength, event.replaceLineCount + 1);
		insert(start, newText);
		
		TextChangedEvent changedEvent = new TextChangedEvent(this);
		changedEvent.start = event.start;
		changedEvent.newCharCount = event.newCharCount;
		changedEvent.newLineCount = event.replaceLineCount;
		changedEvent.newText = event.newText;
		changedEvent.replaceCharCount = event.replaceCharCount;
		changedEvent.replaceLineCount = event.replaceLineCount;
		for (TextChangeListener listener : textListeners) {
			listener.textChanged(changedEvent);
		}
	}
	
	private void delete(int position, int length, int numLines) {
		if (length == 0) return;
		
		int startLine = getLineAtOffset(position);
		int startLineOffset = getOffsetAtLine(startLine);
		int endLine = getLineAtOffset(position + length);
		
		String endText = "";
		boolean splittingDelimiter = false;
		if (position + length < getCharCount()) {
			endText = getTextRange(position + length - 1, 2);
			if ((endText.charAt(0) == CR) && (endText.charAt(1) == LF)) {
				splittingDelimiter = true;
			}
		}
		
		adjustGap(position + length, -length, startLine);
		int[][] oldLines = indexLines(position, length + (gapEnd - gapStart), numLines);
		
		if (position + length == gapStart) {
			gapStart -= length;
		} else {
			gapEnd += length;
		}
		
		int j = position;
		boolean eol = false;
		while (j < textStore.length && !eol) {
			if (j < gapStart || j >= gapEnd) {
				char ch = textStore[j];
				if (isDelimiter(ch)) {
					if (j + 1 < textStore.length) {
						if (ch == CR && (textStore[j + 1] == LF)) {
							j++;
						}
					}
					eol = true;
				}
			}
			j++;
		}
		lines[startLine][1] = (position - startLineOffset) + (j - position);
		int numOldLines = oldLines.length - 1;
		if (splittingDelimiter) numOldLines -= 1;
		for (int i = endLine + 1; i < lineCount; i++) {
			lines[i - numOldLines] = lines[i];
		}
		lineCount -= numOldLines;
		gapLine = getLineAtPhysicalOffset(gapStart);
	}
	
	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
		int gapLength = (gapEnd - gapStart);
		if (end <= gapStart) {
			arraycopy(textStore, start, dest, destoff, end - start);
		} else if (start >= gapStart) {
			arraycopy(textStore, start + gapLength, dest, destoff, end - start);
		} else {
			arraycopy(textStore, start, dest, destoff, gapStart - start);
			arraycopy(textStore, gapStart + gapLength,
					dest, destoff + (gapStart - start),
					end - gapStart);
		}
	}
	
	@Override
	public void drawText(Canvas canvas, int start, int length, float x, float y, Paint paint) {
		int end = start + length;
		int gapLength = (gapEnd - gapStart);
		if (end <= gapStart) {
			canvas.drawText(textStore, start, end - start, x, y, paint);
		} else if (start >= gapStart) {
			canvas.drawText(textStore, start + gapLength, end - start, x, y, paint);
		} else {
			char[] buf = TextUtils.obtain(end - start);
			
			getChars(start, end, buf, 0);
			canvas.drawText(buf, 0, end - start, x, y, paint);
			TextUtils.recycle(buf);
		}
	}
	
	@Override
	public void drawTextRun(Canvas canvas, int start, int length, int contextStart, int contextLength, float x, float y, boolean isRtl, Paint paint) {
		int contextEnd = contextStart + contextLength;
		int gapLength = gapEnd - gapStart;
		if (contextEnd <= gapStart) {
			canvas.drawTextRun(textStore, start, length, contextStart, contextLength, x, y, isRtl, paint);
		} else if (contextStart >= gapStart) {
			canvas.drawTextRun(textStore, start + gapLength, length, contextStart + gapLength,
					contextLength, x, y, isRtl, paint);
		} else {
			char[] buf = TextUtils.obtain(contextLength);
			getChars(contextStart, contextEnd, buf, 0);
			canvas.drawTextRun(buf, start - contextStart, length, 0, contextLength, x, y, isRtl, paint);
			TextUtils.recycle(buf);
		}
	}
	
	@Override
	public float measureText(int start, int length, Paint paint) {
		float ret;
		int end = start + length;
		int gapLength = gapEnd - gapStart;
		if (end <= gapStart) {
			ret = paint.measureText(textStore, start, length);
		} else if (start >= gapStart) {
			ret = paint.measureText(textStore, start + gapLength, length);
		} else {
			char[] buf = TextUtils.obtain(length);
			getChars(start, end, buf, 0);
			ret = paint.measureText(buf, 0, end - start);
			TextUtils.recycle(buf);
		}
		return ret;
	}
	
	@Override
	public int getTextWidths(int start, int length, float[] widths, Paint paint) {
		int ret;
		int end = start + length;
		int gapLength = gapEnd - gapStart;
		if (end <= gapStart) {
			ret = paint.getTextWidths(textStore, start, length, widths);
		} else if (start >= gapStart) {
			ret = paint.getTextWidths(textStore, start + gapLength, length, widths);
		} else {
			char[] buf = TextUtils.obtain(end - start);
			getChars(start, end, buf, 0);
			ret = paint.getTextWidths(buf, 0, end - start, widths);
			TextUtils.recycle(buf);
		}
		
		return ret;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getTextRunAdvances(int start, int length, int contextStart, int contextLength, boolean isRtl, float[] advances, int advancesIndex, Paint paint) {
		float ret;
		int end = start + length;
		int contextEnd = contextStart + contextLength;
		int gapLength = gapEnd - gapStart;
		if (end <= gapStart) {
			ret = paint.getTextRunAdvances(textStore, start, length, contextStart, contextLength,
					isRtl, advances, advancesIndex);
		} else if (start >= gapStart) {
			ret = paint.getTextRunAdvances(textStore, start + gapLength, length,
					contextStart + gapLength, contextLength, isRtl, advances, advancesIndex);
		} else {
			char[] buf = TextUtils.obtain(contextLength);
			getChars(contextStart, contextEnd, buf, 0);
			ret = paint.getTextRunAdvances(buf, start - contextStart, length,
					0, contextLength, isRtl, advances, advancesIndex);
			TextUtils.recycle(buf);
		}
		return ret;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt, Paint paint) {
		int ret;
		int contextEnd = contextStart + contextLength;
		int gapLength = gapEnd - gapStart;
		if (contextEnd <= gapStart) {
			ret = paint.getTextRunCursor(textStore, contextStart, contextLength,
					isRtl, offset, cursorOpt);
		} else if (contextStart >= gapStart) {
			ret = paint.getTextRunCursor(textStore, contextStart + gapLength, contextLength,
					isRtl, offset + gapLength, cursorOpt) - gapLength;
		} else {
			char[] buf = TextUtils.obtain(contextLength);
			getChars(contextStart, contextEnd, buf, 0);
			ret = paint.getTextRunCursor(buf, 0, contextLength,
					isRtl, offset - contextStart, cursorOpt) + contextStart;
			TextUtils.recycle(buf);
		}
		return ret;
	}
	
	@Override
	public int length() {
		return getCharCount();
	}
	
	@Override
	public char charAt(int index) {
		return getChar(index);
	}
	
	@NonNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return getTextRange(start, end - start);
	}
	
	@NonNull
	@Override
	public String toString() {
		return getText();
	}
}
