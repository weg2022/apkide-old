// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.modules.decompiler;


import androidx.annotation.NonNull;

import org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.ConstExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.VarExprent;
import org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.gen.VarType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternHelper {

  /**
   * Method searches if-pattern like <code>if (var instanceof SomeType)</code> pattern,
   * and assignment expression pattern like <code>SomeType s = (SomeType)var;</code>.
   * If the pattern were found, then method replaces found assignment expression with pattern variable.
   *
   * @param statement root statement to start traversal
   * @param structClass owner class of <code>statement</code>
   */
  public static void replaceAssignmentsWithPatternVariables(@NonNull RootStatement statement, @NonNull StructClass structClass) {
    if (!structClass.hasPatternsInInstanceofSupport()) return;
    // todo VarExprent overrides equals, but not hashCode, duplicates are possible
    Map<VarExprent, Statement> tempVarAssignments = new HashMap<>();
    replaceAssignmentsWithPatternVariables(statement, tempVarAssignments);
    SwitchHelper.removeTempVariableDeclarations(tempVarAssignments);
  }

  private static void replaceAssignmentsWithPatternVariables(@NonNull Statement statement,
                                                             @NonNull Map<VarExprent, Statement> tempVarAssignments) {
    if (statement instanceof IfStatement) {
      IfStatement ifStatement = (IfStatement)statement;
      FunctionExprent instanceOfExprent = findInstanceofExprent(ifStatement);
      if (instanceOfExprent == null) return;

      List<Exprent> operands = instanceOfExprent.getLstOperands();
      if (operands.size() != 2 || operands.get(0).type != Exprent.EXPRENT_VAR || operands.get(1).type != Exprent.EXPRENT_CONST) return;
      VarExprent operand = (VarExprent)operands.get(0);
      ConstExprent checkType = (ConstExprent)operands.get(1);

      PatternVariableCandidate patternVarCandidate = findPatternVarCandidate(ifStatement.getIfstat(), operand, checkType);
      if (patternVarCandidate == null && ifStatement.getElsestat() != null) {
        patternVarCandidate = findPatternVarCandidate(ifStatement.getElsestat(), operand, checkType);
      }
      if (patternVarCandidate == null) return;

      operands.remove(1);
      if (!patternVarCandidate.varExprent.isDefinition()) {
        patternVarCandidate.varExprent.setDefinition(true);
        tempVarAssignments.put(patternVarCandidate.varExprent, ifStatement);
      }
      operands.add(patternVarCandidate.varExprent);
      patternVarCandidate.ifElseStat.getExprents().remove(patternVarCandidate.assignmentExprent);
    }
    for (Statement child : statement.getStats()) {
      replaceAssignmentsWithPatternVariables(child, tempVarAssignments);
    }
  }

  private static FunctionExprent findInstanceofExprent(@NonNull IfStatement ifStat) {
    return ifStat.getHeadexprent().getAllExprents(true).stream()
      .filter(expr -> expr.type == Exprent.EXPRENT_FUNCTION).map(expr -> (FunctionExprent)expr)
      .filter(expr -> expr.getFuncType() == FunctionExprent.FUNCTION_INSTANCEOF)
      .findFirst().orElse(null);
  }

  private static PatternVariableCandidate findPatternVarCandidate(@NonNull Statement ifElseStat,
                                                                  @NonNull VarExprent operand,
                                                                  @NonNull ConstExprent checkType) {
    if (ifElseStat instanceof BasicBlockStatement) {
      List<Exprent> ifElseExprents = ifElseStat.getExprents();
      if (ifElseExprents.isEmpty() || ifElseExprents.get(0).type != Exprent.EXPRENT_ASSIGNMENT) return null;

      AssignmentExprent assignmentExprent = (AssignmentExprent)ifElseExprents.get(0);
      if (assignmentExprent.getLeft().type != Exprent.EXPRENT_VAR) return null;
      VarExprent varExprent = (VarExprent)assignmentExprent.getLeft();
      if (assignmentExprent.getRight().type != Exprent.EXPRENT_FUNCTION) return null;
      FunctionExprent castExprent = (FunctionExprent)assignmentExprent.getRight();
      if (castExprent.getFuncType() != FunctionExprent.FUNCTION_CAST) return null;

      if (!varExprent.isDefinition()) {
        Exprent leftAssignmentPart = assignmentExprent.getLeft();
        Exprent rightAssignmentPart = assignmentExprent.getRight();
        if (leftAssignmentPart.type != Exprent.EXPRENT_VAR || rightAssignmentPart.type != Exprent.EXPRENT_FUNCTION ||
            ((FunctionExprent)rightAssignmentPart).getFuncType() != FunctionExprent.FUNCTION_CAST) {
          return null;
        }
        varExprent = ((VarExprent)leftAssignmentPart);
        List<Exprent> castOperands = ((FunctionExprent)rightAssignmentPart).getLstOperands();
        if (castOperands.size() != 2 || castOperands.get(1).type != Exprent.EXPRENT_CONST) return null;
        VarType castType = ((ConstExprent)castOperands.get(1)).getConstType();
        varExprent.setVarType(castType);
      }

      List<Exprent> castExprents = castExprent.getAllExprents();
      if (castExprents.size() == 2 && operand.equals(castExprents.get(0)) && checkType.equals(castExprents.get(1))) {
        return new PatternVariableCandidate(ifElseStat, assignmentExprent, varExprent);
      }
      return null;
    }
    else if (ifElseStat instanceof IfStatement || ifElseStat instanceof SequenceStatement) {
      return findPatternVarCandidate(ifElseStat.getFirst(), operand, checkType);
    }
    return null;
  }

  private static class PatternVariableCandidate {
    private final @NonNull Statement ifElseStat;
    private final @NonNull AssignmentExprent assignmentExprent;
    private final @NonNull VarExprent varExprent;

    private PatternVariableCandidate(@NonNull Statement ifElseStat,
                                     @NonNull AssignmentExprent assignmentExprent,
                                     @NonNull VarExprent varExprent) {
      this.ifElseStat = ifElseStat;
      this.assignmentExprent = assignmentExprent;
      this.varExprent = varExprent;
    }
  }
}
