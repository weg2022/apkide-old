package com.apkide.ui;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

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
        boolean darkMode;
        if (SDK_INT >= VERSION_CODES.R)
            darkMode = getResources().getConfiguration().isNightModeActive();
        else
            darkMode = isNightModeActive(getResources().getConfiguration().uiMode);
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

    public void requestInitializeData() {
        if (SDK_INT >= VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
                requestManageStoragePermission();
        } else if (SDK_INT >= VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        } else
            onInitializeData();
    }

    @RequiresApi(api = VERSION_CODES.R)
    private void requestManageStoragePermission() {
        MessageDialog.show("Request permission", "iEditor needs to get permission to access files and media, do you agree?", "Yes", "No")
                .setCancelable(false)
                .setOkButtonClickListener((dialog, v) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (SDK_INT >= VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                TipDialog.show("Failed to get storage permissions!", WaitDialog.TYPE.ERROR);
                            } else {
                                onInitializeData();
                            }
                        }
                    });
                    return true;
                })
                .setCancelButtonClickListener((dialog, v) -> {
                    dialog.dismiss();
                    TipDialog.show("Failed to get storage permissions!", WaitDialog.TYPE.ERROR);
                    return true;
                });
    }

    protected void onInitializeData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] != PERMISSION_GRANTED)
                        TipDialog.show("Failed to get storage permissions!", WaitDialog.TYPE.ERROR);
                     else
                        onInitializeData();
                }
            }
        }
    }
}
