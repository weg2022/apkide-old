package com.apkide.analysis.cle.api.parenmatcher;

public class ParenMatchResult {
   public int parenMatchLine = -1;
   public int parenMatchColumn;
   public int caretParenMatchLine = -1;
   public int caretParenMatchColumn;
   public boolean matchingValid;

   public ParenMatchResult() {
   }
}
