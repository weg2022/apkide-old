package com.apkide.ui.marketing;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.apkide.ui.IDEOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WhatsNew {


    public static void makeShow(Activity activity) {
        var versionCode = 0;
        try {
            var packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (versionCode <= IDEOptions.getLastVersionCode()) return;
        var title="What's New";
        var message= readAssetsContent(activity,"whatsnew/apk-ide-whatsnew.txt");

        IDEOptions.getPreferences().edit().putInt("versionCode", versionCode).apply();
    }


    private static String readAssetsContent(Activity activity, String fileName) {
        var outputStream = new ByteArrayOutputStream();
        try {
            var stream=activity.getAssets().open(fileName);
            byte[] bytes = new byte[4096];
            while (stream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            return outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


}
