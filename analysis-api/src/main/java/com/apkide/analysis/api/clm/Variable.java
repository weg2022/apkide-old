package com.apkide.analysis.api.clm;

import com.apkide.analysis.api.cle.Language;

public class Variable {
   private final FileEntry file;
   private final int line;
   private final int startColumn;
   private final int endcolumn;
   private final int identifier;
   private final Type type;
   private final Language myLanguage;

   public Variable(FileEntry file,Language language, int line, int startColumn, int endcolumn, int identifier, Type type) {
      this.file = file;
      this.line = line;
      this.startColumn = startColumn;
      this.endcolumn = endcolumn;
      this.identifier = identifier;
      this.type = type;
      this.myLanguage=language;
   }

   public String getHTMLString() {
      return getLanguage().getRenderer() != null ? getLanguage().getRenderer().getHTMLString(this) : "";
   }

   public FileEntry getFile() {
      return this.file;
   }

   public int getLine() {
      return this.line;
   }

   public int getStartColumn() {
      return this.startColumn;
   }

   public int getEndcolumn() {
      return this.endcolumn;
   }

   public int getIdentifier() {
      return this.identifier;
   }

   public Type getType() {
      return this.type;
   }

   public Language getLanguage() {
      return myLanguage;
   }
}
