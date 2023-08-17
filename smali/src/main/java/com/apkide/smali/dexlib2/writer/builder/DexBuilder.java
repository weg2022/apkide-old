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

package com.apkide.smali.dexlib2.writer.builder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.HiddenApiRestriction;
import com.apkide.smali.dexlib2.Opcodes;
import com.apkide.smali.dexlib2.ValueType;
import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.iface.AnnotationElement;
import com.apkide.smali.dexlib2.iface.MethodImplementation;
import com.apkide.smali.dexlib2.iface.MethodParameter;
import com.apkide.smali.dexlib2.iface.reference.CallSiteReference;
import com.apkide.smali.dexlib2.iface.reference.FieldReference;
import com.apkide.smali.dexlib2.iface.reference.MethodHandleReference;
import com.apkide.smali.dexlib2.iface.reference.MethodProtoReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.iface.reference.Reference;
import com.apkide.smali.dexlib2.iface.reference.StringReference;
import com.apkide.smali.dexlib2.iface.reference.TypeReference;
import com.apkide.smali.dexlib2.iface.value.AnnotationEncodedValue;
import com.apkide.smali.dexlib2.iface.value.ArrayEncodedValue;
import com.apkide.smali.dexlib2.iface.value.BooleanEncodedValue;
import com.apkide.smali.dexlib2.iface.value.ByteEncodedValue;
import com.apkide.smali.dexlib2.iface.value.CharEncodedValue;
import com.apkide.smali.dexlib2.iface.value.DoubleEncodedValue;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;
import com.apkide.smali.dexlib2.iface.value.EnumEncodedValue;
import com.apkide.smali.dexlib2.iface.value.FieldEncodedValue;
import com.apkide.smali.dexlib2.iface.value.FloatEncodedValue;
import com.apkide.smali.dexlib2.iface.value.IntEncodedValue;
import com.apkide.smali.dexlib2.iface.value.LongEncodedValue;
import com.apkide.smali.dexlib2.iface.value.MethodEncodedValue;
import com.apkide.smali.dexlib2.iface.value.MethodHandleEncodedValue;
import com.apkide.smali.dexlib2.iface.value.MethodTypeEncodedValue;
import com.apkide.smali.dexlib2.iface.value.ShortEncodedValue;
import com.apkide.smali.dexlib2.iface.value.StringEncodedValue;
import com.apkide.smali.dexlib2.iface.value.TypeEncodedValue;
import com.apkide.smali.dexlib2.util.FieldUtil;
import com.apkide.smali.dexlib2.writer.DexWriter;
import com.apkide.smali.dexlib2.writer.util.StaticInitializerUtil;
import com.apkide.smali.util.ExceptionWithContext;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DexBuilder extends DexWriter<BuilderStringReference, BuilderStringReference, BuilderTypeReference,
        BuilderTypeReference, BuilderMethodProtoReference, BuilderFieldReference, BuilderMethodReference,
        BuilderClassDef, BuilderCallSiteReference, BuilderMethodHandleReference, BuilderAnnotation, BuilderAnnotationSet, BuilderTypeList,
        BuilderField, BuilderMethod, BuilderEncodedValues.BuilderArrayEncodedValue, BuilderEncodedValues.BuilderEncodedValue, BuilderAnnotationElement,
        BuilderStringPool, BuilderTypePool, BuilderProtoPool, BuilderFieldPool, BuilderMethodPool, BuilderClassPool,
        BuilderCallSitePool, BuilderMethodHandlePool, BuilderTypeListPool, BuilderAnnotationPool,
        BuilderAnnotationSetPool, BuilderEncodedArrayPool> {

    public DexBuilder(@NonNull Opcodes opcodes) {
        super(opcodes);
    }

    @NonNull @Override protected SectionProvider getSectionProvider() {
        return new DexBuilderSectionProvider();
    }

    @NonNull public BuilderField internField(@NonNull String definingClass,
                                             @NonNull String name,
                                             @NonNull String type,
                                             int accessFlags,
                                             @Nullable EncodedValue initialValue,
                                             @NonNull Set<? extends Annotation> annotations,
                                             @NonNull Set<HiddenApiRestriction> hiddenApiRestrictions) {
        return new BuilderField(fieldSection.internField(definingClass, name, type),
                accessFlags,
                internNullableEncodedValue(initialValue),
                annotationSetSection.internAnnotationSet(annotations),
                hiddenApiRestrictions);
    }

    @NonNull public BuilderMethod internMethod(@NonNull String definingClass,
                                               @NonNull String name,
                                               @Nullable List<? extends MethodParameter> parameters,
                                               @NonNull String returnType,
                                               int accessFlags,
                                               @NonNull Set<? extends Annotation> annotations,
                                               @NonNull Set<HiddenApiRestriction> hiddenApiRestrictions,
                                               @Nullable MethodImplementation methodImplementation) {
        if (parameters == null) {
            parameters = ImmutableList.of();
        }
        return new BuilderMethod(methodSection.internMethod(definingClass, name, parameters, returnType),
                internMethodParameters(parameters),
                accessFlags,
                annotationSetSection.internAnnotationSet(annotations),
                hiddenApiRestrictions,
                methodImplementation);
    }

    @NonNull public BuilderClassDef internClassDef(@NonNull String type,
                                                   int accessFlags,
                                                   @Nullable String superclass,
                                                   @Nullable List<String> interfaces,
                                                   @Nullable String sourceFile,
                                                   @NonNull Set<? extends Annotation> annotations,
                                                   @Nullable Iterable<? extends BuilderField> fields,
                                                   @Nullable Iterable<? extends BuilderMethod> methods) {
        if (interfaces == null) {
            interfaces = ImmutableList.of();
        } else {
            Set<String> interfaces_copy = Sets.newHashSet(interfaces);
            Iterator<String> interfaceIterator = interfaces.iterator();
            while (interfaceIterator.hasNext()) {
                String iface = interfaceIterator.next();
                if (!interfaces_copy.contains(iface)) {
                    interfaceIterator.remove();
                } else {
                    interfaces_copy.remove(iface);
                }
            }
        }

        ImmutableSortedSet<BuilderField> staticFields = null;
        ImmutableSortedSet<BuilderField> instanceFields = null;
        BuilderEncodedValues.BuilderArrayEncodedValue internedStaticInitializers = null;
        if (fields != null) {
            staticFields = ImmutableSortedSet.copyOf(Iterables.filter(fields, FieldUtil.FIELD_IS_STATIC));
            instanceFields = ImmutableSortedSet.copyOf(Iterables.filter(fields, FieldUtil.FIELD_IS_INSTANCE));
            ArrayEncodedValue staticInitializers = StaticInitializerUtil.getStaticInitializers(staticFields);
            if (staticInitializers != null) {
                internedStaticInitializers = encodedArraySection.internArrayEncodedValue(staticInitializers);
            }
        }

        return classSection.internClass(new BuilderClassDef(typeSection.internType(type),
                accessFlags,
                typeSection.internNullableType(superclass),
                typeListSection.internTypeList(interfaces),
                stringSection.internNullableString(sourceFile),
                annotationSetSection.internAnnotationSet(annotations),
                staticFields,
                instanceFields,
                methods,
                internedStaticInitializers));
    }

    public BuilderCallSiteReference internCallSite(@NonNull CallSiteReference callSiteReference) {
        return callSiteSection.internCallSite(callSiteReference);
    }

    public BuilderMethodHandleReference internMethodHandle(@NonNull MethodHandleReference methodHandleReference) {
        return methodHandleSection.internMethodHandle(methodHandleReference);
    }

    @NonNull public BuilderStringReference internStringReference(@NonNull String string) {
        return stringSection.internString(string);
    }

    @Nullable public BuilderStringReference internNullableStringReference(@Nullable String string) {
        if (string != null) {
            return internStringReference(string);
        }
        return null;
    }

    @NonNull public BuilderTypeReference internTypeReference(@NonNull String type) {
        return typeSection.internType(type);
    }

    @Nullable public BuilderTypeReference internNullableTypeReference(@Nullable String type) {
        if (type != null) {
            return internTypeReference(type);
        }
        return null;
    }

    @NonNull public BuilderFieldReference internFieldReference(@NonNull FieldReference field) {
        return fieldSection.internField(field);
    }

    @NonNull public BuilderMethodReference internMethodReference(@NonNull MethodReference method) {
        return methodSection.internMethod(method);
    }

    @NonNull public BuilderMethodProtoReference internMethodProtoReference(@NonNull MethodProtoReference methodProto) {
        return protoSection.internMethodProto(methodProto);
    }

    @NonNull public BuilderReference internReference(@NonNull Reference reference) {
        if (reference instanceof StringReference) {
            return internStringReference(((StringReference)reference).getString());
        }
        if (reference instanceof TypeReference) {
            return internTypeReference(((TypeReference)reference).getType());
        }
        if (reference instanceof MethodReference) {
            return internMethodReference((MethodReference)reference);
        }
        if (reference instanceof FieldReference) {
            return internFieldReference((FieldReference)reference);
        }
        if (reference instanceof MethodProtoReference) {
            return internMethodProtoReference((MethodProtoReference) reference);
        }
        if (reference instanceof CallSiteReference) {
            return internCallSite((CallSiteReference) reference);
        }
        if (reference instanceof MethodHandleReference) {
            return internMethodHandle((MethodHandleReference) reference);
        }
        throw new IllegalArgumentException("Could not determine type of reference");
    }

    @NonNull private List<BuilderMethodParameter> internMethodParameters(
            @Nullable List<? extends MethodParameter> methodParameters) {
        if (methodParameters == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(Iterators.transform(methodParameters.iterator(),
                new Function<MethodParameter, BuilderMethodParameter>() {
                    @Nullable @Override public BuilderMethodParameter apply(MethodParameter input) {
                        return internMethodParameter(input);
                    }
                }));
    }

    @NonNull private BuilderMethodParameter internMethodParameter(@NonNull MethodParameter methodParameter) {
        return new BuilderMethodParameter(
                typeSection.internType(methodParameter.getType()),
                stringSection.internNullableString(methodParameter.getName()),
                annotationSetSection.internAnnotationSet(methodParameter.getAnnotations()));
    }

    @Override protected void writeEncodedValue(@NonNull InternalEncodedValueWriter writer,
                                               @NonNull BuilderEncodedValues.BuilderEncodedValue encodedValue) throws IOException {
        switch (encodedValue.getValueType()) {
            case ValueType.ANNOTATION:
                BuilderEncodedValues.BuilderAnnotationEncodedValue annotationEncodedValue = (BuilderEncodedValues.BuilderAnnotationEncodedValue)encodedValue;
                writer.writeAnnotation(annotationEncodedValue.typeReference, annotationEncodedValue.elements);
                break;
            case ValueType.ARRAY:
                BuilderEncodedValues.BuilderArrayEncodedValue arrayEncodedValue = (BuilderEncodedValues.BuilderArrayEncodedValue)encodedValue;
                writer.writeArray(arrayEncodedValue.elements);
                break;
            case ValueType.BOOLEAN:
                writer.writeBoolean(((BooleanEncodedValue)encodedValue).getValue());
                break;
            case ValueType.BYTE:
                writer.writeByte(((ByteEncodedValue)encodedValue).getValue());
                break;
            case ValueType.CHAR:
                writer.writeChar(((CharEncodedValue)encodedValue).getValue());
                break;
            case ValueType.DOUBLE:
                writer.writeDouble(((DoubleEncodedValue)encodedValue).getValue());
                break;
            case ValueType.ENUM:
                writer.writeEnum(((BuilderEncodedValues.BuilderEnumEncodedValue)encodedValue).getValue());
                break;
            case ValueType.FIELD:
                writer.writeField(((BuilderEncodedValues.BuilderFieldEncodedValue)encodedValue).fieldReference);
                break;
            case ValueType.FLOAT:
                writer.writeFloat(((FloatEncodedValue)encodedValue).getValue());
                break;
            case ValueType.INT:
                writer.writeInt(((IntEncodedValue)encodedValue).getValue());
                break;
            case ValueType.LONG:
                writer.writeLong(((LongEncodedValue)encodedValue).getValue());
                break;
            case ValueType.METHOD:
                writer.writeMethod(((BuilderEncodedValues.BuilderMethodEncodedValue)encodedValue).methodReference);
                break;
            case ValueType.NULL:
                writer.writeNull();
                break;
            case ValueType.SHORT:
                writer.writeShort(((ShortEncodedValue)encodedValue).getValue());
                break;
            case ValueType.STRING:
                writer.writeString(((BuilderEncodedValues.BuilderStringEncodedValue)encodedValue).stringReference);
                break;
            case ValueType.TYPE:
                writer.writeType(((BuilderEncodedValues.BuilderTypeEncodedValue)encodedValue).typeReference);
                break;
            case ValueType.METHOD_TYPE:
                writer.writeMethodType(((BuilderEncodedValues.BuilderMethodTypeEncodedValue) encodedValue).methodProtoReference);
                break;
            case ValueType.METHOD_HANDLE:
                writer.writeMethodHandle(((BuilderEncodedValues.BuilderMethodHandleEncodedValue) encodedValue).methodHandleReference);
                break;
            default:
                throw new ExceptionWithContext("Unrecognized value type: %d", encodedValue.getValueType());
        }
    }

    @NonNull Set<? extends BuilderAnnotationElement> internAnnotationElements(
            @NonNull Set<? extends AnnotationElement> elements) {
        return ImmutableSet.copyOf(
                Iterators.transform(elements.iterator(),
                        new Function<AnnotationElement, BuilderAnnotationElement>() {
                            @Nullable @Override
                            public BuilderAnnotationElement apply(AnnotationElement input) {
                                return internAnnotationElement(input);
                            }
                        }));
    }

    @NonNull private BuilderAnnotationElement internAnnotationElement(@NonNull AnnotationElement annotationElement) {
        return new BuilderAnnotationElement(stringSection.internString(annotationElement.getName()),
                internEncodedValue(annotationElement.getValue()));
    }

    @Nullable
    BuilderEncodedValues.BuilderEncodedValue internNullableEncodedValue(@Nullable EncodedValue encodedValue) {
        if (encodedValue == null) {
            return null;
        }
        return internEncodedValue(encodedValue);
    }

    @NonNull
    BuilderEncodedValues.BuilderEncodedValue internEncodedValue(@NonNull EncodedValue encodedValue) {
        switch (encodedValue.getValueType()) {
            case ValueType.ANNOTATION:
                return internAnnotationEncodedValue((AnnotationEncodedValue)encodedValue);
            case ValueType.ARRAY:
                return internArrayEncodedValue((ArrayEncodedValue)encodedValue);
            case ValueType.BOOLEAN:
                boolean value = ((BooleanEncodedValue)encodedValue).getValue();
                return value? BuilderEncodedValues.BuilderBooleanEncodedValue.TRUE_VALUE: BuilderEncodedValues.BuilderBooleanEncodedValue.FALSE_VALUE;
            case ValueType.BYTE:
                return new BuilderEncodedValues.BuilderByteEncodedValue(((ByteEncodedValue)encodedValue).getValue());
            case ValueType.CHAR:
                return new BuilderEncodedValues.BuilderCharEncodedValue(((CharEncodedValue)encodedValue).getValue());
            case ValueType.DOUBLE:
                return new BuilderEncodedValues.BuilderDoubleEncodedValue(((DoubleEncodedValue)encodedValue).getValue());
            case ValueType.ENUM:
                return internEnumEncodedValue((EnumEncodedValue)encodedValue);
            case ValueType.FIELD:
                return internFieldEncodedValue((FieldEncodedValue)encodedValue);
            case ValueType.FLOAT:
                return new BuilderEncodedValues.BuilderFloatEncodedValue(((FloatEncodedValue)encodedValue).getValue());
            case ValueType.INT:
                return new BuilderEncodedValues.BuilderIntEncodedValue(((IntEncodedValue)encodedValue).getValue());
            case ValueType.LONG:
                return new BuilderEncodedValues.BuilderLongEncodedValue(((LongEncodedValue)encodedValue).getValue());
            case ValueType.METHOD:
                return internMethodEncodedValue((MethodEncodedValue)encodedValue);
            case ValueType.NULL:
                return BuilderEncodedValues.BuilderNullEncodedValue.INSTANCE;
            case ValueType.SHORT:
                return new BuilderEncodedValues.BuilderShortEncodedValue(((ShortEncodedValue)encodedValue).getValue());
            case ValueType.STRING:
                return internStringEncodedValue((StringEncodedValue)encodedValue);
            case ValueType.TYPE:
                return internTypeEncodedValue((TypeEncodedValue)encodedValue);
            case ValueType.METHOD_TYPE:
                return internMethodTypeEncodedValue((MethodTypeEncodedValue) encodedValue);
            case ValueType.METHOD_HANDLE:
                return internMethodHandleEncodedValue((MethodHandleEncodedValue) encodedValue);
            default:
                throw new ExceptionWithContext("Unexpected encoded value type: %d", encodedValue.getValueType());
        }
    }

    @NonNull private BuilderEncodedValues.BuilderAnnotationEncodedValue internAnnotationEncodedValue(@NonNull AnnotationEncodedValue value) {
        return new BuilderEncodedValues.BuilderAnnotationEncodedValue(
                typeSection.internType(value.getType()),
                internAnnotationElements(value.getElements()));
    }

    @NonNull private BuilderEncodedValues.BuilderArrayEncodedValue internArrayEncodedValue(@NonNull ArrayEncodedValue value) {
        return new BuilderEncodedValues.BuilderArrayEncodedValue(
                ImmutableList.copyOf(
                        Iterators.transform(value.getValue().iterator(),
                                new Function<EncodedValue, BuilderEncodedValues.BuilderEncodedValue>() {
                                    @Nullable @Override public BuilderEncodedValues.BuilderEncodedValue apply(EncodedValue input) {
                                        return internEncodedValue(input);
                                    }
                                })));
    }

    @NonNull private BuilderEncodedValues.BuilderEnumEncodedValue internEnumEncodedValue(@NonNull EnumEncodedValue value) {
        return new BuilderEncodedValues.BuilderEnumEncodedValue(fieldSection.internField(value.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderFieldEncodedValue internFieldEncodedValue(@NonNull FieldEncodedValue value) {
        return new BuilderEncodedValues.BuilderFieldEncodedValue(fieldSection.internField(value.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderMethodEncodedValue internMethodEncodedValue(@NonNull MethodEncodedValue value) {
        return new BuilderEncodedValues.BuilderMethodEncodedValue(methodSection.internMethod(value.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderStringEncodedValue internStringEncodedValue(@NonNull StringEncodedValue string) {
        return new BuilderEncodedValues.BuilderStringEncodedValue(stringSection.internString(string.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderTypeEncodedValue internTypeEncodedValue(@NonNull TypeEncodedValue type) {
        return new BuilderEncodedValues.BuilderTypeEncodedValue(typeSection.internType(type.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderMethodTypeEncodedValue internMethodTypeEncodedValue(
            @NonNull MethodTypeEncodedValue methodType) {
        return new BuilderEncodedValues.BuilderMethodTypeEncodedValue(protoSection.internMethodProto(methodType.getValue()));
    }

    @NonNull private BuilderEncodedValues.BuilderMethodHandleEncodedValue internMethodHandleEncodedValue(
            @NonNull MethodHandleEncodedValue methodHandle) {
        return new BuilderEncodedValues.BuilderMethodHandleEncodedValue(methodHandleSection.internMethodHandle(methodHandle.getValue()));
    }

    protected class DexBuilderSectionProvider extends SectionProvider {
        @NonNull @Override public BuilderStringPool getStringSection() {
            return new BuilderStringPool();
        }

        @NonNull @Override public BuilderTypePool getTypeSection() {
            return new BuilderTypePool(DexBuilder.this);
        }

        @NonNull @Override public BuilderProtoPool getProtoSection() {
            return new BuilderProtoPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderFieldPool getFieldSection() {
            return new BuilderFieldPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderMethodPool getMethodSection() {
            return new BuilderMethodPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderClassPool getClassSection() {
            return new BuilderClassPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderCallSitePool getCallSiteSection() {
            return new BuilderCallSitePool(DexBuilder.this);
        }

        @NonNull @Override public BuilderMethodHandlePool getMethodHandleSection() {
            return new BuilderMethodHandlePool(DexBuilder.this);
        }

        @NonNull @Override public BuilderTypeListPool getTypeListSection() {
            return new BuilderTypeListPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderAnnotationPool getAnnotationSection() {
            return new BuilderAnnotationPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderAnnotationSetPool getAnnotationSetSection() {
            return new BuilderAnnotationSetPool(DexBuilder.this);
        }

        @NonNull @Override public BuilderEncodedArrayPool getEncodedArraySection() {
            return new BuilderEncodedArrayPool(DexBuilder.this);
        }
    }
}
