package com.apkide.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.StyledUI;
import com.apkide.ui.marketing.WhatsNew;

public class MainUI extends StyledUI {
    public static final int REQUEST_PERMISSIONS_CODE = 201;

    @Override
    protected void onUICreated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        WhatsNew.makeShow(this);
        requestPermissions();
    }


    private boolean accessStorage() {
        return checkCallingPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (!accessStorage()) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }
                }
            }

        }
    }
}