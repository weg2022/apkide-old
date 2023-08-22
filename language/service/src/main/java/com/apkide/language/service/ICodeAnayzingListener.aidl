package com.apkide.language.service;

import com.apkide.language.Problem;

interface ICodeAnayzingListener {

    void analyzeFinished(in String filePath,in List<Problem> list);
}