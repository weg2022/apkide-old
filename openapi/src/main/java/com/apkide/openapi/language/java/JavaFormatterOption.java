package com.apkide.openapi.language.java;

import androidx.annotation.NonNull;

import com.apkide.openapi.language.Formatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum JavaFormatterOption implements Formatter.FormatterOption {

    ;

    final String group;
    final String name;
    final String before, after;

    JavaFormatterOption(String group, String name, String before, String after) {
        this.group = group;
        this.name = name;
        this.before = before;
        this.after = after;
    }

    @NonNull
    @Override
    public String getGroup() {
        return group;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getPreview(boolean preview) {
        return preview ? after : before;
    }


    public static List<JavaFormatterOption> getAll(){
        List<JavaFormatterOption> options=new ArrayList<>();

        return options;
    }

    public static Set<String> getDefaultOptions(){
        HashSet<String> set=new HashSet<>();

        return set;
    }
}
