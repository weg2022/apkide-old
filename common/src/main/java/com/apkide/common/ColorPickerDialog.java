package com.apkide.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class ColorPickerDialog extends MessageBox {
	private int newColor;
	private String newHexColor;
	private final ColorPickRunnable ok;
	private final String oldHexColor;
	private final String title;
	private boolean updatingColorPicker;
	private boolean updatingEditText;

	public ColorPickerDialog(String title, String hexColor, ColorPickRunnable ok) {
		if (hexColor == null) {
			this.oldHexColor = "#000000";
		} else {
			this.oldHexColor = hexColor;
		}
		this.newHexColor = this.oldHexColor;
		this.newColor = ColorPickerView.parseColor(this.newHexColor);
		this.ok = ok;
		this.title = title;
	}

	public ColorPickerDialog(String title, int color, ColorPickRunnable runnable) {
		this.oldHexColor = ColorPickerView.toHexColor(color);
		this.newHexColor = this.oldHexColor;
		this.newColor = color;
		this.ok = runnable;
		this.title = title;
	}

	@Override
	protected Dialog buildDialog(Activity context) {
		View view= context.getLayoutInflater().inflate(R.layout.dialog_colorpicker,null);
		ColorPickerView colorPickerView =view.findViewById(R.id.colorPickerView);
		EditText editText = view.findViewById(R.id.editText);
		colorPickerView.setOnColorChangedListener((color, hexColor) -> {
			if (!updatingEditText) {
				updatingColorPicker = true;
				editText.setText(hexColor);
				updatingColorPicker = false;
			}
			newHexColor = hexColor;
			newColor = color;
		});
		colorPickerView.setInitialColor(this.oldHexColor);
		colorPickerView.setCurrentColor(this.oldHexColor);
		editText.setText(this.oldHexColor);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence sequence, int start, int before, int count) {
				if (!updatingColorPicker) {
					updatingEditText = true;
					colorPickerView.setCurrentColor(sequence.toString());
					updatingEditText = false;
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view)
				.setCancelable(true)
				.setPositiveButton(android.R.string.ok, (dialog, id) -> {
					dialog.dismiss();
					ok.run(newColor, newHexColor);
				})
				.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
		builder.setTitle(this.title);
		AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(2);
		return dialog;
	}

	public interface ColorPickRunnable {
		void run(int color, String hexColor);
	}
}
