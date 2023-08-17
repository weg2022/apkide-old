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

package com.apkide.smali.dexlib2.base.reference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.formatter.DexFormatter;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.util.CharSequenceUtils;
import com.apkide.smali.util.CollectionUtils;
import com.google.common.collect.Ordering;

public abstract class BaseMethodReference extends BaseReference implements MethodReference {
    @Override
    public int hashCode() {
        int hashCode = getDefiningClass().hashCode();
        hashCode = hashCode*31 + getName().hashCode();
        hashCode = hashCode*31 + getReturnType().hashCode();
        return hashCode*31 + getParameterTypes().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o != null && o instanceof MethodReference) {
            MethodReference other = (MethodReference)o;
            return getDefiningClass().equals(other.getDefiningClass()) &&
                   getName().equals(other.getName()) &&
                   getReturnType().equals(other.getReturnType()) &&
                   CharSequenceUtils.listEquals(getParameterTypes(), other.getParameterTypes());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull MethodReference o) {
        int res = getDefiningClass().compareTo(o.getDefiningClass());
        if (res != 0) return res;
        res = getName().compareTo(o.getName());
        if (res != 0) return res;
        res = getReturnType().compareTo(o.getReturnType());
        if (res != 0) return res;
        return CollectionUtils.compareAsIterable(Ordering.usingToString(), getParameterTypes(), o.getParameterTypes());
    }

    @Override public String toString() {
        return DexFormatter.INSTANCE.getMethodDescriptor(this);
    }
}