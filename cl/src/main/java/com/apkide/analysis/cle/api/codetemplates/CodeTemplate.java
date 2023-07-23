package com.apkide.analysis.cle.api.codetemplates;

import androidx.annotation.Keep;

@Keep
public class CodeTemplate {
   public String abbrev = "abbrev";
   public String text = "";
   public String description = "";

   public CodeTemplate() {
   }

   public CodeTemplate clone() {
      CodeTemplate res = new CodeTemplate();
      res.abbrev = this.abbrev;
      res.text = this.text;
      res.description = this.description;
      return res;
   }
}
