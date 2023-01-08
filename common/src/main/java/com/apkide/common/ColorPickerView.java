package com.apkide.common;

import static android.graphics.Color.alpha;
import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.colorToHSV;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;
import static java.lang.Long.*;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {
	private final int[] hueBarColors;
	private int currentColor;
	private float currentHue;
	private int currentX;
	private int currentY;
	private final int[] derivedColors;
	private int initialColor;
	private ColorChangedListener listener;
	private Paint paint;
	private float scale;

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.currentHue = 0.0f;
		this.currentX = 0;
		this.currentY = 0;
		this.hueBarColors = new int[258];
		this.derivedColors = new int[65536];
		init();
	}

	public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.currentHue = 0.0f;
		this.currentX = 0;
		this.currentY = 0;
		this.hueBarColors = new int[258];
		this.derivedColors = new int[65536];
		init();
	}

	public ColorPickerView(Context c) {
		super(c);
		this.currentHue = 0.0f;
		this.currentX = 0;
		this.currentY = 0;
		this.hueBarColors = new int[258];
		this.derivedColors = new int[65536];
		init();
	}

	public static String toHexColor(int c) {
		if (alpha(c) == 255) {
			return format("#%06X", 16777215 & c);
		}
		return format("#%08X", c);
	}

	public static int parseColor(String hexColor) {
		if (hexColor != null && hexColor.length() == 9 && hexColor.startsWith("#")) {
			return (int) parseLong(hexColor.substring(1), 16);
		}
		if (hexColor == null || hexColor.length() != 7 || !hexColor.startsWith("#")) {
			return 0xff000000;
		}
		return -16777216 | ((int) parseLong(hexColor.substring(1), 16));
	}

	private void init() {
		updateMainColors();
		Context c = getContext();
		DisplayMetrics displayMetrics=c.getResources().getDisplayMetrics();
		int screenSize= min(displayMetrics.widthPixels/displayMetrics.densityDpi,displayMetrics.heightPixels/displayMetrics.densityDpi);
		this.scale = displayMetrics.density * (min(300.0f, screenSize - 150.0f) / 256.0f);
		this.paint = new Paint(1);
		this.paint.setTextAlign(Paint.Align.CENTER);
		this.paint.setTextSize(12.0f);
	}

	public void setCurrentColor(String hexColor) {
		this.currentColor = parseColor(hexColor);
		float[] hsv = new float[3];
		colorToHSV(this.currentColor | 0xff000000, hsv);
		this.currentHue = hsv[0];
		updateDerivedColors();
		getCurrentXY();
		invalidate();
		this.listener.colorChanged(this.currentColor, toHexColor(this.currentColor));
	}

	public void setInitialColor(String hexColor) {
		this.initialColor = parseColor(hexColor);
	}

	public void setOnColorChangedListener(ColorChangedListener l) {
		this.listener = l;
	}

	private void updateMainColors() {
		int index = 0;
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			this.hueBarColors[index] = rgb(255, 0, (int) i);
			index++;
		}
		for (float i2 = 0.0f; i2 < 256.0f; i2 += 6.0f) {
			this.hueBarColors[index] = rgb(255 - ((int) i2), 0, 255);
			index++;
		}
		for (float i3 = 0.0f; i3 < 256.0f; i3 += 6.0f) {
			this.hueBarColors[index] = rgb(0, (int) i3, 255);
			index++;
		}
		for (float i4 = 0.0f; i4 < 256.0f; i4 += 6.0f) {
			this.hueBarColors[index] = rgb(0, 255, 255 - ((int) i4));
			index++;
		}
		for (float i5 = 0.0f; i5 < 256.0f; i5 += 6.0f) {
			this.hueBarColors[index] = rgb((int) i5, 255, 0);
			index++;
		}
		for (float i6 = 0.0f; i6 < 256.0f; i6 += 6.0f) {
			this.hueBarColors[index] = rgb(255, 255 - ((int) i6), 0);
			index++;
		}
	}

	private void getCurrentXY() {
		int minDist = 1000;
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				int dist = getColorDist(getDerivedColorForXY(x, y), this.currentColor | 0xff000000);
				if (dist == 0) {
					this.currentX = x;
					this.currentY = y;
					return;
				}
				if (dist < minDist) {
					this.currentX = x;
					this.currentY = y;
					minDist = dist;
				}
			}
		}
	}

	private int getColorDist(int c1, int c2) {
		return abs(red(c1) - red(c2)) + abs(green(c1) - green(c2)) + abs(blue(c1) - blue(c2));
	}

	private int getCurrentMainColor() {
		int translatedHue = 255 - ((int) ((this.currentHue * 255.0f) / 360.0f));
		int index = 0;
		for (float i = 0.0f; i < 256.0f; i += 6.0f) {
			if (index == translatedHue) {
				return rgb(255, 0, (int) i);
			}
			index++;
		}
		for (float i2 = 0.0f; i2 < 256.0f; i2 += 6.0f) {
			if (index == translatedHue) {
				return rgb(255 - ((int) i2), 0, 255);
			}
			index++;
		}
		for (float i3 = 0.0f; i3 < 256.0f; i3 += 6.0f) {
			if (index == translatedHue) {
				return rgb(0, (int) i3, 255);
			}
			index++;
		}
		for (float i4 = 0.0f; i4 < 256.0f; i4 += 6.0f) {
			if (index == translatedHue) {
				return rgb(0, 255, 255 - ((int) i4));
			}
			index++;
		}
		for (float i5 = 0.0f; i5 < 256.0f; i5 += 6.0f) {
			if (index == translatedHue) {
				return rgb((int) i5, 255, 0);
			}
			index++;
		}
		for (float i6 = 0.0f; i6 < 256.0f; i6 += 6.0f) {
			if (index == translatedHue) {
				return rgb(255, 255 - ((int) i6), 0);
			}
			index++;
		}
		return 0xffff0000;
	}

	private void updateDerivedColors() {
		int mainColor = getCurrentMainColor();
		int index = 0;
		int[] topColors = new int[256];
		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++) {
				if (y == 0) {
					this.derivedColors[index] = rgb(255 - (((255 - red(mainColor)) * x) / 0xff), 255 - (((255 - green(mainColor)) * x) / 0xff), 255 - (((255 - blue(mainColor)) * x) / 0xff));
					topColors[x] = this.derivedColors[index];
				} else {
					this.derivedColors[index] = rgb(((255 - y) * red(topColors[x])) / 0xff, ((255 - y) * green(topColors[x])) / 0xff, ((255 - y) * blue(topColors[x])) / 0xff);
				}
				index++;
			}
		}
	}

	private int getDerivedColorForXY(int x, int y) {
		int index = (y * 256) + x;
		if (index < 0 || index >= this.derivedColors.length) {
			return 0;
		}
		return this.derivedColors[index];
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		int translatedHue = 255 - ((int) ((this.currentHue * 255.0f) / 360.0f));
		for (int i = 0; i < 256; i++) {
			this.paint.setStrokeWidth(2.0f * this.scale);
			this.paint.setColor(this.hueBarColors[i]);
			canvas.drawLine(((float) (i + 10)) * this.scale, 0.0f, ((float) (i + 10)) * this.scale, 40.0f * this.scale, this.paint);
		}
		this.paint.setStrokeWidth(3.0f * this.scale);
		this.paint.setColor(0xff000000);
		canvas.drawLine(((float) (translatedHue + 10)) * this.scale, 0.0f, ((float) (translatedHue + 10)) * this.scale, 40.0f * this.scale, this.paint);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setColor(0xff000000);
		this.paint.setStrokeWidth(1.0f);
		canvas.drawRect(9.5f * this.scale, 0.0f * this.scale, 266.5f * this.scale, 40.0f * this.scale, this.paint);
		for (int i2 = 0; i2 < 256; i2++) {
			this.paint.setShader(new LinearGradient(0.0f, 50.0f * this.scale, 0.0f, 306.0f * this.scale, new int[]{this.derivedColors[i2], 0xff000000}, (float[]) null, Shader.TileMode.REPEAT));
			this.paint.setStrokeWidth(2.0f * this.scale);
			canvas.drawLine(((float) (i2 + 10)) * this.scale, 50.0f * this.scale, ((float) (i2 + 10)) * this.scale, 306.0f * this.scale, this.paint);
		}
		this.paint.setShader(null);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setColor(0xff000000);
		this.paint.setStrokeWidth(2.0f * this.scale);
		canvas.drawCircle(((float) (this.currentX + 10)) * this.scale, ((float) (this.currentY + 50)) * this.scale, 6.0f * this.scale, this.paint);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setColor(0xff000000);
		this.paint.setStrokeWidth(1.0f);
		canvas.drawRect(9.5f * this.scale, 50.0f * this.scale, 266.5f * this.scale, 306.0f * this.scale, this.paint);
		this.paint.setStyle(Paint.Style.FILL);
		this.paint.setColor(this.currentColor | 0xff000000);
		canvas.drawRect(9.5f * this.scale, 316.0f * this.scale, 139.0f * this.scale, 356.0f * this.scale, this.paint);
		this.paint.setStyle(Paint.Style.FILL);
		this.paint.setColor(this.initialColor | 0xff000000);
		canvas.drawRect(138.0f * this.scale, 316.0f * this.scale, 266.5f * this.scale, 356.0f * this.scale, this.paint);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setColor(0xff000000);
		this.paint.setStrokeWidth(1.0f);
		canvas.drawRect(9.5f * this.scale, 316.0f * this.scale, 266.5f * this.scale, 356.0f * this.scale, this.paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) (276.0f * this.scale), (int) (366.0f * this.scale));
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == 0 || event.getAction() == 2) {
			float x = (event.getX() / this.scale) - 10.0f;
			if (x < 0.0f) {
				x = 0.0f;
			}
			if (x > 255.0f) {
				x = 255.0f;
			}
			float y = event.getY() / this.scale;
			if (y < 40.0f) {
				this.currentHue = ((255.0f - x) * 360.0f) / 255.0f;
				updateDerivedColors();
			} else {
				this.currentX = (int) x;
				this.currentY = ((int) y) - 50;
				if (this.currentY < 0) {
					this.currentY = 0;
				}
				if (this.currentY > 255) {
					this.currentY = 0xff;
				}
			}
			int color = getDerivedColorForXY(this.currentX, this.currentY);
			this.currentColor = argb(alpha(this.currentColor), red(color), green(color), blue(color));
			this.listener.colorChanged(this.currentColor, toHexColor(this.currentColor));
			invalidate();
		}
		return true;
	}

	public interface ColorChangedListener {
		void colorChanged(int color, String hexColor);
	}
}
