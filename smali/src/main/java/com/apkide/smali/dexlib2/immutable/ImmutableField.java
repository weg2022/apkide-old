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
import com.apkide.smali.dexlib2.base.reference.BaseFieldReference;
import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.iface.Field;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;
import com.apkide.smali.dexlib2.immutable.value.ImmutableEncodedValue;
import com.apkide.smali.dexlib2.immutable.value.ImmutableEncodedValueFactory;
import com.apkide.smali.util.ImmutableConverter;
import com.apkide.smali.util.ImmutableUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Set;

public class ImmutableField extends BaseFieldReference implements Field {
    @NonNull
    protected final String definingClass;
    @NonNull protected final String name;
    @NonNull protected final String type;
    protected final int accessFlags;
    @Nullable protected final ImmutableEncodedValue initialValue;
    @NonNull protected final ImmutableSet<? extends ImmutableAnnotation> annotations;
    @NonNull protected final ImmutableSet<HiddenApiRestriction> hiddenApiRestrictions;

    public ImmutableField(@NonNull String definingClass,
                          @NonNull String name,
                          @NonNull String type,
                          int accessFlags,
                          @Nullable EncodedValue initialValue,
                          @Nullable Collection<? extends Annotation> annotations,
                          @Nullable Set<HiddenApiRestriction> hiddenApiRestrictions) {
        this.definingClass = definingClass;
        this.name = name;
        this.type = type;
        this.accessFlags = accessFlags;
        this.initialValue = ImmutableEncodedValueFactory.ofNullable(initialValue);
        this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
        this.hiddenApiRestrictions =
                hiddenApiRestrictions == null ? ImmutableSet.of() : ImmutableSet.copyOf(hiddenApiRestrictions);
    }

    public ImmutableField(@NonNull String definingClass,
                          @NonNull String name,
                          @NonNull String type,
                          int accessFlags,
                          @Nullable ImmutableEncodedValue initialValue,
                          @Nullable ImmutableSet<? extends ImmutableAnnotation> annotations,
                          @Nullable ImmutableSet<HiddenApiRestriction> hiddenApiRestrictions) {
        this.definingClass = definingClass;
        this.name = name;
        this.type = type;
        this.accessFlags = accessFlags;
        this.initialValue = initialValue;
        this.annotations = ImmutableUtils.nullToEmptySet(annotations);
        this.hiddenApiRestrictions = ImmutableUtils.nullToEmptySet(hiddenApiRestrictions);
    }

    public static ImmutableField of(Field field) {
        if (field instanceof  ImmutableField) {
            return (ImmutableField)field;
        }
        return new ImmutableField(
                field.getDefiningClass(),
                field.getName(),
                field.getType(),
                field.getAccessFlags(),
                field.getInitialValue(),
                field.getAnnotations(),
                field.getHiddenApiRestrictions());
    }

    @NonNull @Override public String getDefiningClass() { return definingClass; }
    @NonNull @Override public String getName() { return name; }
    @NonNull @Override public String getType() { return type; }
    @Override public int getAccessFlags() { return accessFlags; }
    @Override public EncodedValue getInitialValue() { return initialValue;}
    @NonNull @Override public ImmutableSet<? extends ImmutableAnnotation> getAnnotations() { return annotations; }
    @NonNull @Override public Set<HiddenApiRestriction> getHiddenApiRestrictions() { return hiddenApiRestrictions; }

    @NonNull
    public static ImmutableSortedSet<ImmutableField> immutableSetOf(@Nullable Iterable<? extends Field> list) {
        return CONVERTER.toSortedSet(Ordering.natural(), list);
    }

    private static final ImmutableConverter<ImmutableField, Field> CONVERTER =
            new ImmutableConverter<ImmutableField, Field>() {
                @Override
                protected boolean isImmutable(@NonNull Field item) {
                    return item instanceof ImmutableField;
                }

                @NonNull
                @Override
                protected ImmutableField makeImmutable(@NonNull Field item) {
                    return ImmutableField.of(item);
                }
            };
}
