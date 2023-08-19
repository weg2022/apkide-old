package com.apkide.ls.xml;
import androidx.annotation.NonNull;
import com.apkide.ls.api.lexer.Lexer;
import static com.apkide.ls.api.lexer.Lexer.*;
import java.io.Reader;
import java.io.IOException;


%%

%{
	    private int elTokenType = PlainStyle;
        private int elTokenType2 =StringStyle;
        private int javaEmbeddedTokenType = StringStyle;
        private boolean myConditionalCommentsSupport;

        public void setConditionalCommentsSupport(final boolean b) {
          myConditionalCommentsSupport = b;
        }

        public void setElTypes(int _elTokenType,int _elTokenType2) {
          elTokenType = _elTokenType;
          elTokenType2 = _elTokenType2;
        }

        public void setJavaEmbeddedType(int _tokenType) {
          javaEmbeddedTokenType = _tokenType;
        }

        private int myPrevState = YYINITIAL;

        public int yyprevstate() {
          return myPrevState;
        }

        private int popState(){
          final int prev = myPrevState;
          myPrevState = YYINITIAL;
          return prev;
        }

    protected void pushState(int state){
       myPrevState = state;
	}

	public XmlLexer(){

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
%class XmlLexer
%implements Lexer
%type int
%line
%column

%state TAG
%state PROCESSING_INSTRUCTION
%state PI_ANY
%state END_TAG
%xstate COMMENT
%state ATTR_LIST
%state ATTR
%state ATTR_VALUE_START
%state ATTR_VALUE_DQ
%state ATTR_VALUE_SQ
%state DTD_MARKUP
%state DOCTYPE
%xstate CDATA
%state C_COMMENT_START
/* this state should be last, number of states should be less than 16 */
%state C_COMMENT_END

IDENTIFIER = [:jletter:] [:jletterdigit:]*

ALPHA=[:letter:]
DIGIT=[0-9]
WS=[\ \n\r\t\f\u2028\u2029\u0085]
S={WS}+

EL_EMBEDMENT_START="${" | "#{"
NAME=({ALPHA}|"_"|":")({ALPHA}|{DIGIT}|"_"|"."|"-")*(":"({ALPHA}|"_")?({ALPHA}|{DIGIT}|"_"|"."|"-")*)?
IDENTIFIER = [:jletter:] [:jletterdigit:]*

END_COMMENT="-->"
CONDITIONAL_COMMENT_CONDITION=({ALPHA})({ALPHA}|{S}|{DIGIT}|"."|"("|")"|"|"|"!"|"&")*

%%
"<![CDATA[" {yybegin(CDATA); return PlainStyle; }
<CDATA>{
 "]]>"  {yybegin(YYINITIAL); return PlainStyle; }
 [^] {return PlainStyle; }
}

"<!--" { yybegin(COMMENT); return BlockCommentStyle; }
<COMMENT> "[" { if (myConditionalCommentsSupport) {
    yybegin(C_COMMENT_START);
    return BlockCommentStyle;
  } else return PlainStyle; }
<COMMENT> "<![" { if (myConditionalCommentsSupport) {
    yybegin(C_COMMENT_END);
    return BlockCommentStyle;
  } else return PlainStyle; }
<COMMENT> {END_COMMENT} { yybegin(YYINITIAL); return BlockCommentStyle; }
<COMMENT> [^\-]|(-[^\-]) { return BlockCommentStyle; }
<COMMENT> [^] { return PlainStyle; }

<C_COMMENT_START,C_COMMENT_END> {CONDITIONAL_COMMENT_CONDITION} { return BlockCommentStyle; }
<C_COMMENT_START> [^] { yybegin(COMMENT); return BlockCommentStyle; }
<C_COMMENT_START> "]>" { yybegin(COMMENT); return BlockCommentStyle; }
<C_COMMENT_START,C_COMMENT_END> {END_COMMENT} { yybegin(YYINITIAL); return BlockCommentStyle; }
<C_COMMENT_END> "]" { yybegin(COMMENT); return BlockCommentStyle; }
<C_COMMENT_END> [^] { yybegin(COMMENT); return BlockCommentStyle; }

"&lt;" |
"&gt;" |
"&apos;" |
"&quot;" |
"&nbsp;" |
"&amp;" |
"&#"{DIGIT}+";" |
"&#x"({DIGIT}|[a-fA-F])+";" { return TypeStyle; }
"&"{NAME}";" { return TypeStyle; }

<YYINITIAL> "<!DOCTYPE" { yybegin(DOCTYPE); return KeywordStyle; }
<DOCTYPE> "SYSTEM" { return KeywordStyle;  }
<DOCTYPE> "PUBLIC" { return KeywordStyle;  }
<DOCTYPE> {NAME} { return KeywordStyle;  }
<DOCTYPE> "\""[^\"]*"\"" { return StringStyle;}
<DOCTYPE> "'"[^']*"'" { return StringStyle;}
<DOCTYPE> "["(([^\]\"]*)|(\"[^\"]*\"))*"]" { return StringStyle;}
<DOCTYPE> ">" { yybegin(YYINITIAL); return KeywordStyle; }

<YYINITIAL> "<?" { yybegin(PROCESSING_INSTRUCTION); return KeywordStyle; }
<PROCESSING_INSTRUCTION> "xml" { yybegin(ATTR_LIST); pushState(PROCESSING_INSTRUCTION); return KeywordStyle; }
<PROCESSING_INSTRUCTION> {NAME} { yybegin(PI_ANY); return KeywordStyle; }
<PI_ANY, PROCESSING_INSTRUCTION> "?>" { yybegin(YYINITIAL); return KeywordStyle; }
<PI_ANY> {S} { return PlainStyle; }
<PI_ANY> [^] { return PlainStyle; }

<YYINITIAL> {EL_EMBEDMENT_START} [^<\}]* "}" {
  return elTokenType;
}

<YYINITIAL> "<" { yybegin(TAG); return KeywordStyle; }
<TAG> {NAME} { yybegin(ATTR_LIST); pushState(TAG); return KeywordStyle; }
<TAG> "/>" { yybegin(YYINITIAL); return KeywordStyle; }
<TAG> ">" { yybegin(YYINITIAL); return KeywordStyle; }

<YYINITIAL> "</" { yybegin(END_TAG); return KeywordStyle; }
<END_TAG> {NAME} { return KeywordStyle; }
<END_TAG> ">" { yybegin(YYINITIAL); return KeywordStyle; }

<ATTR_LIST>{
	{IDENTIFIER} {yybegin(ATTR); return TypeStyle;}
}

<ATTR> ":" { return OperatorStyle;}
<ATTR> {IDENTIFIER} { return PlainStyle;}
<ATTR> "=" { return OperatorStyle;}
<ATTR> "'" { yybegin(ATTR_VALUE_SQ); return StringStyle;}
<ATTR> "\"" { yybegin(ATTR_VALUE_DQ); return StringStyle;}
<ATTR> [^\ \n\r\t\f] {yybegin(ATTR_LIST); yypushback(yylength()); }

<ATTR_VALUE_DQ>{
  "\"" { yybegin(ATTR_LIST); return StringStyle;}
  "&" { return PlainStyle; }
  {EL_EMBEDMENT_START} [^\}\"]* "}" { return elTokenType2; }
  "%=" [^%\"]* "%" { return javaEmbeddedTokenType; }
  [^] { return StringStyle;}
}

<ATTR_VALUE_SQ>{
  "&" { return PlainStyle; }
  "'" { yybegin(ATTR_LIST); return StringStyle;}
  {EL_EMBEDMENT_START} [^\}\']* "}" { return elTokenType2; }
  "%=" [^%\']* "%" { return javaEmbeddedTokenType; }
  [^] { return StringStyle;}
}

<YYINITIAL> {S} { return PlainStyle; }
<ATTR_LIST,ATTR,TAG,END_TAG,DOCTYPE> {S} { return PlainStyle; }
<YYINITIAL> ([^<&\$# \n\r\t\f]|(\\\$)|(\\#))* { return PlainStyle; }
<YYINITIAL> [^<&\ \n\r\t\f]|(\\\$)|(\\#) { return PlainStyle; }

[^] {if(yystate() == YYINITIAL){
        return PlainStyle;
	  } else
		  yybegin(popState()); yypushback(yylength());
	  }

. { return PlainStyle; }
 <<EOF>>  { return YYEOF; }

