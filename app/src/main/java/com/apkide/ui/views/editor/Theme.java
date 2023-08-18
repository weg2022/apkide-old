package com.apkide.ui.views.editor;

import android.graphics.Typeface;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.Color;
import com.apkide.common.TextStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Theme {
    
    public interface Colors {
        int FontColor = 0;
        int BackgroundColor = 1;
        int CaretColor = 2;
        int CaretLineColor = 3;
        int CaretLineNumberColor = 4;
        int LineNumberColor = 5;
        int SelectionColor = 6;
        int DragHandleColor = 7;
        int FindMatchColor = 8;
        int TypingColor = 9;
        int HyperlinkColor = 10;
        int TodoColor = 11;
        int WarningColor = 12;
        int ErrorColor = 13;
        int DeprecatedColor = 14;
        
    }
    
    public interface Styles {
        int PlainStyle = 0;
        int KeywordStyle = 1;
        int OperatorStyle = 2;
        int SeparatorStyle = 3;
        int StringStyle = 4;
        int NumberStyle = 5;
        int MetadataStyle = 6;
        int IdentifierStyle = 7;
        int NamespaceStyle = 8;
        int ClassStyle = 9;
        int VariableStyle = 10;
        int FunctionStyle = 11;
        int FunctionCallStyle = 12;
        int ParameterStyle = 13;
        int CommentStyle = 14;
        int BlockCommentStyle = 15;
    }
    
    public interface ColorScheme {
        
        @NonNull
        String getName();
        
        @NonNull
        String getVersion();
        
        long getVersionCode();
        
        boolean isDark();
        
        void registerColors(@NonNull SparseArray<Color> colors);
        
        void registerStyles(@NonNull Map<Integer, TextStyle> styles);
    }
    
    public interface ThemeListener {
        void themeColorSchemeChanged(@NonNull Theme theme);
        
        void themeColorChanged(int colorId);
        
        void themeStyleChanged(int styleId);
    }
    
    public final static class DefaultColorScheme implements ColorScheme {
        
        private final boolean dark;
        
        public DefaultColorScheme(boolean dark) {
            this.dark = dark;
        }
        
        @NonNull
        @Override
        public String getName() {
            return "Default";
        }
        
        @NonNull
        @Override
        public String getVersion() {
            return "1.0";
        }
        
        @Override
        public long getVersionCode() {
            return 0;
        }
        
        @Override
        public boolean isDark() {
            return dark;
        }
        
        @Override
        public void registerColors(@NonNull SparseArray<Color> colors) {
            //Ignore
        }
        
        @Override
        public void registerStyles(@NonNull Map<Integer, TextStyle> styles) {
            //Ignore
        }
    }
    
    private ColorScheme myScheme;
    private final Map<Integer, TextStyle> myTextStyles = new HashMap<>();
    private final SparseArray<Color> myColors = new SparseArray<>();
    private final List<ThemeListener> myListeners = new Vector<>(1);
    
    public Theme() {
        this(new DefaultColorScheme(false));
    }
    
    public Theme(@NonNull ColorScheme scheme) {
        myScheme = scheme;
        resetDefaults();
    }
    
    public void addListener(@NonNull ThemeListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }
    
    public void removeListener(@NonNull ThemeListener listener) {
        myListeners.remove(listener);
    }
    
    public void resetDefaults() {
        //clear
        myColors.clear();
        myTextStyles.clear();
        
        //default
        myColors.put(Colors.FontColor, isDarkTheme() ? Color.White : Color.Black);
        myColors.put(Colors.BackgroundColor, isDarkTheme() ? Color.Black : Color.White);
        myColors.put(Colors.CaretColor, isDarkTheme() ? Color.White : Color.Black);
        myColors.put(Colors.CaretLineColor, isDarkTheme() ?
                new Color(35, 37, 43) : new Color(234, 242, 255));
        myColors.put(Colors.CaretLineNumberColor, null);
        myColors.put(Colors.LineNumberColor, isDarkTheme() ?
                new Color(130, 136, 143) : new Color(153, 153, 153));
        myColors.put(Colors.SelectionColor, isDarkTheme() ?
                new Color(81, 91, 122) : new Color(164, 205, 255));
        myColors.put(Colors.DragHandleColor, new Color(3, 169, 244));
        myColors.put(Colors.FindMatchColor, isDarkTheme() ?
                new Color(50, 89, 61) : Color.Yellow);
        myColors.put(Colors.TypingColor, new Color(17, 161, 179));
        myColors.put(Colors.HyperlinkColor, isDarkTheme() ?
                new Color(84, 130, 255) : new Color(14, 14, 255));
        myColors.put(Colors.TodoColor, isDarkTheme() ?
                new Color(146, 161, 177) : new Color(74, 85, 96));
        myColors.put(Colors.WarningColor, isDarkTheme() ?
                new Color(190, 145, 23) : new Color(246, 235, 188));
        myColors.put(Colors.ErrorColor, isDarkTheme() ?
                new Color(1588, 41, 39) : Color.Red);
        myColors.put(Colors.DeprecatedColor, isDarkTheme() ?
                new Color(195, 195, 195) : new Color(64, 64, 64));
        
        
        myTextStyles.put(Styles.PlainStyle, null);
        myTextStyles.put(Styles.KeywordStyle, new TextStyle(isDarkTheme() ?
                new Color(252, 95, 163) : new Color(155, 35, 147), Typeface.BOLD));
        myTextStyles.put(Styles.OperatorStyle, null);
        myTextStyles.put(Styles.SeparatorStyle, null);
        myTextStyles.put(Styles.StringStyle, new TextStyle(isDarkTheme() ?
                new Color(252, 106, 93) : new Color(196, 26, 22)));
        myTextStyles.put(Styles.NumberStyle, new TextStyle(isDarkTheme() ?
                new Color(208, 191, 105) : new Color(28, 0, 207)));
        myTextStyles.put(Styles.MetadataStyle, new TextStyle(isDarkTheme() ?
                new Color(208, 168, 205) : new Color(57, 0, 160)));
        myTextStyles.put(Styles.IdentifierStyle, null);
        myTextStyles.put(Styles.NamespaceStyle, new TextStyle(isDarkTheme() ?
                new Color(208, 168, 255) : new Color(108, 54, 169)));
        myTextStyles.put(Styles.ClassStyle, new TextStyle(isDarkTheme() ?
                new Color(93, 216, 255) : new Color(11, 79, 121)));
        myTextStyles.put(Styles.VariableStyle, new TextStyle(isDarkTheme() ?
                new Color(163, 103, 230) : new Color(108, 54, 169)));
        myTextStyles.put(Styles.FunctionStyle, new TextStyle(isDarkTheme() ?
                new Color(65, 161, 192) : new Color(15, 104, 160)));
        myTextStyles.put(Styles.FunctionCallStyle, new TextStyle(isDarkTheme() ?
                new Color(103, 183, 164) : new Color(50, 109, 116)));
        myTextStyles.put(Styles.ParameterStyle, null);
        myTextStyles.put(Styles.CommentStyle, new TextStyle(isDarkTheme() ?
                new Color(108, 121, 134) : new Color(93, 108, 121)));
        myTextStyles.put(Styles.BlockCommentStyle, new TextStyle(isDarkTheme() ?
                new Color(108, 121, 134) : new Color(93, 108, 121)));
        
        //overwrite
        SparseArray<Color> colors = new SparseArray<>(15);
        myScheme.registerColors(colors);
        for (int i = 0; i < colors.size(); i++) {
            int key = colors.keyAt(i);
            Color value = colors.get(key);
            if (value != null) {
                myColors.put(key, value);
            }
        }
        
        Map<Integer, TextStyle> styles = new HashMap<>(30);
        myScheme.registerStyles(styles);
        for (Integer key : styles.keySet()) {
            TextStyle value = styles.get(key);
            if (value != null) {
                myTextStyles.put(key, value);
            }
        }
    }
    
    public void applyScheme(@NonNull ColorScheme scheme) {
        myScheme = scheme;
        resetDefaults();
        for (ThemeListener listener : myListeners) {
            listener.themeColorSchemeChanged(this);
        }
    }
    
    @NonNull
    public ColorScheme getScheme() {
        return myScheme;
    }
    
    public void setTextStyle(int styleId, @NonNull TextStyle style) {
        myTextStyles.put(styleId, style);
        for (ThemeListener listener : myListeners) {
            listener.themeStyleChanged(styleId);
        }
    }
    
    @Nullable
    public TextStyle getTextStyle(int styleId) {
        return myTextStyles.get(styleId);
    }
    
    public void setColor(int colorId, Color colorValue) {
        myColors.put(colorId, colorValue);
        for (ThemeListener listener : myListeners) {
            listener.themeColorChanged(colorId);
        }
    }
    
    @Nullable
    public Color getColor(int colorId) {
        return myColors.get(colorId);
    }
    
    @NonNull
    public String getName() {
        return myScheme.getName();
    }
    
    @NonNull
    public String getVersion() {
        return myScheme.getVersion();
    }
    
    public long getVersionCode() {
        return myScheme.getVersionCode();
    }
    
    public boolean isDarkTheme() {
        return myScheme.isDark();
    }
    
}
