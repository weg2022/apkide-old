package com.apkide.analysis.api.clm;


import com.apkide.analysis.api.clm.collections.SetOfFileEntry;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compiler {
   private static final boolean COMPRESS_CLASSILES = false;
   private static final long ZIP_TIME_STEP = 2000L;
   private final Model model;
   private final boolean verboseWrite;
   private final boolean verboseRead;
   private final boolean fastBuildMode;
   private byte[] buffercache2;
   private byte[] buffercache1;
   private byte[] copybuffer = new byte[10000];
   private CRC32 crc32 = new CRC32();
   private Deflater deflater = new Deflater(1);
   private Inflater inflater = new Inflater();
   private ByteArrayOutputStream zipoutputstream = new ByteArrayOutputStream();
   private HashMap<String, Long> classfileversions = new HashMap<>();
   private HashSet<String> cacheddestinationpaths = new HashSet<>();
   private HashSet<String> cachedinvalidfiles = new HashSet<>();
   private int cachesize;
   private Hashtable<String, String> cachedfiledirs = new Hashtable<>();
   private Hashtable<String, FileContent> cachedfilecontents = new Hashtable<>();
   private Hashtable<String, String> cachedfileclassnames = new Hashtable<>();
   private Hashtable<String, Long> cachedsourcefileversions = new Hashtable<>();
   private Hashtable<String, FileEntry> cachedfilesourcefiles = new Hashtable<>();
   private Hashtable<String, Long> cachedfilechecksums = new Hashtable<>();
   private Hashtable<String, Long> compiledfilechecksums = new Hashtable<>();
   private SetOfFileEntry compiledFilesDebug;
   private SetOfFileEntry compiledFilesRelease;
   private SetOfFileEntry buildRequiredFilesDebug;
   private SetOfFileEntry buildRequiredFilesRelease;

   public Compiler(Model model) {
      this.model = model;
      this.verboseWrite = false;
      this.verboseRead = false;
      this.fastBuildMode = false;
      this.compiledFilesDebug = new SetOfFileEntry(model.filespace);
      this.compiledFilesRelease = new SetOfFileEntry(model.filespace);
      this.buildRequiredFilesDebug = new SetOfFileEntry(model.filespace);
      this.buildRequiredFilesRelease = new SetOfFileEntry(model.filespace);
   }

   protected void load(StoreInputStream stream) throws IOException {
      this.compiledFilesDebug = new SetOfFileEntry(this.model.filespace, stream);
      this.compiledFilesRelease = new SetOfFileEntry(this.model.filespace, stream);
      this.buildRequiredFilesDebug = new SetOfFileEntry(this.model.filespace, stream);
      this.buildRequiredFilesRelease = new SetOfFileEntry(this.model.filespace, stream);
   }

   protected void store(StoreOutputStream stream) throws IOException {
      this.compiledFilesDebug.store(stream);
      this.compiledFilesRelease.store(stream);
      this.buildRequiredFilesDebug.store(stream);
      this.buildRequiredFilesRelease.store(stream);
   }

   public void compile(SyntaxTree ast, boolean createDebugMetadata, boolean build) {
      CompilerAbstraction compiler = ast.getFile().getLanguage().getCompiler();
      if (compiler == null || !this.model.filespace.isExternallyBuilded(ast.getFile())) {
         this.buildRequiredFilesDebug.put(ast.getFile());
         this.buildRequiredFilesRelease.put(ast.getFile());
      } else if (!this.model.errortable.containsCompileErrors(ast.getFile())) {
         if (!build && !createDebugMetadata) {
            this.compiledFilesDebug.put(ast.getFile());
            this.compiledFilesRelease.put(ast.getFile());
         } else {
            compiler.compile(ast, createDebugMetadata);
            if (this.model.filespace.isDebugBuilded(ast.getFile())) {
               this.compiledFilesDebug.remove(ast.getFile());
            } else {
               this.compiledFilesRelease.remove(ast.getFile());
            }
         }
      }
   }

   public void setBuildRequiredFile(FileEntry file) {
      CompilerAbstraction compiler = file.getLanguage().getCompiler();
      if (compiler == null) {
         if (this.model.filespace.isDebugBuilded(file)) {
            this.buildRequiredFilesDebug.put(file);
         } else {
            this.buildRequiredFilesRelease.put(file);
         }
      }
   }

   public void clearBuildRequiredFile(FileEntry file) {
      if (this.model.filespace.isDebugBuilded(file)) {
         this.buildRequiredFilesDebug.remove(file);
      } else {
         this.buildRequiredFilesRelease.remove(file);
      }
   }

   public boolean isBuildRequiredFile(FileEntry file) {
      return this.model.filespace.isDebugBuilded(file) ? this.buildRequiredFilesDebug.contains(file) : this.buildRequiredFilesRelease.contains(file);
   }

   public boolean isUptodate(FileEntry file) {
      if (this.model.filespace.isDebugBuilded(file)) {
         return !this.compiledFilesDebug.contains(file);
      } else {
         return !this.compiledFilesRelease.contains(file);
      }
   }

   public int getCacheSize() {
      return this.cachesize;
   }

   public void cleanDirectories(Set<String> destinationPathsFullClean, Set<String> destinationPathsCleanClasses) throws IOException {
      ArrayList<String> sortedDestinationPaths = new ArrayList<>(destinationPathsFullClean);
      Collections.sort(sortedDestinationPaths);
      Collections.reverse(sortedDestinationPaths);

      for(String destinationDirectory : sortedDestinationPaths) {
         File destinationDir = new File(destinationDirectory);
         if (destinationDir.exists()) {
            if (destinationDir.isFile()) {
               if (destinationDirectory.toUpperCase(Locale.US).endsWith(".JAR")) {
                  if (!destinationDir.delete()) {
                  }

                  new JarOutputStream(new FileOutputStream(destinationDir), new Manifest()).close();
                  destinationDir.setLastModified(0L);
               }
            } else if (destinationDir.isDirectory()) {
               String absRootPath = destinationDir.getAbsolutePath();

               while(absRootPath.endsWith(File.separator + File.separator)) {
                  absRootPath = absRootPath.substring(absRootPath.length());
               }

               if (absRootPath.length() > 1 && !absRootPath.matches("[A-Z]\\:\\\\?")) {
                  try {
                     final String canonRootPath = new File(absRootPath).getCanonicalPath();
                     (new FileTreeWalker() {
                        @Override
                        protected void visitFile(String file) {
                           if (!new File(file).delete()) {
                           }
                        }

                        @Override
                        protected void visitDirectoryFinished(String directory) {
                           try {
                              String absPath = new File(directory).getAbsolutePath();
                              String canonPath = new File(directory).getCanonicalPath();
                              if (canonPath.equals(canonRootPath)) {
                                 return;
                              }

                              if (absPath.equals(canonPath) && !new File(directory).delete()) {
                              }
                           } catch (IOException var4) {
                           }
                        }
                     }).walk(absRootPath);
                  } catch (IOException var9) {
                  }
               }
            }
         }
      }

      for(String destinationPathCleanClasses : destinationPathsCleanClasses) {
         File sourceDir = new File(destinationPathCleanClasses);
         if (sourceDir.exists() && sourceDir.isDirectory()) {
            (new FileTreeWalker() {
               @Override
               protected void visitFile(String file) {
                  if (file.toUpperCase(Locale.US).endsWith(".CLASS") && !new File(file).delete()) {
                  }
               }
            }).walk(sourceDir.getAbsolutePath());
         }
      }
   }

   public void update() {
      this.compiledfilechecksums.clear();
   }

   public void removeClassfiles(FileEntry sourcefile) {
      Enumeration<? extends String> enumeration = this.cachedfilesourcefiles.keys();

      while(enumeration.hasMoreElements()) {
         String filepath = enumeration.nextElement();
         if (this.cachedfilesourcefiles.get(filepath) == sourcefile) {
            this.cachedinvalidfiles.add(filepath);
         }
      }
   }

   private void addResourceFile(FileEntry file, String pakageName) {
      String filepath = file.getPathString();
      String relativedirpath = pakageName.replace('.', File.separatorChar) + File.separator;
      String filename = file.getFullNameString();

      for(String destinationpath : this.model.filespace.getDestinationPaths(file)) {
         String path = destinationpath + File.separator + relativedirpath + filename;
         if (!new File(destinationpath).isDirectory() || file.getDiskVersion() > new File(path).lastModified()) {
            this.cacheddestinationpaths.add(destinationpath);
            this.cachedfilecontents.put(path, new ResourceFileContent(filepath));
            this.cachedfiledirs.put(path, destinationpath + File.separator + relativedirpath);
            this.cachedfilesourcefiles.put(path, file);
            this.cachedsourcefileversions.put(path, file.getDiskVersion());
         }
      }
   }

   public OutputStream getClassfileOutputStream(ClassType sourceclasstype, String classfilename, String classname, boolean isDirectBuild) {
      FileEntry sourcefile = sourceclasstype.getFile();
      String relativedirpath = sourceclasstype.getNamespace().getFullyQualifiedNameString().replace('.', File.separatorChar);
      if (relativedirpath.length() > 0) {
         relativedirpath = relativedirpath + File.separator;
      }

      Set<String> destinationpaths = this.model.filespace.getDestinationPaths(sourcefile);
      OutputStream stream = new CachedOutputStream(
         destinationpaths, relativedirpath, classfilename, sourcefile.getDiskVersion(), sourcefile, classname, isDirectBuild
      );
      return stream;
   }

   public OutputStream getDebugClassfileOutputStream(ClassType sourceclasstype, String classfilename, String classname, boolean isDirectBuild) {
      FileEntry sourcefile = sourceclasstype.getFile();
      String relativedirpath = sourceclasstype.getNamespace().getFullyQualifiedNameString().replace('.', File.separatorChar) + File.separator;
      Set<String> destinationpaths = this.model.filespace.getDestinationPaths(sourcefile);
      OutputStream stream = new CachedOutputStream(
         destinationpaths, relativedirpath, classfilename, sourcefile.getDiskVersion(), sourcefile, classname, isDirectBuild
      );
      return stream;
   }

   public OutputStream getSyntheticClassfileOutputStream(ClassType sourceclasstype, String classname, boolean isDirectBuild) {
      FileEntry sourcefile = sourceclasstype.getFile();
      Set<String> destinationpaths = this.model.filespace.getDestinationPaths(sourcefile);
      String classfilename = classname + ".class";

      for(String destinationpath : destinationpaths) {
         String filepath = destinationpath + File.separator + classfilename;
         if (!this.compiledfilechecksums.containsKey(filepath)) {
            OutputStream stream = new CachedOutputStream(destinationpaths, "", classfilename, 0L, sourcefile, classname, isDirectBuild);
            return stream;
         }
      }

      return null;
   }

   public void clear() {
      this.cachesize = 0;
      this.cachedfilecontents.clear();
      this.cachedfiledirs.clear();
      this.cacheddestinationpaths.clear();
      this.cachedsourcefileversions.clear();
      this.cachedfilesourcefiles.clear();
      this.cachedinvalidfiles.clear();
      this.cachedfilechecksums.clear();
      this.cachedfileclassnames.clear();
   }

   public void hotswap(Set<String> flushedSourcefilePaths, Hashtable<String, byte[]> flushedClasses) throws IOException {
      this.writeCache(false);
      Enumeration<? extends String> enumeration = this.cachedfilecontents.keys();

      while(enumeration.hasMoreElements()) {
         String filepath = enumeration.nextElement();
         long cachedsourceversion = this.cachedsourcefileversions.get(filepath);
         FileEntry sourcefile = this.cachedfilesourcefiles.get(filepath);
         long sourceversion = sourcefile.getDiskVersion();
         if (cachedsourceversion == 0L || cachedsourceversion == sourceversion) {
            FileContent content = this.cachedfilecontents.get(filepath);
            ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
            content.flushInto(bytesout);
            String className = this.cachedfileclassnames.get(filepath);
            flushedClasses.put(className, bytesout.toByteArray());
            flushedSourcefilePaths.add(sourcefile.getPathString());
         }
      }

      if (!this.destinationContainsJars()) {
         this.clear();
      }
   }

   public void build(boolean comporessJars) throws IOException {
      SetOfFileEntry resourceFiles = this.model.filespace.getResourceFiles();
      resourceFiles.DEFAULT_ITERATOR.init();

      while(resourceFiles.DEFAULT_ITERATOR.hasMoreElements()) {
         FileEntry file = resourceFiles.DEFAULT_ITERATOR.nextKey();
         this.addResourceFile(file, this.model.filespace.getResourcePackageName(file));
      }

      this.writeCache(comporessJars);
      if (!this.fastBuildMode) {
         Enumeration<String> enumeration = this.cachedfilesourcefiles.keys();

         while(enumeration.hasMoreElements()) {
            String filepath = enumeration.nextElement();
            if (filepath.endsWith(".class")
               && (filepath.indexOf(36) == -1 || filepath.lastIndexOf(36) < filepath.lastIndexOf(File.separatorChar))
               && new File(filepath).isFile()) {
               long version = new File(filepath).lastModified();
               this.classfileversions.put(filepath, version);
            }
         }
      }

      this.clear();
   }

   public void flush() throws IOException {
      if (!this.destinationContainsJars()) {
         this.writeCache(false);
         if (!this.fastBuildMode) {
            Enumeration<String> enumeration = this.cachedfilesourcefiles.keys();

            while(enumeration.hasMoreElements()) {
               String filepath = enumeration.nextElement();
               if (filepath.endsWith(".class")
                  && (filepath.indexOf(36) == -1 || filepath.lastIndexOf(36) < filepath.lastIndexOf(File.separatorChar))
                  && new File(filepath).isFile()) {
                  long version = new File(filepath).lastModified();
                  this.classfileversions.put(filepath, version);
               }
            }
         }

         this.clear();
      }
   }

   private void writeCache(boolean compressJars) throws IOException {
      IOException thrownIOException = null;
      ArrayList<String> filepaths = new ArrayList<>();
      Enumeration<? extends String> enumeration = this.cachedfilecontents.keys();

      while(enumeration.hasMoreElements()) {
         filepaths.add(enumeration.nextElement());
      }

      Collections.sort(filepaths, new Comparator<String>() {
         public int compare(String a, String b) {
            int alen = a.length() - 6;
            int blen = b.length() - 6;
            int length = alen < blen ? alen : blen;

            for(int i = 0; i < length; ++i) {
               char achar = a.charAt(i);
               char bchar = b.charAt(i);
               if (achar == '$' && achar != bchar) {
                  return -1;
               }

               if (bchar == '$' && achar != bchar) {
                  return 1;
               }

               if (achar == '+' && achar != bchar) {
                  return -1;
               }

               if (bchar == '+' && achar != bchar) {
                  return 1;
               }

               int diff = achar - bchar;
               if (diff < 0) {
                  return -1;
               }

               if (diff > 0) {
                  return 1;
               }
            }

            if (alen < blen) {
               return -1;
            } else {
               return alen > blen ? 1 : 0;
            }
         }
      });
      Set<String> destinationDirs = new HashSet<>();
      Set<String> createdDirs = new HashSet<>();

      for(String destinationpath : this.cacheddestinationpaths) {
         if (!new File(destinationpath).isFile()) {
            destinationDirs.add(destinationpath);

            for(String filepath : filepaths) {
               if (filepath.startsWith(destinationpath) && !this.cachedinvalidfiles.contains(filepath)) {
                  String dirpath = this.cachedfiledirs.get(filepath);
                  if (!createdDirs.contains(dirpath) && !new File(dirpath).exists()) {
                     new File(dirpath).mkdirs();
                  }

                  createdDirs.add(dirpath);
               }
            }
         }
      }

      for(String destinationpath : this.cacheddestinationpaths) {
         if (destinationDirs.contains(destinationpath)) {
            for(String filepath : filepaths) {
               if (filepath.startsWith(destinationpath) && !this.cachedinvalidfiles.contains(filepath)) {
                  try {
                     FileContent content = this.cachedfilecontents.get(filepath);
                     if (this.cachedsourcefileversions.containsKey(filepath)) {
                        long cachedsourceversion = this.cachedsourcefileversions.get(filepath);
                        long sourceversion = this.cachedfilesourcefiles.get(filepath).getDiskVersion();
                        if (cachedsourceversion == 0L || cachedsourceversion == sourceversion) {
                           if (this.verboseWrite) {
                              System.out.println("Writing " + filepath);
                           }

                           OutputStream stream = new FileOutputStream(filepath);
                           content.flushInto(stream);
                           stream.close();
                        }
                     }
                  } catch (IOException var49) {
                     thrownIOException = var49;
                  }
               }
            }

            enumeration = this.cachedsourcefileversions.keys();

            while(enumeration.hasMoreElements()) {
               String filepath = enumeration.nextElement();
               if (filepath.startsWith(destinationpath) && !this.cachedinvalidfiles.contains(filepath)) {
                  long version = this.cachedsourcefileversions.get(filepath);
                  if (version != 0L && new File(filepath).exists() && new File(filepath).lastModified() < version) {
                     if (this.verboseWrite) {
                        System.out.println("Touching " + filepath);
                     }

                     new File(filepath).setLastModified(version);
                  }
               }
            }
         } else {
            try {
               JarInputStream jarcompin = new JarInputStream(new FileInputStream(destinationpath));

               try {
                  while(true) {
                     JarEntry entry = jarcompin.getNextJarEntry();
                     if (entry == null) {
                        break;
                     }

                     String entrypath = destinationpath + entry.getName().replace('/', File.separatorChar);
                     if (!entrypath.endsWith(".class")
                        && this.cachedsourcefileversions.containsKey(entrypath)
                        && entry.getTime() / 2000L * 2000L >= this.cachedsourcefileversions.get(entrypath)) {
                        this.cachedfilecontents.remove(entrypath);
                     }

                     jarcompin.closeEntry();
                  }
               } finally {
                  jarcompin.close();
               }

               enumeration = this.cachedfilecontents.keys();

               while(enumeration.hasMoreElements()) {
                  String filepath = enumeration.nextElement();
                  if (filepath.startsWith(destinationpath)) {
                     if (this.verboseWrite) {
                        System.out.println("Creating " + destinationpath);
                     }

                     filepath = destinationpath.substring(0, destinationpath.length() - 1) + "~";
                     if (new File(filepath).exists()) {
                        new File(filepath).delete();
                     }

                     try {
                        if (!new File(destinationpath).isFile()) {
                           throw new IOException();
                        }

                        JarInputStream stream = new JarInputStream(new FileInputStream(destinationpath));
                        stream.close();
                     } catch (IOException var44) {
                        try {
                           JarOutputStream out = new JarOutputStream(new FileOutputStream(destinationpath), new Manifest());
                           out.close();
                        } catch (IOException var43) {
                        }
                     }

                     JarOutputStream jarout = new JarOutputStream(new FileOutputStream(filepath));
                     if (!compressJars) {
                        jarout.setLevel(0);
                     }

                     JarInputStream jarin = new JarInputStream(new FileInputStream(destinationpath));

                     try {
                        while(true) {
                           JarEntry entry = jarin.getNextJarEntry();
                           if (entry == null) {
                              break;
                           }

                           String entrypath = destinationpath + File.separator + entry.getName().replace('/', File.separatorChar);
                           if (!this.cachedfilecontents.containsKey(entrypath)) {
                              this.cachedfilecontents.remove(entrypath);
                              jarout.putNextEntry(entry);

                              while(true) {
                                 int c = jarin.read(this.copybuffer);
                                 if (c == -1) {
                                    jarout.closeEntry();
                                    break;
                                 }

                                 jarout.write(this.copybuffer, 0, c);
                              }
                           }

                           jarin.closeEntry();
                        }
                     } finally {
                        jarin.close();
                     }

                     try {
                        enumeration = this.cachedfilecontents.keys();

                        while(enumeration.hasMoreElements()) {
                           String filepathx = enumeration.nextElement();
                           if (filepathx.startsWith(destinationpath) && !this.cachedinvalidfiles.contains(filepathx)) {
                              FileContent content = this.cachedfilecontents.get(filepathx);
                              long cachedsourceversion = this.cachedsourcefileversions.get(filepathx);
                              long sourceversion = this.cachedfilesourcefiles.get(filepathx).getDiskVersion();
                              if (cachedsourceversion == 0L || cachedsourceversion == sourceversion) {
                                 if (this.verboseWrite) {
                                    System.out.println("Writing " + filepathx);
                                 }

                                 this.cachedsourcefileversions.remove(filepathx);
                                 String entryname = filepathx.substring(destinationpath.length() + 1, filepathx.length()).replace(File.separatorChar, '/');
                                 JarEntry entry = new JarEntry(entryname);
                                 entry.setTime(cachedsourceversion);
                                 jarout.putNextEntry(entry);
                                 content.flushInto(jarout);
                                 jarout.closeEntry();
                              }
                           }
                        }
                     } finally {
                        jarout.close();
                     }

                     if (!new File(destinationpath).delete()) {
                        throw new IOException("Delete of " + destinationpath + " failed.");
                     }

                     if (!new File(filepath).renameTo(new File(destinationpath))) {
                        throw new IOException("Rename from " + filepath + " to " + destinationpath + " failed.");
                     }
                     break;
                  }
               }
            } catch (IOException var48) {
               thrownIOException = var48;
            }
         }
      }

      if (thrownIOException != null) {
         throw thrownIOException;
      }
   }

   private boolean destinationContainsJars() {
      for(String destinationpath : this.cacheddestinationpaths) {
         if (!new File(destinationpath).isDirectory()) {
            return true;
         }
      }

      return false;
   }

   private class CachedFileContent implements FileContent {
      private byte[] bytes;
      private int pos;

      private CachedFileContent() {
      }

      @Override
      public void flushInto(OutputStream stream) throws IOException {
         stream.write(this.bytes, 0, this.pos);
      }

      public void fillWith(byte[] buf, int len) throws IOException {
         if (this.bytes == null || len > this.bytes.length) {
            this.bytes = new byte[len * 5 / 4];
         }

         System.arraycopy(buf, 0, this.bytes, 0, len);
         this.pos = len;
      }
   }

   private class CachedOutputStream extends OutputStream {
      private byte[] buffer;
      private int bufferpos;
      private long version;
      private FileEntry sourcefile;
      private String relativedirpath;
      private String classfilename;
      private Set<String> destinationpaths;
      private String classname;
      private boolean isDirectBuild;

      public CachedOutputStream(
         Set<String> destinationpaths,
         String relativedirpath,
         String classfilename,
         long version,
         FileEntry sourcefile,
         String classname,
         boolean isDirectBuild
      ) {
         if (Compiler.this.buffercache2 != null) {
            this.buffer = Compiler.this.buffercache2;
            Compiler.this.buffercache2 = null;
         } else {
            this.buffer = new byte[10000];
         }

         this.sourcefile = sourcefile;
         this.version = version;
         this.relativedirpath = relativedirpath;
         this.classfilename = classfilename;
         this.destinationpaths = destinationpaths;
         this.classname = classname;
         this.isDirectBuild = isDirectBuild;
      }

      @Override
      public void write(int b) {
         int newcount = this.bufferpos + 1;
         if (newcount > this.buffer.length) {
            byte[] newbuf = new byte[Math.max(this.buffer.length << 1, newcount)];
            System.arraycopy(this.buffer, 0, newbuf, 0, this.bufferpos);
            this.buffer = newbuf;
         }

         this.buffer[this.bufferpos] = (byte)b;
         this.bufferpos = newcount;
      }

      @Override
      public void write(byte[] b, int off, int len) {
         int newcount = this.bufferpos + len;
         if (newcount > this.buffer.length) {
            byte[] newbuf = new byte[Math.max(this.buffer.length << 1, newcount)];
            System.arraycopy(this.buffer, 0, newbuf, 0, this.bufferpos);
            this.buffer = newbuf;
         }

         System.arraycopy(b, off, this.buffer, this.bufferpos, len);
         this.bufferpos = newcount;
      }

      @Override
      public void close() throws IOException {
         long checksum = 0L;
         if (!this.isDirectBuild) {
            Compiler.this.crc32.reset();
            Compiler.this.crc32.update(this.buffer, 0, this.bufferpos);
            checksum = Compiler.this.crc32.getValue();
         }

         CachedFileContent content = null;

         for(String destinationpath : this.destinationpaths) {
            String filepath = destinationpath + File.separator + this.relativedirpath + this.classfilename;
            String dirpath = destinationpath + File.separator + this.relativedirpath;
            Compiler.this.cachedsourcefileversions.put(filepath, this.version);
            Compiler.this.cachedfilesourcefiles.put(filepath, this.sourcefile);
            Compiler.this.cacheddestinationpaths.add(destinationpath);
            if (this.isDirectBuild) {
               if (content == null) {
                  FileContent filecontent = Compiler.this.cachedfilecontents.get(filepath);
                  if (filecontent instanceof CachedFileContent) {
                     content = (CachedFileContent)filecontent;
                  }

                  if (content == null) {
                     content = Compiler.this.new CachedFileContent();
                  }

                  content.fillWith(this.buffer, this.bufferpos);
               }

               Compiler.this.cachesize += content.pos;
               Compiler.this.cachedfilecontents.put(filepath, content);
               Compiler.this.cachedfileclassnames.put(filepath, this.classname);
               Compiler.this.cachedfiledirs.put(filepath, dirpath);
            } else if (!Compiler.this.compiledfilechecksums.containsKey(filepath) || checksum != Compiler.this.compiledfilechecksums.get(filepath)) {
               Compiler.this.compiledfilechecksums.put(filepath, checksum);
               if (!this.ondisk(destinationpath)) {
                  if (!Compiler.this.cachedfilechecksums.containsKey(filepath) || checksum != Compiler.this.cachedfilechecksums.get(filepath)) {
                     if (content == null) {
                        FileContent filecontent = Compiler.this.cachedfilecontents.get(filepath);
                        if (filecontent instanceof CachedFileContent) {
                           content = (CachedFileContent)filecontent;
                        }

                        if (content == null) {
                           content = Compiler.this.new CachedFileContent();
                        }

                        content.fillWith(this.buffer, this.bufferpos);
                     }

                     Compiler.this.cachesize += content.pos;
                     Compiler.this.cachedfilecontents.put(filepath, content);
                     Compiler.this.cachedfileclassnames.put(filepath, this.classname);
                     Compiler.this.cachedfiledirs.put(filepath, dirpath);
                     Compiler.this.cachedfilechecksums.put(filepath, checksum);
                  }
               } else {
                  Compiler.this.cachedfilecontents.remove(filepath);
                  Compiler.this.cachedfilechecksums.remove(filepath);
                  if (filepath.endsWith(".class")
                     && (filepath.indexOf(36) == -1 || filepath.lastIndexOf(36) < filepath.lastIndexOf(File.separatorChar))
                     && new File(filepath).isFile()) {
                     long version = new File(filepath).lastModified();
                     Compiler.this.classfileversions.put(filepath, version);
                  }
               }
            }
         }

         if (Compiler.this.buffercache2 == null) {
            Compiler.this.buffercache2 = this.buffer;
         }

         this.buffer = null;
      }

      private boolean ondisk(String destinationpath) throws IOException {
         String filepath = destinationpath + File.separator + this.relativedirpath + this.classfilename;
         return new File(destinationpath).isDirectory() ? false : false;
      }
   }

   private interface FileContent {
      void flushInto(OutputStream var1) throws IOException;
   }

   private static class FileTreeWalker {
      private FileTreeWalker() {
      }

      protected void visitFile(String file) {
      }

      protected void visitDirectory(String directory) {
      }

      protected void visitDirectoryFinished(String directory) {
      }

      public void walk(String fileOrDirectory) {
         String name = fileOrDirectory.trim();
         if (name.endsWith(":") && name.indexOf("\\") == -1) {
            fileOrDirectory = name + "\\";
         }

         File entry = new File(fileOrDirectory);
         if (entry != null) {
            if (entry.isDirectory()) {
               if (fileOrDirectory != null) {
                  this.visitDirectory(fileOrDirectory);
               }
            } else if (entry.isFile() && fileOrDirectory != null) {
               this.visitFile(fileOrDirectory);
            }

            String[] files = entry.list();
            if (files != null) {
               for(int i = 0; i < files.length; ++i) {
                  this.walk(fileOrDirectory + File.separator + files[i]);
               }

               this.visitDirectoryFinished(fileOrDirectory);
            }
         }
      }
   }

   private class ResourceFileContent implements FileContent {
      final String filepath;

      public ResourceFileContent(String filepath) {
         this.filepath = filepath;
      }

      @Override
      public void flushInto(OutputStream stream) throws IOException {
         try {
            InputStream in = new BufferedInputStream(new FileInputStream(this.filepath));
            byte[] readbuffer;
            if (Compiler.this.buffercache1 != null) {
               readbuffer = Compiler.this.buffercache1;
               Compiler.this.buffercache1 = null;
            } else {
               readbuffer = new byte[10000];
            }

            int l;
            while((l = in.read(readbuffer)) != -1) {
               stream.write(readbuffer, 0, l);
            }

            in.close();
            Compiler.this.buffercache1 = readbuffer;
         } catch (IOException var5) {
         }
      }
   }
}
