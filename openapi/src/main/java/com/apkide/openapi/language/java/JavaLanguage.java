package com.apkide.openapi.language.java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.openapi.language.Analyzer;
import com.apkide.openapi.language.Formatter;
import com.apkide.openapi.language.Language;
import com.apkide.openapi.language.ResultCallback;
import com.apkide.openapi.language.Tools;
import com.apkide.openapi.language.api.ErrorKind;
import com.apkide.openapi.language.api.FileEntry;
import com.apkide.openapi.language.api.Highlights;
import com.apkide.openapi.language.api.Model;
import com.apkide.openapi.language.java.ast.JavaLexer;
import com.apkide.openapi.language.java.ast.JavaParser;
import com.apkide.openapi.language.java.ast.JavaParserBaseVisitor;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.SyntaxTree;

import java.io.IOException;
import java.io.Reader;

public class JavaLanguage implements Language {
    private final Model myModel;
    private final JavaLexer myLexer = new JavaLexer(null);
    private final JavaParser myParser = new JavaParser(null);
    private JavaFormatter myFormatter;
    private JavaAnalyzer myAnalyzer;
    private JavaTools myTools;

    public JavaLanguage(Model model) {
        myModel = model;
        if (myModel != null) {
            myAnalyzer = new JavaAnalyzer(model, this);
            myTools = new JavaTools(model, this);
        }
        myFormatter = new JavaFormatter(model, this);
    }

    @NonNull
    @Override
    public String getName() {
        return "Java";
    }

    @NonNull
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.java"};
    }

    @Nullable
    @Override
    public Formatter getFormatter() {
        return myFormatter;
    }

    @Nullable
    @Override
    public JavaLexer getLexer() {
        return myLexer;
    }

    @Nullable
    @Override
    public JavaParser getParser() {
        return myParser;
    }

    @Nullable
    @Override
    public Analyzer getAnalyzer() {
        return myAnalyzer;
    }

    @Nullable
    @Override
    public Tools getTools() {
        return myTools;
    }

    @Override
    public boolean isArchiveFileSupported() {
        return false;
    }

    @Nullable
    @Override
    public String[] getArchiveEntries(@NonNull String filePath) throws IOException {
        return new String[0];
    }

    @Nullable
    @Override
    public Reader getArchiveEntryReader(@NonNull String filePath, @NonNull String entryPath, @NonNull String encoding) throws IOException {
        return null;
    }

    @Override
    public long getArchiveVersion(@NonNull String filePath) {
        return 0;
    }

    @Override
    public void closeArchiveFile() {

    }

    @Override
    public void processVersion(@NonNull FileEntry file) {

    }

    @Override
    public void fileHighlighting(@NonNull FileEntry file, @NonNull Reader reader, @NonNull ResultCallback<Highlights> callback) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromReader(reader));
        Highlights highlights = new Highlights();
        if (myModel != null) {
            lexer.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    myModel.getErrorTable().addError(
                            file,
                            line - 1,
                            charPositionInLine - 1,
                            line - 1,
                            charPositionInLine,
                            msg, ErrorKind.SYNTAX_ERROR);
                }
            });
        }
        while (!lexer._hitEOF) {
            Token token = lexer.nextToken();
            int startLine = token.getLine() - 1;
            int startColumn = token.getCharPositionInLine() - 1;
            int endLine = lexer.getLine() - 1;
            int endColumn = lexer.getCharPositionInLine() - 1;
            highlights.highlight(token.getType(), startLine, startColumn, endLine, endColumn);
        }
        callback.resultFound(highlights);
    }

    @Override
    public void semanticHighlighting(@NonNull FileEntry file, @NonNull Language language, @Nullable SyntaxTree ast) throws IOException {
        if (ast instanceof JavaParser.CompilationUnitContext) {
            JavaParser.CompilationUnitContext unit = (JavaParser.CompilationUnitContext) ast;
            JavaParserBaseVisitor<Void> visitor = new JavaParserBaseVisitor<>() {
                @Override
                public Void visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
                    if (ctx != null && !ctx.isEmpty()) {
                        if (ctx.PACKAGE() != null) {
                            Token symbol = ctx.PACKAGE().getSymbol();
                            myModel.getHighlighterCallback().keywordFound(
                                    file,
                                    language,
                                    symbol.getLine() - 1,
                                    symbol.getCharPositionInLine() - 1,
                                    symbol.getLine() - 1,
                                    symbol.getCharPositionInLine() - 1 + symbol.getText().length());
                        }
                        if (ctx.qualifiedName() != null) {
                            if (ctx.qualifiedName().identifier() != null) {
                                for (JavaParser.IdentifierContext context : ctx.qualifiedName().identifier()) {
                                    myModel.getHighlighterCallback().packageFound(
                                            file,
                                            language,
                                            context.getStart().getLine() - 1,
                                            context.getStart().getCharPositionInLine() - 1,
                                            context.getStop().getLine() - 1,
                                            context.getStop().getCharPositionInLine() - 1
                                    );
                                }
                            }
                        }
                    }
                    return super.visitPackageDeclaration(ctx);
                }
            };
            visitor.visitCompilationUnit(unit);
        }
    }

    @Override
    public void createSyntaxTree(@NonNull Reader reader, @NonNull FileEntry file, long syntaxVersion, @NonNull ResultCallback<SyntaxTree> callback, boolean errors, boolean parseCode, boolean parseComments) throws IOException {

    }

}
