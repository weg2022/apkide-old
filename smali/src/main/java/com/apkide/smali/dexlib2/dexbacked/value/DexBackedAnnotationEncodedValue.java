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

package com.apkide.smali.dexlib2.dexbacked.value;

import androidx.annotation.NonNull;

import com.apkide.smali.dexlib2.base.value.BaseAnnotationEncodedValue;
import com.apkide.smali.dexlib2.dexbacked.DexBackedAnnotationElement;
import com.apkide.smali.dexlib2.dexbacked.DexBackedDexFile;
import com.apkide.smali.dexlib2.dexbacked.DexReader;
import com.apkide.smali.dexlib2.dexbacked.util.VariableSizeSet;
import com.apkide.smali.dexlib2.iface.value.AnnotationEncodedValue;

import java.util.Set;

public class DexBackedAnnotationEncodedValue extends BaseAnnotationEncodedValue implements AnnotationEncodedValue {
    @NonNull
    public final DexBackedDexFile dexFile;
    @NonNull public final String type;
    private final int elementCount;
    private final int elementsOffset;

    public DexBackedAnnotationEncodedValue(@NonNull DexBackedDexFile dexFile, @NonNull DexReader reader) {
        this.dexFile = dexFile;
        this.type = dexFile.getTypeSection().get(reader.readSmallUleb128());
        this.elementCount = reader.readSmallUleb128();
        this.elementsOffset = reader.getOffset();
        skipElements(reader, elementCount);
    }

    public static void skipFrom(@NonNull DexReader reader) {
        reader.skipUleb128(); // type
        int elementCount = reader.readSmallUleb128();
        skipElements(reader, elementCount);
    }

    private static void skipElements(@NonNull DexReader reader, int elementCount) {
        for (int i=0; i<elementCount; i++) {
            reader.skipUleb128();
            DexBackedEncodedValue.skipFrom(reader);
        }
    }

    @NonNull @Override public String getType() { return type; }

    @NonNull
    @Override
    public Set<? extends DexBackedAnnotationElement> getElements() {
        return new VariableSizeSet<DexBackedAnnotationElement>(dexFile.getDataBuffer(), elementsOffset, elementCount) {
            @NonNull
            @Override
            protected DexBackedAnnotationElement readNextItem(@NonNull DexReader dexReader, int index) {
                return new DexBackedAnnotationElement(dexFile, dexReader);
            }
        };
    }
}
