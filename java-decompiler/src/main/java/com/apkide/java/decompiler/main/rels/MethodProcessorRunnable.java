// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.apkide.java.decompiler.main.rels;

import com.apkide.java.decompiler.main.collectors.CounterContainer;
import com.apkide.java.decompiler.main.extern.IFernflowerLogger;
import com.apkide.java.decompiler.main.extern.IFernflowerPreferences;
import com.apkide.java.decompiler.modules.decompiler.ClearStructHelper;
import com.apkide.java.decompiler.modules.decompiler.ExitHelper;
import com.apkide.java.decompiler.modules.decompiler.ExprProcessor;
import com.apkide.java.decompiler.modules.decompiler.FinallyProcessor;
import com.apkide.java.decompiler.modules.decompiler.InlineSingleBlockHelper;
import com.apkide.java.decompiler.modules.decompiler.LabelHelper;
import com.apkide.java.decompiler.modules.decompiler.LoopExtractHelper;
import com.apkide.java.decompiler.modules.decompiler.MergeHelper;
import com.apkide.java.decompiler.modules.decompiler.PatternHelper;
import com.apkide.java.decompiler.modules.decompiler.SecondaryFunctionsHelper;
import com.apkide.java.decompiler.modules.decompiler.SequenceHelper;
import com.apkide.java.decompiler.modules.decompiler.StackVarsProcessor;
import com.apkide.java.decompiler.modules.decompiler.SwitchHelper;
import com.apkide.java.decompiler.struct.StructClass;
import com.apkide.java.decompiler.struct.StructMethod;
import com.apkide.java.decompiler.struct.gen.MethodDescriptor;
import com.apkide.java.decompiler.code.CodeConstants;
import com.apkide.java.decompiler.code.InstructionSequence;
import com.apkide.java.decompiler.code.cfg.ControlFlowGraph;
import com.apkide.java.decompiler.main.DecompilerContext;
import com.apkide.java.decompiler.modules.code.DeadCodeHelper;
import com.apkide.java.decompiler.modules.decompiler.DomHelper;
import com.apkide.java.decompiler.modules.decompiler.IdeaNotNullHelper;
import com.apkide.java.decompiler.modules.decompiler.IfHelper;
import com.apkide.java.decompiler.modules.decompiler.PPandMMHelper;
import com.apkide.java.decompiler.modules.decompiler.deobfuscator.ExceptionDeobfuscator;
import com.apkide.java.decompiler.modules.decompiler.stats.RootStatement;
import com.apkide.java.decompiler.modules.decompiler.vars.VarProcessor;

import java.io.IOException;

public class MethodProcessorRunnable implements Runnable {
  public final Object lock = new Object();

  private final StructClass klass;
  private final StructMethod method;
  private final MethodDescriptor methodDescriptor;
  private final VarProcessor varProc;
  private final DecompilerContext parentContext;

  private volatile RootStatement root;
  private volatile Throwable error;
  private volatile boolean finished = false;

  public MethodProcessorRunnable(StructClass klass,
                                 StructMethod method,
                                 MethodDescriptor methodDescriptor,
                                 VarProcessor varProc,
                                 DecompilerContext parentContext) {
    this.klass = klass;
    this.method = method;
    this.methodDescriptor = methodDescriptor;
    this.varProc = varProc;
    this.parentContext = parentContext;
  }

  @Override
  public void run() {
    error = null;
    root = null;

    try {
      DecompilerContext.setCurrentContext(parentContext);
      root = codeToJava(klass, method, methodDescriptor, varProc);
    }
    catch (Throwable t) {
      error = t;
    }
    finally {
      DecompilerContext.setCurrentContext(null);
    }

    finished = true;
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  public static RootStatement codeToJava(StructClass cl, StructMethod mt, MethodDescriptor md, VarProcessor varProc) throws IOException {
    boolean isInitializer = CodeConstants.CLINIT_NAME.equals(mt.getName()); // for now static initializer only

    mt.expandData(cl);
    InstructionSequence seq = mt.getInstructionSequence();
    ControlFlowGraph graph = new ControlFlowGraph(seq);

    DeadCodeHelper.removeDeadBlocks(graph);

    //
    // According to the JVMS11 4.9.1:
    //   If the class file version number is 51.0 or above,
    //   then neither the jsr opcode or the jsr_w opcode may appear in the code array
    //
    // Since jsr instruction is forbidden for class files version 51.0 (Java 7) or above
    // call to inlineJsr() is only meaningful for class files prior to the Java 7.
    //
    if (!cl.isVersion7()) {
      graph.inlineJsr(cl, mt);
    }

    // TODO: move to the start, before jsr inlining
    DeadCodeHelper.connectDummyExitBlock(graph);

    DeadCodeHelper.removeGotos(graph);

    ExceptionDeobfuscator.removeCircularRanges(graph);

    ExceptionDeobfuscator.restorePopRanges(graph);

    if (DecompilerContext.getOption(IFernflowerPreferences.REMOVE_EMPTY_RANGES)) {
      ExceptionDeobfuscator.removeEmptyRanges(graph);
    }

    if (DecompilerContext.getOption(IFernflowerPreferences.ENSURE_SYNCHRONIZED_MONITOR)) {
      // special case: search for 'synchronized' ranges w/o monitorexit instruction (as generated by Kotlin and Scala)
      DeadCodeHelper.extendSynchronizedRangeToMonitorexit(graph);
    }

    if (DecompilerContext.getOption(IFernflowerPreferences.NO_EXCEPTIONS_RETURN)) {
      // special case: single return instruction outside of a protected range
      DeadCodeHelper.incorporateValueReturns(graph);
    }

    //		ExceptionDeobfuscator.restorePopRanges(graph);
    ExceptionDeobfuscator.insertEmptyExceptionHandlerBlocks(graph);

    DeadCodeHelper.mergeBasicBlocks(graph);

    DecompilerContext.getCounterContainer().setCounter(CounterContainer.VAR_COUNTER, mt.getLocalVariables());

    if (ExceptionDeobfuscator.hasObfuscatedExceptions(graph)) {
      DecompilerContext.getLogger().writeMessage("Heavily obfuscated exception ranges found!", IFernflowerLogger.Severity.WARN);
      if (!ExceptionDeobfuscator.handleMultipleEntryExceptionRanges(graph)) {
        DecompilerContext.getLogger().writeMessage("Found multiple entry exception ranges which could not be splitted", IFernflowerLogger.Severity.WARN);
      }
      ExceptionDeobfuscator.insertDummyExceptionHandlerBlocks(graph, mt.getBytecodeVersion());
    }

    RootStatement root = DomHelper.parseGraph(graph);

    FinallyProcessor fProc = new FinallyProcessor(md, varProc);
    while (fProc.iterateGraph(cl, mt, root, graph)) {
      root = DomHelper.parseGraph(graph);
    }

    // remove synchronized exception handler
    // not until now because of comparison between synchronized statements in the finally cycle
    DomHelper.removeSynchronizedHandler(root);

    //		LabelHelper.lowContinueLabels(root, new HashSet<StatEdge>());

    SequenceHelper.condenseSequences(root);

    ClearStructHelper.clearStatements(root);

    ExprProcessor proc = new ExprProcessor(md, varProc);
    proc.processStatement(root, cl);

    SequenceHelper.condenseSequences(root);

    StackVarsProcessor stackProc = new StackVarsProcessor();

    do {
      stackProc.simplifyStackVars(root, mt, cl);
      varProc.setVarVersions(root);
    }
    while (new PPandMMHelper().findPPandMM(root));

    while (true) {
      LabelHelper.cleanUpEdges(root);

      do {
        MergeHelper.enhanceLoops(root);
      }
      while (LoopExtractHelper.extractLoops(root) || IfHelper.mergeAllIfs(root));

      if (DecompilerContext.getOption(IFernflowerPreferences.IDEA_NOT_NULL_ANNOTATION)) {
        if (IdeaNotNullHelper.removeHardcodedChecks(root, mt)) {
          SequenceHelper.condenseSequences(root);
          stackProc.simplifyStackVars(root, mt, cl);
          varProc.setVarVersions(root);
        }
      }

      LabelHelper.identifyLabels(root);

      if (InlineSingleBlockHelper.inlineSingleBlocks(root)) {
        continue;
      }

      // initializer may have at most one return point, so no transformation of method exits permitted
      if (isInitializer || !ExitHelper.condenseExits(root)) {
        break;
      }

      // FIXME: !!
      //if(!EliminateLoopsHelper.eliminateLoops(root)) {
      //  break;
      //}
    }

    ExitHelper.removeRedundantReturns(root);

    SecondaryFunctionsHelper.identifySecondaryFunctions(root, varProc);

    varProc.setVarDefinitions(root);

    // must be the last invocation, because it makes the statement structure inconsistent
    // FIXME: new edge type needed
    LabelHelper.replaceContinueWithBreak(root);

    PatternHelper.replaceAssignmentsWithPatternVariables(root, cl);
    SwitchHelper.simplifySwitchesOnString(root);

    mt.releaseResources();

    return root;
  }

  public RootStatement getResult() throws Throwable {
    Throwable t = error;
    if (t != null) throw t;
    return root;
  }

  public boolean isFinished() {
    return finished;
  }
}