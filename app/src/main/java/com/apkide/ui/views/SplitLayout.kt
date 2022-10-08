package com.apkide.ui.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Keep

@Keep
class SplitLayout : ViewGroup {
	private val dividerSpeed: Float
	private val dividerTouchSize: Int
	private val horizontalSplitRatio: Float
	private val verticalSplitRatio: Float
	private var animationPosition = 0
	private var dragPosition = 0
	private var isDragging: Boolean
	var isHorizontal: Boolean
		private set
	var isSplit: Boolean
		private set
	private var isSwipeEnabled: Boolean
	private var listener: OnSplitChangeListener? = null
	
	constructor(context: Context?) : super(context) {
		horizontalSplitRatio = 0.33f
		verticalSplitRatio = 0.5f
		dividerTouchSize = 30
		dividerSpeed = 1.0f
		isSplit = false
		isHorizontal = false
		isSwipeEnabled = false
		isDragging = false
	}
	
	constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
		horizontalSplitRatio = 0.33f
		verticalSplitRatio = 0.5f
		dividerTouchSize = 30
		dividerSpeed = 1.0f
		isSplit = false
		isHorizontal = false
		isSwipeEnabled = false
		isDragging = false
	}
	
	constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
		context,
		attributeSet,
		i
	) {
		horizontalSplitRatio = 0.33f
		verticalSplitRatio = 0.5f
		dividerTouchSize = 30
		dividerSpeed = 1.0f
		isSplit = false
		isHorizontal = false
		isSwipeEnabled = false
		isDragging = false
	}
	
	private fun getAnimationDuration(drgPos: Int, dividerPos: Int): Long {
		return (Math.abs(drgPos - dividerPos) / resources.displayMetrics.density / dividerSpeed).toLong()
	}
	
	private fun getCurrentDividerPosition(w: Int, h: Int): Int {
		if (isDragging) return getDragPosition()
		return if (animationPosition < 0) getDividerPosition(w, h) else animationPosition
	}
	
	private val dividerPosition: Int
		private get() = getDividerPosition(width, height)
	private val dragDistance: Int
		private get() = if (isSplit) dividerPosition - getDragPosition() else getDragPosition()
	
	private fun getDragPosition(): Int {
		if (isDragging) return Math.min(dragPosition, dividerPosition)
		return if (!isSplit) 0 else dividerPosition
	}
	
	private fun getDragStartOrientation(event: MotionEvent): Boolean? {
		if (isSwipeEnabled && !isSplit && event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1 && event.x < dividerTouchSize * resources.displayMetrics.density && splitHorizontalByDefault()) {
			return java.lang.Boolean.TRUE
		}
		if (isSwipeEnabled && isSplit && isHorizontal && event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1 && event.y > resources.displayMetrics.density * 50.0f && event.y < height - resources.displayMetrics.density * 50.0f && Math.abs(
				event.x - width * horizontalSplitRatio
			) < dividerTouchSize * resources.displayMetrics.density
		) {
			return java.lang.Boolean.TRUE
		}
		if (isSwipeEnabled && !isSplit && event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1 && event.y > height - dividerTouchSize * resources.displayMetrics.density && height > resources.displayMetrics.density * 150.0f) {
			return java.lang.Boolean.FALSE
		}
		return if (isSwipeEnabled && isSplit && !isHorizontal && event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1 && event.x < width - resources.displayMetrics.density * 50.0f && event.x > resources.displayMetrics.density * 50.0f && Math.abs(
				event.y - height * verticalSplitRatio
			) < dividerTouchSize * resources.displayMetrics.density
		) {
			java.lang.Boolean.FALSE
		} else null
	}
	
	private val isVertical: Boolean
		private get() = !isHorizontal
	
	private fun setOrientation(isHorizontal: Boolean, anim: Boolean) {
		if (this.isHorizontal == isHorizontal) return
		if (anim && isSplit) {
			closeSplit(true) { openSplit(isHorizontal, true) }
			return
		}
		this.isHorizontal = isHorizontal
		requestLayout()
		if (listener == null) return
		listener!!.onSplitChanged(isSplit)
	}
	
	private fun startDragging(event: MotionEvent, isHorizontal: Boolean) {
		this.isHorizontal = isHorizontal
		isDragging = true
		updateDragging(event)
		updateChildVisibilities()
	}
	
	private fun stopDragging() {
		if (isSplit == dragDistance.toFloat() > (dividerTouchSize * 2).toFloat() * resources.displayMetrics.density) {
			closeSplit(true)
		} else openSplit(true)
	}
	
	@SuppressLint("WrongConstant")
	fun updateChildVisibilities() {
		var bottomVisibility: Int
		val bottomView = bottomView
		var separatorVisibility = GONE
		if (!isSplit && !isDragging) {
			bottomVisibility = GONE
			bottomView.visibility = bottomVisibility
			val separatorView = separatorView
			if (!isSplit || isDragging) {
				separatorVisibility = VISIBLE
			}
			separatorView.visibility = separatorVisibility
		}
		bottomVisibility = VISIBLE
		bottomView.visibility = bottomVisibility
		val separatorView2 = separatorView
		if (!isSplit) return
		separatorVisibility = VISIBLE
		separatorView2.visibility = separatorVisibility
	}
	
	private fun updateDragging(motionEvent: MotionEvent) {
		dragPosition = (if (isHorizontal) motionEvent.x else height - motionEvent.y).toInt()
		requestLayout()
	}
	
	fun closeSplit(z: Boolean) {
		closeSplit(z, null)
	}
	
	val bottomView: View
		get() = getChildAt(2)
	val separatorView: View
		get() = getChildAt(1)
	val topView: View
		get() = getChildAt(0)
	
	override fun onFinishInflate() {
		super.onFinishInflate()
		updateChildVisibilities()
		isHorizontal = splitHorizontalByDefault()
	}
	
	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		return if (getDragStartOrientation(event) == null) super.onInterceptTouchEvent(event) else true
	}
	
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		val topView = topView
		val bottomView = bottomView
		val separatorView = separatorView
		val viewRight = r - l
		val viewBottom = b - t
		if (bottomView.visibility != GONE) {
			if (isHorizontal) {
				val bottomViewMeasuredWidth = bottomView.measuredWidth
				val measuredWidth = separatorView.measuredWidth + bottomViewMeasuredWidth
				topView.layout(measuredWidth, 0, viewRight, viewBottom)
				separatorView.layout(bottomViewMeasuredWidth, 0, measuredWidth, viewBottom)
				bottomView.layout(0, 0, bottomViewMeasuredWidth, viewBottom)
				return
			}
			val topViewMeasuredHeight = topView.measuredHeight
			val separatorViewMeasuredHeight = separatorView.measuredHeight
			topView.layout(0, 0, viewRight, topViewMeasuredHeight)
			val viewTop = separatorViewMeasuredHeight + topViewMeasuredHeight
			separatorView.layout(0, topViewMeasuredHeight, viewRight, viewTop)
			bottomView.layout(0, viewTop, viewRight, viewBottom)
			return
		}
		topView.layout(0, 0, viewRight, viewBottom)
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val topView = topView
		val bottomView = bottomView
		val separatorView = separatorView
		MeasureSpec.getMode(heightMeasureSpec)
		val hSize = MeasureSpec.getSize(heightMeasureSpec)
		MeasureSpec.getMode(widthMeasureSpec)
		val wSize = MeasureSpec.getSize(widthMeasureSpec)
		if (bottomView.visibility != GONE) {
			if (isHorizontal) {
				bottomView.measure(
					MeasureSpec.makeMeasureSpec(
						getCurrentDividerPosition(
							wSize,
							hSize
						), MeasureSpec.EXACTLY
					), heightMeasureSpec
				)
				topView.measure(
					MeasureSpec.makeMeasureSpec(
						wSize - bottomView.measuredWidth,
						MeasureSpec.EXACTLY
					), heightMeasureSpec
				)
				separatorView.measure(-2, heightMeasureSpec)
			} else {
				bottomView.measure(
					widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(
						getCurrentDividerPosition(wSize, hSize),
						MeasureSpec.EXACTLY
					)
				)
				topView.measure(
					widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(
						hSize - bottomView.measuredHeight,
						MeasureSpec.EXACTLY
					)
				)
				separatorView.measure(widthMeasureSpec, -2)
			}
		} else topView.measure(widthMeasureSpec, heightMeasureSpec)
		setMeasuredDimension(wSize, hSize)
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		val dragStartOrientation = getDragStartOrientation(event)
		return if (dragStartOrientation != null) {
			startDragging(event, dragStartOrientation)
			true
		} else if (isDragging) {
			if (event.action == MotionEvent.ACTION_MOVE) updateDragging(event)
			if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) stopDragging()
			true
		} else super.onTouchEvent(event)
	}
	
	fun openSplit(anim: Boolean) {
		openSplit(isHorizontal, anim)
	}
	
	@Keep
	fun setAnimationPosition(animationPosition: Int) {
		this.animationPosition = animationPosition
		requestLayout()
	}
	
	fun setOnSplitChangeListener(listener: OnSplitChangeListener?) {
		this.listener = listener
	}
	
	fun setSwipeEnabled(swipeEnabled: Boolean) {
		isSwipeEnabled = swipeEnabled
	}
	
	fun splitHorizontalByDefault(): Boolean {
		val width = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
			.defaultDisplay.width / context.resources.displayMetrics.density
		return width >= 650.0f
	}
	
	fun toggleSplit(runnable: Runnable?) {
		if (isSplit && isVertical) closeSplit(
			true,
			runnable
		) else if (isSplit && isHorizontal) setOrientation(false, true) else openSplit(
			splitHorizontalByDefault(),
			true
		)
	}
	
	private fun getDividerPosition(w: Int, h: Int): Int {
		return if (isHorizontal) (horizontalSplitRatio * w).toInt() else (verticalSplitRatio * h).toInt()
	}
	
	fun closeSplit(anim: Boolean, runnable: Runnable?) {
		if (anim) {
			if (isSplit || isDragging) {
				val ofInt = ObjectAnimator.ofInt(this, "animationPosition", getDragPosition(), 0)
				ofInt.duration = getAnimationDuration(getDragPosition(), 0)
				ofInt.start()
				ofInt.addListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						isSplit = false
						updateChildVisibilities()
						if (listener != null) listener!!.onSplitChanged(isSplit)
						if (runnable == null) return
						runnable.run()
					}
				})
			}
		} else if (isSplit) {
			isSplit = false
			updateChildVisibilities()
			if (listener != null) listener!!.onSplitChanged(isSplit)
		}
		isDragging = false
	}
	
	fun openSplit(isHorizontal: Boolean, anim: Boolean) {
		this.isHorizontal = isHorizontal
		if (anim) {
			if (!isSplit || isDragging) {
				val ofInt = ObjectAnimator.ofInt(
					this,
					"animationPosition",
					getDragPosition(),
					dividerPosition
				)
				ofInt.duration = getAnimationDuration(getDragPosition(), dividerPosition)
				ofInt.start()
				ofInt.addListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						animationPosition = -1
						postDelayed({
							if (listener == null) return@postDelayed
							listener!!.onSplitChanged(isSplit)
						}, 50L)
					}
				})
				isSplit = true
				updateChildVisibilities()
			}
		} else if (!isSplit) {
			isSplit = true
			animationPosition = -1
			updateChildVisibilities()
			if (listener != null) listener!!.onSplitChanged(isSplit)
		}
		isDragging = false
	}
	
	interface OnSplitChangeListener {
		fun onSplitChanged(isSplit: Boolean)
	}
}