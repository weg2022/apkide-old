package weg.test.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.Assembly;
import com.apkide.language.api.CodeAnalyzer;
import com.apkide.language.api.CodeAnalyzerCallback;
import com.apkide.language.api.CodeCompleter;
import com.apkide.language.api.CodeCompleterCallback;
import com.apkide.language.api.CodeFormatter;
import com.apkide.language.api.CodeHighlighter;
import com.apkide.language.api.CodeHighlighterCallback;
import com.apkide.language.api.CodeNavigation;
import com.apkide.language.api.CodeRefactor;
import com.apkide.language.api.Language;

public class TestLanguage implements Language {
    private static final String LOG_TAG = "TestLanguage";
    @Override
    public void initialize() {
        Log.d(LOG_TAG, "initialize: ");
    }
    
    @Override
    public void configureAssembly(@NonNull Assembly assembly) {
        Log.d(LOG_TAG, "configureAssembly: ");
    }
    
    @Override
    public void shutdown() {
        Log.d(LOG_TAG, "shutdown: ");
    }
    
    @NonNull
    @Override
    public String getName() {
        return "Test";
    }
    
    @NonNull
    @Override
    public String[] getFilePatterns() {
        return new String[]{"*.test"};
    }
    
    @Nullable
    @Override
    public CodeFormatter getFormatter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeHighlighter getHighlighter() {
        return new CodeHighlighter() {
            @Override
            public void highlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback) {
                Log.d(LOG_TAG, "highlighting: ");
            }
    
            @Override
            public void semanticHighlighting(@NonNull String filePath, @NonNull CodeHighlighterCallback callback) {
                Log.d(LOG_TAG, "semanticHighlighting: ");
            }
        };
    }
    
    @Nullable
    @Override
    public CodeAnalyzer getAnalyzer() {
        return new CodeAnalyzer() {
            @Override
            public void analyze(@NonNull String filePath, @NonNull CodeAnalyzerCallback callback) {
                Log.d(LOG_TAG, "analyze: ");
            }
        };
    }
    
    @Nullable
    @Override
    public CodeCompleter getCompleter() {
        return new CodeCompleter() {
            @Override
            public void completion(@NonNull String filePath, int line, int column, boolean allowTypes, @NonNull CodeCompleterCallback callback) {
                Log.d(LOG_TAG, "completion: ");
            }
        };
    }
    
    @Nullable
    @Override
    public CodeNavigation getNavigation() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeRefactor getRefactor() {
        return null;
    }
}
