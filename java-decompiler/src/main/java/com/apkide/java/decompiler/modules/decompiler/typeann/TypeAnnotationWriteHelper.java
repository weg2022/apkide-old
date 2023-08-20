// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.apkide.java.decompiler.modules.decompiler.typeann;

import androidx.annotation.NonNull;

import com.apkide.java.decompiler.struct.StructTypePathEntry;
import com.apkide.java.decompiler.util.TextBuffer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper around {@link TypeAnnotation} to maintain the state of the {@link StructTypePathEntry} list while writing.
 */
public class TypeAnnotationWriteHelper {
  private final @NonNull Deque<StructTypePathEntry> paths;

  private final @NonNull TypeAnnotation annotation;

  public TypeAnnotationWriteHelper(@NonNull TypeAnnotation annotation) {
    this(annotation, new ArrayDeque<>(annotation.getPaths()));
  }

  public TypeAnnotationWriteHelper(@NonNull TypeAnnotation annotation, @NonNull Deque<StructTypePathEntry> paths) {
    this.annotation = annotation;
    this.paths = paths;
  }

  /**
   * @return Active path relative to the current scope when writing.
   */
  public @NonNull Deque<StructTypePathEntry> getPaths() {
    return paths;
  }

  /**
   * @return The annotation to write
   */
  public @NonNull TypeAnnotation getAnnotation() {
    return annotation;
  }

  public void writeTo(@NonNull StringBuilder sb) {
    annotation.writeTo(sb);
  }

  public void writeTo(@NonNull TextBuffer sb) {
    annotation.writeTo(sb);
  }

  public static List<TypeAnnotationWriteHelper> create(List<TypeAnnotation> typeAnnotations) {
    return typeAnnotations.stream()
      .map(TypeAnnotationWriteHelper::new)
      .collect(Collectors.toList());
  }
}
