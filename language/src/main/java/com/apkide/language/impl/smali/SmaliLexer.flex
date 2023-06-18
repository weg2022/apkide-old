package com.apkide.language.impl.smali;

import static java.lang.Math.toIntExact;
import com.apkide.language.api.Lexer;
import java.io.*;
import java.util.Stack;

%%

%public
%class SmaliLexer
%implements Lexer
%type int
%unicode
%line
%column
%char

%{
    private StringBuffer sb = new StringBuffer();
    private String tokenError = null;
    private int lexerErrors = 0;
    private Stack<Integer> stateStack = new Stack<>();
    private int apiLevel=30;

	public void setApiLevel(int level){
		apiLevel=level;
	}

	public int getApiLevel(){
		return apiLevel;
	}

	  @Override
    	public void setReader(Reader reader){
    		zzReader=reader;
    	}

        @Override
    	public void reset(Reader reader){
    		yyreset(reader);
			lexerErrors=0;
			sb.setLength(0);
    	}

        @Override
    	public int getState(){
    		return yystate()+1;
    	}

        @Override
    	public void setState(int state){
    		yybegin(state-1);
    	}

        @Override
    	public int getDefaultState(){
    		return YYINITIAL+1;
    	}

	@Override
    public int nextToken()throws IOException {
         int token = yylex();
		 if (token == Error) {
			 lexerErrors++;
         }
		 return token;
    }

	@Override
    public int getLine() {
        return this.yyline;
    }

	@Override
    public int getColumn() {
        return this.yycolumn;
    }

    public int getErrorCount() {
        return lexerErrors;
    }

	public String getError(){
		  return tokenError;
	}

    private void beginStateBasedToken(int state) {
        stateStack.push(yystate());
        yybegin(state);
        tokenError = null;
		sb.setLength(0);
    }

    private int endStateBasedToken(int type) {
        if (tokenError != null) {
            return Error;
        }

        yybegin(stateStack.pop());
        return type;
    }

    private void setStateBasedTokenError(String message) {
        if (tokenError == null) {
            tokenError = message;
        }
    }

    private int invalidStateBasedToken(String message) {
        yybegin(stateStack.pop());
        return Error;
    }

	    private String processQuotedSimpleName(String text) {
            // strip backticks
            return text.substring(1, text.length() - 1);
        }

        private String processQuotedSimpleNameWithSpaces(String text) {
            if (apiLevel < 30) {
                setStateBasedTokenError("spaces in class descriptors and member names are not supported prior to API " +
                    "level 30/dex version 040");
            }
            return processQuotedSimpleName(text);
        }


	public SmaliLexer(){

	}
%}

HexPrefix = 0 [xX]

HexDigit = [0-9a-fA-F]
HexDigits = [0-9a-fA-F]{4}
FewerHexDigits = [0-9a-fA-F]{0,3}

Integer1 = 0
Integer2 = [1-9] [0-9]*
Integer3 = 0 [0-7]+
Integer4 = {HexPrefix} {HexDigit}+
Integer = {Integer1} | {Integer2} | {Integer3} | {Integer4}

DecimalExponent = [eE] -? [0-9]+

BinaryExponent = [pP] -? [0-9]+

/*This can either be a floating point number or an identifier*/
FloatOrID1 = -? [0-9]+ {DecimalExponent}
FloatOrID2 = -? {HexPrefix} {HexDigit}+ {BinaryExponent}
FloatOrID3 = -? [iI][nN][fF][iI][nN][iI][tT][yY]
FloatOrID4 = [nN][aA][nN]
FloatOrID =  {FloatOrID1} | {FloatOrID2} | {FloatOrID3} | {FloatOrID4}


/*This can only be a float and not an identifier, due to the decimal point*/
Float1 = -? [0-9]+ "." [0-9]* {DecimalExponent}?
Float2 = -? "." [0-9]+ {DecimalExponent}?
Float3 = -? {HexPrefix} {HexDigit}+ "." {HexDigit}* {BinaryExponent}
Float4 = -? {HexPrefix} "." {HexDigit}+ {BinaryExponent}
Float =  {Float1} | {Float2} | {Float3} | {Float4}

HighSurrogate = [\ud800-\udbff]

LowSurrogate = [\udc00-\udfff]

SimpleNameCharacter = ({HighSurrogate} {LowSurrogate}) | [A-Za-z0-9$\-_\u00a1-\u1fff\u2010-\u2027\u2030-\ud7ff\ue000-\uffef]
UnicodeSpace = [\u0020\u00A0\u1680\u2000-\u200A\u202F\u205F\u3000] /* Zs category */

SimpleNameRaw = {SimpleNameCharacter}+
SimpleNameQuoted = [`] {SimpleNameCharacter}+ [`]
SimpleNameQuotedWithSpaces = [`] ({SimpleNameCharacter} | {UnicodeSpace})+ [`]
SimpleName = {SimpleNameRaw} | {SimpleNameQuoted} | {SimpleNameQuotedWithSpaces}

PrimitiveType = [ZBSCIJFD]

ClassDescriptor = L ({SimpleName} "/")* {SimpleName} ;

ArrayPrefix = "["+

Type = {PrimitiveType} | {ClassDescriptor} | {ArrayPrefix} ({ClassDescriptor} | {PrimitiveType})


%state PARAM_LIST_OR_ID
%state PARAM_LIST
%state ARRAY_DESCRIPTOR
%state STRING
%state CHAR
%state CLASS_DESCRIPTOR_BEGINNING
%state CLASS_DESCRIPTOR_REMAINING

%%

/*Directives*/
<YYINITIAL>
{
    ".class"                |
    ".super"                |
    ".implements"           |
    ".source"               |
    ".field"                |
    ".end field"            |
    ".subannotation"        |
    ".end subannotation"    |
    ".annotation"           |
    ".end annotation"       |
    ".enum"                 |
    ".method"               |
    ".end method"           |
    ".registers"            |
    ".locals"               |
    ".array-data"           |
    ".end array-data"       |
    ".packed-switch"        |
    ".end packed-switch"    |
    ".sparse-switch"        |
    ".end sparse-switch"    |
    ".catch"                |
    ".catchall"             |
    ".line"                 |
    ".param"                |
    ".end param"            |
    ".local"                |
    ".end local"            |
    ".restart local"        |
    ".prologue"             |
    ".epilogue"  { return Keyword; }

    ".end"                  |
    ".end " [a-zA-z0-9\-_]+ |
    ".restart"              |
    ".restart " [a-zA-z0-9\-_]+ { return Error; }
}

/*Literals*/
<YYINITIAL> {
    {Integer}                             |
    - {Integer}                           |
    -? {Integer} [lL]                     |
    -? {Integer} [sS]                     |
    -? {Integer} [tT]                     |
    {FloatOrID} [fF] | -? [0-9]+ [fF]     |
    {FloatOrID} [dD]? | -? [0-9]+ [dD]    |
    {Float} [fF]                          |
    {Float} [dD]?                         |
    "true"|"false"                        |
    "null" { return Literal; }

    "\"" { beginStateBasedToken(STRING); sb.append('"'); return Literal; }

    ' { beginStateBasedToken(CHAR); sb.append('\'');  return Literal;}
}

<PARAM_LIST_OR_ID> {
    {PrimitiveType} { return TypeIdentifier; }
    [^] { yypushback(1); yybegin(YYINITIAL); }
    <<EOF>> { yybegin(YYINITIAL); }
}

<PARAM_LIST> {
    {PrimitiveType} { return TypeIdentifier; }
    {ClassDescriptor} {
        yypushback(yylength());
        beginStateBasedToken(CLASS_DESCRIPTOR_BEGINNING);
        sb.append(yytext());
    }
    {ArrayPrefix} { return TypeIdentifier; }
    [^] { yypushback(1); yybegin(YYINITIAL);}
    <<EOF>> { yybegin(YYINITIAL);}
}

<CLASS_DESCRIPTOR_BEGINNING> {
    "L" {SimpleNameRaw} {
        sb.append(yytext());
        yybegin(CLASS_DESCRIPTOR_REMAINING);
		return NamespaceIdentifier;
    }
    "L" {SimpleNameQuoted} {
        sb.append("L");
        sb.append(processQuotedSimpleName(yytext().substring(1)));
        yybegin(CLASS_DESCRIPTOR_REMAINING);
		return NamespaceIdentifier;
    }
    "L" {SimpleNameQuotedWithSpaces} {
        sb.append("L");
        sb.append(processQuotedSimpleNameWithSpaces(yytext().substring(1)));
        yybegin(CLASS_DESCRIPTOR_REMAINING);
		return NamespaceIdentifier;
    }
}

<CLASS_DESCRIPTOR_REMAINING> {
    "/" {SimpleNameRaw} {
        sb.append(yytext());
		return NamespaceIdentifier;
    }
    "/" {SimpleNameQuoted} {
        sb.append("/");
        sb.append(processQuotedSimpleName(yytext().substring(1)));
		return NamespaceIdentifier;
    }
    "/" {SimpleNameQuotedWithSpaces} {
        sb.append("/");
        sb.append(processQuotedSimpleNameWithSpaces(yytext().substring(1)));
		return NamespaceIdentifier;
    }

    ";" {
        sb.append(yytext());
        return endStateBasedToken(NamespaceIdentifier);
    }
}

<STRING> {
    "\""  { sb.append('"'); return endStateBasedToken(Literal); }

    [^\r\n\"\\]+ { sb.append(yytext()); }
    "\\b" { sb.append('\b'); }
    "\\t" { sb.append('\t'); }
    "\\n" { sb.append('\n'); }
    "\\f" { sb.append('\f'); }
    "\\r" { sb.append('\r'); }
    "\\'" { sb.append('\''); }
    "\\\"" { sb.append('"'); }
    "\\\\" { sb.append('\\'); }
    "\\u" {HexDigits} { sb.append((char)Integer.parseInt(yytext().substring(2,6), 16)); }

    "\\u" {FewerHexDigits} {
        sb.append(yytext());
        setStateBasedTokenError("Invalid \\u sequence. \\u must be followed by 4 hex digits");
    }

    "\\" [^btnfr'\"\\u] {
        sb.append(yytext());
        setStateBasedTokenError("Invalid escape sequence " + yytext());
    }

    [\r\n] { return invalidStateBasedToken("Unterminated string literal"); }
    <<EOF>> { return invalidStateBasedToken("Unterminated string literal"); }
}

<CHAR> {
    ' {
        sb.append('\'');
        if (sb.length() == 2) {
            return invalidStateBasedToken("Empty character literal");
        } else if (sb.length() > 3) {
            return invalidStateBasedToken("Character literal with multiple chars");
        }

        return endStateBasedToken(Literal);
    }

    [^\r\n'\\]+ { sb.append(yytext()); }
    "\\b" { sb.append('\b'); }
    "\\t" { sb.append('\t'); }
    "\\n" { sb.append('\n'); }
    "\\f" { sb.append('\f'); }
    "\\r" { sb.append('\r'); }
    "\\'" { sb.append('\''); }
    "\\\"" { sb.append('"'); }
    "\\\\" { sb.append('\\'); }
    "\\u" {HexDigits} { sb.append((char)Integer.parseInt(yytext().substring(2,6), 16)); }

    "\\u" {HexDigit}* {
        sb.append(yytext());
        setStateBasedTokenError("Invalid \\u sequence. \\u must be followed by exactly 4 hex digits");
    }

    "\\" [^btnfr'\"\\u] {
        sb.append(yytext());
        setStateBasedTokenError("Invalid escape sequence " + yytext());
    }

    [\r\n] { return invalidStateBasedToken("Unterminated character literal"); }
    <<EOF>> { return invalidStateBasedToken("Unterminated character literal"); }
}

/*Misc*/
<YYINITIAL> {
    [vp] [0-9]+  |
    "build" | "runtime" | "system" |
    "public" | "private" | "protected" | "static" | "final" | "synchronized" | "bridge" | "varargs" | "native" |
    "abstract" | "strictfp" | "synthetic" | "constructor" | "declared-synchronized" | "interface" | "enum" |
    "annotation" | "volatile" | "transient" |
    "whitelist" | "greylist" | "blacklist" | "greylist-max-o" | "greylist-max-p" | "greylist-max-q" | "greylist-max-r" |
    "core-platform-api" | "test-api" |
    "no-error" | "generic-error" | "no-such-class" | "no-such-field" | "no-such-method" | "illegal-class-access" |
    "illegal-field-access" | "illegal-method-access" | "class-change-error" | "instantiation-error" {
        return Keyword;
    }

    "inline@0x" {HexDigit}+ { return Keyword; }
    "vtable@0x" {HexDigit}+ { return Keyword; }
    "field@0x" {HexDigit}+ { return Keyword; }

    "static-put" | "static-get" | "instance-put" | "instance-get" |
    "invoke-instance" | "invoke-constructor" {
        return Keyword;
    }

    # [^\r\n]* { return Comment; }
}

/*Instructions*/
<YYINITIAL> {
    "<init>" |
    "goto" |
    "return-void" | "nop" |
    "return-void-barrier" | "return-void-no-barrier" |
    "const/4" |
    "move-result" | "move-result-wide" | "move-result-object" | "move-exception" | "return" | "return-wide" |
    "return-object" | "monitor-enter" | "monitor-exit" | "throw" |
    "move" | "move-wide" | "move-object" | "array-length" | "neg-int" | "not-int" | "neg-long" | "not-long" |
    "neg-float" | "neg-double" | "int-to-long" | "int-to-float" | "int-to-double" | "long-to-int" | "long-to-float" |
    "long-to-double" | "float-to-int" | "float-to-long" | "float-to-double" | "double-to-int" | "double-to-long" |
    "double-to-float" | "int-to-byte" | "int-to-char" | "int-to-short" |
    "add-int/2addr" | "sub-int/2addr" | "mul-int/2addr" | "div-int/2addr" | "rem-int/2addr" | "and-int/2addr" |
    "or-int/2addr" | "xor-int/2addr" | "shl-int/2addr" | "shr-int/2addr" | "ushr-int/2addr" | "add-long/2addr" |
    "sub-long/2addr" | "mul-long/2addr" | "div-long/2addr" | "rem-long/2addr" | "and-long/2addr" | "or-long/2addr" |
    "xor-long/2addr" | "shl-long/2addr" | "shr-long/2addr" | "ushr-long/2addr" | "add-float/2addr" |
    "sub-float/2addr" | "mul-float/2addr" | "div-float/2addr" | "rem-float/2addr" | "add-double/2addr" |
    "sub-double/2addr" | "mul-double/2addr" | "div-double/2addr" | "rem-double/2addr" |
    "throw-verification-error" |
    "goto/16" |
    "sget" | "sget-wide" | "sget-object" | "sget-boolean" | "sget-byte" | "sget-char" | "sget-short" | "sput" |
    "sput-wide" | "sput-object" | "sput-boolean" | "sput-byte" | "sput-char" | "sput-short" |
    "sget-volatile" | "sget-wide-volatile" | "sget-object-volatile" | "sput-volatile" | "sput-wide-volatile" |
    "sput-object-volatile" |
    "const-string" |
    "check-cast" | "new-instance" | "const-class" |
    "const-method-handle"  |
    "const-method-type" |
    "const/high16"  |
    "const-wide/high16"  |
    "const/16" | "const-wide/16" |
    "if-eqz" | "if-nez" | "if-ltz" | "if-gez" | "if-gtz" | "if-lez" |
    "add-int/lit8" | "rsub-int/lit8" | "mul-int/lit8" | "div-int/lit8" | "rem-int/lit8" | "and-int/lit8" |
    "or-int/lit8" | "xor-int/lit8" | "shl-int/lit8" | "shr-int/lit8" | "ushr-int/lit8"  |
    "iget" | "iget-wide" | "iget-object" | "iget-boolean" | "iget-byte" | "iget-char" | "iget-short" | "iput" |
    "iput-wide" | "iput-object" | "iput-boolean" | "iput-byte" | "iput-char" | "iput-short" |
    "iget-volatile" | "iget-wide-volatile" | "iget-object-volatile" | "iput-volatile" | "iput-wide-volatile" |
    "iput-object-volatile" |
    "instance-of" | "new-array" |
    "iget-quick" | "iget-wide-quick" | "iget-object-quick" | "iput-quick" | "iput-wide-quick" | "iput-object-quick" |
    "iput-boolean-quick" | "iput-byte-quick" | "iput-char-quick" | "iput-short-quick"  |
    "rsub-int" |
    "add-int/lit16" | "mul-int/lit16" | "div-int/lit16" | "rem-int/lit16" | "and-int/lit16" | "or-int/lit16" |
    "xor-int/lit16" |
    "if-eq" | "if-ne" | "if-lt" | "if-ge" | "if-gt" | "if-le" |
    "move/from16" | "move-wide/from16" | "move-object/from16" |
    "cmpl-float" | "cmpg-float" | "cmpl-double" | "cmpg-double" | "cmp-long" | "aget" | "aget-wide" | "aget-object" |
    "aget-boolean" | "aget-byte" | "aget-char" | "aget-short" | "aput" | "aput-wide" | "aput-object" | "aput-boolean" |
    "aput-byte" | "aput-char" | "aput-short" | "add-int" | "sub-int" | "mul-int" | "div-int" | "rem-int" | "and-int" |
    "or-int" | "xor-int" | "shl-int" | "shr-int" | "ushr-int" | "add-long" | "sub-long" | "mul-long" | "div-long" |
    "rem-long" | "and-long" | "or-long" | "xor-long" | "shl-long" | "shr-long" | "ushr-long" | "add-float" |
    "sub-float" | "mul-float" | "div-float" | "rem-float" | "add-double" | "sub-double" | "mul-double" | "div-double" |
    "rem-double" |
    "goto/32"  |
    "const-string/jumbo"  |
    "const" |
    "const-wide/32"  |
    "fill-array-data" | "packed-switch" | "sparse-switch"  |
    "move/16" | "move-wide/16" | "move-object/16" |
    "invoke-custom" |
    "invoke-virtual" | "invoke-super"  |
    "invoke-direct" | "invoke-static" | "invoke-interface"  |
    "invoke-direct-empty"  |
    "filled-new-array" |
    "execute-inline"  |
    "invoke-virtual-quick" | "invoke-super-quick"  |
    "invoke-custom/range" |
    "invoke-virtual/range" | "invoke-super/range" | "invoke-direct/range" | "invoke-static/range" |
    "invoke-interface/range" |
    "invoke-object-init/range" |
    "filled-new-array/range" |
    "execute-inline/range" |
    "invoke-virtual-quick/range" | "invoke-super-quick/range" |
    "invoke-polymorphic" |
    "invoke-polymorphic/range" |
    "const-wide" {
        return Keyword;
    }
}

<ARRAY_DESCRIPTOR> {
    {PrimitiveType} { yybegin(YYINITIAL); return TypeIdentifier; }
    {ClassDescriptor} {
        yypushback(yylength());
        beginStateBasedToken(CLASS_DESCRIPTOR_BEGINNING);
        sb.append(yytext());
    }
    [^] { yypushback(1); yybegin(YYINITIAL); }
    <<EOF>> { yybegin(YYINITIAL); }
}

/*Types*/
<YYINITIAL> {

    {ClassDescriptor} {
        yypushback(yylength());
        beginStateBasedToken(CLASS_DESCRIPTOR_BEGINNING);
    }

    // we have to drop into a separate state so that we don't parse something like
    // "[I->" as "[" followed by "I-" as a SIMPLE_NAME
    {ArrayPrefix} {
      yybegin(ARRAY_DESCRIPTOR);
      return TypeIdentifier;
    }

    {PrimitiveType} {PrimitiveType}+ {
        // go back and re-lex it as a PARAM_LIST_OR_ID
        yypushback(yylength());
        yybegin(PARAM_LIST_OR_ID);
    }

    {Type} {Type}+ {
        // go back and re-lex it as a PARAM_LIST
        yypushback(yylength());
        yybegin(PARAM_LIST);
    }

    {PrimitiveType} { return TypeIdentifier; }
    V { return TypeIdentifier; }

    ":" {SimpleNameRaw} "_" {SimpleNameRaw} { return DelegateIdentifier; }
    {SimpleNameRaw} |
    {SimpleNameQuoted} |
    {SimpleNameQuotedWithSpaces} |
    "<" {SimpleNameRaw} ">" { return Identifier; }

}

/*Symbols/Whitespace/EOF*/
<YYINITIAL> {
    ".."        |
    "->"        |
    "="         |
    ":"          { return Operator; }
    ","         |
    "{"         |
    "}"         |
    "("         |
    ")"         |
    "<"         |
    ">"         |
    "@"          { return Separator; }
    [\r\n\t ]+ { return Plain; }
    <<EOF>> { return YYEOF; }
}

/*catch all*/
<YYINITIAL> {
    "."                                |
    "." [a-zA-z\-_]                    |
    "." [a-zA-z\-_] [a-zA-z0-9\-_]*    |
    [^] { return Error; }
}

<<EOF>> { return YYEOF; }