package com.apkide.language.runtime;

import java.io.Reader;

public interface Highlighter {

    void highlighting(Reader reader,TokenIterator iterator);
}
