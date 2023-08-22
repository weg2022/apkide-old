package com.apkide.language.service;

import com.apkide.language.FileHighlighting;

interface ICodeHighlightingListener {

    void highlightingFinished(in FileHighlighting highlight);

    void semanticHighlightingFinished(in FileHighlighting highlight);
}