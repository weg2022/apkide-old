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

import com.apkide.smali.dexlib2.MethodHandleType;
import com.apkide.smali.dexlib2.base.reference.BaseMethodHandleReference;
import com.apkide.smali.dexlib2.iface.reference.FieldReference;
import com.apkide.smali.dexlib2.iface.reference.MethodHandleReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.iface.reference.Reference;
import com.apkide.smali.util.ExceptionWithContext;

public class ImmutableMethodHandleReference extends BaseMethodHandleReference implements ImmutableReference {
    protected final int methodHandleType;
    @NonNull
    protected final ImmutableReference memberReference;

    public ImmutableMethodHandleReference(int methodHandleType, @NonNull ImmutableReference memberReference) {
        this.methodHandleType = methodHandleType;
        this.memberReference = memberReference;
    }

    public ImmutableMethodHandleReference(int methodHandleType, @NonNull Reference memberReference) {
        this.methodHandleType = methodHandleType;
        this.memberReference = ImmutableReferenceFactory.of(memberReference);
    }

    @NonNull
    public static ImmutableMethodHandleReference of(@NonNull MethodHandleReference methodHandleReference) {
        if (methodHandleReference instanceof ImmutableMethodHandleReference) {
            return (ImmutableMethodHandleReference) methodHandleReference;
        }
        int methodHandleType = methodHandleReference.getMethodHandleType();
        ImmutableReference memberReference;

        switch (methodHandleType) {
            case MethodHandleType.STATIC_PUT:
            case MethodHandleType.STATIC_GET:
            case MethodHandleType.INSTANCE_PUT:
            case MethodHandleType.INSTANCE_GET:
                memberReference = ImmutableFieldReference.of(
                        (FieldReference) methodHandleReference.getMemberReference());
                break;
            case MethodHandleType.INVOKE_STATIC:
            case MethodHandleType.INVOKE_INSTANCE:
            case MethodHandleType.INVOKE_CONSTRUCTOR:
            case MethodHandleType.INVOKE_DIRECT:
            case MethodHandleType.INVOKE_INTERFACE:
                memberReference = ImmutableMethodReference.of(
                        (MethodReference) methodHandleReference.getMemberReference());
                break;
            default:
                throw new ExceptionWithContext("Invalid method handle type: %d", methodHandleType);
        }
        return new ImmutableMethodHandleReference(methodHandleType, memberReference);
    }

    @Override public int getMethodHandleType() { return methodHandleType; }
    @NonNull @Override public Reference getMemberReference() { return memberReference; }
}
