// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.apkide.java.decompiler.modules.decompiler.stats;

import com.apkide.java.decompiler.main.collectors.BytecodeMappingTracer;
import com.apkide.java.decompiler.modules.decompiler.ExprProcessor;
import com.apkide.java.decompiler.util.TextBuffer;

public class RootStatement extends Statement {
  private final DummyExitStatement dummyExit;

  public RootStatement(Statement head, DummyExitStatement dummyExit) {
    super(StatementType.ROOT);

    first = head;
    this.dummyExit = dummyExit;

    stats.addWithKey(first, first.id);
    first.setParent(this);
  }

  @Override
  public TextBuffer toJava(int indent, BytecodeMappingTracer tracer) {
    return ExprProcessor.listToJava(varDefinitions, indent, tracer).append(first.toJava(indent, tracer));
  }

  public DummyExitStatement getDummyExit() {
    return dummyExit;
  }
}