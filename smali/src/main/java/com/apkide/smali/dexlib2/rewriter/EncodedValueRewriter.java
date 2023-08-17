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

import com.apkide.smali.dexlib2.ValueType;
import com.apkide.smali.dexlib2.base.value.BaseAnnotationEncodedValue;
import com.apkide.smali.dexlib2.base.value.BaseArrayEncodedValue;
import com.apkide.smali.dexlib2.base.value.BaseEnumEncodedValue;
import com.apkide.smali.dexlib2.base.value.BaseFieldEncodedValue;
import com.apkide.smali.dexlib2.base.value.BaseMethodEncodedValue;
import com.apkide.smali.dexlib2.base.value.BaseTypeEncodedValue;
import com.apkide.smali.dexlib2.iface.AnnotationElement;
import com.apkide.smali.dexlib2.iface.reference.FieldReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.iface.value.AnnotationEncodedValue;
import com.apkide.smali.dexlib2.iface.value.ArrayEncodedValue;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;
import com.apkide.smali.dexlib2.iface.value.EnumEncodedValue;
import com.apkide.smali.dexlib2.iface.value.FieldEncodedValue;
import com.apkide.smali.dexlib2.iface.value.MethodEncodedValue;
import com.apkide.smali.dexlib2.iface.value.TypeEncodedValue;

import java.util.List;
import java.util.Set;

public class EncodedValueRewriter implements Rewriter<EncodedValue> {
    @NonNull
    protected final Rewriters rewriters;

    public EncodedValueRewriter(@NonNull Rewriters rewriters) {
        this.rewriters = rewriters;
    }

    @NonNull @Override public EncodedValue rewrite(@NonNull EncodedValue encodedValue) {
        switch (encodedValue.getValueType()) {
            case ValueType.TYPE:
                return new RewrittenTypeEncodedValue((TypeEncodedValue)encodedValue);
            case ValueType.FIELD:
                return new RewrittenFieldEncodedValue((FieldEncodedValue)encodedValue);
            case ValueType.METHOD:
                return new RewrittenMethodEncodedValue((MethodEncodedValue)encodedValue);
            case ValueType.ENUM:
                return new RewrittenEnumEncodedValue((EnumEncodedValue)encodedValue);
            case ValueType.ARRAY:
                return new RewrittenArrayEncodedValue((ArrayEncodedValue)encodedValue);
            case ValueType.ANNOTATION:
                return new RewrittenAnnotationEncodedValue((AnnotationEncodedValue)encodedValue);
            default:
                return encodedValue;
        }
    }

    protected class RewrittenTypeEncodedValue extends BaseTypeEncodedValue {
        @NonNull protected TypeEncodedValue typeEncodedValue;

        public RewrittenTypeEncodedValue(@NonNull TypeEncodedValue typeEncodedValue) {
            this.typeEncodedValue = typeEncodedValue;
        }

        @Override @NonNull public String getValue() {
            return rewriters.getTypeRewriter().rewrite(typeEncodedValue.getValue());
        }
    }

    protected class RewrittenFieldEncodedValue extends BaseFieldEncodedValue {
        @NonNull protected FieldEncodedValue fieldEncodedValue;

        public RewrittenFieldEncodedValue(@NonNull FieldEncodedValue fieldEncodedValue) {
            this.fieldEncodedValue = fieldEncodedValue;
        }

        @Override @NonNull public FieldReference getValue() {
            return rewriters.getFieldReferenceRewriter().rewrite(fieldEncodedValue.getValue());
        }
    }

    protected class RewrittenEnumEncodedValue extends BaseEnumEncodedValue {
        @NonNull protected EnumEncodedValue enumEncodedValue;

        public RewrittenEnumEncodedValue(@NonNull EnumEncodedValue enumEncodedValue) {
            this.enumEncodedValue = enumEncodedValue;
        }

        @Override @NonNull public FieldReference getValue() {
            return rewriters.getFieldReferenceRewriter().rewrite(enumEncodedValue.getValue());
        }
    }

    protected class RewrittenMethodEncodedValue extends BaseMethodEncodedValue {
        @NonNull protected MethodEncodedValue methodEncodedValue;

        public RewrittenMethodEncodedValue(@NonNull MethodEncodedValue methodEncodedValue) {
            this.methodEncodedValue = methodEncodedValue;
        }

        @Override @NonNull public MethodReference getValue() {
            return rewriters.getMethodReferenceRewriter().rewrite(methodEncodedValue.getValue());
        }
    }

    protected class RewrittenArrayEncodedValue extends BaseArrayEncodedValue {
        @NonNull protected ArrayEncodedValue arrayEncodedValue;

        public RewrittenArrayEncodedValue(@NonNull ArrayEncodedValue arrayEncodedValue) {
            this.arrayEncodedValue = arrayEncodedValue;
        }

        @Override @NonNull public List<? extends EncodedValue> getValue() {
            return RewriterUtils.rewriteList(rewriters.getEncodedValueRewriter(), arrayEncodedValue.getValue());
        }
    }

    protected class RewrittenAnnotationEncodedValue extends BaseAnnotationEncodedValue {
        @NonNull protected AnnotationEncodedValue annotationEncodedValue;

        public RewrittenAnnotationEncodedValue(@NonNull AnnotationEncodedValue annotationEncodedValue) {
            this.annotationEncodedValue = annotationEncodedValue;
        }

        @NonNull @Override public String getType() {
            return rewriters.getTypeRewriter().rewrite(annotationEncodedValue.getType());
        }

        @NonNull @Override public Set<? extends AnnotationElement> getElements() {
            return RewriterUtils.rewriteSet(rewriters.getAnnotationElementRewriter(),
                    annotationEncodedValue.getElements());
        }
    }
}
