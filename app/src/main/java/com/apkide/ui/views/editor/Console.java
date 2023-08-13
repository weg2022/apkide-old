package com.apkide.ui.views.editor;

import static java.lang.Math.log10;
import static java.lang.Math.max;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.apkide.common.Location;
import com.apkide.common.Position;
import com.apkide.common.Size;
import com.apkide.common.SyncRunner;

import java.util.ArrayList;
import java.util.List;

public class Console extends View implements ModelListener {
	public Console(Context context) {
		super(context);
		initView();
	}

	public Console(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public Console(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}


	private static final String TAG = "Console";

	private int myFontColor;
	private int mySelectionColor;
	private int myCaretColor;
	private int myCaretLineColor;
	private int myLineNumberColor;
	private int myWarningColor;
	private int myErrorColor;
	private TextStyle[] myStyles;
	private final Rect myRect = new Rect(0, 0, 0, 0);
	private Typeface myTypeface = Typeface.MONOSPACE;
	private int myFontSize = 16;
	private final TextPaint myPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
	private float myFontTop;
	private float myFontBottom;
	private float myFontHeight;
	private float myNumberCharWidth;
	private float myTabCharWidth;
	private float mySpaceCharWidth;

	private int myTabSize = 4;
	private boolean myInsertTabsAsSpaces;

	private final List<OverwriteModeListener> myOverwriteModeListeners = new ArrayList<>(1);
	private boolean myOverwriteMode;

	private final List<CaretListener> myCaretListeners = new ArrayList<>(1);
	private Caret myCaret;
	private boolean myCaretVisibility;
	private int myCaretLine;
	private int myCaretColumn;
	private final List<SelectionListener> mySelectionListeners = new ArrayList<>(1);
	private boolean mySelectionVisibility;
	private int mySelectionAnchorLine;
	private int mySelectionAnchorColumn;
	private int mySelectionPointLine;
	private int mySelectionPointColumn;
	private int myFirstSelectedLine;
	private int myFirstSelectedColumn;
	private int myLastSelectedLine;
	private int myLastSelectedColumn;
	private Model myModel;
	private int myMaxColumn;
	private int myMaxColumnLine;
	private float mySidebarX;
	private SyncRunner myComputeMaxColumnRunner;
	private SyncRunner myLayoutUpdateRunner;

	private void initView() {
		myCaret = new Caret(this);

		myComputeMaxColumnRunner = new SyncRunner(this::computeMaxColumn);
		myLayoutUpdateRunner = new SyncRunner(() -> {
			if (isShown()) {
				requestLayout();
				invalidate();
			}
		});
	}

	public void setModel(@NonNull Model model) {
		if (myModel != null) {
			myModel.removeModelListener(this);
		}
		myModel = model;
		myModel.addModelListener(this);
		configurePaint();
		configureColors();
		computeMaxColumn();
		updateSidebar();
		redrawOnVisible();
		myLayoutUpdateRunner.run();
	}

	public Model getModel() {
		return myModel;
	}

	protected void configureColors() {
		if (myModel != null) {
			myStyles = new TextStyle[myModel.getStyleCount()];
			for (int i = 0; i < myStyles.length; i++) {
				myStyles[i] = myModel.getTextStyle(i);
			}
		}
		myFontColor = 0xff000000;
		mySelectionColor = 0xff999999;
		myCaretColor = 0xff000000;
		myCaretLineColor = 0xfff5f5f5;
		myLineNumberColor = 0xffd0d0d0;
		myWarningColor = 0xffF2BF57;
		myErrorColor = 0xffF50000;
	}

	public void setFontColor(int fontColor) {
		myFontColor = fontColor;
	}

	public int getFontColor() {
		return myFontColor;
	}

	public void setSelectionColor(int selectionColor) {
		if (mySelectionColor != selectionColor) {
			mySelectionColor = selectionColor;
			if (mySelectionVisibility) invalidate();
		}
	}

	public int getSelectionColor() {
		return mySelectionColor;
	}

	public void setCaretColor(int caretColor) {
		myCaretColor = caretColor;
	}

	public int getCaretColor() {
		return myCaretColor;
	}

	public void setCaretLineColor(int caretLineColor) {
		myCaretLineColor = caretLineColor;
	}

	public int getCaretLineColor() {
		return myCaretLineColor;
	}

	public void setLineNumberColor(int lineNumberColor) {
		myLineNumberColor = lineNumberColor;
	}

	public int getLineNumberColor() {
		return myLineNumberColor;
	}

	public void setWarningColor(int warningColor) {
		myWarningColor = warningColor;
	}

	public int getWarningColor() {
		return myWarningColor;
	}

	public void setErrorColor(int errorColor) {
		myErrorColor = errorColor;
	}

	public int getErrorColor() {
		return myErrorColor;
	}

	private void updateSidebar() {
		float padding = (float) ((myNumberCharWidth *
				log10(myModel.getLineCount() + 2.0d))) + getSidebarPaddingLeft();
		mySidebarX = padding + (mySpaceCharWidth * 2.0f);
	}

	protected float getSidebarPaddingLeft() {
		return 0;
	}


	public void setTabSize(int tabSize) {
		if (myTabSize != tabSize) {
			myTabSize = tabSize;
			myTabCharWidth = myTabSize * mySpaceCharWidth;
			invalidate();
			myComputeMaxColumnRunner.run();
			myLayoutUpdateRunner.run();
		}
	}

	public int getTabSize() {
		return myTabSize;
	}

	public void setInsertTabsAsSpaces(boolean insertTabsAsSpaces) {
		myInsertTabsAsSpaces = insertTabsAsSpaces;
	}

	public boolean isInsertTabsAsSpaces() {
		return myInsertTabsAsSpaces;
	}

	public void addOverwriteModeListener(@NonNull OverwriteModeListener listener) {
		if (!myOverwriteModeListeners.contains(listener))
			myOverwriteModeListeners.add(listener);
	}

	public void removeOverwriteModeListener(@NonNull OverwriteModeListener listener) {
		myOverwriteModeListeners.remove(listener);
	}

	public void setOverwriteMode(boolean overwriteMode) {
		if (myOverwriteMode != overwriteMode) {
			myOverwriteMode = overwriteMode;
			fireOverwriteMode(myOverwriteMode);
		}
	}

	protected void fireOverwriteMode(boolean overwriteMode) {
		for (OverwriteModeListener listener : myOverwriteModeListeners) {
			listener.overwriteModeToggle(this, overwriteMode);
		}
	}

	public boolean isOverwriteMode() {
		return myOverwriteMode;
	}

	protected void configurePaint() {
		myPaint.setTypeface(myTypeface);
		myPaint.setTextSize(getDeviceFontSize());
		myPaint.getFontMetrics(myFontMetrics);
		myFontTop = -myFontMetrics.top;
		myFontBottom = myFontMetrics.bottom;
		myFontHeight = ((-myFontMetrics.top) + myFontMetrics.bottom + myFontMetrics.leading);
		mySpaceCharWidth = myPaint.measureText("M");
		myNumberCharWidth = myPaint.measureText("8");
		myTabCharWidth = mySpaceCharWidth * myTabSize;
	}

	public void setTypeface(@NonNull Typeface typeface) {
		if (myTypeface != typeface) {
			myTypeface = typeface;
			configurePaint();
			invalidate();
			myLayoutUpdateRunner.run();
		}
	}

	@NonNull
	public Typeface getTypeface() {
		return myTypeface;
	}

	public void setFontSize(int fontSize) {
		if (myFontSize != fontSize) {
			myFontSize = fontSize;
			configurePaint();
			invalidate();
			myLayoutUpdateRunner.run();
		}
	}

	public int getFontSize() {
		return myFontSize;
	}

	public float getDeviceFontSize() {
		return myFontSize * getContext().getResources().getDisplayMetrics().density;
	}

	public float getFontTop() {
		return myFontTop;
	}

	public float getFontBottom() {
		return myFontBottom;
	}

	public float getFontHeight() {
		return myFontHeight;
	}

	public float getSpaceCharWidth() {
		return mySpaceCharWidth;
	}

	public float getTabCharWidth() {
		return myTabCharWidth;
	}

	protected void computeMaxColumn() {
		int lineCount = myModel.getLineCount();
		int maxColumnLine = 0;
		int maxColumn = 0;
		for (int line = 0; line < lineCount; line++) {
			int length = myModel.getLineLength(line);
			int lineMaxColumn = 0;
			for (int column = 0; column < length; column++) {
				lineMaxColumn += myModel.getChar(line, column) == '\t' ?
						myTabSize - (lineMaxColumn % myTabSize) : 1;
			}
			if (lineMaxColumn > maxColumn) {
				maxColumn = lineMaxColumn;
				maxColumnLine = line;
			}
		}
		myMaxColumn = maxColumn;
		myMaxColumnLine = maxColumnLine;
	}

	private int getLineMaxColumn(int line) {
		int length = myModel.getLineLength(line);
		int maxColumn = 0;
		for (int column = 0; column < length; column++) {
			maxColumn += myModel.getChar(line, column) == '\t' ?
					myTabSize - (maxColumn % myTabSize) : 1;
		}
		return maxColumn;
	}

	public int getCaretLine() {
		return myCaretLine;
	}

	public int getCaretColumn() {
		return myCaretColumn;
	}

	@NonNull
	public Position getCaretPosition() {
		return new Position(myCaretLine, myCaretColumn);
	}


	public void setCaretVisibility(boolean caretVisibility) {
		if (myCaretVisibility != caretVisibility) {
			if (caretVisibility) myCaret.schedule();
			else myCaret.cancel();

			myCaretVisibility = caretVisibility;
			invalidate();
		}
	}

	public boolean getCaretVisibility() {
		return myCaretVisibility;
	}

	public void setCaretBlinks(boolean caretBlinks) {
		myCaret.setBlinks(caretBlinks);
	}

	public boolean isCaretBlinks() {
		return myCaret.isBlinks();
	}

	public boolean isCaretShowing() {
		return myCaret.isShowing();
	}

	public void moveCaret(int line, int column) {
		moveCaret(line, column, false);
	}

	private void moveCaret(int line, int column, boolean isTyping) {
		if (line < 0) line = 0;
		if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;

		if (column < 0) column = 0;
		if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);

		if (myCaretLine == line && myCaretColumn == column) return;

		myCaretLine = line;
		myCaretColumn = column;
		fireCaretMoved(myCaretLine, myCaretColumn, isTyping);
		if (myCaretVisibility) myCaret.schedule();

		invalidate();
	}

	protected void fireCaretMoved(int caretLine, int caretColumn, boolean isTyping) {
		for (CaretListener listener : myCaretListeners) {
			listener.caretMove(this, caretLine, caretColumn, isTyping);
		}
	}

	public void addCaretListener(@NonNull CaretListener listener) {
		if (!myCaretListeners.contains(listener)) myCaretListeners.add(listener);
	}

	public void removeCaretListener(@NonNull CaretListener listener) {
		myCaretListeners.remove(listener);
	}


	public void addSelectionListener(@NonNull SelectionListener listener) {
		if (!mySelectionListeners.contains(listener))
			mySelectionListeners.add(listener);
	}

	public void removeSelectionListener(@NonNull SelectionListener listener) {
		mySelectionListeners.remove(listener);
	}

	public void setSelectionVisibility(boolean selectionVisibility) {
		if (mySelectionVisibility != selectionVisibility) {
			if (!selectionVisibility ||
					computeDiffCount(mySelectionAnchorLine,
					mySelectionAnchorColumn, mySelectionPointLine,
					mySelectionPointColumn) != 0) {
				mySelectionVisibility = selectionVisibility;
				fireSelectionChanged(selectionVisibility);
				invalidate();
			}
		}
	}

	protected void fireSelectionChanged(boolean selectionVisibility) {
		for (SelectionListener listener : mySelectionListeners) {
			listener.selectChanged(this, selectionVisibility);
		}
	}

	public boolean getSelectionVisibility() {
		return mySelectionVisibility;
	}


	public void selection(int startLine, int startColumn, int endLine, int endColumn) {
		if (startLine >= myModel.getLineCount())
			startLine = myModel.getLineCount() - 1;

		if (startColumn >= myModel.getLineLength(startLine))
			startColumn = myModel.getLineLength(startLine);

		if (endLine >= myModel.getLineCount())
			endLine = myModel.getLineCount() - 1;

		if (endColumn >= myModel.getLineLength(endLine))
			endColumn = myModel.getLineLength(endLine);

		moveCaret(startLine, startColumn);
		setSelectionVisibility(true);
		setSelectionAnchor(startLine, startColumn);
		setSelectionPoint(endLine, endColumn);
	}

	public void setSelectionAnchor(int line, int column) {
		if (line < 0) line = 0;

		if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;

		if (column < 0) column = 0;

		if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);

		mySelectionAnchorLine = line;
		mySelectionAnchorColumn = column;
		updateSelection();
	}

	public void setSelectionPoint(int line, int column) {
		if (line < 0) line = 0;

		if (line >= myModel.getLineCount()) line = myModel.getLineCount() - 1;

		if (column < 0) column = 0;

		if (column >= myModel.getLineLength(line)) column = myModel.getLineLength(line);

		mySelectionPointLine = line;
		mySelectionPointColumn = column;
		updateSelection();
	}

	protected void updateSelection() {
		int count = computeDiffCount(mySelectionAnchorLine, mySelectionAnchorColumn, mySelectionPointLine, mySelectionPointColumn);
		if (count < 0) {
			setSelectionVisibility(true);
			updateSelection(mySelectionAnchorLine, mySelectionAnchorColumn, mySelectionPointLine, mySelectionPointColumn);
		} else if (count > 0) {
			setSelectionVisibility(true);
			updateSelection(mySelectionPointLine, mySelectionPointColumn, mySelectionAnchorLine, mySelectionAnchorColumn);
		} else {
			myFirstSelectedLine = myLastSelectedLine = myCaretLine;
			myFirstSelectedColumn = myLastSelectedColumn = myCaretColumn;
			setSelectionVisibility(false);
			invalidate();
		}
	}

	protected void updateSelection(int startLine, int startColumn, int endLine, int endColumn) {
		if (startLine == myFirstSelectedLine && startColumn == myFirstSelectedColumn
				&& myLastSelectedLine == endLine && myLastSelectedColumn == endColumn) {
			return;
		}
		myFirstSelectedLine = startLine;
		myFirstSelectedColumn = startColumn;
		myLastSelectedLine = endLine;
		myLastSelectedColumn = endColumn;
		fireSelectionUpdate();
		invalidate();
	}

	protected void fireSelectionUpdate() {
		for (SelectionListener listener : mySelectionListeners) {
			listener.selectUpdate(this);
		}
	}

	public int getSelectionAnchorLine() {
		return mySelectionAnchorLine;
	}

	public int getSelectionAnchorColumn() {
		return mySelectionAnchorColumn;
	}

	@NonNull
	public Position getSelectionAnchorPosition() {
		return new Position(mySelectionAnchorLine, mySelectionAnchorColumn);
	}

	public int getSelectionPointLine() {
		return mySelectionPointLine;
	}

	public int getSelectionPointColumn() {
		return mySelectionPointColumn;
	}

	@NonNull
	public Position getSelectionPointPosition() {
		return new Position(mySelectionPointLine, mySelectionPointColumn);
	}

	public int getFirstSelectedLine() {
		return myFirstSelectedLine;
	}

	public int getFirstSelectedColumn() {
		return myFirstSelectedColumn;
	}

	public int getLastSelectedLine() {
		return myLastSelectedLine;
	}

	public int getLastSelectedColumn() {
		return myLastSelectedColumn;
	}

	@NonNull
	public Region getSelectedRegion() {
		return new Region(myFirstSelectedLine, myFirstSelectedColumn, myLastSelectedLine, myLastSelectedColumn);
	}


	protected int computeDiffCount(int firstLine, int firstColumn,
	                               int lastLine, int lastColumn) {
		return firstLine == lastLine ? firstColumn - lastColumn : firstLine - lastLine;
	}


	@Override
	public void insertUpdate(@NonNull Model model,
	                         int startLine, int startColumn,
	                         int endLine, int endColumn) {
		updateSidebar();
		invalidate();
		if (startLine < myCaretLine && startLine != endLine) {
			moveCaret((myCaretLine + endLine) - startLine, myCaretColumn, true);
		} else if (startLine == myCaretLine && startColumn <= myCaretColumn) {
			if (startLine == endLine) {
				moveCaret(myCaretLine,
						((myCaretColumn + endColumn) - startColumn) + 1, true);
			} else {
				moveCaret((myCaretLine + endLine) - startLine,
						((myCaretColumn + endColumn) - startColumn) + 1, true);
			}
		}

		setSelectionVisibility(false);

		if (startLine == endLine) {
			int maxColumn = getLineMaxColumn(startLine);
			if (myModel.getLineCount() == 1) {
				myMaxColumn = maxColumn;
				return;
			} else if (myComputeMaxColumnRunner.isRunning() || maxColumn <= myMaxColumn) {
				return;
			} else {
				myMaxColumnLine = startLine;
				myMaxColumn = maxColumn;
				myLayoutUpdateRunner.run();
				return;
			}
		}
		if (endLine - startLine > myModel.getLineCount()) {
			myComputeMaxColumnRunner.run();
		} else if (!myComputeMaxColumnRunner.isRunning()) {
			for (int i = startLine; i <= endLine; i++) {
				int maxColumn = getLineMaxColumn(i);
				if (maxColumn > myMaxColumn) {
					myMaxColumn = maxColumn;
					myMaxColumnLine = i;
					break;
				}
			}
		}
		myLayoutUpdateRunner.run();
	}

	@Override
	public void removeUpdate(@NonNull Model model,
	                         int startLine, int startColumn,
	                         int endLine, int endColumn) {
		updateSidebar();

		invalidate();
		if (startLine == endLine) {
			if (endLine == myCaretLine && startColumn < myCaretColumn) {
				moveCaret(myCaretLine, max(startColumn,
						myCaretColumn - ((endColumn - startColumn) + 1)), true);
			}
		} else if ((myCaretLine == startLine && startColumn < myCaretColumn)
				|| (myCaretLine > startLine && myCaretLine < endLine)
				|| (myCaretLine == endLine && myCaretColumn < endColumn)) {
			moveCaret(startLine, startColumn, true);
		} else if (myCaretLine == endLine) {
			moveCaret(startLine,
					((myCaretColumn + startColumn) - endColumn) - 1, true);
		} else if (myCaretLine > endLine) {
			moveCaret(myCaretLine - (endLine - startLine),
					myCaretColumn, true);
		}
		setSelectionVisibility(false);

		if (startLine == endLine) {
			if (startLine == myMaxColumnLine) {
				if (myModel.getLineCount() == 1) {
					myMaxColumn = getLineMaxColumn(startLine);
					return;
				}
				myComputeMaxColumnRunner.run();
				myLayoutUpdateRunner.run();
				return;
			}
			return;
		}
		invalidate();
		myComputeMaxColumnRunner.run();
		myLayoutUpdateRunner.run();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Actual size, no over-provisioning
		setMeasuredDimension(
				(int) (((myMaxColumn + 1) * mySpaceCharWidth) + mySidebarX),
				myModel == null ? 0 : (int) (myModel.getLineCount() * myFontHeight));
	}

	@NonNull
	public Size getSize() {
		return new Size(getMeasuredWidth(), getMeasuredHeight());
	}

	public float getSidebarX() {
		return mySidebarX;
	}

	@NonNull
	public Location computeLocation(int line, int column) {
		if (line < 0) line = 0;
		if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
		float y = computeLocationY(line);
		float x = computeLocationX(line, column);
		return new Location((int) x, (int) y);
	}

	@NonNull
	public Position computePosition(float x, float y) {
		int line = computeLine(y);
		int column = computeColumn(line, x);
		return new Position(line, column);
	}

	public int computeLine(float y) {
		return (int) (y / myFontHeight);
	}

	public float computeLocationY(int line) {
		return (line + 1) * myFontHeight;
	}

	public int computeColumn(float x, float y) {
		int line = computeLine(y);
		return computeColumn(line, x);
	}

	public int computeColumn(int line, float x) {
		if (line < 0) line = 0;
		if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
		int length = getModel().getLineLength(line);
		float columnX = mySidebarX;
		if (length == 0) return 0;
		int column = 0;
		float[] buffer = Buffers.floats(length);
		getLineWidths(line, 0, length, buffer);
		for (int i = 0; i < length; i++) {
			if (columnX >= x) break;
			columnX += buffer[i];
			column++;
		}
		Buffers.recycle(buffer);
		return column;
	}

	public float computeLocationX(int line, int column) {
		if (line < 0) line = 0;
		if (line >= getModel().getLineCount()) line = getModel().getLineCount() - 1;
		int length = getModel().getLineLength(line);
		if (column < 0) column = 0;
		if (column > length) column = length;
		float x = mySidebarX;
		if (column > 0) {
			x += measureLine(line, 0, column);
		}
		return x;
	}


	public float measureLine(int line, int column, int length) {
		return nativeMeasureLine(line, column, length);
	}

	public void getLineWidths(int line, int column, int length, float[] widths) {
		nativeGetLineWidths(line, column, length, widths);
	}


	protected float nativeMeasureLine(int line, int column, int length) {
		int end = column+length;
		float w = myModel.measureLine(line, column, length, myPaint);
		float tabAdvance = myPaint.measureText("\t");
		for (int i = column; i < end; i++) {
			char c = myModel.getChar(line, i);
			if (c == '\t') {
				w -= tabAdvance;
				w += myTabCharWidth;
			}
		}
		return w;
	}

	protected void nativeGetLineWidths(int line, int column, int length, float[] widths) {
		int end = column+length;
		myModel.getLineWidths(line, column, length, widths, myPaint);
		for (int i = column; i < end; i++) {
			char c = myModel.getChar(line, i);
			if (c == '\t')
				widths[i] = myTabCharWidth;
		}
	}

	protected float drawText(Canvas canvas, int line, int column, int length, float x, float y) {
		int end = column + length;
		int begin = column;
		for (int i = column; i < end; i++) {
			if (myModel.getChar(line, i) == '\t') {
				if (myPaint.getColor() != 0)
					myModel.drawLine(canvas, line, begin, i - begin, x, y, myPaint);
				x += measureLine(line, begin, i - begin + 1);
				begin = i + 1;
			}
		}
		if (begin < end) {
			if (myPaint.getColor() != 0)
				myModel.drawLine(canvas, line, begin, end - begin, x, y, myPaint);
			x += measureLine(line, begin, end - begin);
		}
		return x;
	}

	public void redrawOnVisible() {
		getLocalVisibleRect(myRect);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (myRect.isEmpty()) return;

		int firstLine = computeLine(myRect.top);
		int lastLine = computeLine(myRect.bottom + 1);
		if (firstLine >= getModel().getLineCount()) firstLine = getModel().getLineCount() - 1;

		if (lastLine >= getModel().getLineCount()) lastLine = getModel().getLineCount() - 1;

		if (firstLine < 0) firstLine = 0;

		if (firstLine <= lastLine) redrawLines(canvas, firstLine, lastLine);
	}

	protected void redrawLines(Canvas canvas, int startLine, int endLine) {
		float x = mySidebarX;
		float y = computeLocationY(startLine);
		for (int i = startLine; i <= endLine; i++) {
			int length = myModel.getLineLength(i);
			redrawCaretLine(canvas, i, length, x, y);
			redrawLineNumber(canvas, i, length, x, y);
			redrawSelection(canvas, i, length, x, y);
			redrawText(canvas, i, length, x, y);
			redrawCaret(canvas, i, length, x, y);
			y += myFontHeight;
		}
	}

	protected void redrawLineNumber(Canvas canvas, int line, int length, float x, float y) {
		if (myLineNumberColor != 0 && myRect.contains((int) mySidebarX, (int) y)) {
			myPaint.setColor(myLineNumberColor);
			canvas.drawText(Integer.toString(line + 1), 0, y, myPaint);
		}
	}

	protected void redrawCaretLine(Canvas canvas, int line, int length, float x, float y) {
		if (myCaretLineColor != 0 && !mySelectionVisibility && myCaretLine == line) {
			myPaint.setColor(myCaretLineColor);
			canvas.drawRect(x, y - myFontTop, myRect.right, y + myFontBottom, myPaint);
		}
	}

	protected void redrawCaret(Canvas canvas, int line, int length, float x, float y) {
		if (myCaretColor != 0 && !mySelectionVisibility && myCaretLine == line) {
			if (!myCaretVisibility) return;
			boolean showing = true;
			if (myCaret.isBlinks()) showing = myCaret.isShowing();

			if (showing) {
				myPaint.setColor(myCaretColor);
				float caretX = x;
				float density = getContext().getResources().getDisplayMetrics().density;
				if (myCaretColumn > 0) caretX += measureLine(line, 0, myCaretColumn);

				canvas.drawRect(caretX, y - myFontTop, caretX + density, y + myFontBottom, myPaint);
			}
		}
	}

	protected void redrawSelection(Canvas canvas, int line, int length, float x, float y) {
		if (!mySelectionVisibility || mySelectionColor == 0 || line < myFirstSelectedLine || line > myLastSelectedLine)
			return;
		myPaint.setColor(mySelectionColor);
		float spaceWidth = mySpaceCharWidth;
		if (myFirstSelectedLine == myLastSelectedLine) {
			float firstX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myFirstSelectedColumn);
			float lastX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myLastSelectedColumn);
			canvas.drawRect(firstX, y - myFontTop, lastX, y + myFontBottom, myPaint);
		} else {
			if (line == myFirstSelectedLine) {
				float width = measureLine(line, 0, length);
				float cX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myFirstSelectedColumn);
				canvas.drawRect(cX, y - myFontTop, x + width, y + myFontBottom, myPaint);
			} else if (line == myLastSelectedLine) {
				float cX = length == 0 ? x + spaceWidth : x + measureLine(line, 0, myLastSelectedColumn);
				canvas.drawRect(x, y - myFontTop, cX, y + myFontBottom, myPaint);
			} else {
				float width = length==0? spaceWidth: measureLine(line, 0, length);
				canvas.drawRect(x, y - myFontTop, x + width, y + myFontBottom, myPaint);
			}
		}
	}

	protected void redrawText(Canvas canvas, int line, int length, float x, float y) {
		if (length == 0) return;

		int lastStyle = getModel().getStyle(line, 0);
		TextStyle textStyle = myStyles[lastStyle];
		if (textStyle != null) applyFontStyle(textStyle.fontStyle);
		else applyFontStyle(Typeface.NORMAL);

		int column = 0;
		for (int i = 0; i < length; i++) {
			int style = getModel().getStyle(line, i);
			if (style != lastStyle) {
				if (textStyle != null) {
					if (textStyle.backgroundColor != 0) {
						myPaint.setColor(textStyle.backgroundColor);
						float advance = nativeMeasureLine(line, column, i - column);
						canvas.drawRect(x, y - myFontTop, x + advance, y + myFontBottom, myPaint);
					}
					myPaint.setColor(textStyle.fontColor);
				} else {
					myPaint.setColor(myFontColor);
				}

				x = drawText(canvas, line, column, i - column, x, y);
				column = i;
				lastStyle = style;
				textStyle = myStyles[lastStyle];
				if (textStyle != null) applyFontStyle(textStyle.fontStyle);
				else applyFontStyle(Typeface.NORMAL);
			}
		}

		if (column < length) {
			if (textStyle != null) {
				applyFontStyle(textStyle.fontStyle);
				myPaint.setColor(textStyle.fontColor);
			} else {
				applyFontStyle(Typeface.NORMAL);
				myPaint.setColor(myFontColor);
			}
			drawText(canvas, line, column, length - column, x, y);
		}

		applyFontStyle(Typeface.NORMAL);
		myPaint.setColor(myFontColor);
	}

	protected void applyFontStyle(int fontStyle) {
		switch (fontStyle) {
			case Typeface.BOLD:
				myPaint.setFakeBoldText(true);
				break;
			case Typeface.BOLD_ITALIC:
				myPaint.setFakeBoldText(true);
				myPaint.setTextSkewX(-0.25f);
				break;
			case Typeface.ITALIC:
				myPaint.setTextSkewX(-0.25f);
				break;
			default:
				myPaint.setFakeBoldText(false);
				myPaint.setTextSkewX(0);
				break;
		}
	}


	protected void log(String msg) {
		Log.i(TAG, msg);
	}
}
