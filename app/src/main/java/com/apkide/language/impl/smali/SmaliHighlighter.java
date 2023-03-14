package com.apkide.language.impl.smali;

import com.android.tools.smali.smali.smaliFlexLexer;
import com.android.tools.smali.smali.smaliParser;
import com.apkide.language.api.Highlighter;
import com.apkide.language.api.SyntaxKind;
import com.apkide.language.api.TokenIterator;

import org.antlr.runtime.Token;

import java.io.Reader;

public class SmaliHighlighter implements Highlighter {
	public static final int API_VERSION =15;
	private smaliFlexLexer lexer;
	@Override
	public int highlightLine(Reader reader, int state, TokenIterator iterator) {
		if (lexer==null)
			lexer=new smaliFlexLexer(reader, API_VERSION);
		else{
			lexer.yyreset(reader);
			lexer.yybegin(state);
		}
		
		while (true){
			Token token=lexer.nextToken();
			if (token==null||token.getType()==-1)break;
			iterator.tokenFound(token.getType(),token.getLine()-1,token.getCharPositionInLine());
		}
		return lexer.yystate();
	}
	
	@Override
	public int getDefaultState() {
		return smaliFlexLexer.YYINITIAL+1;
	}
	
	@Override
	public SyntaxKind getSyntaxKind(int token) {
		switch (token){
			case smaliParser.CLASS_DIRECTIVE:
			case smaliParser.SUPER_DIRECTIVE:
			case smaliParser.IMPLEMENTS_DIRECTIVE:
			case smaliParser.SOURCE_DIRECTIVE:
			case smaliParser.FIELD_DIRECTIVE:
			case smaliParser.END_FIELD_DIRECTIVE:
			case smaliParser.SUBANNOTATION_DIRECTIVE:
			case smaliParser.END_SUBANNOTATION_DIRECTIVE:
			case smaliParser.ANNOTATION_DIRECTIVE:
			case smaliParser.END_ANNOTATION_DIRECTIVE:
			case smaliParser.ENUM_DIRECTIVE:
			case smaliParser.METHOD_DIRECTIVE:
			case smaliParser.END_METHOD_DIRECTIVE:
			case smaliParser.REGISTERS_DIRECTIVE:
			case smaliParser.LOCALS_DIRECTIVE:
			case smaliParser.ARRAY_DATA_DIRECTIVE:
			case smaliParser.END_ARRAY_DATA_DIRECTIVE:
			case smaliParser.PACKED_SWITCH_DIRECTIVE:
			case smaliParser.END_PACKED_SWITCH_DIRECTIVE:
			case smaliParser.SPARSE_SWITCH_DIRECTIVE:
			case smaliParser.END_SPARSE_SWITCH_DIRECTIVE:
			case smaliParser.CATCH_DIRECTIVE:
			case smaliParser.CATCHALL_DIRECTIVE:
			case smaliParser.LINE_DIRECTIVE:
			case smaliParser.PARAMETER_DIRECTIVE:
			case smaliParser.END_PARAMETER_DIRECTIVE:
			case smaliParser.LOCAL_DIRECTIVE:
			case smaliParser.END_LOCAL_DIRECTIVE:
			case smaliParser.RESTART_LOCAL_DIRECTIVE:
			case smaliParser.PROLOGUE_DIRECTIVE:
			case smaliParser.EPILOGUE_DIRECTIVE:
			
			case smaliParser.ANNOTATION_VISIBILITY:
			case smaliParser.ACCESS_SPEC:
			case smaliParser.HIDDENAPI_RESTRICTION:
			case smaliParser.VERIFICATION_ERROR_TYPE:
			case smaliParser.INLINE_INDEX:
			case smaliParser.VTABLE_INDEX:
			case smaliParser.FIELD_OFFSET:
			case smaliParser.METHOD_HANDLE_TYPE_FIELD:
			case smaliParser.METHOD_HANDLE_TYPE_METHOD:
			
			case smaliParser.INSTRUCTION_FORMAT10t:
			case smaliParser.INSTRUCTION_FORMAT10x:
			case smaliParser.INSTRUCTION_FORMAT10x_ODEX:
			case smaliParser.INSTRUCTION_FORMAT11n:
			case smaliParser.INSTRUCTION_FORMAT11x:
			case smaliParser.INSTRUCTION_FORMAT12x_OR_ID:
			case smaliParser.INSTRUCTION_FORMAT12x:
			case smaliParser.INSTRUCTION_FORMAT20bc:
			case smaliParser.INSTRUCTION_FORMAT20t:
			case smaliParser.INSTRUCTION_FORMAT21c_FIELD:
			case smaliParser.INSTRUCTION_FORMAT21c_FIELD_ODEX:
			case smaliParser.INSTRUCTION_FORMAT21c_STRING:
			case smaliParser.INSTRUCTION_FORMAT21c_TYPE:
			case smaliParser.INSTRUCTION_FORMAT21c_METHOD_HANDLE:
			case smaliParser.INSTRUCTION_FORMAT21c_METHOD_TYPE:
			case smaliParser.INSTRUCTION_FORMAT21ih:
			case smaliParser.INSTRUCTION_FORMAT21lh:
			case smaliParser.INSTRUCTION_FORMAT21s:
			case smaliParser.INSTRUCTION_FORMAT21t:
			case smaliParser.INSTRUCTION_FORMAT22b:
			case smaliParser.INSTRUCTION_FORMAT22c_FIELD:
			case smaliParser.INSTRUCTION_FORMAT22c_FIELD_ODEX:
			case smaliParser.INSTRUCTION_FORMAT22c_TYPE:
			case smaliParser.INSTRUCTION_FORMAT22cs_FIELD:
			case smaliParser.INSTRUCTION_FORMAT22s_OR_ID:
			case smaliParser.INSTRUCTION_FORMAT22s:
			case smaliParser.INSTRUCTION_FORMAT22t:
			case smaliParser.INSTRUCTION_FORMAT22x:
			case smaliParser.INSTRUCTION_FORMAT23x:
			case smaliParser.INSTRUCTION_FORMAT30t:
			case smaliParser.INSTRUCTION_FORMAT31c:
			case smaliParser.INSTRUCTION_FORMAT31i_OR_ID:
			case smaliParser.INSTRUCTION_FORMAT31i:
			case smaliParser.INSTRUCTION_FORMAT31t:
			case smaliParser.INSTRUCTION_FORMAT32x:
			case smaliParser.INSTRUCTION_FORMAT35c_CALL_SITE:
			case smaliParser.INSTRUCTION_FORMAT35c_METHOD:
			case smaliParser.INSTRUCTION_FORMAT35c_METHOD_OR_METHOD_HANDLE_TYPE:
			case smaliParser.INSTRUCTION_FORMAT35c_METHOD_ODEX:
			case smaliParser.INSTRUCTION_FORMAT35c_TYPE:
			case smaliParser.INSTRUCTION_FORMAT35mi_METHOD:
			case smaliParser.INSTRUCTION_FORMAT35ms_METHOD:
			case smaliParser.INSTRUCTION_FORMAT3rc_CALL_SITE:
			case smaliParser.INSTRUCTION_FORMAT3rc_METHOD:
			case smaliParser.INSTRUCTION_FORMAT3rc_METHOD_ODEX:
			case smaliParser.INSTRUCTION_FORMAT3rc_TYPE:
			case smaliParser.INSTRUCTION_FORMAT3rmi_METHOD:
			case smaliParser.INSTRUCTION_FORMAT3rms_METHOD:
			case smaliParser.INSTRUCTION_FORMAT45cc_METHOD:
			case smaliParser.INSTRUCTION_FORMAT4rcc_METHOD:
			case smaliParser.INSTRUCTION_FORMAT51l:
			
				return SyntaxKind.Keyword;
			case smaliParser.PARAM_LIST_OR_ID_PRIMITIVE_TYPE:
			case smaliParser.PRIMITIVE_TYPE:
			case smaliParser.VOID_TYPE:
			case smaliParser.CLASS_DESCRIPTOR:
			case smaliParser.ARRAY_TYPE_PREFIX:
				return SyntaxKind.TypeIdentifier;

			case smaliParser.SIMPLE_NAME:
			case smaliParser.MEMBER_NAME:
				return SyntaxKind.Identifier;
				
			case smaliParser.DOTDOT:
			case smaliParser.ARROW:
			case smaliParser.EQUAL:
			case smaliParser.COLON:
			case smaliParser.COMMA:
			case smaliParser.OPEN_BRACE:
			case smaliParser.CLOSE_BRACE:
			case smaliParser.OPEN_PAREN:
			case smaliParser.CLOSE_PAREN:
				return SyntaxKind.Separator;
				
			case smaliParser.POSITIVE_INTEGER_LITERAL:
			case smaliParser.NEGATIVE_INTEGER_LITERAL:
			case smaliParser.LONG_LITERAL:
			case smaliParser.SHORT_LITERAL:
			case smaliParser.BYTE_LITERAL:
			case smaliParser.FLOAT_LITERAL_OR_ID:
			case smaliParser.DOUBLE_LITERAL_OR_ID:
			case smaliParser.FLOAT_LITERAL:
			case smaliParser.DOUBLE_LITERAL:
			case smaliParser.BOOL_LITERAL:
			case smaliParser.NULL_LITERAL:
			case smaliParser.CHAR_LITERAL:
			case smaliParser.STRING_LITERAL:
				return SyntaxKind.Literal;
			case smaliParser.LINE_COMMENT:
				return SyntaxKind.Comment;
			case smaliParser.WHITE_SPACE:
				default:
				return SyntaxKind.Plain;
		}
	}
}
