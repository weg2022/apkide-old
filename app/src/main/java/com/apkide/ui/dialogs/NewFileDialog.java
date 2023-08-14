package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.common.MessageBox;
import com.apkide.ui.App;

import java.io.File;
import java.io.IOException;

public class NewFileDialog extends MessageBox {

	private final String filePath;
	private final boolean isDirectory;

	public NewFileDialog(@NonNull String filePath, boolean isDirectory) {
		this.filePath = filePath;
		this.isDirectory = isDirectory;
	}

	@NonNull
	@Override
	protected Dialog buildDialog(@NonNull Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("New " + (isDirectory ? "Directory" : "File"));
		EditText editText = new EditText(activity);
		editText.setHint((isDirectory ? "Directory" : "File")+" Name");
		editText.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
		builder.setView(editText);
		builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
			dialog.dismiss();
			if (editText.getText().length() != 0) {
				String newFilePath = filePath + File.separator + editText.getText().toString();
				try {
					if (isDirectory) {
						FileSystem.mkdir(newFilePath);
					} else {
						FileSystem.createFile(newFilePath);
					}
				} catch (IOException e) {
					Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			App.getFileBrowserService().sync();
		});
		builder.setNegativeButton(
				android.R.string.cancel,
				(dialog, which) -> dialog.dismiss());

		builder.setCancelable(false);
		return builder.create();
	}
}
