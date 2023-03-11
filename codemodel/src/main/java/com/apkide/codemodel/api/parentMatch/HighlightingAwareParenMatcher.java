package com.apkide.codemodel.api.parentMatch;

import com.apkide.codemodel.api.EditorParenMatcher;

public class HighlightingAwareParenMatcher implements EditorParenMatcher {
   private final char[] openingParens;
   private final char[] closingParens;
   private final int[] inferiorStyles;
   private final ByteStack parenMatchStack;

   public HighlightingAwareParenMatcher(String openingParens, String closingParens, int[] inferiorStyles) {
      this.openingParens = openingParens.toCharArray();
      this.closingParens = closingParens.toCharArray();
      this.inferiorStyles = inferiorStyles;
      this.parenMatchStack = new ByteStack();
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
         int currentParenIndex = -1;
         boolean matchForward = false;
         int i = 0;

         while(i < this.openingParens.length && this.openingParens[i] != currentParenChar) {
            ++i;
         }

         if (i != this.openingParens.length) {
            currentParenIndex = i;
            matchForward = true;
         } else {
            i = 0;

            while(i < this.closingParens.length && this.closingParens[i] != currentParenChar) {
               ++i;
            }

            if (i != this.closingParens.length) {
               currentParenIndex = i;
            }
         }

         result.matchingValid = false;
         if (currentParenIndex != -1) {
            i = model.getStyle(caretLine + 1, col + 1);
            boolean inferiorMatching = false;
            int ix = 0;

            while(ix < this.inferiorStyles.length && i != this.inferiorStyles[ix]) {
               ++ix;
            }

            if (ix != this.inferiorStyles.length) {
               inferiorMatching = true;
            }

            result.caretParenMatchColumn = col + 1;
            result.caretParenMatchLine = caretLine + 1;
            if (matchForward) {
               ix = caretLine;
               this.parenMatchStack.clear();
               this.parenMatchStack.push((byte)currentParenIndex);
               int lineWidth = model.getLineWidth(caretLine + 1);

               label152:
               while(true) {
                  do {
                     do {
                        if (this.parenMatchStack.elementCount() == 0) {
                           break label152;
                        }

                        if (col < lineWidth - 1) {
                           ++col;
                           break;
                        }

                        if (ix >= model.getLineCount()) {
                           break label152;
                        }

                        ++ix;
                        col = 0;
                        lineWidth = model.getLineWidth(ix + 1);
                     } while(lineWidth == 0);

                     int chStyle = model.getStyle(ix + 1, col + 1);
                     if (inferiorMatching) {
                        if (chStyle != i) {
                           result.parenMatchLine = -1;
                           return;
                        }
                        break;
                     }

                     i = 0;

                     while(i < this.inferiorStyles.length && chStyle != this.inferiorStyles[i]) {
                        ++i;
                     }
                  } while(i != this.inferiorStyles.length);

                  char ch = model.getChar(ix + 1, col + 1);

                  for(int ixx = 0; ixx < this.openingParens.length; ++ixx) {
                     if (this.openingParens[ixx] == ch) {
                        this.parenMatchStack.push((byte)ixx);
                     }
                  }

                  for(int ixx = 0; ixx < this.closingParens.length; ++ixx) {
                     if (this.closingParens[ixx] == ch) {
                        if (ixx != this.parenMatchStack.top()) {
                           break label152;
                        }

                        this.parenMatchStack.pop();
                     }
                  }
               }

               if (ix == model.getLineCount()) {
                  result.parenMatchLine = -1;
               } else {
                  result.parenMatchLine = ix + 1;
               }
   
            } else {
               ix = caretLine;
               this.parenMatchStack.clear();
               this.parenMatchStack.push((byte)currentParenIndex);

               label195:
               while(true) {
                  do {
                     do {
                        if (this.parenMatchStack.elementCount() == 0) {
                           break label195;
                        }

                        if (col > 0) {
                           --col;
                           break;
                        }

                        if (ix <= 0) {
                           break label195;
                        }

                        --ix;
                        col = model.getLineWidth(ix + 1) - 1;
                     } while(col == -1);

                     int chStyle = model.getStyle(ix + 1, col + 1);
                     if (inferiorMatching) {
                        if (chStyle != i) {
                           result.parenMatchLine = -1;
                           return;
                        }
                        break;
                     }

                     i = 0;

                     while(i < this.inferiorStyles.length && chStyle != this.inferiorStyles[i]) {
                        ++i;
                     }
                  } while(i != this.inferiorStyles.length);

                  char ch = model.getChar(ix + 1, col + 1);

                  for(int ixx = 0; ixx < this.openingParens.length; ++ixx) {
                     if (this.openingParens[ixx] == ch) {
                        if (ixx != this.parenMatchStack.top()) {
                           break label195;
                        }

                        this.parenMatchStack.pop();
                     }
                  }

                  for(int ixx = 0; ixx < this.closingParens.length; ++ixx) {
                     if (this.closingParens[ixx] == ch) {
                        this.parenMatchStack.push((byte)ixx);
                     }
                  }
               }

               result.parenMatchLine = ix + 1;
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
      for (char openingParen : this.openingParens) {
         if (openingParen == ch) {
            return true;
         }
      }
   
      for (char closingParen : this.closingParens) {
         if (closingParen == ch) {
            return true;
         }
      }
      return false;
   }
}
