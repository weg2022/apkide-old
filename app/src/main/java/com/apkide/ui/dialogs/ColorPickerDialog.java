package com.apkide.ui.dialogs;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
import static java.util.Objects.requireNonNullElse;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.apkide.ui.util.MessageBox;
import com.apkide.ui.views.ColorPickerView;


public class ColorPickerDialog extends MessageBox {

	private int myNewColor;
	private String myNewHexColor;
	private final ColorRunnable myColorRunnable;
	private final String myOldHexColor;
	private final String myTitle;
	private boolean myUpdatingColorPicker;
	private boolean myUpdatingEditText;

	public interface ColorRunnable {
		void run(int color, @NonNull String hexColor);
	}

	public ColorPickerDialog(@NonNull String title, String hexColor, @NonNull ColorRunnable ok) {
		myOldHexColor = requireNonNullElse(hexColor, "#000000");
		myNewHexColor = myOldHexColor;
		myNewColor = ColorPickerView.parseColor(myNewHexColor);
		myColorRunnable = ok;
		myTitle = title;
	}

	public ColorPickerDialog(@NonNull String title, int color, @NonNull ColorRunnable ok) {
		myOldHexColor = ColorPickerView.toHexColor(color);
		myNewHexColor = myOldHexColor;
		myNewColor = color;
		myColorRunnable = ok;
		myTitle = title;
	}

	@NonNull
	@Override
	protected Dialog buildDialog(@NonNull Activity activity) {
		LinearLayoutCompat layout = new LinearLayoutCompat(activity);
		layout.setOrientation(LinearLayoutCompat.VERTICAL);
		ColorPickerView colorPickerView = new ColorPickerView(activity);
		layout.addView(colorPickerView);
		EditText editText = new EditText(activity);
		editText.setEms(10);
		layout.addView(editText);
		colorPickerView.setOnColorChangedListener((color, hexColor) -> {
			if (!myUpdatingEditText) {
				myUpdatingColorPicker = true;
				editText.setText(hexColor);
				myUpdatingColorPicker = false;
			}
			myNewHexColor = hexColor;
			myNewColor = color;
		});
		colorPickerView.setInitialColor(myOldHexColor);
		colorPickerView.setCurrentColor(myOldHexColor);
		editText.setText(myOldHexColor);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!myUpdatingColorPicker) {
					myUpdatingEditText = true;
					colorPickerView.setCurrentColor(s.toString());
					myUpdatingEditText = false;
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(layout)
				.setCancelable(true)
				.setPositiveButton(android.R.string.ok, (dialog, id) -> {
					dialog.dismiss();
					myColorRunnable.run(myNewColor, myNewHexColor);
				})
				.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
		if (myTitle != null)
			builder.setTitle(myTitle);

		builder.setCancelable(false);

		AlertDialog dialog = builder.create();
		if (dialog.getWindow() != null)
			dialog.getWindow().setSoftInputMode(SOFT_INPUT_STATE_HIDDEN);

		return dialog;

	}
}
