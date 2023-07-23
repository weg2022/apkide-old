package com.apkide.analysis.clm.api.callback;

import com.apkide.analysis.clm.api.Entity;
import com.apkide.analysis.clm.api.FileEntry;
import com.apkide.analysis.clm.api.Type;
import com.apkide.analysis.clm.api.Variable;

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

   void listCompleted(FileEntry file, int line, int column);

   void parameterListEntityFound(Entity entity, Type type);

   void parameterListFound(FileEntry file, int line, int column, int[] lines, int[] columns);

   void parameterListNotFound(FileEntry file, int line, int column);
}
