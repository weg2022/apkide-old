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


package com.apkide.smali.dexlib2.writer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.Opcode;
import com.apkide.smali.dexlib2.iface.instruction.Instruction;
import com.apkide.smali.dexlib2.iface.instruction.SwitchElement;
import com.apkide.smali.dexlib2.iface.reference.Reference;

import java.util.List;

public interface InstructionFactory<Ref extends Reference> {
    Instruction makeInstruction10t(@NonNull Opcode opcode, int codeOffset);
    Instruction makeInstruction10x(@NonNull Opcode opcode);
    Instruction makeInstruction11n(@NonNull Opcode opcode, int registerA, int literal);
    Instruction makeInstruction11x(@NonNull Opcode opcode, int registerA);
    Instruction makeInstruction12x(@NonNull Opcode opcode, int registerA, int registerB);
    Instruction makeInstruction20bc(@NonNull Opcode opcode, int verificationError, @NonNull Ref reference);
    Instruction makeInstruction20t(@NonNull Opcode opcode, int codeOffset);
    Instruction makeInstruction21c(@NonNull Opcode opcode, int registerA, @NonNull Ref reference);
    Instruction makeInstruction21ih(@NonNull Opcode opcode, int registerA, int literal);
    Instruction makeInstruction21lh(@NonNull Opcode opcode, int registerA, long literal);
    Instruction makeInstruction21s(@NonNull Opcode opcode, int registerA, int literal);
    Instruction makeInstruction21t(@NonNull Opcode opcode, int registerA, int codeOffset);
    Instruction makeInstruction22b(@NonNull Opcode opcode, int registerA, int registerB, int literal);
    Instruction makeInstruction22c(@NonNull Opcode opcode, int registerA, int registerB, @NonNull Ref reference);
    Instruction makeInstruction22s(@NonNull Opcode opcode, int registerA, int registerB, int literal);
    Instruction makeInstruction22t(@NonNull Opcode opcode, int registerA, int registerB, int codeOffset);
    Instruction makeInstruction22x(@NonNull Opcode opcode, int registerA, int registerB);
    Instruction makeInstruction23x(@NonNull Opcode opcode, int registerA, int registerB, int registerC);
    Instruction makeInstruction30t(@NonNull Opcode opcode, int codeOffset);
    Instruction makeInstruction31c(@NonNull Opcode opcode, int registerA, @NonNull Ref reference);
    Instruction makeInstruction31i(@NonNull Opcode opcode, int registerA, int literal);
    Instruction makeInstruction31t(@NonNull Opcode opcode, int registerA, int codeOffset);
    Instruction makeInstruction32x(@NonNull Opcode opcode, int registerA, int registerB);
    Instruction makeInstruction35c(@NonNull Opcode opcode, int registerCount, int registerC, int registerD, int registerE,
                            int registerF, int registerG, @NonNull Ref reference);
    Instruction makeInstruction3rc(@NonNull Opcode opcode,  int startRegister, int registerCount,
                            @NonNull Ref reference);
    Instruction makeInstruction51l(@NonNull Opcode opcode, int registerA, long literal);
    Instruction makeSparseSwitchPayload(@Nullable List<? extends SwitchElement> switchElements);
    Instruction makePackedSwitchPayload(@Nullable List<? extends SwitchElement> switchElements);
    Instruction makeArrayPayload(int elementWidth, @Nullable List<Number> arrayElements);
}
