package com.apkide.analysis.cle.api.highlighter;


import androidx.annotation.Keep;

@Keep
public class HighlightingStyle {
   public byte tokenValue;
   public int fontStyle;
   public int fontColor = 0xff212121;
   public int backgroundColor = 0;

   public HighlightingStyle() {
   }

   @Override
   public Object clone() {
      HighlightingStyle result = new HighlightingStyle();
      result.tokenValue = this.tokenValue;
      result.fontColor = this.fontColor;
      result.fontStyle = this.fontStyle;
      result.backgroundColor = this.backgroundColor;
      return result;
   }
}
