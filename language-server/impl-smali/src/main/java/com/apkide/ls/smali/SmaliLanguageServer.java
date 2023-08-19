package com.apkide.ls.smali;

import static com.apkide.smali.smali.SmaliParser.*;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ls.api.Highlights;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.lexer.Lexer;
import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;
import com.apkide.smali.smali.SmaliFlexLexer;
import com.apkide.smali.smali.SmaliParser;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SmaliLanguageServer implements LanguageServer {
    private Model myModel;
    
    @Override
    public void configure(@NonNull Model model) {
        myModel = model;
    }
    
    @Override
    public void initialize(@NonNull String rootPath, @NonNull Map<String, Object> options, @NonNull Map<String, String> workspacePaths) {
    
    }
    
    @Override
    public void shutdown() {
    
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
    
    private final SmaliFlexLexer myLexer = new SmaliFlexLexer(null, 40);
    private final Highlights myHighlights = new Highlights();
    
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
    
    @Override
    public void requestHighlighting(@NonNull String filePath) {
        myModel.getHighlighterCallback().highlightStarted(filePath, 0);
        try {
            myHighlights.clear();
            myLexer.yyreset(FileSystem.readFile(filePath));
            myLexer.setSuppressErrors(false);
            myLexer.yybegin(myLexer.yystate());
            
            Token token = myLexer.nextToken();
            int style = getStyle(token.getType());
            int startColumn = myLexer.getLine() - 1;
            int startLine = myLexer.getColumn();
            
            
            while (true) {
                token = myLexer.nextToken();
                if (token == null) break;
                
                int nextStyle = getStyle(token.getType());
                int line = myLexer.getLine() - 1;
                int column = myLexer.getColumn();
                if (nextStyle == Lexer.StringStyle) {
                    column = Math.min(column, column - token.getText().length());
                }
                
                myHighlights.highlight(style, startLine, startColumn, line, column);
                
                style = nextStyle;
                startColumn = column;
                startLine = line;
                
                if (token.getType() == -1) break;
            }
            myModel.getHighlighterCallback().fileHighlighting(filePath, 0, myHighlights);
        } catch (IOException ignored) {
        
        }
        try {
            myLexer.yyreset(FileSystem.readFile(filePath));
            myLexer.yybegin(myLexer.yystate());
            myLexer.setSourceFile(new File(filePath));
    
            CommonTokenStream tokens = new CommonTokenStream((TokenSource) myLexer);
            SmaliParser parser = new SmaliParser(tokens);
            parser.setVerboseErrors(false);
            parser.setAllowOdex(true);
            parser.setApiLevel(40);
    
            SmaliParser.smali_file_return result = parser.smali_file();
            CommonTree t = result.getTree();
            TreeVisitor visitor=new TreeVisitor();
            visitor.visit(t, new TreeVisitorAction() {
                @Override
                public Object pre(Object t) {
                    return t;
                }
    
                @Override
                public Object post(Object t) {
                    CommonTree tree= (CommonTree) t;
                    //TODO: Semantic highlighting
                    return t;
                }
            });
            
        }catch (Exception e){
        
        }
    
        myModel.getHighlighterCallback().highlightFinished(filePath, 0);
    }
    
    @Override
    public void requestCompletion(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void findSymbols(@NonNull String filePath) {
    
    }
    
    @Override
    public void findAPI(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {
    
    }
    
    @Override
    public void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
    
    }
    
    @Override
    public void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
    
    }
    
    @Override
    public void prepareInline(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void inline(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void safeDelete(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void surroundWith(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
    
    @Override
    public void comment(@NonNull String filePath, @NonNull Range range) {
    
    }
    
    @Override
    public void uncomment(@NonNull String filePath, @NonNull Range range) {
    
    }
}
