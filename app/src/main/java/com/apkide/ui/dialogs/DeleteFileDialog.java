package com.apkide.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.util.MessageBox;
import com.apkide.ui.App;

import java.io.File;
import java.io.IOException;

public class DeleteFileDialog extends MessageBox {

	private final String filePath;

	public DeleteFileDialog(String filePath) {
		this.filePath = filePath;
	}

	@NonNull
	@Override
	protected Dialog buildDialog(@NonNull Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		boolean isDir = FileSystem.isNormalDirectory(filePath);
		String name = FileSystem.getName(filePath);
		String parent = FileSystem.getParentPath(filePath);
		name = parent == null ? name : FileSystem.getName(parent) + File.separator + name;
		builder.setTitle("Delete " + (isDir ? "Directory" : "File"));
		builder.setMessage(String.format("Are you sure you want to delete\n \"...%s\" %s?", name, isDir ? "Directory" : "File"));
		builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
			dialog.dismiss();
			try {
				FileSystem.delete(filePath);
			} catch (IOException e) {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			App.getFileBrowserService().sync();
		});
		builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
			dialog.dismiss();
		});
		builder.setCancelable(false);
		return builder.create();
	}
}
