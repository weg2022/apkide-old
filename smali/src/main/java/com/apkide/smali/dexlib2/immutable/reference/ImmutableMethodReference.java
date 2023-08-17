/*
 * Copyright 2012, Google LLC
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

package com.apkide.smali.dexlib2.immutable.reference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.base.reference.BaseMethodReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.immutable.util.CharSequenceConverter;
import com.apkide.smali.util.ImmutableUtils;
import com.google.common.collect.ImmutableList;

public class ImmutableMethodReference extends BaseMethodReference implements ImmutableReference {
    @NonNull
    protected final String definingClass;
    @NonNull protected final String name;
    @NonNull protected final ImmutableList<String> parameters;
    @NonNull protected final String returnType;

    public ImmutableMethodReference(@NonNull String definingClass,
                                    @NonNull String name,
                                    @Nullable Iterable<? extends CharSequence> parameters,
                                    @NonNull String returnType) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = CharSequenceConverter.immutableStringList(parameters);
        this.returnType = returnType;
    }

    public ImmutableMethodReference(@NonNull String definingClass,
                                    @NonNull String name,
                                    @Nullable ImmutableList<String> parameters,
                                    @NonNull String returnType) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = ImmutableUtils.nullToEmptyList(parameters);
        this.returnType = returnType;
    }

    @NonNull
    public static ImmutableMethodReference of(@NonNull MethodReference methodReference) {
        if (methodReference instanceof ImmutableMethodReference) {
            return (ImmutableMethodReference)methodReference;
        }
        return new ImmutableMethodReference(
                methodReference.getDefiningClass(),
                methodReference.getName(),
                methodReference.getParameterTypes(),
                methodReference.getReturnType());
    }

    @NonNull @Override public String getDefiningClass() { return definingClass; }
    @NonNull @Override public String getName() { return name; }
    @NonNull @Override public ImmutableList<String> getParameterTypes() { return parameters; }
    @NonNull @Override public String getReturnType() { return returnType; }


}
