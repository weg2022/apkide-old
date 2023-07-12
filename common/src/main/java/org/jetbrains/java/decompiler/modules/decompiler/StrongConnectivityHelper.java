// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.modules.decompiler;


import androidx.annotation.NonNull;

import org.jetbrains.java.decompiler.modules.decompiler.StatEdge.EdgeDirection;
import org.jetbrains.java.decompiler.modules.decompiler.StatEdge.EdgeType;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;
import org.jetbrains.java.decompiler.util.ListStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class finds the strongly connected components (SCCs) of a directed graph,
 * implementing "Tarjan's strongly connected components" algorithm.
 * Running time is linear.
 */
// todo should be replaced or reuse InferenceGraphNode.strongConnect or DFSTBuilder.Tarjan?
public class StrongConnectivityHelper {
  private final List<List<Statement>> components = new ArrayList<>();
  private final Set<Statement> setProcessed = new HashSet<>();
  private final ListStack<Statement> component = new ListStack<>();
  private final Set<Statement> visited = new HashSet<>();
  private final Map<Statement, Integer> indices = new HashMap<>();
  private final Map<Statement, Integer> lowIndices = new HashMap<>();

  private int nextIndex;

  public StrongConnectivityHelper(@NonNull Statement startStatement) {
    visitTree(startStatement.getFirst());
    for (Statement statement : startStatement.getStats()) {
      if (!setProcessed.contains(statement) && statement.getPredecessorEdges(EdgeType.DIRECT_ALL).isEmpty()) {
        visitTree(statement);
      }
    }
    // should not find any more nodes! FIXME: ??
    for (Statement statement : startStatement.getStats()) {
      if (!setProcessed.contains(statement)) {
        visitTree(statement);
      }
    }
  }

  private void visitTree(@NonNull Statement statement) {
    component.clear();
    visited.clear();
    indices.clear();
    lowIndices.clear();
    nextIndex = 0;

    visit(statement);

    setProcessed.addAll(visited);
    setProcessed.add(statement);
  }

  private void visit(@NonNull Statement statement) {
    component.push(statement);
    indices.put(statement, nextIndex);
    lowIndices.put(statement, nextIndex);
    nextIndex++;
    List<Statement> successors = statement.getNeighbours(EdgeType.REGULAR, EdgeDirection.FORWARD); // TODO: set?
    successors.removeAll(setProcessed);
    for (Statement successor : successors) {
      int successorIndex;
      if (visited.contains(successor)) {
        successorIndex = indices.get(successor);
      }
      else {
        visited.add(successor);
        visit(successor);
        successorIndex = lowIndices.get(successor);
      }
      lowIndices.put(statement, Math.min(lowIndices.get(statement), successorIndex));
    }
    if (lowIndices.get(statement).intValue() == indices.get(statement).intValue()) {
      List<Statement> component = new ArrayList<>();
      Statement statementInComponent;
      do {
        statementInComponent = this.component.pop();
        component.add(statementInComponent);
      }
      while (statementInComponent != statement);
      components.add(component);
    }
  }

  public static boolean isExitComponent(@NonNull List<? extends Statement> component) {
    Set<Statement> statements = new HashSet<>();
    for (Statement statement : component) {
      statements.addAll(statement.getNeighbours(EdgeType.REGULAR, EdgeDirection.FORWARD));
    }
    for (Statement statement : component) {
      statements.remove(statement);
    }
    return statements.size() == 0;
  }

  public @NonNull List<Statement> getExitReps() {
    List<Statement> result = new ArrayList<>();
    for (List<Statement> component : components) {
      if (isExitComponent(component)) {
        result.add(component.get(0));
      }
    }
    return result;
  }

  public @NonNull List< List<Statement>> getComponents() {
    return components;
  }
}