/*
 * Copyright 2018, Google LLC
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

import com.apkide.smali.dexlib2.base.reference.BaseCallSiteReference;
import com.apkide.smali.dexlib2.iface.reference.CallSiteReference;
import com.apkide.smali.dexlib2.iface.reference.MethodHandleReference;
import com.apkide.smali.dexlib2.iface.reference.MethodProtoReference;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;
import com.apkide.smali.dexlib2.immutable.value.ImmutableEncodedValue;
import com.apkide.smali.dexlib2.immutable.value.ImmutableEncodedValueFactory;
import com.apkide.smali.util.ImmutableUtils;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ImmutableCallSiteReference extends BaseCallSiteReference implements ImmutableReference {
    @NonNull
    protected final String name;
    @NonNull protected final ImmutableMethodHandleReference methodHandle;
    @NonNull protected final String methodName;
    @NonNull protected final ImmutableMethodProtoReference methodProto;
    @NonNull protected final ImmutableList<? extends ImmutableEncodedValue> extraArguments;

    public ImmutableCallSiteReference(@NonNull String name, @NonNull MethodHandleReference methodHandle,
                                      @NonNull String methodName, @NonNull MethodProtoReference methodProto,
                                      @NonNull Iterable<? extends EncodedValue> extraArguments) {
        this.name = name;
        this.methodHandle = ImmutableMethodHandleReference.of(methodHandle);
        this.methodName = methodName;
        this.methodProto = ImmutableMethodProtoReference.of(methodProto);
        this.extraArguments = ImmutableEncodedValueFactory.immutableListOf(extraArguments);
    }

    public ImmutableCallSiteReference(@NonNull String name, @NonNull ImmutableMethodHandleReference methodHandle,
                                      @NonNull String methodName, @NonNull ImmutableMethodProtoReference methodProto,
                                      @Nullable ImmutableList<? extends ImmutableEncodedValue> extraArguments) {
        this.name = name;
        this.methodHandle = methodHandle;
        this.methodName = methodName;
        this.methodProto = methodProto;
        this.extraArguments = ImmutableUtils.nullToEmptyList(extraArguments);
    }

    @NonNull
    public static ImmutableCallSiteReference of(@NonNull CallSiteReference callSiteReference) {
        if (callSiteReference instanceof ImmutableCallSiteReference) {
            return (ImmutableCallSiteReference) callSiteReference;
        }
        return new ImmutableCallSiteReference(callSiteReference.getName(),
                ImmutableMethodHandleReference.of(callSiteReference.getMethodHandle()),
                callSiteReference.getMethodName(),
                ImmutableMethodProtoReference.of(callSiteReference.getMethodProto()),
                ImmutableEncodedValueFactory.immutableListOf(callSiteReference.getExtraArguments()));
    }

    @NonNull @Override public String getName() { return name; }
    @NonNull @Override public MethodHandleReference getMethodHandle() { return methodHandle; }
    @NonNull @Override public String getMethodName() { return methodName; }
    @NonNull @Override public MethodProtoReference getMethodProto() { return methodProto; }
    @NonNull @Override public List<? extends EncodedValue> getExtraArguments() { return extraArguments; }
}
