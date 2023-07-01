package com.apkide.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.apkide.common.ApplicationProvider;

import java.util.Collection;
import java.util.HashSet;

import brut.androlib.options.BuildOptions;

public class AppPreferences {


    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_application, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_editor, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_compiler, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_sourcecontrol, false);
        BuildOptions buildOptions = new BuildOptions() {
            @Override
            public boolean isForceBuildAll() {
                return AppPreferences.isForceBuildAll();
            }

            @Override
            public boolean isForceDeleteFramework() {
                return AppPreferences.isForceDeleteFramework();
            }

            @Override
            public boolean isDebugMode() {
                return AppPreferences.isDebugMode();
            }

            @Override
            public boolean isNetSecConf() {
                return AppPreferences.isNetSecConf();
            }

            @Override
            public boolean isVerbose() {
                return AppPreferences.isVerbose();
            }

            @Override
            public boolean isCopyOriginalFiles() {
                return AppPreferences.isCopyOriginalFiles();
            }

            @Override
            public boolean isUpdateFiles() {
                return AppPreferences.isUpdateFiles();
            }

            @Override
            public void setIsFramework(boolean isFramework) {
                AppPreferences.setIsFramework(isFramework);
            }

            @Override
            public boolean isFramework() {
                return AppPreferences.isFramework();
            }

            @Override
            public void setResourceAreCompressed(boolean resourceAreCompressed) {
                AppPreferences.setResourceAreCompressed(resourceAreCompressed);
            }

            @Override
            public boolean isResourcesAreCompressed() {
                return AppPreferences.isResourcesAreCompressed();
            }

            @Override
            public boolean isUseAapt2() {
                return AppPreferences.isUseAapt2();
            }

            @Override
            public boolean isNoCrunch() {
                return AppPreferences.isNoCrunch();
            }

            @Override
            public int getForceApi() {
                return AppPreferences.getForceApi();
            }

            @Override
            public void setDoNotCompressResources(Collection<String> doNotCompress) {
                AppPreferences.setDoNotCompress(doNotCompress);
            }

            @Override
            public Collection<String> getDoNotCompress() {
                return AppPreferences.getDoNotCompress();
            }

            @Override
            public void setFrameworkFolderLocation(String frameworkFolderLocation) {
                AppPreferences.setFrameworkFolderLocation(frameworkFolderLocation);
            }

            @Override
            public String getFrameworkFolderLocation() {
                return AppPreferences.getFrameworkFolderLocation();
            }

            @Override
            public void setFrameworkTag(String frameworkTag) {
                AppPreferences.setFrameworkTag(frameworkTag);
            }

            @Override
            public String getFrameworkTag() {
                return AppPreferences.getFrameworkTag();
            }

            @Override
            public String getAaptPath() {
                return AppPreferences.getAaptPath();
            }
        };
        BuildOptions.set(buildOptions);
    }

    @NonNull
    public static SharedPreferences getPreferences() {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.get().getContext());
        return preferences;
    }

    @NonNull
    public static SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    public static void setForceBuildAll(boolean forceBuildAll) {
        getEditor().putBoolean("forceBuildAll", forceBuildAll).apply();
    }

    public static boolean isForceBuildAll() {
        return getPreferences().getBoolean("forceBuildAll", false);
    }

    public static void setForceDeleteFramework(boolean forceDeleteFramework) {
        getEditor().putBoolean("forceDeleteFramework", forceDeleteFramework).apply();
    }

    public static boolean isForceDeleteFramework() {
        return getPreferences().getBoolean("forceDeleteFramework", false);
    }

    public static void setDebugMode(boolean debugMode) {
        getEditor().putBoolean("debugMode", debugMode).apply();
    }

    public static boolean isDebugMode() {
        return getPreferences().getBoolean("debugMode", false);
    }

    public static void setNetSecConf(boolean netSecConf) {
        getEditor().putBoolean("netSecConf", netSecConf).apply();
    }

    public static boolean isNetSecConf() {
        return getPreferences().getBoolean("netSecConf", false);
    }


    public static void setVerbose(boolean verbose) {
        getEditor().putBoolean("verbose", verbose).apply();
    }

    public static boolean isVerbose() {
        return getPreferences().getBoolean("verbose", false);
    }

    public static void setCopyOriginalFiles(boolean copyOriginalFiles) {
        getEditor().putBoolean("copyOriginalFiles", copyOriginalFiles).apply();
    }

    public static boolean isCopyOriginalFiles() {
        return getPreferences().getBoolean("copyOriginalFiles", false);
    }

    public static void setUpdateFiles(boolean updateFiles) {
        getEditor().putBoolean("updateFiles", updateFiles).apply();
    }

    public static boolean isUpdateFiles() {
        return getPreferences().getBoolean("updateFiles", false);
    }

    public static void setIsFramework(boolean isFramework) {
        getEditor().putBoolean("isFramework", isFramework).apply();
    }

    public static boolean isFramework() {
        return getPreferences().getBoolean("isFramework", false);
    }

    public static void setResourceAreCompressed(boolean resourceAreCompressed) {
        getEditor().putBoolean("isResourceAreCompressed", resourceAreCompressed).apply();
    }

    public static boolean isResourcesAreCompressed() {
        return getPreferences().getBoolean("isResourceAreCompressed", false);
    }

    public static void setNoCrunch(boolean noCrunch) {
        getEditor().putBoolean("noCrunch", noCrunch).apply();
    }

    public static boolean isNoCrunch() {
        return getPreferences().getBoolean("noCrunch", false);
    }

    public static void setForceApi(int api) {
        getEditor().putInt("forceApi", api).apply();
    }

    public static int getForceApi() {
        return getPreferences().getInt("forceApi", 0);
    }


    public static void setDoNotCompress(Collection<String> notCompress) {
        getEditor().putStringSet("doNotCompress", new HashSet<>(notCompress)).apply();
    }

    public static Collection<String> getDoNotCompress() {
        return getPreferences().getStringSet("doNotCompress", new HashSet<>());
    }

    public static void setUseAapt2(boolean useAapt2) {
        getEditor().putBoolean("useAapt2", useAapt2).apply();
    }

    public static boolean isUseAapt2() {
        return getPreferences().getBoolean("useAapt2", false);
    }

    public static void setFrameworkFolderLocation(String location) {
        getEditor().putString("frameworkFolderLocation", location).apply();
    }

    public static String getFrameworkFolderLocation() {
        return getPreferences().getString("frameworkFolderLocation", "");
    }

    public static void setFrameworkTag(String tag) {
        getEditor().putString("frameworkTag", tag).apply();
    }

    public static String getFrameworkTag() {
        return getPreferences().getString("frameworkTag", "");
    }

    public static void setAaptPath(String aaptPath) {
        getEditor().putString("aaptPath", aaptPath).apply();
    }

    public static String getAaptPath() {
        return getPreferences().getString("aaptPath", "");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Editor Preferences
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isUseTab() {
        return getPreferences().getBoolean("editor.useTab", true);
    }

    public static int getTabSize() {
        return getPreferences().getInt("editor.tabSize", 4);
    }
}
