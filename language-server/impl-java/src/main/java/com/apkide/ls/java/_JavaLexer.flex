package com.apkide.ls.java;
import androidx.annotation.NonNull;
import com.apkide.ls.api.lexer.Lexer;
import static com.apkide.ls.api.lexer.Lexer.*;
import java.io.Reader;
import java.io.IOException;


%%

%{

  private boolean myAssertKeyword;
  private boolean myEnumKeyword;

  public JavaLexer(){
	  this(8);
  }

  public JavaLexer(int level) {
    this((java.io.Reader)null);
    myAssertKeyword = level>=4;
    myEnumKeyword =level>=5;
  }

	@Override
  	public void reset(@NonNull Reader reader){
  		yyreset(reader);
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
  	public int nextToken()throws IOException{
  		return yylex();
  	}
    @Override
    public boolean atEOF(){
      return zzAtEOF;
    }

  	@Override
  	public int getLine(){
  		return yyline;
  	}

  	@Override
  	public int getColumn(){
  		return yycolumn;
  	}

%}
%buffer 8096
%public
%unicode
%class JavaLexer
%implements Lexer
%type int
%line
%column


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
    {END_OF_LINE_COMMENT} { return CommentStyle; }
    {DOC_COMMENT} { return BlockCommentStyle; }

    {LONG_LITERAL} |
    {INTEGER_LITERAL}  |
    {FLOAT_LITERAL}  |
    {DOUBLE_LITERAL}  { return NumberStyle;}
    {CHARACTER_LITERAL}  |
    {STRING_LITERAL} { return StringStyle; }

 "true" |
 "false" |
 "null"|
 "abstract" |
 "boolean" |
 "break"  |
 "byte"  |
 "case" |
 "catch" |
 "char" |
 "class" |
 "const" |
 "continue" |
 "default"  |
 "do"  |
 "double" |
 "else"  |
 "extends" |
 "final" |
 "finally" |
 "float"  |
 "for" |
 "goto" |
 "if" |
 "implements" |
 "import" |
 "instanceof"  |
 "int"  |
 "interface" |
 "long"  |
 "native"  |
 "new"  |
 "package" |
 "private" |
 "public"  |
 "short" |
 "super" |
 "switch" |
 "synchronized" |
 "this" |
 "throw" |
 "protected" |
 "transient" |
 "return" |
 "void" |
 "static" |
 "strictfp" |
 "while" |
 "try" |
 "volatile" |
 "throws" { return KeywordStyle; }

 "assert" { return myAssertKeyword ? KeywordStyle : PlainStyle; }
 "enum" { return myEnumKeyword ? KeywordStyle : PlainStyle; }

 "@" {IDENTIFIER} { return TypeStyle; }

 {IDENTIFIER} { return IdentifierStyle; }

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
 "%=" { return OperatorStyle; }

 "("   |
 ")"   |
 "{"   |
 "}"   |
 "["   |
 "]"   |
 ";"   |
 ","   |
 "..." |
 "."   { return SeparatorStyle; }

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
 "%" { return OperatorStyle; }
 "@" { return PlainStyle; }

 "::" |
 "->" { return OperatorStyle; }

{WHITE_SPACE_CHAR}+ {return PlainStyle;}

 }
. { return PlainStyle; }
 <<EOF>>  { return YYEOF; }

