package com.apkide.ui;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static android.os.Build.VERSION.SDK_INT;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.apkide.common.AppLog;
import com.apkide.ui.util.MessageBox;


public class StyledUI extends AppCompatActivity {


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applyTheme();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void applyTheme() {
		boolean darkMode = isNightMode();
		WindowInsetsControllerCompat compat =
				new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
		compat.setAppearanceLightStatusBars(!darkMode);
		compat.setAppearanceLightStatusBars(!darkMode);
	}

	private boolean isNightModeActive(int uiMode) {
		return (uiMode & UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES;
	}

	public boolean isNightMode() {
		if (SDK_INT >= VERSION_CODES.R) {
			return getResources().getConfiguration().isNightModeActive();
		}
		return isNightModeActive(getResources().getConfiguration().uiMode);
	}

	private static final int REQUEST_PERMISSION_CODE = 1234;


	public void requestStorage() {
		if (SDK_INT >= VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				requestManageStoragePermission();
			}
		} else {
			if (ActivityCompat.checkSelfPermission(this,
					WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
			}
		}
	}

	private Runnable myStorageRunnable;

	public void runOnStorage(@NonNull Runnable runnable) {
		myStorageRunnable = runnable;
		if (SDK_INT >= VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				requestManageStoragePermission();
				return;
			}
		} else {
			if (ActivityCompat.checkSelfPermission(this,
					WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
				return;
			}
		}
		myStorageRunnable.run();
	}


	@RequiresApi(api = VERSION_CODES.R)
	private void requestManageStoragePermission() {
		MessageBox.showInfo(this,
				getString(R.string.request_permission_label),
				getString(R.string.app_name) +
						getString(R.string.request_permission_message),
				false,
				() -> {
					try {
						Intent intent = new Intent(
								Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivity(intent);
						ActivityCompat.startActivityForResult(
								this, intent, REQUEST_PERMISSION_CODE, null);
					} catch (ActivityNotFoundException e) {
						AppLog.e(e);
					}
				}, () -> {
					AppLog.s(this, "requestManageStoragePermission canceled.");
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PERMISSION_CODE) {
			if (SDK_INT >= VERSION_CODES.R) {
				if (Environment.isExternalStorageManager()) {
					if (myStorageRunnable != null)
						myStorageRunnable.run();
				}
			} else {
				if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
						!= PERMISSION_GRANTED) {
					if (myStorageRunnable != null)
						myStorageRunnable.run();
				}
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_PERMISSION_CODE) {
			if (SDK_INT >= VERSION_CODES.R) {
				if (Environment.isExternalStorageManager()) {
					if (myStorageRunnable != null)
						myStorageRunnable.run();
				}
			} else {
				if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
						!= PERMISSION_GRANTED) {
					if (myStorageRunnable != null)
						myStorageRunnable.run();
				}
			}
		}
	}
}
