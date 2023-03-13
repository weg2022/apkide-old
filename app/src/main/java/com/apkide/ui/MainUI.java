package com.apkide.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.apkide.language.api.ColorScheme;
import com.apkide.language.impl.cpp.CppLanguage;
import com.apkide.language.impl.groovy.GroovyLanguage;
import com.apkide.language.impl.java.JavaLanguage;
import com.apkide.language.impl.javascript.JavaScriptLanguage;
import com.apkide.language.impl.smali.SmaliLanguage;
import com.apkide.language.impl.xml.XmlLanguage;

import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;

public class MainUI extends ThemeUI implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Test
		CodeEditor editor = new CodeEditor(this);
		setContentView(editor);
		ColorScheme scheme=new ColorScheme();
		editor.setLineSeparator(LineSeparator.CRLF);
		editor.setColorScheme(scheme);
		editor.setEditorLanguage(new SmaliLanguage());
		editor.setText(".class public Lcom/apkide/ui/services/file/OpenFileService;\n" +
				".super Ljava/lang/Object;\n" +
				".source \"OpenFileService.java\"\n" +
				"\n" +
				"\n" +
				"# direct methods\n" +
				".method public constructor <init>()V\n" +
				"    .registers 1\n" +
				"\n" +
				"    .prologue\n" +
				"    .line 3\n" +
				"    invoke-direct {p0}, Ljava/lang/Object;-><init>()V\n" +
				"\n" +
				"    return-void\n" +
				".end method\n");
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	
	}
}
