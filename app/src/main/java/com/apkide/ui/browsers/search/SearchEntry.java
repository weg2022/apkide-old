package com.apkide.ui.browsers.search;

import com.apkide.ls.api.symbol.Symbol;
import com.apkide.ui.util.Entry;

public class SearchEntry implements Entry, Comparable<SearchEntry> {
    
    private String label;
    private Symbol mySymbol;
    
    
    public boolean isSymbol() {
        return mySymbol != null;
    }
    
    public Symbol getSymbol() {
        return mySymbol;
    }
    
    @Override
    public int compareTo(SearchEntry o) {
        
        if (isSymbol() && o.isSymbol()) {
            if (getSymbol().location.filePath.equals(o.getSymbol().location.filePath)) {
                return getSymbol().location.compareTo(o.getSymbol().location);
            }
        }
        
        return label.compareTo(o.label);
    }
}
