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

import com.apkide.ui.util.RunCallback;
import com.kongzue.dialogx.dialogs.MessageDialog;


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

    private RunCallback callback;

    public void runOnStorageOperation(RunCallback callback) {
        if (callback == null)
            throw new IllegalArgumentException("callback is null.");

        if (this.callback != null)
            this.callback.cancel();

        this.callback = callback;
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
        this.callback.done();
        this.callback = null;
    }


    @RequiresApi(api = VERSION_CODES.R)
    private void requestManageStoragePermission() {
        MessageDialog.show("缺少必要权限",
                        getString(R.string.app_name) + ": 需要获得访问文件和媒体的权限")
                .setOkButton("同意", (dialog, v) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        ActivityCompat.startActivityForResult(StyledUI.this, intent, REQUEST_PERMISSION_CODE, null);
                    } catch (ActivityNotFoundException e) {
                        if (callback != null) {
                            callback.fail(e);
                            callback = null;
                        }
                    }
                    return true;
                })
                .setCancelButton("", (dialog, v) -> {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.cancel();
                        callback = null;
                    }
                    return true;
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SDK_INT >= VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                if (callback != null) {
                    callback.fail(new Exception("获取存储权限失败!"));
                    callback = null;
                }
            } else {
                if (callback != null) {
                    callback.done();
                    callback = null;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                if (callback != null) {
                    callback.fail(new Exception("获取存储权限失败!"));
                    callback = null;
                }
            } else {
                if (callback != null) {
                    callback.done();
                    callback = null;
                }
            }
        }
    }
}
