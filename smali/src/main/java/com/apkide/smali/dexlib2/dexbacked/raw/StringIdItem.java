/*
 * Copyright 2013, Google LLC
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

package com.apkide.smali.dexlib2.dexbacked.raw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.dexbacked.DexBackedDexFile;
import com.apkide.smali.dexlib2.dexbacked.raw.util.DexAnnotator;
import com.apkide.smali.dexlib2.util.AnnotatedBytes;
import com.apkide.smali.util.StringUtils;

public class StringIdItem {
    public static final int ITEM_SIZE = 4;

    @NonNull
    public static SectionAnnotator makeAnnotator(@NonNull DexAnnotator annotator, @NonNull MapItem mapItem) {
        return new SectionAnnotator(annotator, mapItem) {
            @NonNull @Override public String getItemName() {
                return "string_id_item";
            }

            @Override
            public void annotateItem(@NonNull AnnotatedBytes out, int itemIndex, @Nullable String itemIdentity) {
                int stringDataOffset = dexFile.getBuffer().readSmallUint(out.getCursor());
                try {
                    String stringValue = dexFile.getStringSection().get(itemIndex);
                    out.annotate(4, "string_data_item[0x%x]: \"%s\"", stringDataOffset,
                            StringUtils.escapeString(stringValue));
                    return;
                } catch (Exception ex) {
                    System.err.print("Error while resolving string value at index: ");
                    System.err.print(itemIndex);
                    ex.printStackTrace(System.err);
                }

                out.annotate(4, "string_id_item[0x%x]", stringDataOffset);
            }
        };
    }

    @NonNull
    public static String getReferenceAnnotation(@NonNull DexBackedDexFile dexFile, int stringIndex) {
        return getReferenceAnnotation(dexFile, stringIndex, false);
    }

    public static String getReferenceAnnotation(@NonNull DexBackedDexFile dexFile, int stringIndex, boolean quote) {
        try {
            String string = dexFile.getStringSection().get(stringIndex);
            if (quote) {
                string = String.format("\"%s\"", StringUtils.escapeString(string));
            }
            return String.format("string_id_item[%d]: %s", stringIndex, string);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return String.format("string_id_item[%d]", stringIndex);
    }


    @NonNull
    public static String getOptionalReferenceAnnotation(@NonNull DexBackedDexFile dexFile, int stringIndex) {
        return getOptionalReferenceAnnotation(dexFile, stringIndex, false);
    }

    public static String getOptionalReferenceAnnotation(@NonNull DexBackedDexFile dexFile, int stringIndex,
                                                        boolean quote) {
        if (stringIndex == -1) {
            return "string_id_item[NO_INDEX]";
        }
        return getReferenceAnnotation(dexFile, stringIndex, quote);
    }
}
