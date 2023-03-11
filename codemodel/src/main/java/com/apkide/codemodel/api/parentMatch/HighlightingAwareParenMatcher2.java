package com.apkide.codemodel.api.parentMatch;

import com.apkide.codemodel.api.EditorParenMatcher;

import java.util.Vector;


public class HighlightingAwareParenMatcher2 implements EditorParenMatcher {
   private final ByteStack parenMatchStack = new ByteStack();
   private final StyleGroup[] styleToGroup;

   public HighlightingAwareParenMatcher2(StyleGroup[] groups) {
      Vector<StyleGroup> groupsVec = new Vector<>();

      for(StyleGroup group : groups) {
         for(int i = 0; i < group.styles.length; ++i) {
            if (group.styles[i] >= groupsVec.size()) {
               groupsVec.setSize(group.styles[i] + 1);
            }

            groupsVec.setElementAt(group, group.styles[i]);
         }
      }

      this.styleToGroup = new StyleGroup[groupsVec.size()];
      groupsVec.copyInto(this.styleToGroup);
   }

   @Override
   public void findMatchingParen(ParenMatcherEditor model, int caretLine, int caretColumn, ParenMatchResult result) {
      --caretLine;
      --caretColumn;
      int col = caretColumn - 1;
      if (col == -1) {
         result.parenMatchLine = -1;
         result.caretParenMatchLine = -1;
      } else {
         char currentParenChar = model.getChar(caretLine + 1, col + 1);
         int currentStyle = model.getStyle(caretLine + 1, col + 1);
         int currentParenIndex = -1;
         boolean matchForward = false;
         StyleGroup currentStyleGroup = null;
         if (currentStyle < this.styleToGroup.length) {
            currentStyleGroup = this.styleToGroup[currentStyle];
         }

         if (currentStyleGroup != null) {
            int i = 0;

            while(i < currentStyleGroup.openingParens.length && currentStyleGroup.openingParens[i] != currentParenChar) {
               ++i;
            }

            if (i != currentStyleGroup.openingParens.length) {
               currentParenIndex = i;
               matchForward = true;
            } else {
               i = 0;

               while(i < currentStyleGroup.closingParens.length && currentStyleGroup.closingParens[i] != currentParenChar) {
                  ++i;
               }

               if (i != currentStyleGroup.closingParens.length) {
                  currentParenIndex = i;
               }
            }
         }

         result.matchingValid = false;
         if (currentParenIndex != -1) {
            result.caretParenMatchColumn = col + 1;
            result.caretParenMatchLine = caretLine + 1;
            int line = caretLine;
            if (matchForward) {
               this.parenMatchStack.clear();
               this.parenMatchStack.push((byte)currentParenIndex);
               int lineWidth = model.getLineWidth(caretLine + 1);

               label143:
               while(true) {
                  do {
                     if (this.parenMatchStack.elementCount() == 0) {
                        break label143;
                     }

                     if (col < lineWidth - 1) {
                        ++col;
                        break;
                     }

                     if (line >= model.getLineCount()) {
                        break label143;
                     }

                     ++line;
                     col = 0;
                     lineWidth = model.getLineWidth(line + 1);
                  } while(lineWidth == 0);

                  int chStyle = model.getStyle(line + 1, col + 1);
                  if (currentStyleGroup.onlyOneConnectedRegion) {
                     if (chStyle != currentStyle) {
                        result.parenMatchLine = -1;
                        return;
                     }
                  } else {
                     int i = 0;

                     while(true) {
                        if (i >= currentStyleGroup.styles.length) {
                           continue label143;
                        }

                        if (chStyle == currentStyleGroup.styles[i]) {
                           break;
                        }

                        ++i;
                     }
                  }

                  char ch = model.getChar(line + 1, col + 1);

                  for(int i = 0; i < currentStyleGroup.openingParens.length; ++i) {
                     if (currentStyleGroup.openingParens[i] == ch) {
                        this.parenMatchStack.push((byte)i);
                     }
                  }

                  for(int i = 0; i < currentStyleGroup.closingParens.length; ++i) {
                     if (currentStyleGroup.closingParens[i] == ch) {
                        if (i != this.parenMatchStack.top()) {
                           break label143;
                        }

                        this.parenMatchStack.pop();
                     }
                  }
               }

               if (line == model.getLineCount()) {
                  result.parenMatchLine = -1;
               } else {
                  result.parenMatchLine = line + 1;
               }
      
            } else {
               this.parenMatchStack.clear();
               this.parenMatchStack.push((byte)currentParenIndex);

               label183:
               while(true) {
                  do {
                     if (this.parenMatchStack.elementCount() == 0) {
                        break label183;
                     }

                     if (col > 0) {
                        --col;
                        break;
                     }

                     if (line <= 0) {
                        break label183;
                     }

                     --line;
                     col = model.getLineWidth(line + 1) - 1;
                  } while(col == -1);

                  int chStyle = model.getStyle(line + 1, col + 1);
                  if (currentStyleGroup.onlyOneConnectedRegion) {
                     if (chStyle != currentStyle) {
                        result.parenMatchLine = -1;
                        return;
                     }
                  } else {
                     int i = 0;

                     while(true) {
                        if (i >= currentStyleGroup.styles.length) {
                           continue label183;
                        }

                        if (chStyle == currentStyleGroup.styles[i]) {
                           break;
                        }

                        ++i;
                     }
                  }

                  char ch = model.getChar(line + 1, col + 1);

                  for(int i = 0; i < currentStyleGroup.openingParens.length; ++i) {
                     if (currentStyleGroup.openingParens[i] == ch) {
                        if (i != this.parenMatchStack.top()) {
                           break label183;
                        }

                        this.parenMatchStack.pop();
                     }
                  }

                  for(int i = 0; i < currentStyleGroup.closingParens.length; ++i) {
                     if (currentStyleGroup.closingParens[i] == ch) {
                        this.parenMatchStack.push((byte)i);
                     }
                  }
               }

               result.parenMatchLine = line + 1;
            }
            result.parenMatchColumn = col + 1;
            if (this.parenMatchStack.elementCount() == 0) {
               result.matchingValid = true;
            }
         } else {
            result.parenMatchLine = -1;
            result.caretParenMatchLine = -1;
         }
      }
   }

   @Override
   public boolean isParenChar(char ch) {
      for (StyleGroup styleGroup : this.styleToGroup) {
         if (styleGroup != null) {
            for (int j = 0; j < styleGroup.openingParens.length; ++j) {
               if (ch == styleGroup.openingParens[j]) {
                  return true;
               }
            }
         
            for (int j = 0; j < styleGroup.closingParens.length; ++j) {
               if (ch == styleGroup.closingParens[j]) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static class StyleGroup {
      private final int[] styles;
      private final char[] openingParens;
      private final char[] closingParens;
      private final boolean onlyOneConnectedRegion;

      public StyleGroup(int[] styles, String openingParens, String closingParens, boolean onlyOneConnectedRegion) {
         this.styles = styles;
         this.openingParens = openingParens.toCharArray();
         this.closingParens = closingParens.toCharArray();
         this.onlyOneConnectedRegion = onlyOneConnectedRegion;
      }
   }
}
