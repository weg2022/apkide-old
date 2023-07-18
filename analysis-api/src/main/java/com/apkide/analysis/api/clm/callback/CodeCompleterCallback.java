package com.apkide.analysis.api.clm.callback;


import com.apkide.analysis.api.clm.Entity;
import com.apkide.analysis.api.clm.FileEntry;
import com.apkide.analysis.api.clm.Type;
import com.apkide.analysis.api.clm.Variable;

public interface CodeCompleterCallback {
   void listElementUnknownIdentifierFound(String identifier);

   void listStarted();

   void listTitleFound(Entity entity);

   void listElementEntityFound(Entity entity, Type type, boolean isEnclosing);

   void listElementEntityFound(Entity entity, Type type, boolean isEnclosing, String name);

   void listElementEntityFound(Entity entity, Type type);

   void listElementEntityFound(Entity entity, boolean isEnclosing);

   void listElementEntityFound(Entity entity);

   void listElementVariableFound(Variable variable);

   void listElementKeywordFound(String keyword);

   void listCompleted(FileEntry fileEntry, int line, int column, boolean var4, boolean var5);

   void parameterListEntityFound(Entity entity, Type type);

   void parameterListFound(FileEntry fileEntry, int line, int column, int[] lines, int[] columns);

   void parameterListNotFound(FileEntry fileEntry, int line, int column);
}
