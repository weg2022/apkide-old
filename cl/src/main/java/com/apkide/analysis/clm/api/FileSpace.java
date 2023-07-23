package com.apkide.analysis.clm.api;

import com.apkide.analysis.clm.Language;
import com.apkide.analysis.clm.Parser;
import com.apkide.analysis.clm.api.callback.OpenFileCallback;
import com.apkide.analysis.clm.api.callback.StopCallback;
import com.apkide.analysis.clm.api.collections.FunctionOfIntInt;
import com.apkide.analysis.clm.api.collections.MapOfIntLong;
import com.apkide.analysis.clm.api.collections.OrderedMapOfIntInt;
import com.apkide.analysis.clm.api.collections.RelationOfIntInt;
import com.apkide.analysis.clm.api.collections.SetOfFileEntry;
import com.apkide.analysis.clm.api.collections.SetOfInt;
import com.apkide.analysis.clm.api.collections.StoreInputStream;
import com.apkide.analysis.clm.api.collections.StoreOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileSpace {
   private static final boolean DEBUG_INCLUDE = false;
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
   private FunctionOfIntInt filelanguages;
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
   private Language[] languages;
   private boolean packed;

   public FileSpace(IdentifierSpace identifiers, OpenFileCallback openfilecallback, StopCallback stopcallback) {
      this.identifiers = identifiers;
      this.openfilecallback = openfilecallback;
      this.stopcallback = stopcallback;
      this.entries = new FileEntry[1000];
      this.entries[0] = new FileEntry(identifiers, this, openfilecallback, identifiers.get(""), null);
      this.entryCount = 1;
      this.includedEntryVersions = new MapOfIntLong();
      this.includes = new OrderedMapOfIntInt();
      this.filelanguages = new FunctionOfIntInt();
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
   }

   public void close() {
      for(Language l : this.languages) {
         if (l.getArchiveReader() != null) {
            l.getArchiveReader().close();
         }
      }
   }

   public void storeEntries(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.entryCount);
   }

   public void loadEntries(StoreInputStream stream) throws IOException {
      this.entryCount = stream.readInt();
      this.entries = new FileEntry[this.entryCount * 2];

      for(int i = 0; i < this.entryCount; ++i) {
         this.entries[i] = new FileEntry(this.identifiers, this, this.openfilecallback);
      }
   }

   protected void load(StoreInputStream stream) throws IOException {
      for(int i = 0; i < this.entryCount; ++i) {
         this.entries[i].load(stream);
         if (stream.readChar() != 'E') {
            throw new IOException();
         }
      }

      this.includedEntryVersions = new MapOfIntLong(stream);
      this.includes = new OrderedMapOfIntInt(stream);
      this.filelanguages = new FunctionOfIntInt(stream);
   }

   protected void store(StoreOutputStream stream) throws IOException {
      for(int i = 0; i < this.entryCount; ++i) {
         this.entries[i].store(stream);
         stream.writeChar(69);
      }

      this.includedEntryVersions.store(stream);
      this.includes.store(stream);
      this.filelanguages.store(stream);
   }

   protected void update(boolean updateFileSpace) {
      if (updateFileSpace) {
         this.synchronize();
      }

      this.changedIncludeFiles.clear();
      this.includedEntryVersions.DEFAULT_ITERATOR.init();

      while(this.includedEntryVersions.DEFAULT_ITERATOR.hasMoreElements()) {
         FileEntry entry = this.getFileEntry(this.includedEntryVersions.DEFAULT_ITERATOR.nextKey());
         long version = this.includedEntryVersions.DEFAULT_ITERATOR.nextValue();
         if (version != entry.getVersion()) {
            this.changedIncludeFiles.put(entry.getID());
         }
      }

      this.changedIncludeFiles.DEFAULT_ITERATOR.init();

      while(this.changedIncludeFiles.DEFAULT_ITERATOR.hasMoreElements()) {
         this.includedEntryVersions.remove(this.changedIncludeFiles.DEFAULT_ITERATOR.nextKey());
      }

      this.changedFiles.clear();
      this.includes.DEFAULT_ITERATOR.init();

      while(this.includes.DEFAULT_ITERATOR.hasMoreElements()) {
         FileEntry entry = this.getFileEntry(this.includes.DEFAULT_ITERATOR.nextValue());
         FileEntry file = this.getFileEntry(this.includes.DEFAULT_ITERATOR.nextKey());
         if (this.changedIncludeFiles.contains(entry.getID())) {
            this.changedFiles.put(file.getID());
         }
      }

      this.changedFiles.DEFAULT_ITERATOR.init();

      while(this.changedFiles.DEFAULT_ITERATOR.hasMoreElements()) {
         this.includes.remove(this.changedFiles.DEFAULT_ITERATOR.nextKey());
      }
   }

   private void synchronize() {
      for(FileEntry entry : this.entries) {
         if (entry != null) {
            entry.synchronize();
         }
      }

      this.packed = false;
   }

   public Language[] getLanguages() {
      return this.languages;
   }

   protected int getLanguageID(Language language) {
      for(int i = 0; i < this.languages.length; ++i) {
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
      return this.assemblyRootNamespaces.getOrDefault(assembly, "");
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

   protected void configureFile(FileEntry file, int assembly, Language language, boolean checked) {
      for(FileEntry f = file; !f.isRootDirectory() && !new File(f.getPathString()).isDirectory(); f = f.getParentDirectory()) {
         this.filelanguages.put(f.getID(), this.getLanguageID(language));
      }

      this.filelanguages.put(file.getID(), this.getLanguageID(language));
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

   protected void configureLanguages(Language[] languages) {
      this.languages = languages;
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
      return this.assemblyTargetVersions.getOrDefault(this.getAssembly(file), "");
   }

   public boolean isExternallyBuilded(FileEntry file) {
      return !this.getConfiguration(file).equals("Extern");
   }

   public boolean isDebugBuilded(FileEntry file) {
      return this.getConfiguration(file).equals("Debug");
   }

   private String getConfiguration(FileEntry file) {
      return this.assemblyConfigurations.getOrDefault(this.getAssembly(file), "");
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

      while(this.assemblyreferences.DEFAULT_ITERATOR.hasMoreElements()) {
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

         while(this.registeredcheckedfiles.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry file = this.registeredcheckedfiles.DEFAULT_ITERATOR.nextKey();
            this.pack(file, this.fileassemblys.get(file.getID()), this.filelanguages.get(file.getID()), this.checkedfiles);
         }

         this.registeredsolutionfiles.DEFAULT_ITERATOR.init();

         while(this.registeredsolutionfiles.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry file = this.registeredsolutionfiles.DEFAULT_ITERATOR.nextKey();
            this.pack(file, this.fileassemblys.get(file.getID()), this.filelanguages.get(file.getID()), this.solutionfiles);
         }
      }
   }

   private void pack(FileEntry file, int assembly, int lang, SetOfFileEntry files) {
      if (file.isDirectory()) {
         int count = file.getChildCount();

         for(int i = 0; i < count; ++i) {
            FileEntry child = file.getChildEntry(i);
            this.pack(child, assembly, lang, files);
         }
      } else {
         this.fileassemblys.put(file.getID(), assembly);
         this.filelanguages.put(file.getID(), lang);
         files.put(file);
      }
   }

   public String getResourcePackageName(FileEntry file) {
      return this.resourcepackagenames.getOrDefault(file.getID(), "");
   }

   public Language getLanguage(FileEntry file) {
      if (this.filelanguages.contains(file.getID())) {
         return this.getLanguage(this.filelanguages.get(file.getID()));
      } else {
         this.pack();
         return this.getLanguage(this.filelanguages.get(file.getID()));
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
      for(int i = 0; i < this.entryCount; ++i) {
         FileEntry entry = this.entries[i];
         if (!entry.isSynchronized() && entry.getLanguage() != null) {
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
      while(path.endsWith(File.separator)) {
         path = path.substring(0, path.length() - 1);
      }

      while(path.startsWith(File.separator)) {
         path = path.substring(1);
      }

      FileEntry entry = this.getRootDirectory();
      int oldpos = 0;

      for(int pos = path.indexOf(File.separatorChar, oldpos); pos != -1; pos = path.indexOf(File.separatorChar, oldpos)) {
         String name = path.substring(oldpos, pos);
         if (!name.equals(".")) {
            if (name.equals("..") && this.getRootDirectory() != entry) {
               entry = entry.getParentDirectory();
            } else {
               entry = entry.getEntry(name);
            }
         }

         oldpos = pos + 1;

         while(path.charAt(oldpos) == File.separatorChar) {
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
      if (entry.getLanguage() != null && entry.getLanguage().getPreProcessor() != null) {
         if (!this.includes.contains(entry.getID())) {
            this.includes.put(entry.getID(), entry.getID());
            this.includedEntryVersions.put(entry.getID(), entry.getVersion());
            this.currentIncludeFile = entry;
            entry.getLanguage().getPreProcessor().processVersion(entry);
            this.currentIncludeFile = null;
         }

         long version = 0L;
         this.includes.DEFAULT_ITERATOR.init(entry.getID());

         while(this.includes.DEFAULT_ITERATOR.hasMoreElements()) {
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
         Parser parser = file.getLanguage().getParser();
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
      int BOM_LENGTH = 4;
      PushbackInputStream pbInputStream = new PushbackInputStream(inStream, 4);
      byte[] bomBytes = new byte[4];

      int totalCount;
      int count;
      for(totalCount = 0; totalCount < 4; totalCount += count) {
         count = pbInputStream.read(bomBytes, totalCount, 4 - totalCount);
         if (count == -1) {
            break;
         }
      }

      String encoding = null;
      if (totalCount == 4) {
         if ((bomBytes[0] & 255) == 0 && (bomBytes[1] & 255) == 0 && (bomBytes[2] & 255) == 254 && (bomBytes[3] & 255) == 255) {
            encoding = "UTF-32BE";
         } else if ((bomBytes[0] & 255) == 255 && (bomBytes[1] & 255) == 254 && (bomBytes[2] & 255) == 0 && (bomBytes[3] & 255) == 0) {
            encoding = "UTF-32LE";
         }
      }

      if (encoding == null && totalCount >= 3 && (bomBytes[0] & 255) == 239 && (bomBytes[1] & 255) == 187 && (bomBytes[2] & 255) == 191) {
         encoding = "UTF-8";
         pbInputStream.unread(bomBytes, 3, totalCount - 3);
      }

      if (encoding == null && totalCount >= 2) {
         if ((bomBytes[0] & 255) == 254 && (bomBytes[1] & 255) == 255) {
            encoding = "UTF-16BE";
         } else if ((bomBytes[0] & 255) == 255 && (bomBytes[1] & 255) == 254) {
            encoding = "UTF-16LE";
            pbInputStream.unread(bomBytes, 2, totalCount - 2);
         }
      }

      if (encoding == null) {
         pbInputStream.unread(bomBytes, 0, totalCount);
      }

      if (encoding == null) {
         return fallbackEncoding != null ? new InputStreamReader(pbInputStream, fallbackEncoding) : new InputStreamReader(pbInputStream);
      } else {
         return new InputStreamReader(pbInputStream, encoding);
      }
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

         while(this.pos < this.maxpos && count < len) {
            if (this.line == this.completionLine && this.column == this.completionColumn) {
               if (count + this.str.length() >= len) {
                  return count;
               }

               count += this.str.length();

               for(int i = 0; i < this.str.length(); ++i) {
                  cbuf[off++] = this.str.charAt(i);
               }
            }

            char c = this.buffer[this.pos++];
            switch(c) {
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

            while(srcoff < maxoff) {
               char c = cbuf[srcoff++];
               switch(c) {
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
