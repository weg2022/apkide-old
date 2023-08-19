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
package com.apkide.apktool.androlib.res.data.value;

import com.apkide.apktool.androlib.exceptions.AndrolibException;
import com.apkide.apktool.androlib.res.data.ResResSpec;
import com.apkide.apktool.androlib.res.data.ResResource;
import com.apkide.apktool.androlib.res.xml.ResValuesXmlSerializable;
import com.apkide.apktool.ext.org.xmlpull.v1.XmlSerializer;
import com.apkide.apktool.util.Duo;
import com.apkide.common.Logger;

import java.io.IOException;

public class ResStyleValue extends ResBagValue implements
        ResValuesXmlSerializable {
    ResStyleValue(ResReferenceValue parent,
                  Duo<Integer, ResScalarValue>[] items, ResValueFactory factory) {
        super(parent);

        mItems = new Duo[items.length];
        for (int i = 0; i < items.length; i++) {
            mItems[i] = new Duo<>(
                factory.newReference(items[i].m1, null), items[i].m2);
        }
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        serializer.startTag(null, "style");
        serializer.attribute(null, "name", res.getResSpec().getName());
        if (!mParent.isNull() && !mParent.referentIsNull()) {
            serializer.attribute(null, "parent", mParent.encodeAsResXmlAttr());
        } else if (res.getResSpec().getName().indexOf('.') != -1) {
            serializer.attribute(null, "parent", "");
        }
        for (Duo<ResReferenceValue, ResScalarValue> mItem : mItems) {
            ResResSpec spec = mItem.m1.getReferent();

            if (spec == null) {
                LOGGER.verbose(String.format("null reference: m1=0x%08x(%s), m2=0x%08x(%s)",
                    mItem.m1.getRawIntValue(), mItem.m1.getType(), mItem.m2.getRawIntValue(), mItem.m2.getType()));
                continue;
            }

            String name;
            String value = null;

            ResValue resource = spec.getDefaultResource().getValue();
            if (resource instanceof ResReferenceValue) {
                continue;
            } else if (resource instanceof ResAttr) {
                ResAttr attr = (ResAttr) resource;
                value = attr.convertToResXmlFormat(mItem.m2);
                name = spec.getFullName(res.getResSpec().getPackage(), true);
            } else {
                name = "@" + spec.getFullName(res.getResSpec().getPackage(), false);
            }

            if (value == null) {
                value = mItem.m2.encodeAsResXmlValue();
            }

            if (value == null) {
                continue;
            }

            serializer.startTag(null, "item");
            serializer.attribute(null, "name", name);
            serializer.text(value);
            serializer.endTag(null, "item");
        }
        serializer.endTag(null, "style");
    }

    private final Duo<ResReferenceValue, ResScalarValue>[] mItems;

    private static final Logger LOGGER = Logger.getLogger(ResStyleValue.class.getName());
}
