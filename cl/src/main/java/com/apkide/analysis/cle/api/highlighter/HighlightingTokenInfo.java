package com.apkide.analysis.cle.api.highlighter;

public class HighlightingTokenInfo {
   private final byte tokenValue;
   private final String description;

   public HighlightingTokenInfo(byte tokenValue, String description) {
      this.tokenValue = tokenValue;
      this.description = description;
   }

   public byte getTokenValue() {
      return this.tokenValue;
   }

   public String getDescription() {
      return this.description;
   }
}
