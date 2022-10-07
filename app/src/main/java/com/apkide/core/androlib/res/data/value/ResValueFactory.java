/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.apkide.core.androlib.res.data.value;

import android.util.TypedValue;
import com.apkide.core.androlib.AndrolibException;
import com.apkide.core.androlib.res.data.ResPackage;
import com.apkide.core.androlib.res.data.ResTypeSpec;
import com.apkide.core.androlib.util.Duo;

public class ResValueFactory {
    /** The value contains no data. */
    public static final int TYPE_NULL = 0x00;
    
    /** The <var>data</var> field holds a resource identifier. */
    public static final int TYPE_REFERENCE = 0x01;
    /**
     * The <var>data</var> field holds an attribute resource identifier
     * (referencing an attribute in the current theme style, not a resource
     * entry).
     */
    public static final int TYPE_ATTRIBUTE = 0x02;
    /**
     * The <var>string</var> field holds string data. In addition, if
     * <var>data</var> is non-zero then it is the string block index of the
     * string and <var>assetCookie</var> is the set of assets the string came
     * from.
     */
    public static final int TYPE_STRING = 0x03;
    /** The <var>data</var> field holds an IEEE 754 floating point number. */
    public static final int TYPE_FLOAT = 0x04;
    /**
     * The <var>data</var> field holds a complex number encoding a dimension
     * value.
     */
    public static final int TYPE_DIMENSION = 0x05;
    /**
     * The <var>data</var> field holds a complex number encoding a fraction of a
     * container.
     */
    public static final int TYPE_FRACTION = 0x06;
    /**
     * The <var>data</var> holds a dynamic res table reference, which needs to be
     * resolved before it can be used like TYPE_REFERENCE
     */
    public static final int TYPE_DYNAMIC_REFERENCE = 0x07;
    /**
     * The <var>data</var> an attribute resource identifier, which needs to be resolved
     * before it can be used like a TYPE_ATTRIBUTE.
     */
    public static final int TYPE_DYNAMIC_ATTRIBUTE = 0x08;
    /**
     * Identifies the start of plain integer values. Any type value from this to
     * {@link #TYPE_LAST_INT} means the <var>data</var> field holds a generic
     * integer value.
     */
    public static final int TYPE_FIRST_INT = 0x10;
    
    /**
     * The <var>data</var> field holds a number that was originally specified in
     * decimal.
     */
    public static final int TYPE_INT_DEC = 0x10;
    /**
     * The <var>data</var> field holds a number that was originally specified in
     * hexadecimal (0xn).
     */
    public static final int TYPE_INT_HEX = 0x11;
    /**
     * The <var>data</var> field holds 0 or 1 that was originally specified as
     * "false" or "true".
     */
    public static final int TYPE_INT_BOOLEAN = 0x12;
    
    /**
     * Identifies the start of integer values that were specified as color
     * constants (starting with '#').
     */
    public static final int TYPE_FIRST_COLOR_INT = 0x1c;
    
    /**
     * The <var>data</var> field holds a color that was originally specified as
     * #aarrggbb.
     */
    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    /**
     * The <var>data</var> field holds a color that was originally specified as
     * #rrggbb.
     */
    public static final int TYPE_INT_COLOR_RGB8 = 0x1d;
    /**
     * The <var>data</var> field holds a color that was originally specified as
     * #argb.
     */
    public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    /**
     * The <var>data</var> field holds a color that was originally specified as
     * #rgb.
     */
    public static final int TYPE_INT_COLOR_RGB4 = 0x1f;
    
    /**
     * Identifies the end of integer values that were specified as color
     * constants.
     */
    public static final int TYPE_LAST_COLOR_INT = 0x1f;
    
    /** Identifies the end of plain integer values. */
    public static final int TYPE_LAST_INT = 0x1f;
    
    /* ------------------------------------------------------------ */
    
    /** Complex data: bit location of unit information. */
    public static final int COMPLEX_UNIT_SHIFT = 0;
    /**
     * Complex data: mask to extract unit information (after shifting by
     * {@link #COMPLEX_UNIT_SHIFT}). This gives us 16 possible types, as defined
     * below.
     */
    public static final int COMPLEX_UNIT_MASK = 0xf;
    
    /** {@link #TYPE_DIMENSION} complex unit: Value is raw pixels. */
    public static final int COMPLEX_UNIT_PX = 0;
    /**
     * {@link #TYPE_DIMENSION} complex unit: Value is Device Independent Pixels.
     */
    public static final int COMPLEX_UNIT_DIP = 1;
    /** {@link #TYPE_DIMENSION} complex unit: Value is a scaled pixel. */
    public static final int COMPLEX_UNIT_SP = 2;
    /** {@link #TYPE_DIMENSION} complex unit: Value is in points. */
    public static final int COMPLEX_UNIT_PT = 3;
    /** {@link #TYPE_DIMENSION} complex unit: Value is in inches. */
    public static final int COMPLEX_UNIT_IN = 4;
    /** {@link #TYPE_DIMENSION} complex unit: Value is in millimeters. */
    public static final int COMPLEX_UNIT_MM = 5;
    
    /**
     * {@link #TYPE_FRACTION} complex unit: A basic fraction of the overall size.
     */
    public static final int COMPLEX_UNIT_FRACTION = 0;
    /** {@link #TYPE_FRACTION} complex unit: A fraction of the parent size. */
    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;
    
    /**
     * Complex data: where the radix information is, telling where the decimal
     * place appears in the mantissa.
     */
    public static final int COMPLEX_RADIX_SHIFT = 4;
    /**
     * Complex data: mask to extract radix information (after shifting by
     * {@link #COMPLEX_RADIX_SHIFT}). This give us 4 possible fixed point
     * representations as defined below.
     */
    public static final int COMPLEX_RADIX_MASK = 0x3;
    
    /** Complex data: the mantissa is an integral number -- i.e., 0xnnnnnn.0 */
    public static final int COMPLEX_RADIX_23p0 = 0;
    /** Complex data: the mantissa magnitude is 16 bits -- i.e, 0xnnnn.nn */
    public static final int COMPLEX_RADIX_16p7 = 1;
    /** Complex data: the mantissa magnitude is 8 bits -- i.e, 0xnn.nnnn */
    public static final int COMPLEX_RADIX_8p15 = 2;
    /** Complex data: the mantissa magnitude is 0 bits -- i.e, 0x0.nnnnnn */
    public static final int COMPLEX_RADIX_0p23 = 3;
    
    /** Complex data: bit location of mantissa information. */
    public static final int COMPLEX_MANTISSA_SHIFT = 8;
    /**
     * Complex data: mask to extract mantissa information (after shifting by
     * {@link #COMPLEX_MANTISSA_SHIFT}). This gives us 23 bits of precision; the
     * top bit is the sign.
     */
    public static final int COMPLEX_MANTISSA_MASK = 0xffffff;
    
    /* ------------------------------------------------------------ */
    
    /**
     * {@link #TYPE_NULL} data indicating the value was not specified.
     */
    public static final int DATA_NULL_UNDEFINED = 0;
    /**
     * {@link #TYPE_NULL} data indicating the value was explicitly set to null.
     */
    public static final int DATA_NULL_EMPTY = 1;
    
    /* ------------------------------------------------------------ */
    
    /**
     * If density is equal to this value, then the density should be
     * treated as the system's default density value:
     */
    public static final int DENSITY_DEFAULT = 0;
    
    /**
     * If density is equal to this value, then there is no density
     * associated with the resource and it should not be scaled.
     */
    public static final int DENSITY_NONE = 0xffff;
    
    /* ------------------------------------------------------------ */
    
    private final ResPackage mPackage;

    public ResValueFactory(ResPackage package_) {
        this.mPackage = package_;
    }

    public ResScalarValue factory(int type, int value, String rawValue) throws AndrolibException {
        switch (type) {
            case TYPE_NULL:
                if (value == DATA_NULL_UNDEFINED) { // Special case $empty as explicitly defined empty value
                    return new ResStringValue(null, value);
                } else if (value == DATA_NULL_EMPTY) {
                    return new ResEmptyValue(value, rawValue, type);
                }
                return new ResReferenceValue(mPackage, 0, null);
            case TYPE_REFERENCE:
                return newReference(value, null);
            case TYPE_ATTRIBUTE:
            case TYPE_DYNAMIC_ATTRIBUTE:
                return newReference(value, rawValue, true);
            case TYPE_STRING:
                return new ResStringValue(rawValue, value);
            case TYPE_FLOAT:
                return new ResFloatValue(Float.intBitsToFloat(value), value, rawValue);
            case TYPE_DIMENSION:
                return new ResDimenValue(value, rawValue);
            case TYPE_FRACTION:
                return new ResFractionValue(value, rawValue);
            case TYPE_INT_BOOLEAN:
                return new ResBoolValue(value != 0, value, rawValue);
            case TYPE_DYNAMIC_REFERENCE:
                return newReference(value, rawValue);
        }

        if (type >= TYPE_FIRST_COLOR_INT && type <= TYPE_LAST_COLOR_INT) {
            return new ResColorValue(value, rawValue);
        }
        if (type >= TYPE_FIRST_INT && type <= TYPE_LAST_INT) {
            return new ResIntValue(value, rawValue, type);
        }

        throw new AndrolibException("Invalid value type: " + type);
    }

    public ResIntBasedValue factory(String value, int rawValue) {
        if (value == null) {
            return new ResFileValue("", rawValue);
        }
        if (value.startsWith("res/")) {
            return new ResFileValue(value, rawValue);
        }
        if (value.startsWith("r/") || value.startsWith("R/")) { //AndroResGuard
            return new ResFileValue(value, rawValue);
        }
        return new ResStringValue(value, rawValue);
    }

    public ResBagValue bagFactory(int parent, Duo<Integer, ResScalarValue>[] items, ResTypeSpec resTypeSpec) throws AndrolibException {
        ResReferenceValue parentVal = newReference(parent, null);

        if (items.length == 0) {
            return new ResBagValue(parentVal);
        }
        int key = items[0].m1;
        if (key == ResAttr.BAG_KEY_ATTR_TYPE) {
            return ResAttr.factory(parentVal, items, this, mPackage);
        }

        String resTypeName = resTypeSpec.getName();

        // Android O Preview added an unknown enum for c. This is hardcoded as 0 for now.
        if (ResTypeSpec.RES_TYPE_NAME_ARRAY.equals(resTypeName)
                || key == ResArrayValue.BAG_KEY_ARRAY_START || key == 0) {
            return new ResArrayValue(parentVal, items);
        }

        if (ResTypeSpec.RES_TYPE_NAME_PLURALS.equals(resTypeName) ||
                (key >= ResPluralsValue.BAG_KEY_PLURALS_START && key <= ResPluralsValue.BAG_KEY_PLURALS_END)) {
            return new ResPluralsValue(parentVal, items);
        }

        if (ResTypeSpec.RES_TYPE_NAME_ATTR.equals(resTypeName)) {
            return new ResAttr(parentVal, 0, null, null, null);
        }
        
        if (resTypeName.startsWith(ResTypeSpec.RES_TYPE_NAME_STYLES)) {
            return new ResStyleValue(parentVal, items, this);
        }

        throw new AndrolibException("unsupported res type name for bags. Found: " + resTypeName);
    }

    public ResReferenceValue newReference(int resID, String rawValue) {
        return newReference(resID, rawValue, false);
    }

    public ResReferenceValue newReference(int resID, String rawValue, boolean theme) {
        return new ResReferenceValue(mPackage, resID, rawValue, theme);
    }
}
