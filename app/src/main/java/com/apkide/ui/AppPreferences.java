package com.apkide.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.apkide.common.ApplicationProvider;

import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import brut.androlib.options.BuildOptions;

public class AppPreferences implements IFernflowerPreferences {


    private static SharedPreferences preferences;

    public static void init(Context context) {
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

    public static void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
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


    private static String getOption(String key) {
        boolean enabled = getPreferences().getBoolean(key, false);
        return enabled ? "1" : "0";
    }

    public static Map<String, Object> getClassDecompileOptions() {
        Map<String, Object> defaults = new ArrayMap<>(40);
        defaults.put(REMOVE_BRIDGE, getOption(REMOVE_BRIDGE));
        defaults.put(REMOVE_SYNTHETIC, getOption(REMOVE_SYNTHETIC));
        defaults.put(DECOMPILE_INNER, getOption(DECOMPILE_INNER));
        defaults.put(DECOMPILE_CLASS_1_4, getOption(DECOMPILE_CLASS_1_4));
        defaults.put(DECOMPILE_ASSERTIONS, getOption(DECOMPILE_ASSERTIONS));
        defaults.put(HIDE_EMPTY_SUPER, getOption(HIDE_EMPTY_SUPER));
        defaults.put(HIDE_DEFAULT_CONSTRUCTOR, getOption(HIDE_DEFAULT_CONSTRUCTOR));
        defaults.put(DECOMPILE_GENERIC_SIGNATURES, getOption(DECOMPILE_GENERIC_SIGNATURES));
        defaults.put(NO_EXCEPTIONS_RETURN, getOption(NO_EXCEPTIONS_RETURN));
        defaults.put(ENSURE_SYNCHRONIZED_MONITOR, getOption(ENSURE_SYNCHRONIZED_MONITOR));
        defaults.put(DECOMPILE_ENUM, getOption(DECOMPILE_ENUM));
        defaults.put(REMOVE_GET_CLASS_NEW, getOption(REMOVE_GET_CLASS_NEW));
        defaults.put(LITERALS_AS_IS, getOption(LITERALS_AS_IS));
        defaults.put(BOOLEAN_TRUE_ONE, getOption(BOOLEAN_TRUE_ONE));
        defaults.put(ASCII_STRING_CHARACTERS, getOption(ASCII_STRING_CHARACTERS));
        defaults.put(SYNTHETIC_NOT_SET, getOption(SYNTHETIC_NOT_SET));
        defaults.put(UNDEFINED_PARAM_TYPE_OBJECT, getOption(UNDEFINED_PARAM_TYPE_OBJECT));
        defaults.put(USE_DEBUG_VAR_NAMES, getOption(USE_DEBUG_VAR_NAMES));
        defaults.put(USE_METHOD_PARAMETERS, getOption(USE_METHOD_PARAMETERS));
        defaults.put(REMOVE_EMPTY_RANGES, getOption(REMOVE_EMPTY_RANGES));
        defaults.put(FINALLY_DEINLINE, getOption(FINALLY_DEINLINE));
        defaults.put(IDEA_NOT_NULL_ANNOTATION, getOption(IDEA_NOT_NULL_ANNOTATION));
        defaults.put(LAMBDA_TO_ANONYMOUS_CLASS, getOption(LAMBDA_TO_ANONYMOUS_CLASS));
        defaults.put(BYTECODE_SOURCE_MAPPING, getOption(BYTECODE_SOURCE_MAPPING));
        defaults.put(IGNORE_INVALID_BYTECODE, getOption(IGNORE_INVALID_BYTECODE));
        defaults.put(VERIFY_ANONYMOUS_CLASSES, getOption(VERIFY_ANONYMOUS_CLASSES));

        defaults.put(LOG_LEVEL, IFernflowerLogger.Severity.INFO.name());
        defaults.put(MAX_PROCESSING_METHOD, getOption(MAX_PROCESSING_METHOD));
        defaults.put(RENAME_ENTITIES, getOption(RENAME_ENTITIES));
        defaults.put(NEW_LINE_SEPARATOR, (InterpreterUtil.IS_WINDOWS ? "0" : "1"));
        defaults.put(INDENT_STRING, "   ");
        defaults.put(BANNER, "");
        defaults.put(UNIT_TEST_MODE, getOption(UNIT_TEST_MODE));
        defaults.put(DUMP_ORIGINAL_LINES, getOption(DUMP_ORIGINAL_LINES));

        return Collections.unmodifiableMap(defaults);
    }

}
