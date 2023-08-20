package com.apkide.common;

import static org.junit.Assert.assertEquals;

import com.apkide.common.text.TextModel;
import com.apkide.common.text.TextModelImpl;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        
        TextModel model=new TextModelImpl();
        model.setText("package com.apkide.common;\n" +
                "\n" +
                "import static org.junit.Assert.assertEquals;\n" +
                "\n" +
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
                "        assertEquals(4, 2 + 2);\n" +
                "        \n" +
                "        TextModel model=new TextModelImpl();\n" +
                "        model.setText(\"\");\n" +
                "    }\n" +
                "}");
        System.out.println(model.getText());
        System.out.println("-----------------------------");
        model.insert(1,0,"Hello");
        System.out.println(model.getText());
        System.out.println("-----------------------------");
        model.insertLineBreak(1,0);
        System.out.println(model.getText());
        System.out.println("-----------------------------");
        model.removeLineBreak(1);
        System.out.println(model.getText());
        System.out.println("-----------------------------");
        model.remove(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
        System.out.println(model.getText());
        System.out.println("-----------------------------");
    }
}