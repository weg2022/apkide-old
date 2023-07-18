package com.apkide.analysis.api.clm;


import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.cle.Language;
import com.apkide.analysis.api.clm.callback.FileReaderCallback;
import com.apkide.analysis.api.clm.callback.OpenFileCallback;
import com.apkide.analysis.api.clm.callback.StopCallback;
import com.apkide.analysis.api.clm.collections.RelationOfIntInt;
import com.apkide.analysis.api.clm.collections.SetOfFileEntry;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.collections.FunctionOfIntInt;
import com.apkide.common.collections.MapOfIntLong;
import com.apkide.common.collections.OrderedMapOfIntInt;
import com.apkide.common.collections.SetOfInt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileSpace {
    private final IdentifierSpace identifiers;
    private final OpenFileCallback openfilecallback;
    private final StopCallback stopcallback;
    private CompletionReader completionReader = new CompletionReader();
    private MapOfIntLong includedEntryVersions;
    private OrderedMapOfIntInt includes;
    private FileEntry currentIncludeFile;
    private SetOfInt changedIncludeFiles = new SetOfInt();
    private SetOfInt changedFiles = new SetOfInt();
    private String encoding;
    private FileEntry[] entries;
    private int entryCount;
    private FunctionOfIntInt fileCodeModels;
    private FunctionOfIntInt fileassemblys;
    private RelationOfIntInt references;
    private OrderedMapOfIntInt assemblyreferences;
    private SetOfFileEntry registeredcheckedfiles;
    private SetOfFileEntry registeredsolutionfiles;
    private SetOfFileEntry registeredresourcefiles;
    private SetOfFileEntry checkedfiles;
    private SetOfFileEntry solutionfiles;
    private HashMap<Integer, String> resourcepackagenames;
    private HashMap<Integer, String> assemblyProjectFilePaths;
    private HashMap<Integer, String> assemblyRootNamespaces;
    private HashMap<Integer, List<String>> assemblyTagLibs;
    private HashMap<Integer, List<String>> assemblyDefaultImports;
    private HashMap<Integer, List<String>> assemblyDefinedSymbols;
    private HashMap<Integer, String> assemblyDestinations;
    private HashMap<Integer, String> assemblyTargetVersions;
    private HashMap<Integer, String> assemblyConfigurations;
    private HashMap<CodeModel,List<String>> myCodeModelFilePatterns;
    private CodeModel[] myCodeModels;
    private Language[] languages;
    private boolean packed;
    private FileReaderCallback myFileReaderCallback;

    public FileSpace(IdentifierSpace identifiers, OpenFileCallback openfilecallback, StopCallback stopcallback,FileReaderCallback fileReaderCallback) {
        this.identifiers = identifiers;
        this.openfilecallback = openfilecallback;
        this.stopcallback = stopcallback;
        this.myFileReaderCallback=fileReaderCallback;
        this.entries = new FileEntry[1000];
        this.entries[0] = new FileEntry(identifiers, this, openfilecallback, identifiers.get(""), null);
        this.entryCount = 1;
        this.includedEntryVersions = new MapOfIntLong();
        this.includes = new OrderedMapOfIntInt();
        this.fileCodeModels = new FunctionOfIntInt();
        this.fileassemblys = new FunctionOfIntInt();
        this.references = new RelationOfIntInt();
        this.assemblyreferences = new OrderedMapOfIntInt();
        this.registeredcheckedfiles = new SetOfFileEntry(this);
        this.registeredsolutionfiles = new SetOfFileEntry(this);
        this.registeredresourcefiles = new SetOfFileEntry(this);
        this.checkedfiles = new SetOfFileEntry(this);
        this.solutionfiles = new SetOfFileEntry(this);
        this.assemblyDestinations = new HashMap<>();
        this.assemblyProjectFilePaths = new HashMap<>();
        this.assemblyTagLibs = new HashMap<>();
        this.assemblyDefaultImports = new HashMap<>();
        this.assemblyDefinedSymbols = new HashMap<>();
        this.assemblyRootNamespaces = new HashMap<>();
        this.assemblyTargetVersions = new HashMap<>();
        this.assemblyConfigurations = new HashMap<>();
        this.resourcepackagenames = new HashMap<>();
        this.myCodeModelFilePatterns=new HashMap<>();
    }

    public void configureCodeModelFilePatterns(CodeModel codeModel,List<String> filePatterns){
        myCodeModelFilePatterns.put(codeModel,filePatterns);
    }

    public void configureCodeModels(CodeModel[] codeModels) {
        myCodeModels = codeModels;
        List<Language> languageList = new ArrayList<>();
        for (CodeModel codeModel : myCodeModels) {
            if (codeModel != null) {
                for (Language language : codeModel.getLanguages()) {
                    if (!languageList.contains(language))
                        languageList.add(language);
                }
            }
        }
        languages = languageList.toArray(new Language[0]);
    }

    public void close() {
        for (CodeModel codeModel : myCodeModels) {
            if (codeModel != null && codeModel.isSupportsFileArchives())
                codeModel.close();
        }
    }

    public void storeEntries(StoreOutputStream stream) throws IOException {
        stream.writeInt(this.entryCount);
    }

    public void loadEntries(StoreInputStream stream) throws IOException {
        this.entryCount = stream.readInt();
        this.entries = new FileEntry[this.entryCount * 2];

        for (int i = 0; i < this.entryCount; ++i) {
            this.entries[i] = new FileEntry(this.identifiers, this, this.openfilecallback);
        }
    }

    protected void load(StoreInputStream stream) throws IOException {
        for (int i = 0; i < this.entryCount; ++i) {
            this.entries[i].load(stream);
            if (stream.readChar() != 'E') {
                throw new IOException();
            }
        }

        this.includedEntryVersions = new MapOfIntLong(stream);
        this.includes = new OrderedMapOfIntInt(stream);
        this.fileCodeModels = new FunctionOfIntInt(stream);
    }

    protected void store(StoreOutputStream stream) throws IOException {
        for (int i = 0; i < this.entryCount; ++i) {
            this.entries[i].store(stream);
            stream.writeChar(69);
        }

        this.includedEntryVersions.store(stream);
        this.includes.store(stream);
        this.fileCodeModels.store(stream);
    }

    protected void update(boolean updateFileSpace) {
        if (updateFileSpace) {
            this.synchronize();
        }

        this.changedIncludeFiles.clear();
        this.includedEntryVersions.DEFAULT_ITERATOR.init();

        while (this.includedEntryVersions.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry entry = this.getFileEntry(this.includedEntryVersions.DEFAULT_ITERATOR.nextKey());
            long version = this.includedEntryVersions.DEFAULT_ITERATOR.nextValue();
            if (version != entry.getVersion()) {
                this.changedIncludeFiles.put(entry.getID());
            }
        }

        this.changedIncludeFiles.DEFAULT_ITERATOR.init();

        while (this.changedIncludeFiles.DEFAULT_ITERATOR.hasMoreElements()) {
            this.includedEntryVersions.remove(this.changedIncludeFiles.DEFAULT_ITERATOR.nextKey());
        }

        this.changedFiles.clear();
        this.includes.DEFAULT_ITERATOR.init();

        while (this.includes.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry entry = this.getFileEntry(this.includes.DEFAULT_ITERATOR.nextValue());
            FileEntry file = this.getFileEntry(this.includes.DEFAULT_ITERATOR.nextKey());
            if (this.changedIncludeFiles.contains(entry.getID())) {
                this.changedFiles.put(file.getID());
            }
        }

        this.changedFiles.DEFAULT_ITERATOR.init();

        while (this.changedFiles.DEFAULT_ITERATOR.hasMoreElements()) {
            this.includes.remove(this.changedFiles.DEFAULT_ITERATOR.nextKey());
        }
    }

    private void synchronize() {
        for (FileEntry entry : this.entries) {
            if (entry != null) {
                entry.synchronize();
            }
        }

        this.packed = false;
    }

    public CodeModel[] getCodeModels() {
        return myCodeModels;
    }

    protected int getCodeModelID(CodeModel codeModel) {
        for (int i = 0; i < this.myCodeModels.length; ++i) {
            if (codeModel == this.myCodeModels[i]) {
                return i;
            }
        }

        return -1;
    }

    protected CodeModel getCodeModel(int id) {
        return id < 0 ? null : this.myCodeModels[id];
    }


    public Language[] getLanguages() {
        return this.languages;
    }

    protected int getLanguageID(Language language) {
        for (int i = 0; i < this.languages.length; ++i) {
            if (language == this.languages[i]) {
                return i;
            }
        }

        return -1;
    }

    protected Language getLanguage(int id) {
        return id < 0 ? null : this.languages[id];
    }

    protected int getAssembly(FileEntry file) {
        this.pack();
        return this.fileassemblys.get(file.getID());
    }

    protected String getRootNamespace(FileEntry file) {
        int assembly = file.getAssembly();
        return this.assemblyRootNamespaces.containsKey(assembly) ? this.assemblyRootNamespaces.get(assembly) : "";
    }

    protected List<String> getDefaultImports(FileEntry file) {
        return this.assemblyDefaultImports.get(file.getAssembly());
    }

    protected List<String> getDefinedSymbols(FileEntry file) {
        return this.assemblyDefinedSymbols.get(file.getAssembly());
    }

    protected void configureEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected void configureAssembly(
            int assembly, String projectFilePath, String rootNamespace, List<String> defaultImports, List<String> definedSymbols, List<String> tagLibPaths
    ) {
        this.assemblyProjectFilePaths.put(assembly, projectFilePath);
        this.assemblyRootNamespaces.put(assembly, rootNamespace);
        this.assemblyTagLibs.put(assembly, tagLibPaths);
        this.assemblyDefaultImports.put(assembly, defaultImports);
        this.assemblyDefinedSymbols.put(assembly, definedSymbols);
    }

    protected void configureReference(int assembly1, int assembly2) {
        this.references.put(assembly1, assembly2);
        this.assemblyreferences.insert(assembly1, assembly2);
    }

    protected void configureDestination(int assembly, String destinationPath, String targetVersion, String configuration) {
        if (destinationPath == null) {
            destinationPath = "";
        }

        if (targetVersion == null) {
            targetVersion = "";
        }

        this.assemblyDestinations.put(assembly, destinationPath);
        this.assemblyTargetVersions.put(assembly, targetVersion);
        this.assemblyConfigurations.put(assembly, configuration);
    }

    protected void configureFile(FileEntry file, int assembly, CodeModel codeModel, boolean checked) {
        for (FileEntry f = file; !f.isRootDirectory() && !new File(f.getPathString()).isDirectory(); f = f.getParentDirectory()) {
            this.fileCodeModels.put(f.getID(), this.getCodeModelID(codeModel));
        }

        this.fileCodeModels.put(file.getID(), this.getCodeModelID(codeModel));
        this.fileassemblys.put(file.getID(), assembly);
        if (checked) {
            this.registeredcheckedfiles.put(file);
        }

        this.registeredsolutionfiles.put(file);
    }

    protected void configureResourceFile(FileEntry file, int assembly, String packageName) {
        this.fileassemblys.put(file.getID(), assembly);
        if (packageName != null) {
            this.resourcepackagenames.put(file.getID(), packageName);
        }

        this.registeredresourcefiles.put(file);
    }

    protected void configure() {
        this.fileassemblys.clear();
        this.references.clear();
        this.assemblyreferences.clear();
        this.registeredcheckedfiles.clear();
        this.registeredsolutionfiles.clear();
        this.registeredresourcefiles.clear();
        this.checkedfiles.clear();
        this.solutionfiles.clear();
        this.assemblyProjectFilePaths.clear();
        this.assemblyRootNamespaces.clear();
        this.assemblyTagLibs.clear();
        this.assemblyDefaultImports.clear();
        this.assemblyDefinedSymbols.clear();
        this.assemblyDestinations.clear();
        this.assemblyConfigurations.clear();
        this.assemblyTargetVersions.clear();
        this.resourcepackagenames.clear();
        this.myCodeModelFilePatterns.clear();
        this.synchronize();
    }

    public List<String> getAssemblyTagLibPaths(int assembly) {
        return this.assemblyTagLibs.get(assembly);
    }

    public FileEntry getAssemblyHome(int assembly) {
        return this.getEntry(this.assemblyProjectFilePaths.get(assembly)).getParentDirectory();
    }

    public String getAssemblyNameString(int assembly) {
        return this.assemblyProjectFilePaths.get(assembly);
    }

    public String getTargetVersion(FileEntry file) {
        return this.assemblyTargetVersions.containsKey(this.getAssembly(file)) ? this.assemblyTargetVersions.get(this.getAssembly(file)) : "";
    }

    public boolean isExternallyBuilded(FileEntry file) {
        return !this.getConfiguration(file).equals("Extern");
    }

    public boolean isDebugBuilded(FileEntry file) {
        return this.getConfiguration(file).equals("Debug");
    }

    private String getConfiguration(FileEntry file) {
        return this.assemblyConfigurations.containsKey(this.getAssembly(file)) ? this.assemblyConfigurations.get(this.getAssembly(file)) : "";
    }

    public Set<String> getDestinationPaths(FileEntry file) {
        return this.assemblyDestinations.containsKey(this.getAssembly(file))
                ? Collections.singleton(this.assemblyDestinations.get(this.getAssembly(file)))
                : Collections.EMPTY_SET;
    }

    protected boolean hasHigherPriority(FileEntry file, FileEntry referedFile1, FileEntry referedFile2) {
        int assembly1 = this.fileassemblys.get(referedFile1.getID());
        int assembly2 = this.fileassemblys.get(referedFile2.getID());
        int assembly = this.fileassemblys.get(file.getID());
        this.assemblyreferences.DEFAULT_ITERATOR.init(assembly);

        while (this.assemblyreferences.DEFAULT_ITERATOR.hasMoreElements()) {
            int referedassembly = this.assemblyreferences.DEFAULT_ITERATOR.nextValue();
            if (referedassembly == assembly1) {
                return true;
            }

            if (referedassembly == assembly2) {
                return false;
            }
        }

        return false;
    }

    protected boolean isReferableFrom(FileEntry file, FileEntry referingFile) {
        this.pack();
        return this.references.contains(this.fileassemblys.get(referingFile.getID()), this.fileassemblys.get(file.getID()));
    }

    protected boolean isReferableFrom(int assembly, int referingAssembly) {
        this.pack();
        return this.references.contains(referingAssembly, assembly);
    }

    private void pack() {
        if (!this.packed) {
            this.packed = true;
            this.registeredcheckedfiles.DEFAULT_ITERATOR.init();

            while (this.registeredcheckedfiles.DEFAULT_ITERATOR.hasMoreElements()) {
                FileEntry file = this.registeredcheckedfiles.DEFAULT_ITERATOR.nextKey();
                this.pack(file, this.fileassemblys.get(file.getID()), this.fileCodeModels.get(file.getID()), this.checkedfiles);
            }

            this.registeredsolutionfiles.DEFAULT_ITERATOR.init();

            while (this.registeredsolutionfiles.DEFAULT_ITERATOR.hasMoreElements()) {
                FileEntry file = this.registeredsolutionfiles.DEFAULT_ITERATOR.nextKey();
                this.pack(file, this.fileassemblys.get(file.getID()), this.fileCodeModels.get(file.getID()), this.solutionfiles);
            }
        }
    }

    private void pack(FileEntry file, int assembly, int lang, SetOfFileEntry files) {
        if (file.isDirectory()) {
            int count = file.getChildCount();

            for (int i = 0; i < count; ++i) {
                FileEntry child = file.getChildEntry(i);
                this.pack(child, assembly, lang, files);
            }
        } else {
            this.fileassemblys.put(file.getID(), assembly);
            this.fileCodeModels.put(file.getID(), lang);
            files.put(file);
        }
    }

    public String getResourcePackageName(FileEntry file) {
        return this.resourcepackagenames.containsKey(file.getID()) ? this.resourcepackagenames.get(file.getID()) : "";
    }

    public CodeModel getCodeModel(FileEntry file){
        if (this.fileCodeModels.contains(file.getID())) {
            return this.getCodeModel(this.fileCodeModels.get(file.getID()));
        } else {
            this.pack();
            return this.getCodeModel(this.fileCodeModels.get(file.getID()));
        }
    }

    protected boolean isCheckedSolutionFile(FileEntry file) {
        this.pack();
        return this.checkedfiles.contains(file);
    }

    protected boolean isSolutionFile(FileEntry file) {
        this.pack();
        return this.solutionfiles.contains(file);
    }

    public SetOfFileEntry getCheckedSolutionFiles() {
        this.pack();
        return this.checkedfiles;
    }

    public SetOfFileEntry getSolutionFiles() {
        this.pack();
        return this.solutionfiles;
    }

    public SetOfFileEntry getResourceFiles() {
        this.pack();
        return this.registeredresourcefiles;
    }

    public int getID(FileEntry file) {
        return file == null ? -1 : file.getID();
    }

    public FileEntry getFileEntry(int id) {
        return id == -1 ? null : this.entries[id];
    }

    public boolean checkSynchronized() throws InterruptedException {
        for (int i = 0; i < this.entryCount; ++i) {
            FileEntry entry = this.entries[i];
            if (!entry.isSynchronized() && entry.getCodeModel() != null) {
                return false;
            }

            if (this.stopcallback.stopAsyncSynchronize()) {
                throw new InterruptedException();
            }
        }

        return true;
    }

    public FileEntry getRootDirectory() {
        return this.entries[0];
    }

    public FileEntry getEntry(String path) {
        while (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        while (path.startsWith(File.separator)) {
            path = path.substring(1);
        }

        FileEntry entry = this.getRootDirectory();
        int oldpos = 0;

        for (int pos = path.indexOf(File.separatorChar, oldpos); pos != -1; pos = path.indexOf(File.separatorChar, oldpos)) {
            String name = path.substring(oldpos, pos);
            if (!name.equals(".")) {
                if (name.equals("..") && this.getRootDirectory() != entry) {
                    entry = entry.getParentDirectory();
                } else {
                    entry = entry.getEntry(name);
                }
            }

            oldpos = pos + 1;

            while (path.charAt(oldpos) == File.separatorChar) {
                ++oldpos;
            }
        }

        return entry.getEntry(path.substring(oldpos));
    }

    protected String getEncoding() {
        return this.encoding;
    }

    protected int declareEntry(FileEntry e) {
        if (this.entryCount >= this.entries.length) {
            FileEntry[] entities = new FileEntry[this.entries.length * 2 + 1];
            System.arraycopy(this.entries, 0, entities, 0, this.entries.length);
            this.entries = entities;
        }

        this.entries[this.entryCount] = e;
        return this.entryCount++;
    }

    protected void fileIncluded(FileEntry entry) {
        if (this.currentIncludeFile != null && this.currentIncludeFile != entry) {
            this.includedEntryVersions.put(entry.getID(), entry.getVersion());
            this.includes.insert(this.currentIncludeFile.getID(), entry.getID());
        }
    }

    protected long getSyntaxVersion(FileEntry entry) {
        if (entry.getCodeModel() != null && entry.getCodeModel().getPreprocessor() != null) {
            if (!this.includes.contains(entry.getID())) {
                this.includes.put(entry.getID(), entry.getID());
                this.includedEntryVersions.put(entry.getID(), entry.getVersion());
                this.currentIncludeFile = entry;
                entry.getCodeModel().getPreprocessor().processVersion(entry);
                this.currentIncludeFile = null;
            }

            long version = 0L;
            this.includes.DEFAULT_ITERATOR.init(entry.getID());

            while (this.includes.DEFAULT_ITERATOR.hasMoreElements()) {
                FileEntry includedFile = this.getFileEntry(this.includes.DEFAULT_ITERATOR.nextValue());
                version = version * 37L ^ includedFile.getVersion();
            }

            return version;
        } else {
            return entry.getVersion();
        }
    }

    protected void createSyntaxTree(
            FileEntry file, SyntaxTree ast, boolean errors, boolean parseCode, boolean parseComments, String completionStr, int completionLine, int completionColumn
    ) {
        try {
            ast.clear();
            Reader reader = file.getPreProcessedReader(ast);
            if (completionStr != null) {
                this.completionReader.init(reader, completionStr, completionLine, completionColumn);
                reader = this.completionReader;
            }

            ast.declareFile(file);
            ast.declareSyntax(file.getLanguage().getSyntax());
            ast.declareVersion(file.getSyntaxVersion());
            ast.declareContainsComments(parseComments);
            ast.declareContainsCode(parseCode);
            ParserAbstraction parser = file.getLanguage().getParser();
            if (parser != null) {
                parser.fillSyntaxTree(reader, file, file.getSyntaxVersion(), errors, ast, parseCode, parseComments);
            } else {
                int rootnode = ast.declareNode(0, true, new int[0], 0, 0, 1, 1);
                ast.declareContent(this.identifiers, null, rootnode);
            }

            reader.close();
        } catch (IOException ignored) {
        }
    }

    protected Reader createBomReader(InputStream inStream, String fallbackEncoding) throws IOException {
      return myFileReaderCallback.createBomReader(inStream,fallbackEncoding);
    }

    protected Reader createLineEndingNormalizedReader(Reader reader) {
        return new LineEndingNormalizedReader(reader);
    }

    private static class CompletionReader extends Reader {
        private int completionLine;
        private int completionColumn;
        private String str;
        private int line;
        private int column;
        private int pos;
        private int maxpos;
        private Reader reader;
        private char[] buffer = new char[10000];

        public CompletionReader(Reader reader, String str, int completionLine, int completionColumn) {
            this.init(reader, str, completionLine, completionColumn);
        }

        public CompletionReader() {
        }

        public void init(Reader reader, String str, int completionLine, int completionColumn) {
            this.str = str;
            this.completionLine = completionLine;
            this.completionColumn = completionColumn;
            this.reader = reader;
            this.line = this.column = 1;
            this.pos = 0;
            this.maxpos = 0;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (this.pos >= this.maxpos) {
                this.maxpos = this.reader.read(this.buffer);
                if (this.maxpos == -1) {
                    return -1;
                }

                this.pos = 0;
            }

            int count = 0;

            while (this.pos < this.maxpos && count < len) {
                if (this.line == this.completionLine && this.column == this.completionColumn) {
                    if (count + this.str.length() >= len) {
                        return count;
                    }

                    count += this.str.length();

                    for (int i = 0; i < this.str.length(); ++i) {
                        cbuf[off++] = this.str.charAt(i);
                    }
                }

                char c = this.buffer[this.pos++];
                switch (c) {
                    case '\n':
                        cbuf[off++] = c;
                        ++count;
                        ++this.line;
                        this.column = 1;
                        break;
                    case '\uFFEE':
                        if (this.pos < this.maxpos - 8 && count < len - 8) {
                            cbuf[off++] = c;
                            count += 9;
                            this.line = 0;
                            this.column = 0;
                            this.line |= (cbuf[off++] = this.buffer[this.pos++]) << 24;
                            this.line |= (cbuf[off++] = this.buffer[this.pos++]) << 16;
                            this.line |= (cbuf[off++] = this.buffer[this.pos++]) << '\b';
                            this.line |= (cbuf[off++] = this.buffer[this.pos++]);
                            this.column |= (cbuf[off++] = this.buffer[this.pos++]) << 24;
                            this.column |= (cbuf[off++] = this.buffer[this.pos++]) << 16;
                            this.column |= (cbuf[off++] = this.buffer[this.pos++]) << '\b';
                            this.column |= (cbuf[off++] = this.buffer[this.pos++]);
                            break;
                        }
                    default:
                        cbuf[off++] = c;
                        ++count;
                        ++this.column;
                }
            }

            return count;
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }

    private static class LineEndingNormalizedReader extends Reader {
        private Reader reader;
        private boolean rseen;

        public LineEndingNormalizedReader(Reader reader) {
            this.reader = reader;
            this.rseen = false;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int readlen = this.reader.read(cbuf, off, len);
            if (readlen == -1) {
                return -1;
            } else {
                boolean rseen = this.rseen;
                int destoff = off;
                int srcoff = off;
                int maxoff = off + readlen;

                while (srcoff < maxoff) {
                    char c = cbuf[srcoff++];
                    switch (c) {
                        case '\n':
                            if (!rseen) {
                                cbuf[destoff++] = '\n';
                            }

                            rseen = false;
                            break;
                        case '\r':
                            cbuf[destoff++] = '\n';
                            rseen = true;
                            break;
                        default:
                            cbuf[destoff++] = c;
                            rseen = false;
                    }
                }

                this.rseen = rseen;
                return destoff - off;
            }
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
            this.reader = null;
        }
    }
}
