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

import java.util.Collections;
import java.util.Map;

import brut.androlib.Config;

public class AppPreferences implements IFernflowerPreferences {


    private static SharedPreferences preferences;

    public static void init(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences_application, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_editor, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_compiler, false);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_sourcecontrol, false);
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

    public static void applyApkToolConfig() {
        Config config = new Config() {
            @Override
            public void onChanged() {

            }
        };
        //TODO: Configurable ApkTool options.
        Config.set(config);
    }

}
