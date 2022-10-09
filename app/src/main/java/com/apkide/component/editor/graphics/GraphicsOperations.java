package com.apkide.component.editor.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface GraphicsOperations {
	void drawText(Canvas canvas, int start, int length,
	              float x, float y, Paint paint);
	
	void drawTextRun(Canvas canvas, int start, int length, int contextStart, int contextLength,
	                 float x, float y, boolean isRtl, Paint paint);
	
	float measureText(int start, int length, Paint paint);
	
	
	int getTextWidths(int start, int length, float[] widths, Paint paint);
	
	
	float getTextRunAdvances(int start, int length, int contextStart, int contextLength,
	                         boolean isRtl, float[] advances, int advancesIndex, Paint paint);
	
	int getTextRunCursor(int contextStart, int contextLength, boolean isRtl, int offset,
	                     int cursorOpt, Paint paint);
}
