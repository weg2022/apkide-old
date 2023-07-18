package com.apkide.analysis.api.clm;


import com.apkide.common.collections.ListOfInt;

public class SyntaxTreePositionMaintainer {
   private ListOfInt positions = new ListOfInt();

   public SyntaxTreePositionMaintainer() {
   }

   public void add(SyntaxTree ast, int node) {
      for(int j = 0; j < this.positions.size(); j += 5) {
         if (this.positions.get(j) == node) {
            return;
         }
      }

      this.positions.push(node);
      this.positions.push(ast.getStartLine(node));
      this.positions.push(ast.getStartColumn(node));
      this.positions.push(ast.getEndLine(node));
      this.positions.push(ast.getEndColumn(node));
   }

   public void clear() {
      this.positions.clear();
   }

   public void replaceRegion(int startline, int startcolumn, int endline, int endcolumn, String str) {
      this.removeRegion(startline, startcolumn, endline, endcolumn);
      this.insertChars(startline, startcolumn, str);
   }

   public int getStartLine(SyntaxTree ast, int node) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i) == node) {
            return this.positions.get(i + 1);
         }
      }

      return ast.getStartLine(node);
   }

   public int getStartColumn(SyntaxTree ast, int node) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i) == node) {
            return this.positions.get(i + 2);
         }
      }

      return ast.getStartColumn(node);
   }

   public int getEndLine(SyntaxTree ast, int node) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i) == node) {
            return this.positions.get(i + 3);
         }
      }

      return ast.getEndLine(node);
   }

   public int getEndColumn(SyntaxTree ast, int node) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i) == node) {
            return this.positions.get(i + 4);
         }
      }

      return ast.getEndColumn(node);
   }

   public void insertRegion(int line, int column, int startLine, int startColumn, int endLine, int endColumn) {
      if (startLine == endLine) {
         this.insertChars(line, column, endColumn - startColumn);
      } else {
         this.insertChars(line, column, endColumn - 1);

         for(int i = startLine; i < endLine; ++i) {
            this.insertLineBreak(line, column);
         }
      }
   }

   public void removeRegion(int startLine, int startColumn, int endLine, int endColumn) {
      if (startLine == endLine) {
         this.removeChars(startLine, startColumn, endColumn - startColumn);
      } else {
         this.removeChars(endLine, 1, endColumn - startColumn);

         for(int i = 0; i < this.positions.size(); i += 5) {
            if (this.positions.get(i + 1) > startLine) {
               this.positions.set(i + 1, this.positions.get(i + 1) - (endLine - startLine));
            }

            if (this.positions.get(i + 3) > startLine) {
               this.positions.set(i + 3, this.positions.get(i + 3) - (endLine - startLine));
            }
         }
      }
   }

   public void removeChars(int line, int column, int count) {
      this.insertChars(line, column, -count);
   }

   public void insertChars(int line, int column, String str) {
      if (str.lastIndexOf(10) == -1) {
         this.insertChars(line, column, str.length());
      } else {
         this.insertChars(line, column, str.length() - str.lastIndexOf(10) - 1);
         int linecount = 0;

         for(int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '\n') {
               ++linecount;
            }
         }

         for(int i = 0; i < linecount; ++i) {
            this.insertLineBreak(line, column);
         }
      }
   }

   public void insertChars(int line, int column, int count) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i + 1) == line && this.positions.get(i + 2) >= column) {
            this.positions.set(i + 2, this.positions.get(i + 2) + count);
         }

         if (this.positions.get(i + 3) == line && this.positions.get(i + 4) > column) {
            this.positions.set(i + 4, this.positions.get(i + 4) + count);
         }
      }
   }

   public void insertLineBreak(int line, int column) {
      for(int i = 0; i < this.positions.size(); i += 5) {
         if (this.positions.get(i + 1) == line && this.positions.get(i + 2) >= column) {
            this.positions.set(i + 1, this.positions.get(i + 1) + 1);
            this.positions.set(i + 2, this.positions.get(i + 2) - column + 1);
         } else if (this.positions.get(i + 1) > line) {
            this.positions.set(i + 1, this.positions.get(i + 1) + 1);
         }

         if (this.positions.get(i + 3) == line && this.positions.get(i + 4) > column) {
            this.positions.set(i + 3, this.positions.get(i + 3) + 1);
            this.positions.set(i + 4, this.positions.get(i + 4) - column + 1);
         } else if (this.positions.get(i + 3) > line) {
            this.positions.set(i + 3, this.positions.get(i + 3) + 1);
         }
      }
   }
}
