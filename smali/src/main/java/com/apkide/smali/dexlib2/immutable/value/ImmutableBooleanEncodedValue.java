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

package com.apkide.smali.dexlib2.immutable.value;

import com.apkide.smali.dexlib2.base.value.BaseBooleanEncodedValue;
import com.apkide.smali.dexlib2.iface.value.BooleanEncodedValue;

public class ImmutableBooleanEncodedValue extends BaseBooleanEncodedValue implements ImmutableEncodedValue {
    public static final ImmutableBooleanEncodedValue TRUE_VALUE = new ImmutableBooleanEncodedValue(true);
    public static final ImmutableBooleanEncodedValue FALSE_VALUE = new ImmutableBooleanEncodedValue(false);

    protected final boolean value;

    private ImmutableBooleanEncodedValue(boolean value) {
        this.value = value;
    }

    public static ImmutableBooleanEncodedValue forBoolean(boolean value) {
        return value?TRUE_VALUE:FALSE_VALUE;
    }

    public static ImmutableBooleanEncodedValue of(BooleanEncodedValue booleanEncodedValue) {
        return forBoolean(booleanEncodedValue.getValue());
    }

    @Override public boolean getValue() { return value; }
}
