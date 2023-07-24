package com.apkide.openapi;

import static org.junit.Assert.assertEquals;

import com.apkide.openapi.language.java.ast.JavaLexer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        JavaLexer lexer=new JavaLexer(CharStreams.fromString("package com.apkide.openapi;\n" +
                "\n" +
                "import static org.junit.Assert.assertEquals;\n" +
                "\n" +
                "import com.apkide.openapi.language.java.ast.JavaLexer;\n" +
                "\n" +
                "import org.antlr.v4.runtime.CharStreams;\n" +
                "import org.junit.Test;\n" +
                "\n" +
                "/**\n" +
                " * Example local unit test, which will execute on the development machine (host).\n" +
                " *\n" +
                " * @see <a href=\"http://d.android.com/tools/testing\">Testing documentation</a>\n" +
                " */\n" +
                "public class ExampleUnitTest {\n" +
                "    @Test\n" +
                "    public void addition_isCorrect() {\n" +
                "        JavaLexer lexer=new JavaLexer(CharStreams.fromString(\"\"))\n" +
                "        assertEquals(4, 2 + 2);\n" +
                "    }\n" +
                "}"));
        while (!lexer._hitEOF) {
            Token token=lexer.nextToken();
            int startLine=token.getLine();
            int startColumn=token.getCharPositionInLine();
            int endLine=lexer.getLine();
            int endColumn=lexer.getCharPositionInLine();
            System.out.println(String.format("sl:%d sc:%d el:%d ec:%d", startLine,startColumn,endLine,endColumn));
        }
        assertEquals(4, 2 + 2);
    }
}