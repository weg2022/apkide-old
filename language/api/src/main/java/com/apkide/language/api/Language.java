package com.apkide.language.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于表示代码语言接口
 */
public interface Language {
    
    /**
     * 初始化
     */
    void initialize();
    
    void configureAssembly(@NonNull Assembly assembly);
    
    /**
     * 关闭.
     */
    void shutdown();
    
    /**
     * 获取名称
     *
     * @return 代码语言名称
     */
    @NonNull
    String getName();
    
    /**
     * 获取默认文件模式
     *
     * @return 文件模式
     */
    @NonNull
    String[] getFilePatterns();
    

    /**
     * 获取代码格式化器
     *
     * @return 代码格式化器
     */
    @Nullable
    CodeFormatter getFormatter();
    
    /**
     * 获取代码高亮器
     *
     * @return 代码高亮器
     */
    @Nullable
    CodeHighlighter getHighlighter();
    
    /**
     * 获取代码分析器
     *
     * @return 代码分析器
     */
    @Nullable
    CodeAnalyzer getAnalyzer();
    
    //**
    // * 获取代码编译器
    //*
    //* @return 代码编译器
    //*/
    //@Nullable
    //CodeCompiler getCompiler();
    
    /**
     * 获取代码补全器
     *
     * @return 代码补全器
     */
    @Nullable
    CodeCompleter getCompleter();
    
    /**
     * 获取代码导航
     *
     * @return 代码导航
     */
    @Nullable
    CodeNavigation getNavigation();
    
    /**
     * 获取代码重构器
     *
     * @return 代码重构器
     */
    @Nullable
    CodeRefactor getRefactor();
}
