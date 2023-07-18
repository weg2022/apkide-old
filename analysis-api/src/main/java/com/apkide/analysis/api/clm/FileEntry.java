package com.apkide.analysis.api.clm;

import com.apkide.analysis.api.cle.CodeModel;
import com.apkide.analysis.api.clm.callback.OpenFileCallback;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.collections.ListOfInt;
import com.apkide.common.collections.MapOfIntInt;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Locale;

public class FileEntry {
   private final FileSpace filespace;
   private final IdentifierSpace identifiers;
   private final OpenFileCallback openfilecallback;
   private int nameidentifier;
   private FileEntry parentdir;
   private int id;
   private MapOfIntInt entries;
   private ListOfInt childs;
   private FileEntry parentArchivefile;
   private String relativeArchiveEntrypath;
   private boolean isArchiveEntry;
   private boolean isArchive;
   private boolean isDirectory;
   private long version;
   private boolean loaded;
   private boolean exists;

   public FileEntry(IdentifierSpace identifiers, FileSpace filespace, OpenFileCallback openfilecallback, int nameidentifier, FileEntry parentdir) {
      this.identifiers = identifiers;
      this.filespace = filespace;
      this.openfilecallback = openfilecallback;
      this.nameidentifier = nameidentifier;
      this.parentdir = parentdir;
      this.id = filespace.declareEntry(this);
      this.version = -1L;
      this.init();
   }

   public FileEntry(IdentifierSpace identifiers, FileSpace filespace, OpenFileCallback openfilecallback) {
      this.identifiers = identifiers;
      this.filespace = filespace;
      this.openfilecallback = openfilecallback;
      this.version = -1L;
      this.init();
   }

   protected void load(StoreInputStream stream) throws IOException {
      this.id = stream.readInt();
      this.nameidentifier = stream.readInt();
      this.parentdir = this.filespace.getFileEntry(stream.readInt());
      if (stream.readBoolean()) {
         this.entries = new MapOfIntInt(stream);
      }

      this.version = stream.readLong();
   }

   protected void store(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.id);
      stream.writeInt(this.nameidentifier);
      stream.writeInt(this.filespace.getID(this.parentdir));
      stream.writeBoolean(this.entries != null);
      if (this.entries != null) {
         this.entries.store(stream);
      }

      stream.writeLong(this.version);
   }

   public boolean isCheckedSolutionFile() {
      return this.filespace.isCheckedSolutionFile(this);
   }

   public boolean isSolutionFile() {
      return this.filespace.isSolutionFile(this);
   }

   public String getExtensionString() {
      String filename = this.getFullNameString();
      int index=filename.lastIndexOf('.');
      return index > 0 ? filename.substring(index) : "";
   }

   public String getFullNameString() {
      return this.identifiers.getString(this.nameidentifier);
   }

   public int getNameIdentifier() {
      String filename = this.getFullNameString();
      int index=filename.lastIndexOf('.');
      if (!this.isDirectory() && index > 0) {
         filename = filename.substring(0, index);
      }

      return this.identifiers.get(filename);
   }

   public int getFullNameIdentifier() {
      return this.nameidentifier;
   }

   public int getAssembly() {
      return this.filespace.getAssembly(this);
   }

   public boolean isReferable(FileEntry file) {
      return this.filespace.isReferableFrom(this, file);
   }

   public String getRootNamespace() {
      return this.filespace.getRootNamespace(this);
   }

   public List<String> getDefaultImports() {
      return this.filespace.getDefaultImports(this);
   }

   public List<String> getDefinedSymbols() {
      return this.filespace.getDefinedSymbols(this);
   }

   public Reader getPreProcessedReader(SyntaxTree ast) {
      return getCodeModel() != null && getCodeModel().getPreprocessor() != null
         ? getCodeModel().getPreprocessor().process(this, this.getReaderForInclude(), ast)
         : this.getReader();
   }

   public FileEntry getParentDirectory() {
      return this.parentdir;
   }

   public long getSize() {
      this.load();
      if (this.isOpen()) {
         return this.openfilecallback.getOpenFileSize(this);
      } else {
         String path = this.getPathString();
         return new File(path).exists() ? new File(path).length() : 0L;
      }
   }

   public FileEntry getParentArchive() {
      return this.parentArchivefile;
   }

   public String getRelativeArchivePath() {
      return this.relativeArchiveEntrypath;
   }

   public boolean isArchive() {
      return this.isArchive;
   }

   public boolean isArchiveEntry() {
      return this.isArchiveEntry;
   }

   public long getArchiveVersion() {
      return !this.isArchiveEntry() ? 0L : this.getParentArchive().getSyntaxVersion();
   }

   public boolean isEnclosingDir(FileEntry child) {
      while(!child.isRootDirectory()) {
         if (child == this) {
            return true;
         }

         child = child.getParentDirectory();
      }

      return false;
   }

   public void createSyntaxTree(
      SyntaxTree ast, boolean errors, boolean parseCode, boolean parseComments, String completionStr, int completionLine, int completionColumn
   ) {
      this.filespace.createSyntaxTree(this, ast, errors, parseCode, parseComments, completionStr, completionLine, completionColumn);
   }

   public long getSyntaxVersion() {
      return this.filespace.getSyntaxVersion(this);
   }

   public long getDiskVersion() {
      this.load();
      return this.isOpen() ? this.fetchDiskVersion() : this.getVersion();
   }

   public long getVersion() {
      this.load();
      if (!this.isArchiveEntry && this.isOpen()) {
         this.version = -1L;
         return this.openfilecallback.getOpenFileVersion(this);
      } else {
         if (this.version == -1L) {
            this.version = this.fetchDiskVersion();
         }

         return this.version;
      }
   }

   public Reader getReaderForInclude() {
      this.filespace.fileIncluded(this);
      return this.getReader();
   }

   public boolean isOpen() {
      return this.openfilecallback != null && this.openfilecallback.isOpenFile(this);
   }

   public boolean isDirectory() {
      this.load();
      return this.isDirectory;
   }

   public boolean isRootDirectory() {
      return this.parentdir == null;
   }

   public Reader getReader() {
      this.load();
      if (this.isOpen()) {
         return this.openfilecallback.getOpenFileReader(this);
      } else if (!this.exists()) {
         return new CharArrayReader(new char[0]);
      } else {
         if (!this.isSynchronized()) {
            System.out.println("File not synchronized " + this.getPathString());
         }

         try {
            if (this.isArchiveEntry) {
               String encoding = this.filespace.getEncoding();
               String cutomfilepath = this.parentArchivefile.getPathString();
               return this.filespace
                  .createLineEndingNormalizedReader(
                    getCodeModel().getArchiveEntryReader(cutomfilepath, this.getRelativeArchivePath(), encoding)
                  );
            } else {
               String encoding = this.filespace.getEncoding();
               return this.filespace.createLineEndingNormalizedReader(this.filespace.createBomReader(new FileInputStream(this.getPathString()), encoding));
            }
         } catch (IOException e) {
            return new CharArrayReader(new char[0]);
         }
      }
   }

   public String getPathString() {
      if (this.parentdir == null && System.getProperty("os.name").toUpperCase(Locale.US).indexOf("WINDOWS") == -1) {
         return File.separator;
      } else {
         String path = "";
         if (this.parentdir != null) {
            path = this.parentdir.getPathString();
         }

         if (path.length() > 1) {
            path = path + File.separator;
         }

         return path + this.getFullNameString();
      }
   }

   public int getChildCount() {
      this.load();
      return this.childs == null ? 0 : this.childs.size();
   }

   public FileEntry getChildEntry(int number) {
      return this.filespace.getFileEntry(this.childs.get(number));
   }

   public FileEntry getEntry(String name) {
      if (this.entries == null) {
         this.entries = new MapOfIntInt();
      }

      int nameidentifier = this.identifiers.get(name);
      if (!this.entries.contains(nameidentifier)) {
         FileEntry entry = new FileEntry(this.identifiers, this.filespace, this.openfilecallback, nameidentifier, this);
         this.entries.insert(nameidentifier, entry.getID());
      }

      return this.filespace.getFileEntry(this.entries.get(nameidentifier));
   }

   public FileEntry getSuccessorFile() {
      FileEntry dir = this.getParentDirectory();
      int count = dir.getChildCount();

      for(int i = 0; i < count - 1; ++i) {
         if (dir.getChildEntry(i) == this) {
            return dir.getChildEntry(i + 1);
         }
      }

      return null;
   }

   public CodeModel getCodeModel() {
      return filespace.getCodeModel(this);
   }

   public int getID() {
      return this.id;
   }

   public boolean isSynchronized() {
      if (this.isOpen()) {
         return true;
      } else if (this.version == -1L) {
         return true;
      } else if (this.isArchive) {
         return this.version == this.fetchDiskVersion();
      } else if (!this.isDirectory && !this.isArchiveEntry) {
         return this.version == this.fetchFileDiskVersion();
      } else {
         return true;
      }
   }

   public void synchronize() {
      if (this.parentdir != null) {
         this.parentdir.loaded = false;
      }

      if (this.parentArchivefile != null) {
         this.parentArchivefile.loaded = false;

         for(FileEntry dir = this.parentdir; dir != null && dir != this.parentArchivefile; dir = dir.parentdir) {
            dir.loaded = false;
         }
      }

      if (this.childs != null) {
         this.childs.clear();
      }

      if (!this.isDirectory && !this.isArchiveEntry && this.version != -1L) {
         this.version = this.fetchFileDiskVersion();
      } else {
         this.version = -1L;
      }

      this.init();
   }

   public boolean exists() {
      this.load();
      return this.exists;
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof FileEntry)) {
         return false;
      } else {
         return ((FileEntry)obj).getID() == this.id;
      }
   }

   private void init() {
      this.loaded = false;
      this.parentArchivefile = null;
      this.relativeArchiveEntrypath = null;
      this.isArchiveEntry = false;
      this.isDirectory = false;
      this.isArchive = false;
      this.exists = false;
   }

   private long fetchDiskVersion() {
      if (!this.exists()) {
         return 0L;
      } else if (this.isArchive) {
         return getCodeModel().getArchiveVersion(this.getPathString());
      } else if (this.isArchiveEntry) {
         return this.parentArchivefile.getVersion();
      } else {
         return this.isRootDirectory() && System.getProperty("os.name").toUpperCase(Locale.US).indexOf("WINDOWS") != -1 ? 0L : this.fetchFileDiskVersion();
      }
   }

   private long fetchFileDiskVersion() {
      File f = new File(this.getPathString());
      if (f.isFile()) {
         return f.lastModified();
      } else {
         return f.isDirectory() ? f.lastModified() : 0L;
      }
   }

   private void sort(String[] names, int i, int j) {
      if (i < j) {
         int oldi = i;
         int oldj = j;
         int z = i + (j - i) / 2;
         String pivot = names[z];

         while(i <= j) {
            while(names[i].compareTo(pivot) < 0) {
               ++i;
            }

            while(pivot.compareTo(names[j]) < 0) {
               --j;
            }

            if (i <= j) {
               String temp = names[j];
               names[j] = names[i];
               names[i] = temp;
               ++i;
               --j;
            }
         }

         this.sort(names, oldi, j);
         this.sort(names, i, oldj);
      }
   }

   private void load() {
      if (!this.loaded) {
         if (this.parentdir != null) {
            this.parentdir.load();
         }

         if (this.loaded) {
            return;
         }

         this.loaded = true;
         String path = this.getPathString();
         if (this.isRootDirectory() && System.getProperty("os.name").toUpperCase(Locale.US).contains("WINDOWS")) {
            for(char c = 'A'; c <= 'Z'; ++c) {
               String drivepath = c + ":";
               this.addDirectory(drivepath);
            }

            this.exists = true;
         } else if (getCodeModel() != null && getCodeModel().isSupportsFileArchives()) {
            this.isArchive = true;

            try {
               this.version = getCodeModel().getArchiveVersion(path);
               String[] fileentries = getCodeModel().getArchiveEntries(path);
               if (fileentries != null) {
                  this.parentArchivefile = this;
                  this.isDirectory = true;

                  for(String relativepath : fileentries) {
                     if (relativepath != null) {
                        this.addCustomEntry(relativepath, relativepath);
                     }
                  }
               }
            } catch (IOException ignored) {
            }

            this.exists = true;
         } else if (new File(path).isDirectory()) {
            String[] entries = new File(path + File.separatorChar).list();
            if (entries != null) {
               for(int i = 0; i < entries.length; ++i) {
                  String entrypath = path + File.separatorChar + entries[i];
                  if (new File(entrypath).isDirectory()) {
                     this.addDirectory(entries[i]);
                  } else {
                     this.addFile(entries[i]);
                  }
               }
            }

            this.exists = true;
         }
      }
   }

   private void addCustomEntry(String relativepath, String entrypath) {
      int p = relativepath.indexOf(47);
      if (p == -1) {
         p = relativepath.indexOf(92);
      }

      if (p == -1) {
         FileEntry file = this.addFile(relativepath);
         file.loaded = true;
         file.isArchiveEntry = true;
         file.relativeArchiveEntrypath = entrypath;
         file.parentArchivefile = this.parentArchivefile;
      } else {
         String dirname = relativepath.substring(0, p);
         FileEntry dir = this.getEntry(dirname);
         if (!dir.exists) {
            this.addDirectory(dirname);
         }

         dir.loaded = true;
         dir.isArchiveEntry = true;
         dir.parentArchivefile = this.parentArchivefile;
         dir.relativeArchiveEntrypath = entrypath;
         dir.addCustomEntry(relativepath.substring(p + 1), entrypath);
      }
   }

   private FileEntry addDirectory(String name) {
      FileEntry entry = this.getEntry(name);
      entry.isDirectory = true;
      entry.exists = true;
      if (this.childs == null) {
         this.childs = new ListOfInt();
      }

      this.childs.add(entry.getID());
      return entry;
   }

   private FileEntry addFile(String name) {
      FileEntry entry = this.getEntry(name);
      entry.isDirectory = false;
      entry.exists = true;
      if (this.childs == null) {
         this.childs = new ListOfInt();
      }

      this.childs.add(entry.getID());
      return entry;
   }
}
