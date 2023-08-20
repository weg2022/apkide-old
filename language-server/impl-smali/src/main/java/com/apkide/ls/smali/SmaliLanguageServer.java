package com.apkide.ls.smali;

import static com.apkide.smali.smali.SmaliParser.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.ls.api.CodeCompiler;
import com.apkide.ls.api.CodeCompleter;
import com.apkide.ls.api.CodeFormatter;
import com.apkide.ls.api.CodeHighlighter;
import com.apkide.ls.api.CodeNavigation;
import com.apkide.ls.api.CodeRefactor;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.lexer.Lexer;

public class SmaliLanguageServer implements LanguageServer {
    private Model myModel;
    
    @Override
    public void initialize(@NonNull Model model) {
        myModel = model;
    }
    
    @Override
    public void shutdown() {
    
    }
    
    @Override
    public void configureRootPah(@NonNull String rootPath) {
    
    }
    
    @NonNull
    @Override
    public String getName() {
        return "Smali";
    }
    
    @NonNull
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.smali"};
    }
    
    @Nullable
    @Override
    public CodeCompleter getCompleter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeFormatter getFormatter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeHighlighter getHighlighter() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeNavigation getNavigation() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeRefactor getRefactor() {
        return null;
    }
    
    @Nullable
    @Override
    public CodeCompiler getCompiler() {
        return null;
    }

    private int getStyle(int type) {
        switch (type) {
            case CLASS_DIRECTIVE:
            case SUPER_DIRECTIVE:
            case IMPLEMENTS_DIRECTIVE:
            case SOURCE_DIRECTIVE:
            case FIELD_DIRECTIVE:
            case END_FIELD_DIRECTIVE:
            case SUBANNOTATION_DIRECTIVE:
            case END_SUBANNOTATION_DIRECTIVE:
            case ANNOTATION_DIRECTIVE:
            case END_ANNOTATION_DIRECTIVE:
            case ENUM_DIRECTIVE:
            case METHOD_DIRECTIVE:
            case END_METHOD_DIRECTIVE:
            case REGISTERS_DIRECTIVE:
            case LOCALS_DIRECTIVE:
            case ARRAY_DATA_DIRECTIVE:
            case END_ARRAY_DATA_DIRECTIVE:
            case PACKED_SWITCH_DIRECTIVE:
            case END_PACKED_SWITCH_DIRECTIVE:
            case SPARSE_SWITCH_DIRECTIVE:
            case END_SPARSE_SWITCH_DIRECTIVE:
            case CATCH_DIRECTIVE:
            case CATCHALL_DIRECTIVE:
            case LINE_DIRECTIVE:
            case PARAMETER_DIRECTIVE:
            case END_PARAMETER_DIRECTIVE:
            case LOCAL_DIRECTIVE:
            case END_LOCAL_DIRECTIVE:
            case RESTART_LOCAL_DIRECTIVE:
            case PROLOGUE_DIRECTIVE:
            case EPILOGUE_DIRECTIVE:
                return Lexer.MetadataStyle;
            
            case BOOL_LITERAL:
            case NULL_LITERAL:
                return Lexer.KeywordStyle;
            
            case POSITIVE_INTEGER_LITERAL:
            case NEGATIVE_INTEGER_LITERAL:
            case LONG_LITERAL:
            case SHORT_LITERAL:
            case BYTE_LITERAL:
            case FLOAT_LITERAL_OR_ID:
            case DOUBLE_LITERAL_OR_ID:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                return Lexer.NumberStyle;
            
            case STRING_LITERAL:
            case CHAR_LITERAL:
                return Lexer.StringStyle;
            
            case ANNOTATION_VISIBILITY:
            case ACCESS_SPEC:
            case HIDDENAPI_RESTRICTION:
            case VERIFICATION_ERROR_TYPE:
            case METHOD_HANDLE_TYPE_FIELD:
            case METHOD_HANDLE_TYPE_METHOD:
                return Lexer.KeywordStyle;
            
            case INLINE_INDEX:
            case VTABLE_INDEX:
            case FIELD_OFFSET:
                return Lexer.TypeStyle;
            
            case INSTRUCTION_FORMAT10t:
            case INSTRUCTION_FORMAT10x:
            case INSTRUCTION_FORMAT10x_ODEX:
            case INSTRUCTION_FORMAT11n:
            case INSTRUCTION_FORMAT11x:
            case INSTRUCTION_FORMAT12x_OR_ID:
            case INSTRUCTION_FORMAT12x:
            case INSTRUCTION_FORMAT20bc:
            case INSTRUCTION_FORMAT20t:
            case INSTRUCTION_FORMAT21c_FIELD:
            case INSTRUCTION_FORMAT21c_FIELD_ODEX:
            case INSTRUCTION_FORMAT21c_STRING:
            case INSTRUCTION_FORMAT21c_TYPE:
            case INSTRUCTION_FORMAT21c_METHOD_HANDLE:
            case INSTRUCTION_FORMAT21c_METHOD_TYPE:
            case INSTRUCTION_FORMAT21ih:
            case INSTRUCTION_FORMAT21lh:
            case INSTRUCTION_FORMAT21s:
            case INSTRUCTION_FORMAT21t:
            case INSTRUCTION_FORMAT22b:
            case INSTRUCTION_FORMAT22c_FIELD:
            case INSTRUCTION_FORMAT22c_FIELD_ODEX:
            case INSTRUCTION_FORMAT22c_TYPE:
            case INSTRUCTION_FORMAT22cs_FIELD:
            case INSTRUCTION_FORMAT22s_OR_ID:
            case INSTRUCTION_FORMAT22s:
            case INSTRUCTION_FORMAT22t:
            case INSTRUCTION_FORMAT22x:
            case INSTRUCTION_FORMAT23x:
            case INSTRUCTION_FORMAT30t:
            case INSTRUCTION_FORMAT31c:
            case INSTRUCTION_FORMAT31i_OR_ID:
            case INSTRUCTION_FORMAT31i:
            case INSTRUCTION_FORMAT31t:
            case INSTRUCTION_FORMAT32x:
            case INSTRUCTION_FORMAT35c_CALL_SITE:
            case INSTRUCTION_FORMAT35c_METHOD:
            case INSTRUCTION_FORMAT35c_METHOD_OR_METHOD_HANDLE_TYPE:
            case INSTRUCTION_FORMAT35c_METHOD_ODEX:
            case INSTRUCTION_FORMAT35c_TYPE:
            case INSTRUCTION_FORMAT35mi_METHOD:
            case INSTRUCTION_FORMAT35ms_METHOD:
            case INSTRUCTION_FORMAT3rc_CALL_SITE:
            case INSTRUCTION_FORMAT3rc_METHOD:
            case INSTRUCTION_FORMAT3rc_METHOD_ODEX:
            case INSTRUCTION_FORMAT3rc_TYPE:
            case INSTRUCTION_FORMAT3rmi_METHOD:
            case INSTRUCTION_FORMAT3rms_METHOD:
            case INSTRUCTION_FORMAT45cc_METHOD:
            case INSTRUCTION_FORMAT4rcc_METHOD:
            case INSTRUCTION_FORMAT51l:
                return Lexer.KeywordStyle;
            
            
            case LINE_COMMENT:
                return Lexer.CommentStyle;
            
            default:
                return 0;
        }
    }
    
}
