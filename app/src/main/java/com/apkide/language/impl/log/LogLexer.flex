package com.apkide.language.impl.log;

import com.apkide.language.api.Lexer;
import java.io.Reader;
import java.io.IOException;

%%

%{


    public LogLexer(){
    }


	@Override
    public void setReader(Reader reader){
		zzReader=reader;
	}

	@Override
  	public void reset(Reader reader){
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
  	public int getLine(){
  		return yyline;
  	}

  	@Override
  	public int getColumn(){
  		return yycolumn;
  	}

%}
%public
%unicode
%class LogLexer
%implements Lexer
%type int
%line
%column
%ignorecase

WHITE_SPACE_CHAR = [\ \n\r\t\f]

%%
<YYINITIAL>{
    "info:"[^\r\n]* {return Plain;}
    "warning:"[^\r\n]* {return Warning;}
    "error:"[^\r\n]*   {return Error;}
    {WHITE_SPACE_CHAR} {return Plain;}
}

. { return Plain; }
 <<EOF>>  { return YYEOF; }

