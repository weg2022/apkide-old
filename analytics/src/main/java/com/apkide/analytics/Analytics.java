package com.apkide.analytics;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;

public class Analytics {

    private static final String FLURRY_ANALYTICS_KEY = "WPPHPT9K4TPJHWX27DZ5";
    private static AnalyticsOptionsCallback callback;
    private static String versionName = "";
    private static boolean initialized = false;
    
    private Analytics() {
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static void initialize(@NonNull Context context,boolean debug,@NonNull AnalyticsOptionsCallback callback) {
        Analytics.callback = callback;
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        
        new FlurryAgent.Builder()
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .withReportLocation(true)
                .build(context, FLURRY_ANALYTICS_KEY);
        FlurryAgent.setVersionName(versionName);
        initialized = true;
        if (debug) {
            FlurryAgent.setLogEnabled(true);
            FlurryAgent.setLogLevel(Log.VERBOSE);
        }
        ensureInitializedAndEnabled();
    }
    
    private static boolean ensureInitializedAndEnabled() {
        if (!isInitialized()) {
            throw new IllegalStateException("Analytics not initialized");
        }
 
        return callback.isAnalyticsEnabled();
    }
    
    public static void startSession(Context context) {
        if (ensureInitializedAndEnabled()) {
            FlurryAgent.onStartSession(context);
        }
    }
    
    public static void endSession(Context context) {
        if (ensureInitializedAndEnabled()) {
            FlurryAgent.onEndSession(context);
        }
    }
    
    
    public static void logEvent(String eventName) {
        if (ensureInitializedAndEnabled()) {
            sendGaEvent(eventName);
        }
    }
    
    private static void sendGaEvent(String eventName) {
        FlurryAgent.logEvent(eventName);
    }
    
    
}