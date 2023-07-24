package com.apkide.openapi.language.api;

import com.apkide.common.collections.HashtableOfInt;
import com.apkide.common.collections.SetOfInt;
import com.apkide.openapi.language.Language;
import com.apkide.openapi.language.Tools;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Vector;


public class ErrorTable {
    private final Model model;
    private final FileSpace filespace;
    private HashtableOfInt<FileContent> filecontents;
    private SetOfInt semanticerrors;
    private SetOfInt compileerrors;
    private Error lastError;

    protected ErrorTable(Model model) {
        this.model = model;
        this.filespace = model.getFileSpace();
        this.semanticerrors = new SetOfInt();
        this.compileerrors = new SetOfInt();
        this.filecontents = new HashtableOfInt<>();
    }
    public void addInspection(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        Error error = new Error(startLine, startColumn, endLine, endColumn, "Warning: " + msg, kind);
        if (!this.filecontents.contains(file.getID())) {
            this.filecontents.put(file.getID(), new FileContent());
        }

        this.filecontents.get(file.getID()).inspections.addElement(error);
        this.lastError = error;
    }

    public void addWarning(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        if (startLine > 0) {
            Error error = new Error(startLine, startColumn, endLine, endColumn, msg, kind);
            if (!this.filecontents.contains(file.getID())) {
                this.filecontents.put(file.getID(), new FileContent());
            }

            this.filecontents.get(file.getID()).analysiserrors.addElement(error);
            this.lastError = error;
        } else {
            this.lastError = null;
        }
    }

    public void addSemanticWarning(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        if (startLine > 0) {
            Error error = new Error(startLine, startColumn, endLine, endColumn, msg, kind);
            if (!this.filecontents.contains(file.getID())) {
                this.filecontents.put(file.getID(), new FileContent());
            }

            this.filecontents.get(file.getID()).analysiserrors.addElement(error);
            this.semanticerrors.put(file.getID());
            this.lastError = error;
        } else {
            this.lastError = null;
        }
    }

    public void addSemanticError(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        if (startLine > 0) {
            Error error = new Error(startLine, startColumn, endLine, endColumn, msg, kind);
            if (!this.filecontents.contains(file.getID())) {
                this.filecontents.put(file.getID(), new FileContent());
            }

            this.filecontents.get(file.getID()).analysiserrors.addElement(error);
            this.semanticerrors.put(file.getID());
            this.compileerrors.put(file.getID());
            this.lastError = error;
        } else {
            this.lastError = null;
        }
    }

    public void addError(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        if (startLine > 0) {
            Error error = new Error(startLine, startColumn, endLine, endColumn, msg, kind);
            if (!this.filecontents.contains(file.getID())) {
                this.filecontents.put(file.getID(), new FileContent());
            }

            this.filecontents.get(file.getID()).analysiserrors.addElement(error);
            this.compileerrors.put(file.getID());
            this.lastError = error;
        } else {
            this.lastError = null;
        }
    }

    public void addParseError(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
        if (startLine > 0) {
            Error error = new Error(startLine, startColumn, endLine, endColumn, msg, kind);
            if (!this.filecontents.contains(file.getID())) {
                this.filecontents.put(file.getID(), new FileContent());
            }

            this.filecontents.get(file.getID()).parseerrors.addElement(error);
            this.compileerrors.put(file.getID());
            this.lastError = error;
        } else {
            this.lastError = null;
        }
    }

    public void addFixClass(FileEntry file, FileEntry dir, int identifier, String msg) {
    }

    public void addFixGenerify(FileEntry file, String p1) {
    }

    public void addFixSafeDelete(ParseTree entity, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(6, entity, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixMethod(
            FileEntry file, int line, int column, int modifiers, int identifier, ParseTree type, int[] argmodifiers, ParseTree[] argtypes, int[] argnames, String msg
    ) {
        if (this.lastError != null) {
            Fix fix = new Fix(5, file, line, column, modifiers, identifier, type, argmodifiers, argtypes, argnames, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixField(FileEntry file, int line, int column, int modifiers, int identifier, ParseTree type, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(4, file, line, column, modifiers, identifier, type, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixVariable(FileEntry file, int line, int column, int identifier, ParseTree type, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(3, line, column, identifier, type, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixParameter(FileEntry file, int line, int column, int identifier, ParseTree type, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(10, line, column, identifier, type, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixSurround(FileEntry file, int line, int column, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(8, line, column, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixAddThrows(FileEntry file, int line, int column, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(9, line, column, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixImplement(FileEntry file, int line, int column, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(1, line, column, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixImport(FileEntry file, ParseTree classtype, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(2, classtype, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixReplace(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String replacement, String msg) {
        if (this.lastError != null) {
            Fix fix = new Fix(0, startLine, startColumn, endLine, endColumn, replacement, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public void addFixRemoveTwice(
            FileEntry file,
            int startLine1,
            int startColumn1,
            int endLine1,
            int endColumn1,
            int startLine2,
            int startColumn2,
            int endLine2,
            int endColumn2,
            String msg
    ) {
        if (this.lastError != null) {
            Fix fix = new Fix(7, startLine1, startColumn1, endLine1, endColumn1, startLine2, startColumn2, endLine2, endColumn2, msg);
            if (this.lastError.fixes == null) {
                this.lastError.fixes = new Vector<>(3);
            }

            this.lastError.fixes.addElement(fix);
        }
    }

    public SetOfFileEntry getFilesWithErrors() {
        SetOfFileEntry files = new SetOfFileEntry(this.filespace);
        this.filecontents.DEFAULT_ITERATOR.init();

        while (this.filecontents.DEFAULT_ITERATOR.hasMoreElements()) {
            files.put(this.filespace.getFileEntry(this.filecontents.DEFAULT_ITERATOR.nextKey()));
        }

        return files;
    }

    public void clearInspections(FileEntry file) {
        if (this.filecontents.contains(file.getID())) {
            this.filecontents.get(file.getID()).inspections = new Vector<>();
        }
    }

    public void clearNonParserErrors(FileEntry file) {
        if (this.filecontents.contains(file.getID())) {
            this.filecontents.get(file.getID()).analysiserrors = new Vector<>();
        }

        this.semanticerrors.remove(file.getID());
        this.compileerrors.remove(file.getID());
    }

    public void clearParseErrors(FileEntry file) {
        if (this.filecontents.contains(file.getID())) {
            this.filecontents.get(file.getID()).parseerrors = new Vector<>();
        }
    }

    public boolean containsInspections(FileEntry file) {
        return this.filecontents.contains(file.getID()) && this.filecontents.get(file.getID()).inspections.size() > 0;
    }

    public boolean containsCompileErrors(FileEntry file) {
        return this.compileerrors.contains(file.getID());
    }

    public boolean containsErrorsOrWarnings(FileEntry file) {
        return this.filecontents.contains(file.getID());
    }

    public boolean containsSemanticErrors(FileEntry file) {
        return this.semanticerrors.contains(file.getID());
    }

    public int getInspectionCount(FileEntry file) {
        return !this.filecontents.contains(file.getID()) ? 0 : this.filecontents.get(file.getID()).inspections.size();
    }

    public int getInspectionStartLine(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).startLine;
    }

    public int getInspectionStartColumn(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).startColumn;
    }

    public int getInspectionEndLine(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).endLine;
    }

    public int getInspectionEndColumn(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).endColumn;
    }

    public String getInspectionText(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).msg;
    }

    public int getInspectionKind(FileEntry file, int n) {
        return this.filecontents.get(file.getID()).inspections.elementAt(n).kind;
    }

    public int getInspectionFixesCount(FileEntry file, int n) {
        Vector<Fix> fixes = this.filecontents.get(file.getID()).inspections.elementAt(n).fixes;
        return fixes == null ? 0 : fixes.size();
    }

    public String getInspectionFixText(FileEntry file, int n, int i) {
        Fix fix = this.filecontents.get(file.getID()).inspections.elementAt(n).fixes.elementAt(i);
        return fix.msg;
    }

    public int getErrorCount(FileEntry file) {
        if (!this.filecontents.contains(file.getID())) {
            return 0;
        } else {
            Vector<Error> analysiserrors = this.filecontents.get(file.getID()).analysiserrors;
            Vector<Error> parseerrors = this.filecontents.get(file.getID()).parseerrors;
            return analysiserrors.size() + parseerrors.size();
        }
    }

    public int getErrorStartLine(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).startLine
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).startLine;
    }

    public int getErrorStartColumn(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).startColumn
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).startColumn;
    }

    public int getErrorEndLine(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).endLine
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).endLine;
    }

    public int getErrorEndColumn(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).endColumn
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).endColumn;
    }

    public String getErrorText(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).msg
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).msg;
    }

    public int getErrorKind(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        return n >= pn
                ? this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn).kind
                : this.filecontents.get(file.getID()).parseerrors.elementAt(n).kind;
    }

    public int getErrorFixesCount(FileEntry file, int n) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        Error error;
        if (n >= pn) {
            error = this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn);
        } else {
            error = this.filecontents.get(file.getID()).parseerrors.elementAt(n);
        }

        return error.fixes == null ? 0 : error.fixes.size();
    }

    public String getErrorFixText(FileEntry file, int n, int i) {
        int pn = this.filecontents.get(file.getID()).parseerrors.size();
        Error error;
        if (n >= pn) {
            error = this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn);
        } else {
            error = this.filecontents.get(file.getID()).parseerrors.elementAt(n);
        }

        Fix fix = error.fixes.elementAt(i);
        return fix.msg;
    }

    public void applyFix(FileEntry file, Language language, boolean inspection, int n, int i) {
        Tools tools = language.getTools();
        if (tools != null) {
            Fix fix;
            if (inspection) {
                Vector<Fix> fixes = this.filecontents.get(file.getID()).inspections.elementAt(n).fixes;
                fix = fixes.elementAt(i);
            } else {
                int pn = this.filecontents.get(file.getID()).parseerrors.size();
                Error error;
                if (n >= pn) {
                    error = this.filecontents.get(file.getID()).analysiserrors.elementAt(n - pn);
                } else {
                    error = this.filecontents.get(file.getID()).parseerrors.elementAt(n);
                }

                fix = error.fixes.elementAt(i);
            }

            switch (fix.kind) {
                case 0:
                    this.fixReplaceText(file, fix.startLine, fix.startColumn, fix.endLine, fix.endColumn, fix.replacement);
                    break;
                case 1:
                    tools.implementMembers(file, fix.startLine, fix.startColumn);
                    break;
                case 2:
                    this.addImport(file,  fix.entity);
                    break;
                case 3:
                    tools.createVariable(file, fix.startLine, fix.startColumn, fix.identifier,  fix.entity);
                    break;
                case 4:
                    tools.createField(fix.file, fix.startLine, fix.startColumn, fix.modifiers, fix.identifier, fix.entity);
                    break;
                case 5:
                    tools.createMethod(
                            fix.file, fix.startLine, fix.startColumn, fix.modifiers, fix.identifier,  fix.entity, fix.argmodifiers, fix.argtypes, fix.argnames
                    );
                    break;
                case 6:
                    tools.safeDelete(file,fix.entity);
                    break;
                case 7:
                    this.fixRemoveTextTwice(
                            file, fix.startLine, fix.startColumn, fix.endLine, fix.endColumn, fix.startLine2, fix.startColumn2, fix.endLine2, fix.endColumn2
                    );
                    break;
                case 8:
                    tools.surroundWithTryCatch(file, fix.startLine, fix.startColumn, fix.startLine, fix.startColumn);
                    break;
                case 9:
                    tools.addThrows(file, fix.startLine, fix.startColumn);
                    break;
                case 10:
                    this.addParameter(file, fix.startLine, fix.startColumn, fix.identifier, fix.entity);
            }
        }
    }

    protected void addImport(FileEntry file, ParseTree entity) {
    }

    protected void addParameter(FileEntry file, int startLine, int startColumn, int identifier, ParseTree entity) {
    }

    private void fixRemoveTextTwice(
            FileEntry file,
            int startLine,
            int startColumn,
            int endLine,
            int endColumn,
            int startLine2,
            int startColumn2,
            int endLine2,
            int endColumn2
    ) {
        this.model.getRefactoringCallback().analysisStarted(file);
        this.model.getRefactoringCallback().replaceText(file, startLine, startColumn, endLine, endColumn, "");
        this.model.getRefactoringCallback().replaceText(file, startLine2, startColumn2, endLine2, endColumn2, "");
        this.model.getRefactoringCallback().fixAnalysisFinished(file);
    }

    private void fixReplaceText(FileEntry file, int startLine, int startColumn, int endLine, int endColumn, String replacement) {
        this.model.getRefactoringCallback().analysisStarted(file);
        int lineCount = this.getLineCount(replacement);
        if (lineCount > 0) {
            this.model.getRefactoringCallback().replaceText(file, startLine, startColumn, endLine, endColumn, replacement);
            this.model.getRefactoringCallback().indentLines(file, startLine, startLine + lineCount);
        } else {
            this.model.getRefactoringCallback().replaceText(file, startLine, startColumn, endLine, endColumn, replacement);
        }

        this.model.getRefactoringCallback().fixAnalysisFinished(file);
    }

    private int getLineCount(String doc) {
        int count = 0;

        for (int i = 0; i < doc.length(); ++i) {
            if (doc.charAt(i) == '\r') {
                if (i + 1 < doc.length() && doc.charAt(i + 1) == '\n')
                    i++;
                count++;
            } else if (doc.charAt(i) == '\n') {
                ++count;
            }
        }

        return count;
    }

    private class Error {
        public final int kind;
        public final int startLine;
        public final int startColumn;
        public final int endLine;
        public final int endColumn;
        public final String msg;
        public Vector<Fix> fixes;

        public Error(int startLine,
                     int startColumn,
                     int endLine,
                     int endColumn,
                     String msg,
                     int kind) {
            this.kind = kind;
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.endLine = endLine;
            this.endColumn = endColumn;
            this.msg = msg;
        }

    }

    private class FileContent {
        public Vector<Error> inspections;
        public Vector<Error> parseerrors;
        public Vector<Error> analysiserrors;

        public FileContent() {
            this.inspections = new Vector<>();
            this.parseerrors = new Vector<>();
            this.analysiserrors = new Vector<>();
        }
    }

    private class Fix {
        public static final int REPLACE = 0;
        public static final int IMPLEMENT = 1;
        public static final int IMPORT = 2;
        public static final int CREATEVARIABLE = 3;
        public static final int CREATEPARAMETER = 10;
        public static final int CREATEFIELD = 4;
        public static final int CREATEMETHOD = 5;
        public static final int SAFEDELETE = 6;
        public static final int REMOVETWICE = 7;
        public static final int SURROUND = 8;
        public static final int ADDTHROWS = 9;
        public int kind;
        public String msg;
        public ParseTree entity;
        public FileEntry file;
        public int identifier;
        public int modifiers;
        public int[] argmodifiers;
        public ParseTree[] argtypes;
        public int[] argnames;
        public int startLine;
        public int startColumn;
        public int endLine;
        public int endColumn;
        public int startLine2;
        public int startColumn2;
        public int endLine2;
        public int endColumn2;
        public String replacement = "";

        public Fix(
                int kind,
                FileEntry file,
                int line,
                int column,
                int modifiers,
                int identifier,
                ParseTree type,
                int[] argmodifiers,
                ParseTree[] argtypes,
                int[] argnames,
                String msg
        ) {
            this.kind = kind;
            this.msg = msg;
            this.file = file;
            this.startLine = line;
            this.startColumn = column;
            this.entity = type;
            this.identifier = identifier;
            this.modifiers = modifiers;
            this.argtypes = argtypes;
            this.argnames = argnames;
            this.argmodifiers = argmodifiers;
        }

        public Fix(int kind, String msg) {
            this.kind = kind;
            this.msg = msg;
        }

        public Fix(int kind, ParseTree entity, String msg) {
            this.kind = kind;
            this.msg = msg;
            this.entity = entity;
        }

        public Fix(int kind, int line, int column, String msg) {
            this.kind = kind;
            this.msg = msg;
            this.startLine = line;
            this.startColumn = column;
        }

        public Fix(int kind,
                   int line,
                   int column,
                   int identifier,
                   ParseTree type,
                   String msg) {
            this.kind = kind;
            this.msg = msg;
            this.startLine = line;
            this.startColumn = column;
            this.entity = type;
            this.identifier = identifier;
        }

        public Fix(int kind,
                   FileEntry file,
                   int line,
                   int column,
                   int modifiers,
                   int identifier,
                   ParseTree type,
                   String msg) {
            this.kind = kind;
            this.msg = msg;
            this.file = file;
            this.startLine = line;
            this.startColumn = column;
            this.entity = type;
            this.identifier = identifier;
            this.modifiers = modifiers;
        }

        public Fix(int kind,
                   int startLine,
                   int startColumn,
                   int endLine,
                   int endColumn,
                   String replacement, String msg) {
            this.kind = kind;
            this.msg = msg;
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.endLine = endLine;
            this.endColumn = endColumn;
            this.replacement = replacement;
        }

        public Fix(
                int kind,
                int startLine1,
                int startColumn1,
                int endLine1,
                int endColumn1,
                int startLine2,
                int startColumn2,
                int endLine2,
                int endColumn2,
                String msg
        ) {
            this.kind = kind;
            this.msg = msg;
            this.startLine = startLine1;
            this.startColumn = startColumn1;
            this.endLine = endLine1;
            this.endColumn = endColumn1;
            this.startLine2 = startLine2;
            this.startColumn2 = startColumn2;
            this.endLine2 = endLine2;
            this.endColumn2 = endColumn2;
        }
    }
}
