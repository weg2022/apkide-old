package com.apkide.ui.views;


import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class SplitLayout extends ViewGroup {

	private final int dividerTouchSize = 30;
	private final float verticalSplitRatio = 0.5f;
	private int dragPosition;
	private boolean isDragging;
	private boolean isSplit;
	private OnSplitChangeListener listener;

	public SplitLayout(Context context) {
		super(context);
		initView();
	}

	public SplitLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SplitLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {

	}


	private int getCurrentDividerPosition(int height) {
		return isDragging ? getDragPosition() : getDividerPosition(height);
	}

	private int getDividerPosition() {
		return getDividerPosition(getHeight());
	}

	private int getDragDistance() {
		return isSplit ? getDividerPosition() - getDragPosition() : getDragPosition();
	}

	private int getDragPosition() {
		return isDragging ? Math.min(dragPosition, getDividerPosition()) :
				isSplit ? getDividerPosition() : 0;
	}

	private boolean getDragStartOrientation(MotionEvent e) {
		return false;
	}

	private void startDragging(MotionEvent event) {
		isDragging = true;
		updateDragging(event);
		updateChildVisibilities();
	}

	private void stopDragging() {
		if (isSplit == (((float) getDragDistance()) >
				((float) (dividerTouchSize * 2)) *
						getResources().getDisplayMetrics().density)) {
			closeSplit();
		} else
			openSplit();
	}

	public void updateChildVisibilities() {
		boolean visible = !(!isSplit && !isDragging);
		getBottomView().setVisibility(visible ? VISIBLE : GONE);
		getSeparatorView().setVisibility(visible ? VISIBLE : GONE);
	}

	private void updateDragging(MotionEvent motionEvent) {
		dragPosition = (int) motionEvent.getX();
		requestLayout();
	}

	public void closeSplit() {
		closeSplit(null);
	}

	public View getBottomView() {
		return getChildAt(2);
	}

	public View getSeparatorView() {
		return getChildAt(1);
	}

	public View getTopView() {
		return getChildAt(0);
	}

	public boolean isSplit() {
		return isSplit;
	}

	public boolean isDragging() {
		return isDragging;
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		updateChildVisibilities();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
		if (getDragStartOrientation(motionEvent))
			return true;

		return super.onInterceptTouchEvent(motionEvent);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View topView = getTopView();
		View bottomView = getBottomView();
		View separatorView = getSeparatorView();
		int w = r - l;
		int h = b - t;
		if (bottomView.getVisibility() != GONE) {
			int measuredHeight = topView.getMeasuredHeight();
			int separatorHeight = separatorView.getMeasuredHeight();
			topView.layout(0, 0, w, measuredHeight);
			int mHeight = separatorHeight + measuredHeight;
			separatorView.layout(0, measuredHeight, w, mHeight);
			bottomView.layout(0, mHeight, w, h);
			return;
		}
		topView.layout(0, 0, w, h);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View topView = getTopView();
		View bottomView = getBottomView();
		View separatorView = getSeparatorView();
		int h = getSize(heightMeasureSpec);
		int w = getSize(widthMeasureSpec);
		if (bottomView.getVisibility() != GONE) {
			bottomView.measure(
					widthMeasureSpec,
					makeMeasureSpec(getCurrentDividerPosition(h), EXACTLY));
			topView.measure(
					widthMeasureSpec,
					makeMeasureSpec(h - bottomView.getMeasuredHeight(), EXACTLY));
			separatorView.measure(widthMeasureSpec, -2);
		} else {
			topView.measure(widthMeasureSpec, heightMeasureSpec);
		}
		setMeasuredDimension(w, h);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean dragStartOrientation = getDragStartOrientation(event);
		if (dragStartOrientation) {
			startDragging(event);
			return true;
		} else if (isDragging) {
			if (event.getAction() == MotionEvent.ACTION_MOVE)
				updateDragging(event);

			if (event.getAction() == MotionEvent.ACTION_CANCEL ||
					event.getAction() == MotionEvent.ACTION_UP) {
				stopDragging();
			}
			return true;
		} else
			return super.onTouchEvent(event);
	}


	public void setOnSplitChangeListener(OnSplitChangeListener listener) {
		this.listener = listener;
	}
	

	public void toggleSplit(Runnable closeRun) {
		if (isSplit())
			closeSplit(closeRun);
		else
			openSplit();
	}

	private int getDividerPosition(int height) {
		return (int) (verticalSplitRatio * height);
	}

	public void closeSplit(Runnable closeRun) {
		if (isSplit) {
			isSplit = false;
			updateChildVisibilities();
			if (listener != null)
				listener.onSplitChanged(isSplit);

			if (closeRun != null)
				closeRun.run();
		}
		isDragging = false;
	}

	public void openSplit() {
		if (!isSplit) {
			isSplit = true;
			updateChildVisibilities();
			if (listener != null)
				listener.onSplitChanged(isSplit);
		}
		isDragging = false;
	}


	public interface OnSplitChangeListener {

		void onSplitChanged(boolean isSplit);
	}
}
