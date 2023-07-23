package com.apkide.analysis.cle.api;

import com.apkide.analysis.cle.api.codetemplates.CodeTemplate;
import com.apkide.analysis.cle.api.highlighter.HighlightingStyle;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.Reader;

public class XmlUtilities {


   public static HighlightingStyle[] getHighlightingStylesFromXml(Reader xmlReader) {
      XStream xstream = new XStream(new DomDriver());
      xstream.setMode(1001);
      xstream.alias("highlightingStyle", HighlightingStyle.class);
      xstream.alias("highlightingStyles", HighlightingStyle[].class);

      try {
         return (HighlightingStyle[])xstream.fromXML(xmlReader);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   public static CodeTemplate[] getCodeTemplatesFromXml(Reader xmlReader) {
      XStream xstream = new XStream(new DomDriver());
      xstream.setMode(1001);
      xstream.alias("smartTemplate", CodeTemplate.class);
      xstream.alias("codeTemplates", CodeTemplate[].class);

      try {
         return (CodeTemplate[])xstream.fromXML(xmlReader);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }
}
