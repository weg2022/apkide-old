package com.apkide.ui;

import static com.apkide.app.AppContext.getPreferences;

public class EditorPreferences {

    public static int getFontSize(){
        return getPreferences().getInt("fontSize", 14);
    }

    public static String getExternalFont(){
        return getPreferences().getString("externalFont", "");
    }

    public static boolean isWordWrapEnabled(){
        return getPreferences().getBoolean("wordwrapEnabled", false);
    }

    public static boolean isZoomable(){
        return getPreferences().getBoolean("zoomable", true);
    }

    public static boolean isLineNumberShow(){
        return getPreferences().getBoolean("lineNumberShow", true);
    }

    public static boolean isLineHighlightShow(){
        return getPreferences().getBoolean("lineHighlightShow", true);
    }

    public static boolean isWhitespaceShow(){
        return getPreferences().getBoolean("whitespaceShow", true);
    }

    public static boolean isMethodSeparatorShow(){
        return getPreferences().getBoolean("methodSeparatorShow", true);
    }

    public static boolean isStructureShow(){
        return getPreferences().getBoolean("structureShow", true);
    }

    public static boolean isCaretBlockMode(){
        return getPreferences().getBoolean("caretBlockMode", false);
    }

    public static boolean isCaretBlinkEnabled(){
        return getPreferences().getBoolean("caretBlinkEnabled", true);
    }

    public static long getCaretBlinkRate(){
        return getPreferences().getLong("caretBlinkRate", 500);
    }

    public static boolean isLigatureEnabled(){
        return getPreferences().getBoolean("ligatureEnabled", true);
    }

    public static int getTabSize(){
        return getPreferences().getInt("tabSize", 4);
    }

    public static int getIndentationSize(){
        return getPreferences().getInt("indentSize", 4);
    }

    public static boolean isIndentationEnabled(){
        return getPreferences().getBoolean("indentationEnabled", true);
    }

    public static boolean isUseTabsInsteadSpaces(){
        return getPreferences().getBoolean("useTabs", true);
    }

    public static boolean isBasicSuggestionEnabled(){
        return getPreferences().getBoolean("basicSuggestionEnabled", true);
    }




}
