// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.modules.decompiler.sforms;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent;
import org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;

import java.util.ArrayList;
import java.util.List;


public class DirectNode {
  public final @NonNull DirectNodeType type;
  public final @NonNull String id;
  public final @NonNull Statement statement;
  public final @Nullable BasicBlockStatement block;
  public final List<DirectNode> successors = new ArrayList<>();
  public final List<DirectNode> predecessors = new ArrayList<>();
  public List<Exprent> exprents = new ArrayList<>();

  public DirectNode(@NonNull DirectNodeType type, @NonNull Statement statement, @NonNull String id) {
    this.type = type;
    this.statement = statement;
    this.id = id;
    this.block = null;
  }

  public DirectNode(@NonNull DirectNodeType type, @NonNull Statement statement, @NonNull BasicBlockStatement block) {
    this.type = type;
    this.statement = statement;
    this.id = Integer.toString(block.id);
    this.block = block;
  }

  @Override
  public String toString() {
    return id;
  }

  public enum DirectNodeType {
    DIRECT,
    TAIL,
    INIT,
    CONDITION,
    INCREMENT,
    TRY
  }
}
