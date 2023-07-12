package com.apkide.analysis.language.cpp;

import com.apkide.analysis.api.clm.util.JFlexLexer;
import java.io.Reader;
import java.io.IOException;

%%

%{
	public char[] getBuffer()
	{
		return zzBuffer;
	}

	public int getBufferIndex()
	{
		return zzStartRead;
	}

	public int getCharIndex()
	{
		return (int)yychar;
	}
%}

%ignorecase
%unicode
%public
%class CppLexer
%implements JFlexLexer
%int
%char


WHITE_SPACE_CHAR = [\ \n\r\t\f]

IDENTIFIER = [:jletter:] [:jletterdigit:]*

C_STYLE_COMMENT=("/*"[^"*"]{COMMENT_TAIL})|"/*"
DOC_COMMENT="/*""*"+("/"|([^"/""*"]{COMMENT_TAIL}))?
COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?
END_OF_LINE_COMMENT="/""/"[^\r\n]*

DIGIT = [0-9]
DIGIT_OR_UNDERSCORE = [_0-9]
DIGITS = {DIGIT} | {DIGIT} {DIGIT_OR_UNDERSCORE}*
HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]

INTEGER_LITERAL = {DIGITS} | {HEX_INTEGER_LITERAL} | {BIN_INTEGER_LITERAL}
LONG_LITERAL = {INTEGER_LITERAL} [Ll]
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

ESCAPE_SEQUENCE = \\[^\r\n]
CHARACTER_LITERAL = "'" ([^\\\'\r\n] | {ESCAPE_SEQUENCE})* ("'"|\\)?
STRING_LITERAL = \" ([^\\\"\r\n] | {ESCAPE_SEQUENCE})* (\"|\\)?

%%
<YYINITIAL>{

    {C_STYLE_COMMENT} |
    {END_OF_LINE_COMMENT} { return Comment; }
    {DOC_COMMENT} { return DocComment; }

    {LONG_LITERAL} |
    {INTEGER_LITERAL}  |
    {FLOAT_LITERAL}  |
    {DOUBLE_LITERAL}  |
    {CHARACTER_LITERAL}  |
    {STRING_LITERAL} { return Literal; }

  "#define" |
  "#elif" |
  "#else" |
  "#endif" |
  "#error" |
  "#ifdef" |
  "#ifndef" |
  "#if" |
  "#import" |
  "#include" |
  "#line" |
  "#pragma" |
  "#undef" |
  "#using" {return Preprocessor;}


   "break" |
   "case" |
   "catch" |
   "continue" |
   "default" |
   "do" |
   "else" |
   "for" |
   "goto" |
   "enum" |
   "if" |
   "inline" |
   "mutable" |
   "noinline" |
   "return" |
   "safecast" |
   "sealed" |
   "selectany" |
   "sizeof" |
   "static_cast" |
   "switch" |
   "template" |
   "this" |
   "thread" |
   "throw" |
   "try" |
   "typedef" |
   "typeid" |
   "typename" |
   "using" |
   "uuid" |
   "value" |
   "virtual" |
   "while" |
   "new"       |
   "delete"    |
   "this"      |
   "friend"    |
   "using"     |
   "throw"     |
   "try"       |
   "catch"     |
   "class"     |
   "typename"  |
   "template"  |
   "namespace" |
   "static" |
   "struct" |
   "union" |
   "volatile" |
   "register" |
   "extern" |
   "const" |
   "signed" |
   "unsigned" |
   "bool" |
   "char" |
   "double" |
   "int" |
   "long" |
   "float" |
   "short" |
   "void" |
   "public"     |
   "protected"  |
   "private"    |
   "virtual"    |
   "inline"     |
   "virtual"    |
   "explicit"   |
   "export"     |
   "bool"       |
   "wchar_t"
    { return Keyword; }

 {IDENTIFIER} { return Identifier; }

 "==" |
 "!=" |
 "||" |
 "++" |
 "--" |
 "<"  |
 "<=" |
 "<<="|
 "<<" |
 ">"  |
 "&"  |
 "&&" |
 "+=" |
 "-=" |
 "*=" |
 "/=" |
 "&=" |
 "|=" |
 "^=" |
 "%=" { return Operator; }

 "("   |
 ")"   |
 "{"   |
 "}"   |
 "["   |
 "]"   |
 ";"   |
 ","   |
 "..." |
 "."   { return Separator; }

 "=" |
 "!" |
 "~" |
 "?" |
 ":" |
 "+" |
 "-" |
 "*" |
 "/" |
 "|" |
 "^" |
 "%" { return Operator; }
 "@" { return Plain; }

 "::" |
 "->" { return Operator; }

{WHITE_SPACE_CHAR}+ {return Plain;}

 }
. { return Plain; }
 <<EOF>>  { return YYEOF; }

