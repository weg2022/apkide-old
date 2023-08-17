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

package com.apkide.smali.dexlib2.immutable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.HiddenApiRestriction;
import com.apkide.smali.dexlib2.base.reference.BaseMethodReference;
import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.iface.Method;
import com.apkide.smali.dexlib2.iface.MethodImplementation;
import com.apkide.smali.dexlib2.iface.MethodParameter;
import com.apkide.smali.util.ImmutableConverter;
import com.apkide.smali.util.ImmutableUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;

import java.util.Set;

public class ImmutableMethod extends BaseMethodReference implements Method {
    @NonNull
    protected final String definingClass;
    @NonNull protected final String name;
    @NonNull protected final ImmutableList<? extends ImmutableMethodParameter> parameters;
    @NonNull protected final String returnType;
    protected final int accessFlags;
    @NonNull protected final ImmutableSet<? extends ImmutableAnnotation> annotations;
    @NonNull protected final ImmutableSet<HiddenApiRestriction> hiddenApiRestrictions;
    @Nullable protected final ImmutableMethodImplementation methodImplementation;

    public ImmutableMethod(@NonNull String definingClass,
                           @NonNull String name,
                           @Nullable Iterable<? extends MethodParameter> parameters,
                           @NonNull String returnType,
                           int accessFlags,
                           @Nullable Set<? extends Annotation> annotations,
                           @Nullable Set<HiddenApiRestriction> hiddenApiRestrictions,
                           @Nullable MethodImplementation methodImplementation) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = ImmutableMethodParameter.immutableListOf(parameters);
        this.returnType = returnType;
        this.accessFlags = accessFlags;
        this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
        this.hiddenApiRestrictions =
                hiddenApiRestrictions == null ? ImmutableSet.of() : ImmutableSet.copyOf(hiddenApiRestrictions);
        this.methodImplementation = ImmutableMethodImplementation.of(methodImplementation);
    }

    public ImmutableMethod(@NonNull String definingClass,
                           @NonNull String name,
                           @Nullable ImmutableList<? extends ImmutableMethodParameter> parameters,
                           @NonNull String returnType,
                           int accessFlags,
                           @Nullable ImmutableSet<? extends ImmutableAnnotation> annotations,
                           @Nullable ImmutableSet<HiddenApiRestriction> hiddenApiRestrictions,
                           @Nullable ImmutableMethodImplementation methodImplementation) {
        this.definingClass = definingClass;
        this.name = name;
        this.parameters = ImmutableUtils.nullToEmptyList(parameters);
        this.returnType = returnType;
        this.accessFlags = accessFlags;
        this.annotations = ImmutableUtils.nullToEmptySet(annotations);
        this.hiddenApiRestrictions = ImmutableUtils.nullToEmptySet(hiddenApiRestrictions);
        this.methodImplementation = methodImplementation;
    }

    public static ImmutableMethod of(Method method) {
        if (method instanceof ImmutableMethod) {
            return (ImmutableMethod)method;
        }
        return new ImmutableMethod(
                method.getDefiningClass(),
                method.getName(),
                method.getParameters(),
                method.getReturnType(),
                method.getAccessFlags(),
                method.getAnnotations(),
                method.getHiddenApiRestrictions(),
                method.getImplementation());
    }

    @Override @NonNull public String getDefiningClass() { return definingClass; }
    @Override @NonNull public String getName() { return name; }
    @Override @NonNull public ImmutableList<? extends CharSequence> getParameterTypes() { return parameters; }
    @Override @NonNull public ImmutableList<? extends ImmutableMethodParameter> getParameters() { return parameters; }
    @Override @NonNull public String getReturnType() { return returnType; }
    @Override public int getAccessFlags() { return accessFlags; }
    @Override @NonNull public ImmutableSet<? extends ImmutableAnnotation> getAnnotations() { return annotations; }
    @NonNull @Override public Set<HiddenApiRestriction> getHiddenApiRestrictions() { return hiddenApiRestrictions; }
    @Override @Nullable public ImmutableMethodImplementation getImplementation() { return methodImplementation; }

    @NonNull
    public static ImmutableSortedSet<ImmutableMethod> immutableSetOf(@Nullable Iterable<? extends Method> list) {
        return CONVERTER.toSortedSet(Ordering.natural(), list);
    }

    private static final ImmutableConverter<ImmutableMethod, Method> CONVERTER =
            new ImmutableConverter<ImmutableMethod, Method>() {
                @Override
                protected boolean isImmutable(@NonNull Method item) {
                    return item instanceof ImmutableMethod;
                }

                @NonNull
                @Override
                protected ImmutableMethod makeImmutable(@NonNull Method item) {
                    return ImmutableMethod.of(item);
                }
            };
}
