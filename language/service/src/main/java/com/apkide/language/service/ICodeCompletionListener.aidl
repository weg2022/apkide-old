package com.apkide.language.service;

import com.apkide.language.Completion;

interface ICodeCompletionListener {

    void completionFinished(in String filePath,in int line,in int column,
                            in boolean allowTypes,in List<Completion> list);
}