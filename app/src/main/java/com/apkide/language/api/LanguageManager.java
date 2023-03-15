package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.FileUtils;
import com.apkide.language.impl.classfile.ClassFileLanguage;
import com.apkide.language.impl.cpp.CppLanguage;
import com.apkide.language.impl.groovy.GroovyLanguage;
import com.apkide.language.impl.java.JavaLanguage;
import com.apkide.language.impl.javascript.JavaScriptLanguage;
import com.apkide.language.impl.json.JsonLanguage;
import com.apkide.language.impl.kotlin.KotlinLanguage;
import com.apkide.language.impl.smali.SmaliLanguage;
import com.apkide.language.impl.xml.XmlLanguage;
import com.apkide.language.impl.yaml.YamlLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LanguageManager {

	private static final Map<String, CommonLanguage> languageMap = new HashMap<>();
	private static final Map<String, List<String>> filePatternMap = new HashMap<>();

	public static void registerDefaults() {
		languageMap.clear();
		filePatternMap.clear();
		addLanguage(new CppLanguage());
		addLanguage(new GroovyLanguage());
		addLanguage(new JavaLanguage());
		addLanguage(new ClassFileLanguage());
		addLanguage(new JavaScriptLanguage());
		addLanguage(new JsonLanguage());
		addLanguage(new KotlinLanguage());
		addLanguage(new SmaliLanguage());
		addLanguage(new XmlLanguage());
		addLanguage(new YamlLanguage());
	}

	public static void addLanguage(@NonNull CommonLanguage language) {
		languageMap.put(language.getName(), language);
		filePatternMap.put(language.getName(), Arrays.asList(language.getDefaultFilePatterns()));
	}

	@NonNull
	public static List<CommonLanguage> getLanguages() {
		return new ArrayList<>(languageMap.values());
	}

	@NonNull
	public static List<String> getLanguageNames() {
		return new ArrayList<>(languageMap.keySet());
	}


	public static boolean containsLanguage(@NonNull String languageName) {
		return languageMap.containsKey(languageName);
	}

	@Nullable
	public static CommonLanguage getLanguageByName(@Nullable String languageName) {
		if (languageName == null) return null;
		return languageMap.get(languageName);
	}

	@Nullable
	public static String findLanguageName(@NonNull String filePath) {
		for (String key : filePatternMap.keySet()) {
			List<String> patterns = getFilePattern(key);
			if (patterns != null) {
				for (String pattern : patterns) {
					if (pattern.startsWith("*.")) pattern = pattern.substring(2);
					String mExt = FileUtils.getExtension(pattern);
					String ext = FileUtils.getExtension(filePath);
					if (pattern.equals(filePath) || ext.equals(pattern) || mExt.equals(ext))
						return key;
				}
			}
		}
		return null;
	}

	@Nullable
	public static CommonLanguage findLanguage(@NonNull String filePath) {
		return getLanguageByName(findLanguageName(filePath));
	}

	@Nullable
	public static List<String> getFilePattern(@NonNull String languageName) {
		return filePatternMap.get(languageName);
	}


}
