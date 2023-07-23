package com.apkide.openapi.language;


import androidx.annotation.NonNull;

import org.antlr.v4.runtime.tree.SyntaxTree;

public interface Renderer {



    @NonNull
    String getName(@NonNull SyntaxTree element);
}
