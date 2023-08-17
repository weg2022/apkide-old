/*
 * Copyright 2014, Google LLC
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google LLC nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.apkide.smali.dexlib2.rewriter;

import androidx.annotation.NonNull;

import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.iface.AnnotationElement;
import com.apkide.smali.dexlib2.iface.ClassDef;
import com.apkide.smali.dexlib2.iface.DexFile;
import com.apkide.smali.dexlib2.iface.ExceptionHandler;
import com.apkide.smali.dexlib2.iface.Field;
import com.apkide.smali.dexlib2.iface.Method;
import com.apkide.smali.dexlib2.iface.MethodImplementation;
import com.apkide.smali.dexlib2.iface.MethodParameter;
import com.apkide.smali.dexlib2.iface.TryBlock;
import com.apkide.smali.dexlib2.iface.debug.DebugItem;
import com.apkide.smali.dexlib2.iface.instruction.Instruction;
import com.apkide.smali.dexlib2.iface.reference.FieldReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;

public class RewriterModule {
    @NonNull
    public Rewriter<DexFile> getDexFileRewriter(@NonNull Rewriters rewriters) {
        return new DexFileRewriter(rewriters);
    }

    @NonNull public Rewriter<ClassDef> getClassDefRewriter(@NonNull Rewriters rewriters) {
        return new ClassDefRewriter(rewriters);
    }

    @NonNull public Rewriter<Field> getFieldRewriter(@NonNull Rewriters rewriters) {
        return new FieldRewriter(rewriters);
    }

    @NonNull public Rewriter<Method> getMethodRewriter(@NonNull Rewriters rewriters) {
        return new MethodRewriter(rewriters);
    }

    @NonNull public Rewriter<MethodParameter> getMethodParameterRewriter(@NonNull Rewriters rewriters) {
        return new MethodParameterRewriter(rewriters);
    }

    @NonNull public Rewriter<MethodImplementation> getMethodImplementationRewriter(@NonNull Rewriters rewriters) {
        return new MethodImplementationRewriter(rewriters);
    }

    @NonNull public Rewriter<Instruction> getInstructionRewriter(@NonNull Rewriters rewriters) {
        return new InstructionRewriter(rewriters);
    }

    @NonNull public Rewriter<TryBlock<? extends ExceptionHandler>> getTryBlockRewriter(@NonNull Rewriters rewriters) {
        return new TryBlockRewriter(rewriters);
    }

    @NonNull public Rewriter<ExceptionHandler> getExceptionHandlerRewriter(@NonNull Rewriters rewriters) {
        return new ExceptionHandlerRewriter(rewriters);
    }

    @NonNull public Rewriter<DebugItem> getDebugItemRewriter(@NonNull Rewriters rewriters) {
        return new DebugItemRewriter(rewriters);
    }

    @NonNull public Rewriter<String> getTypeRewriter(@NonNull Rewriters rewriters) {
        return new TypeRewriter();
    }

    @NonNull public Rewriter<FieldReference> getFieldReferenceRewriter(@NonNull Rewriters rewriters) {
        return new FieldReferenceRewriter(rewriters);
    }

    @NonNull public Rewriter<MethodReference> getMethodReferenceRewriter(@NonNull Rewriters rewriters) {
        return new MethodReferenceRewriter(rewriters);
    }

    @NonNull public Rewriter<Annotation> getAnnotationRewriter(@NonNull Rewriters rewriters) {
        return new AnnotationRewriter(rewriters);
    }

    @NonNull public Rewriter<AnnotationElement> getAnnotationElementRewriter(@NonNull Rewriters rewriters) {
        return new AnnotationElementRewriter(rewriters);
    }

    @NonNull public Rewriter<EncodedValue> getEncodedValueRewriter(@NonNull Rewriters rewriters) {
        return new EncodedValueRewriter(rewriters);
    }
}
