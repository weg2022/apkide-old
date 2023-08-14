package com.apkide.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class ColorPickerView extends View {
	private int currentColor;
	private float currentHue;
	private int currentX;
	private int currentY;
	private int[] derivedColors;
	private int[] hueBarColors;
	private int initialColor;
	private OnColorChangedListener listener;
	private Paint paint;
	private float scale;

	public interface OnColorChangedListener {
		void onColorChanged(int color, @NonNull String hexColor);
	}

	public ColorPickerView(Context context) {
		super(context);
		initView();
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		this.currentHue = 0.0f;
		this.currentX = 0;
		this.currentY = 0;
		this.hueBarColors = new int[258];
		this.derivedColors = new int[65536];
		updateMainColors();
		this.scale = getContext().getResources().getDisplayMetrics().density * (300.0f / 256.0f);
		this.paint = new Paint(1);
		this.paint.setTextAlign(Paint.Align.CENTER);
		this.paint.setTextSize(12.0f);
	}

	public void setCurrentColor(@NonNull String hexColor) {
		currentColor = parseColor(hexColor);
		float[] hsv = new float[3];
		Color.colorToHSV(currentColor | 0xff000000, hsv);
		currentHue = hsv[0];
		updateDerivedColors();
		getCurrentXY();
		invalidate();
		if (listener != null)
			listener.onColorChanged(currentColor, toHexColor(currentColor));
	}

	public void setInitialColor(@NonNull String hexColor) {
		this.initialColor = parseColor(hexColor);
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		this.listener = listener;
	}

	private void updateMainColors() {
		int index = 0;
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb(255, 0, (int) i);
			index++;
		}

		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb(255 - ((int) i), 0, 255);
			index++;
		}

		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb(0, (int) i, 255);
			index++;
		}

		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb(0, 255, 255 - ((int) i));
			index++;
		}

		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb((int) i, 255, 0);
			index++;
		}

		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			hueBarColors[index] = Color.rgb(255, 255 - ((int) i), 0);
			index++;
		}
	}

	private void getCurrentXY() {
		int minDist = 1000;
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				int dist = getColorDist(getDerivedColorForXY(x, y), currentColor | 0xff000000);
				if (dist == 0) {
					currentX = x;
					currentY = y;
					return;
				}
				if (dist < minDist) {
					currentX = x;
					currentY = y;
					minDist = dist;
				}
			}
		}
	}

	private int getColorDist(int c1, int c2) {
		return Math.abs(Color.red(c1) - Color.red(c2)) +
				Math.abs(Color.green(c1) - Color.green(c2)) +
				Math.abs(Color.blue(c1) - Color.blue(c2));
	}

	private int getCurrentMainColor() {
		int translatedHue = 255 - ((int) ((currentHue * 255.0f) / 360.0f));
		int index = 0;
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb(255, 0, (int) i);
			}
			index++;
		}
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb(255 - ((int) i), 0, 255);
			}
			index++;
		}
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb(0, (int) i, 255);
			}
			index++;
		}
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb(0, 255, 255 - ((int) i));
			}
			index++;
		}
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb((int) i, 255, 0);
			}
			index++;
		}
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return Color.rgb(255, 255 - ((int) i), 0);
			}
			index++;
		}
		return -65536;
	}

	private void updateDerivedColors() {
		int mainColor = getCurrentMainColor();
		int index = 0;
		int[] topColors = new int[256];
		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++) {
				if (y == 0) {
					derivedColors[index] = Color.rgb(
							255 - (((255 - Color.red(mainColor)) * x) / 255),
							255 - (((255 - Color.green(mainColor)) * x) / 255),
							255 - (((255 - Color.blue(mainColor)) * x) / 255));
					topColors[x] = derivedColors[index];
				} else {
					derivedColors[index] = Color.rgb(
							((255 - y) * Color.red(topColors[x])) / 255,
							((255 - y) * Color.green(topColors[x])) / 255,
							((255 - y) * Color.blue(topColors[x])) / 255);
				}
				index++;
			}
		}
	}

	private int getDerivedColorForXY(int x, int y) {
		int index = (y * 256) + x;
		if (index < 0 || index >= derivedColors.length) {
			return 0;
		}
		return derivedColors[index];
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(@NonNull Canvas canvas) {

		int translatedHue = 255 - ((int) ((currentHue * 255.0f) / 360.0f));
		for (int i = 0; i < 256; i++) {
			paint.setStrokeWidth(2.0f * scale);
			paint.setColor(hueBarColors[i]);
			canvas.drawLine((i + 10) * scale, 0.0f,
					(i + 10) * scale, 40.0f * scale, paint);
		}
		paint.setStrokeWidth(3.0f * scale);
		paint.setColor(0xff000000);
		canvas.drawLine((translatedHue + 10) * scale, 0.0f,
				(translatedHue + 10) * scale, 40.0f * scale, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xff000000);
		paint.setStrokeWidth(1.0f);
		canvas.drawRect(9.5f * scale, 0.0f * scale,
				266.5f * scale, 40.0f * scale, paint);

		for (int i = 0; i < 256; i++) {
			int[] colors = {derivedColors[i], 0xff000000};
			Shader shader = new LinearGradient(0.0f, 50.0f * scale,
					0.0f, 306.0f * scale, colors, null, Shader.TileMode.REPEAT);
			paint.setShader(shader);
			paint.setStrokeWidth(2.0f * scale);
			canvas.drawLine((i + 10) * scale,
					50.0f * scale, (i + 10) * scale,
					306.0f * scale, paint);
		}

		paint.setShader(null);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xff000000);
		paint.setStrokeWidth(2.0f * scale);
		canvas.drawCircle((currentX + 10) * scale,
				(currentY + 50) * scale, 6.0f * scale, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xff000000);
		paint.setStrokeWidth(1.0f);

		float top = 50.0f * scale;
		float right = 266.5f * scale;
		float bottom = 306.0f * scale;
		canvas.drawRect(9.5f * scale, top, right, bottom, paint);

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(currentColor | 0xff000000);
		top = 316.0f * scale;
		right = 139.0f * scale;
		bottom = 356.0f * scale;
		canvas.drawRect(9.5f * scale, top, right, bottom, paint);

		right = 266.5f * scale;
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(initialColor | 0xff000000);
		canvas.drawRect(138.0f * scale, top, right, bottom, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xff000000);
		paint.setStrokeWidth(1.0f);
		canvas.drawRect(9.5f * scale, top, right, bottom, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) (276.0f * scale), (int) (366.0f * scale));
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_MOVE) {

			float x = (event.getX() / scale) - 10.0f;
			x = Math.max(0.0f, Math.min(x, 255.0f));
			float y = event.getY() / scale;
			if (y < 40.0f) {
				currentHue = ((255.0f - x) * 360.0f) / 255.0f;
				updateDerivedColors();
			} else {
				currentX = (int) x;
				currentY = ((int) y) - 50;
				currentY = Math.max(0, Math.min(255, currentY));
			}
			int color = getDerivedColorForXY(currentX, currentY);
			currentColor = Color.argb(
					Color.alpha(currentColor),
					Color.red(color),
					Color.green(color),
					Color.blue(color));
			if (listener != null)
				listener.onColorChanged(currentColor, toHexColor(currentColor));
			invalidate();
		}
		return true;
	}

	public static String toHexColor(int color) {
		if (Color.alpha(color) == 255) {
			return String.format("#%06X", 16777215 & color);
		}
		return String.format("#%08X", color);
	}

	public static int parseColor(String hexColor) {
		if (hexColor != null && hexColor.length() == 9 && hexColor.startsWith("#")) {
			return (int) Long.parseLong(hexColor.substring(1), 16);
		}
		return (hexColor != null && hexColor.length() == 7 &&
				hexColor.startsWith("#")) ? (-16777216) |
				((int) Long.parseLong(hexColor.substring(1), 16)) : 0xff000000;
	}
}
