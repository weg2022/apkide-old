package com.apkide.analysis.cle.api.formatter;

public class CodeFormatterSizeOptions {
   public int indentationSize;
   public int tabSize;
   public int maxLineWidth;

   public CodeFormatterSizeOptions() {
      this.setDefaults();
   }

   public CodeFormatterSizeOptions clone() {
      CodeFormatterSizeOptions options = new CodeFormatterSizeOptions();
      options.indentationSize = this.indentationSize;
      options.tabSize = this.tabSize;
      options.maxLineWidth = this.maxLineWidth;
      return options;
   }

   private void setDefaults() {
      this.indentationSize = 4;
      this.tabSize = 4;
      this.maxLineWidth = 100;
   }
}
