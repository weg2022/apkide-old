package com.apkide.common;

import static com.apkide.common.TextAttributeKind.Comment;
import static com.apkide.common.TextAttributeKind.Deprecated;
import static com.apkide.common.TextAttributeKind.DocComment;
import static com.apkide.common.TextAttributeKind.Error;
import static com.apkide.common.TextAttributeKind.Function;
import static com.apkide.common.TextAttributeKind.Hyperlink;
import static com.apkide.common.TextAttributeKind.Keyword;
import static com.apkide.common.TextAttributeKind.Namespace;
import static com.apkide.common.TextAttributeKind.NumberLiteral;
import static com.apkide.common.TextAttributeKind.Operator;
import static com.apkide.common.TextAttributeKind.Parameter;
import static com.apkide.common.TextAttributeKind.Plain;
import static com.apkide.common.TextAttributeKind.Preprocessor;
import static com.apkide.common.TextAttributeKind.Separator;
import static com.apkide.common.TextAttributeKind.StringLiteral;
import static com.apkide.common.TextAttributeKind.Type;
import static com.apkide.common.TextAttributeKind.Typo;
import static com.apkide.common.TextAttributeKind.Unknown;
import static com.apkide.common.TextAttributeKind.Unused;
import static com.apkide.common.TextAttributeKind.Variable;
import static com.apkide.common.TextAttributeKind.Warning;
import static com.apkide.common.TextAttributeKind.Whitespace;

import android.graphics.Typeface;
import android.os.Parcel;


public class IntelliJLightColorScheme extends BaseColorScheme {

    public IntelliJLightColorScheme() {
        super("IntelliJ Light", false);
    }

    @Override
    protected void setDefaults() {
        register(Deprecated.id,new TextAttribute(0, 0, 0,0));
        register(Error.id,new TextAttribute(0, 0, 0,0));
        register(Warning.id,new TextAttribute(0, 0, 0,0));
        register(Typo.id,new TextAttribute(0, 0, 0,0));
        register(Unknown.id,new TextAttribute(0xFFF50000));
        register(Unused.id,new TextAttribute(0xFF808080));

        register(Plain.id, new TextAttribute(0xFF080808, 0xFFFFFFFF));
        register(Hyperlink.id,new TextAttribute(0xFF006DCC,0,Typeface.ITALIC));
        register(Whitespace.id,new TextAttribute(0xFFADADAD));
        register(Preprocessor.id,new TextAttribute(0xFF9E880D,0,Typeface.ITALIC));
        register(Namespace.id,new TextAttribute(0xFF008080,0,Typeface.ITALIC));
        register(Keyword.id,new TextAttribute(0xFF0033B3,0, Typeface.BOLD));
        register(Operator.id,new TextAttribute(0));
        register(Separator.id,new TextAttribute(0));
        register(StringLiteral.id,new TextAttribute(0xFF067D17));
        register(Type.id,new TextAttribute(0xFF371F80));
        register(NumberLiteral.id,new TextAttribute(0xFF1750EB));
        register(Variable.id,new TextAttribute(0xFF871094));
        register(Function.id,new TextAttribute(0xFF00627A));
        register(Parameter.id,new TextAttribute(0));
        register(Comment.id,new TextAttribute(0xFF8C8C8C,0,Typeface.ITALIC));
        register(DocComment.id,new TextAttribute(0xFF8C8C8C,0,Typeface.ITALIC));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
    }

    protected IntelliJLightColorScheme(Parcel in) {
        super(in);
    }

    public static final Creator<IntelliJLightColorScheme> CREATOR = new Creator<IntelliJLightColorScheme>() {
        @Override
        public IntelliJLightColorScheme createFromParcel(Parcel source) {
            return new IntelliJLightColorScheme(source);
        }

        @Override
        public IntelliJLightColorScheme[] newArray(int size) {
            return new IntelliJLightColorScheme[size];
        }
    };
}
