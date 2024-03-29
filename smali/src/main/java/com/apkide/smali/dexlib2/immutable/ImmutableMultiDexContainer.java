/*
 * Copyright 2019, Google LLC
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

import com.apkide.smali.dexlib2.iface.MultiDexContainer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class ImmutableMultiDexContainer implements MultiDexContainer<ImmutableDexFile> {

    private final ImmutableMap<String, ImmutableDexEntry> entries;

    public ImmutableMultiDexContainer(Map<String, ImmutableDexFile> entries) {
        ImmutableMap.Builder<String, ImmutableDexEntry> builder = ImmutableMap.builder();

        for (Map.Entry<String, ImmutableDexFile> entry : entries.entrySet()) {
            ImmutableDexEntry dexEntry = new ImmutableDexEntry(entry.getKey(), entry.getValue());
            builder.put(dexEntry.getEntryName(), dexEntry);
        }

        this.entries = builder.build();
    }


    @NonNull
    @Override
    public List<String> getDexEntryNames() {
        return ImmutableList.copyOf(entries.keySet());
    }

    @Nullable
    @Override
    public ImmutableDexEntry getEntry(@NonNull String entryName) {
        return entries.get(entryName);
    }

    public class ImmutableDexEntry implements DexEntry<ImmutableDexFile> {

        private final String entryName;
        private final ImmutableDexFile dexFile;

        protected ImmutableDexEntry(String entryName, ImmutableDexFile dexFile) {
            this.entryName = entryName;
            this.dexFile = dexFile;
        }

        @NonNull
        @Override
        public String getEntryName() {
            return entryName;
        }

        @NonNull
        @Override
        public ImmutableDexFile getDexFile() {
            return dexFile;
        }

        @NonNull
        @Override
        public MultiDexContainer<? extends ImmutableDexFile> getContainer() {
            return ImmutableMultiDexContainer.this;
        }
    }
}
