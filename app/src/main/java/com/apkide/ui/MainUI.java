package com.apkide.ui;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class MainUI extends ThemeUI implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        App.init(this);
        super.onCreate(savedInstanceState);
        AppPreferences.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            changeTheme(newConfig.isNightModeActive());
        else
            changeTheme(isNightModeActive(newConfig.uiMode));
    }

    private void changeTheme(boolean dark) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private static final int REQUEST_PERMISSION_CODE = 10014;

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("缺少访问文件权限");
                builder.setMessage("接下来需要您去授权访问文件权限");
                builder.setPositiveButton("好的", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                });
                builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PERMISSION_GRANTED) {

                        return;
                    } else {
                        Toast.makeText(this, "获取访问外部存储权限失败...", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }
    }
}
