package com.apkide.openapi.language.api;

import static java.io.File.separator;
import static java.io.File.separatorChar;
import static java.util.Objects.requireNonNull;

import com.apkide.common.FileNameMatcher;
import com.apkide.common.collections.FunctionOfIntInt;
import com.apkide.common.collections.FunctionOfIntLong;
import com.apkide.common.collections.MapOfIntLong;
import com.apkide.common.collections.OrderedMapOfIntInt;
import com.apkide.common.collections.SetOfInt;
import com.apkide.openapi.language.Language;
import com.apkide.openapi.language.api.callback.OpenFileCallback;
import com.apkide.openapi.language.api.callback.StopCallback;

import org.antlr.v4.runtime.tree.SyntaxTree;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;


public class FileSpace {
    private final IdentifierSpace myIdentifiers;
    private final OpenFileCallback myOpenFileCallback;
    private final StopCallback myStopCallback;
    private MapOfIntLong myIncludedEntryVersions;
    private OrderedMapOfIntInt myIncludes;
    private FileEntry myCurrentIncludeFile;
    private final SetOfInt myChangedIncludeFiles = new SetOfInt();
    private final SetOfInt myChangedFiles = new SetOfInt();
    private String myEncoding;
    private FileEntry[] myEntries;
    private int myEntryCount;
    private Language[] myLanguages;
    private final HashMap<Integer, Assembly> myAssemblyMap;
    private final HashMap<Language, List<String>> myFilePatterns;
    private final FunctionOfIntLong mySyntaxVersion;
    private FunctionOfIntInt myFileLanguages;
    private final FunctionOfIntInt myFileAssembles;
    private final RelationOfIntInt myReferences;
    private final OrderedMapOfIntInt myAssemblyReferences;
    private final SetOfFileEntry myRegisteredCheckedFiles;
    private final SetOfFileEntry myRegisteredSolutionFiles;
    private final SetOfFileEntry myRegisteredResourceFiles;
    private final SetOfFileEntry myCheckedFiles;
    private final SetOfFileEntry mySolutionFiles;
    private final HashMap<Integer, String> myResourcePackageNames;
    private boolean myPacked;
    protected char[] chars = new char[10000];
    protected byte[] bytes = new byte[10000];
    protected CRC32 crc32 = new CRC32();

    public FileSpace(IdentifierSpace identifiers, OpenFileCallback openFileCallback, StopCallback stopCallback) {
        myIdentifiers = identifiers;
        myOpenFileCallback = openFileCallback;
        myStopCallback = stopCallback;
        myEntries = new FileEntry[1000];
        myEntries[0] = new FileEntry(identifiers, this, openFileCallback, identifiers.get(""), null);
        myEntryCount = 1;
        mySyntaxVersion = new FunctionOfIntLong();
        myIncludedEntryVersions = new MapOfIntLong();
        myIncludes = new OrderedMapOfIntInt();
        myFileLanguages = new FunctionOfIntInt();
        myFileAssembles = new FunctionOfIntInt();
        myReferences = new RelationOfIntInt();
        myAssemblyReferences = new OrderedMapOfIntInt();
        myRegisteredCheckedFiles = new SetOfFileEntry(this);
        myRegisteredSolutionFiles = new SetOfFileEntry(this);
        myRegisteredResourceFiles = new SetOfFileEntry(this);
        myCheckedFiles = new SetOfFileEntry(this);
        mySolutionFiles = new SetOfFileEntry(this);
        myAssemblyMap = new HashMap<>();
        myResourcePackageNames = new HashMap<>();
        myFilePatterns = new HashMap<>();
    }

    protected void configureLanguages(Language[] languages) {
        List<Language> list = new ArrayList<>();
        for (Language language : languages) {
            if (!list.contains(language)) {
                list.add(language);
                myFilePatterns.put(language, List.of(language.getDefaultFilePatterns()));
            }
        }

        myLanguages = list.toArray(new Language[0]);
    }

    public void close() {
        for (Language language : myLanguages) {
            if (language.isArchiveFileSupported())
                language.closeArchiveFile();
        }
    }

    protected void update(boolean updateFileSpace) {
        if (updateFileSpace)
            synchronize();

        myChangedIncludeFiles.clear();
        myIncludedEntryVersions.DEFAULT_ITERATOR.init();
        while (myIncludedEntryVersions.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry entry = getFileEntry(myIncludedEntryVersions.DEFAULT_ITERATOR.nextKey());
            long version = myIncludedEntryVersions.DEFAULT_ITERATOR.nextValue();
            if (version != entry.getVersion())
                myChangedIncludeFiles.put(entry.getID());
        }

        myChangedIncludeFiles.DEFAULT_ITERATOR.init();
        while (myChangedIncludeFiles.DEFAULT_ITERATOR.hasMoreElements()) {
            myIncludedEntryVersions.remove(myChangedIncludeFiles.DEFAULT_ITERATOR.nextKey());
        }

        myChangedFiles.clear();
        myIncludes.DEFAULT_ITERATOR.init();
        while (myIncludes.DEFAULT_ITERATOR.hasMoreElements()) {
            FileEntry entry = getFileEntry(myIncludes.DEFAULT_ITERATOR.nextValue());
            FileEntry file = getFileEntry(myIncludes.DEFAULT_ITERATOR.nextKey());
            if (myChangedIncludeFiles.contains(entry.getID()))
                myChangedFiles.put(file.getID());
        }

        myChangedFiles.DEFAULT_ITERATOR.init();
        while (myChangedFiles.DEFAULT_ITERATOR.hasMoreElements()) {
            int nextKey = myChangedFiles.DEFAULT_ITERATOR.nextKey();
            myIncludes.remove(nextKey);
            mySyntaxVersion.remove(nextKey);
        }
    }

    private void synchronize() {
        for (FileEntry entry : myEntries) {
            if (entry != null)
                entry.synchronize();
        }
        myPacked = false;
    }


    protected int getLanguageID(Language language) {
        for (int i = 0; i < myLanguages.length; ++i) {
            if (language == myLanguages[i])
                return i;
        }
        return -1;
    }

    protected Language getLanguage(int id) {
        return id < 0 ? null : myLanguages[id];
    }

    public Language[] getLanguages() {
        return myLanguages;
    }

    protected int getLanguage(Language language) {
        for (int i = 0; i < myLanguages.length; i++) {
            if (myLanguages[i] == language)
                return i;
        }
        return -1;
    }

    protected int getAssembly(FileEntry file) {
        pack();
        return myFileAssembles.get(file.getID());
    }

    protected String getRootNamespace(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).rootNamespace;
        }
        return "";
    }

    protected List<String> getDefaultImports(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).defaultImports;
        }
        return Collections.emptyList();
    }

    protected List<String> getDefinedSymbols(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).definedSymbols;
        }
        return Collections.emptyList();
    }

    protected void configureEncoding(String encoding) {
        myEncoding = encoding;
    }

    protected void configureReference(int assembly1, int assembly2) {
        myReferences.put(assembly1, assembly2);
        myAssemblyReferences.insert(assembly1, assembly2);
    }

    protected void configureAssembly(int assembly,
                                     String projectAssembly,
                                     String projectFilePath,
                                     String rootNamespace,
                                     String configuration,
                                     List<String> defaultImports,
                                     List<String> definedSymbols,
                                     List<String> tagLibPaths,
                                     String destinationPath,
                                     String targetVersion,
                                     boolean isExternal,
                                     boolean isDebug,
                                     boolean isRelease
    ) {
        myAssemblyMap.put(assembly, new Assembly(
                projectAssembly,
                projectFilePath,
                rootNamespace,
                configuration,
                defaultImports,
                definedSymbols,
                tagLibPaths,
                destinationPath,
                targetVersion,
                isExternal,
                isDebug,
                isRelease));
    }

    protected void configureFile(FileEntry file, int assembly, Language lang, boolean checked) {
        for (FileEntry fileEntry = file; !fileEntry.isRootDirectory() && !new File(fileEntry.getPathString()).isDirectory(); fileEntry = fileEntry.getParentDirectory()) {
            myFileLanguages.put(fileEntry.getID(), getLanguage(lang));
        }

        myFileLanguages.put(file.getID(), getLanguage(lang));
        myFileAssembles.put(file.getID(), assembly);
        if (checked)
            myRegisteredCheckedFiles.put(file);

        myRegisteredSolutionFiles.put(file);
    }

    protected void configureResourceFile(FileEntry file, int assembly, String packageName) {
        myFileAssembles.put(file.getID(), assembly);
        if (packageName != null)
            myResourcePackageNames.put(file.getID(), packageName);

        myRegisteredResourceFiles.put(file);
    }

    protected void configure() {
        myFileAssembles.clear();
        myReferences.clear();
        myAssemblyReferences.clear();
        myRegisteredCheckedFiles.clear();
        myRegisteredSolutionFiles.clear();
        myRegisteredResourceFiles.clear();
        myCheckedFiles.clear();
        mySolutionFiles.clear();
        myAssemblyMap.clear();
        myResourcePackageNames.clear();
        myFilePatterns.clear();
        synchronize();
    }

    public List<String> getAssemblyTagLibPaths(int assembly) {
        if (myAssemblyMap.containsKey(assembly)) {
            return requireNonNull(myAssemblyMap.get(assembly)).tagLibPaths;
        }
        return Collections.emptyList();
    }

    public FileEntry getAssemblyHome(int assembly) {
        return getEntry(requireNonNull(myAssemblyMap.get(assembly)).projectFilePath).getParentDirectory();
    }

    public String getAssemblyName(int assembly) {
        if (myAssemblyMap.containsKey(assembly)) {
            return requireNonNull(myAssemblyMap.get(assembly)).assembly;
        }
        return "";
    }

    public String getTargetVersion(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).targetVersion;
        }
        return "";
    }

    public boolean isExternally(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).isExternal;
        }
        return false;
    }

    public boolean isDebug(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).isDebug;
        }
        return false;
    }

    public boolean isRelease(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).isRelease;
        }
        return false;
    }

    private String getConfiguration(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).configuration;
        }
        return "";
    }

    public String getDestinationPath(FileEntry file) {
        if (myAssemblyMap.containsKey(getAssembly(file))) {
            return requireNonNull(myAssemblyMap.get(getAssembly(file))).destinationPath;
        }
        return "";
    }

    protected boolean hasHigherPriority(FileEntry file, FileEntry referedFile1, FileEntry referedFile2) {
        int assembly1 = myFileAssembles.get(referedFile1.getID());
        int assembly2 = myFileAssembles.get(referedFile2.getID());
        int assembly = myFileAssembles.get(file.getID());
        myAssemblyReferences.DEFAULT_ITERATOR.init(assembly);

        while (myAssemblyReferences.DEFAULT_ITERATOR.hasMoreElements()) {
            int referedAssembly = myAssemblyReferences.DEFAULT_ITERATOR.nextValue();
            if (referedAssembly == assembly1)
                return true;

            if (referedAssembly == assembly2)
                return false;
        }

        return false;
    }

    protected boolean isReferableFrom(FileEntry file, FileEntry referingFile) {
        pack();
        return myReferences.contains(myFileAssembles.get(referingFile.getID()), myFileAssembles.get(file.getID()));
    }

    protected boolean isReferableFrom(int assembly, int referingAssembly) {
        pack();
        return myReferences.contains(referingAssembly, assembly);
    }

    private void pack() {
        if (!myPacked) {
            myPacked = true;
            myRegisteredCheckedFiles.DEFAULT_ITERATOR.init();
            while (myRegisteredCheckedFiles.DEFAULT_ITERATOR.hasMoreElements()) {
                FileEntry file = myRegisteredCheckedFiles.DEFAULT_ITERATOR.nextKey();
                pack(file, myFileAssembles.get(file.getID()),
                        myFileLanguages.get(file.getID()),
                        myCheckedFiles);
            }

            myRegisteredSolutionFiles.DEFAULT_ITERATOR.init();
            while (myRegisteredSolutionFiles.DEFAULT_ITERATOR.hasMoreElements()) {
                FileEntry file = myRegisteredSolutionFiles.DEFAULT_ITERATOR.nextKey();
                pack(file, myFileAssembles.get(file.getID()),
                        myFileLanguages.get(file.getID()),
                        mySolutionFiles);
            }
        }
    }

    private void pack(FileEntry file, int assembly, int codeModel, SetOfFileEntry files) {
        if (file.isDirectory()) {
            int count = file.getChildCount();

            for (int i = 0; i < count; ++i) {
                FileEntry child = file.getChildEntry(i);
                pack(child, assembly, codeModel, files);
            }
        } else {
            myFileAssembles.put(file.getID(), assembly);
            myFileLanguages.put(file.getID(), codeModel);
            files.put(file);
        }
    }

    public String getResourcePackageName(FileEntry file) {
        if (myResourcePackageNames.containsKey(file.getID())) {
            return myResourcePackageNames.get(file.getID());
        }
        return "";
    }

    public Language getLanguage(FileEntry file) {
        if (myFileLanguages.contains(file.getID()))
            return getLanguage(myFileLanguages.get(file.getID()));

        pack();
        Language lang = getLanguage(myFileLanguages.get(file.getID()));
        if (lang == null) {
            String name = file.getFullName();
            for (Language language : myLanguages) {
                if (myFilePatterns.containsKey(language)) {
                    for (String filePattern : requireNonNull(myFilePatterns.get(language))) {
                        if (FileNameMatcher.get() != null && FileNameMatcher.get().match(name, filePattern)) {
                            myFileLanguages.put(file.getID(), getLanguage(language));
                            return language;
                        }
                    }
                }
            }
            myFileLanguages.put(file.getID(), -1);
            return null;
        }
        return lang;
    }

    protected boolean isCheckedSolutionFile(FileEntry file) {
        pack();
        return myCheckedFiles.contains(file);
    }

    protected boolean isSolutionFile(FileEntry file) {
        pack();
        return mySolutionFiles.contains(file);
    }

    public SetOfFileEntry getCheckedSolutionFiles() {
        pack();
        return myCheckedFiles;
    }

    public SetOfFileEntry getSolutionFiles() {
        pack();
        return mySolutionFiles;
    }

    public SetOfFileEntry getResourceFiles() {
        pack();
        return myRegisteredResourceFiles;
    }

    public int getID(FileEntry file) {
        return file == null ? -1 : file.getID();
    }

    public FileEntry getFileEntry(int id) {
        return id == -1 ? null : myEntries[id];
    }

    public boolean checkSynchronized() throws InterruptedException {
        for (int i = 0; i < myEntryCount; ++i) {
            FileEntry entry = myEntries[i];
            if (!entry.isSynchronized() && entry.getLanguage() != null)
                return false;

            if (myStopCallback.stopAsyncSynchronize())
                throw new InterruptedException();
        }

        return true;
    }

    public FileEntry getRootDirectory() {
        return myEntries[0];
    }

    public FileEntry getEntry(String path) {
        while (path.endsWith(separator)) {
            path = path.substring(0, path.length() - 1);
        }

        while (path.startsWith(separator)) {
            path = path.substring(1);
        }

        FileEntry entry = getRootDirectory();
        int oldpos = 0;

        int pos = path.indexOf(separatorChar, oldpos);
        while (pos != -1) {
            String name = path.substring(oldpos, pos);
            if (!name.equals(".")) {
                if (name.equals("..") && getRootDirectory() != entry)
                    entry = entry.getParentDirectory();
                else
                    entry = entry.getEntry(name);
            }

            oldpos = pos + 1;

            while (path.charAt(oldpos) == separatorChar) {
                ++oldpos;
            }
            pos = path.indexOf(separatorChar, oldpos);
        }

        return entry.getEntry(path.substring(oldpos));
    }

    protected String getEncoding() {
        return myEncoding;
    }

    protected int declareEntry(FileEntry e) {
        if (myEntryCount >= myEntries.length) {
            FileEntry[] entities = new FileEntry[myEntries.length * 2 + 1];
            System.arraycopy(myEntries, 0, entities, 0, myEntries.length);
            myEntries = entities;
        }

        myEntries[myEntryCount] = e;
        return myEntryCount++;
    }

    protected void fileIncluded(FileEntry entry) {
        if (myCurrentIncludeFile != null && myCurrentIncludeFile != entry) {
            myIncludedEntryVersions.put(entry.getID(), entry.getVersion());
            myIncludes.insert(myCurrentIncludeFile.getID(), entry.getID());
        }
    }

    protected long getSyntaxVersion(FileEntry file) {
        Language language = file.getLanguage();
        if (language != null) {
            if (mySyntaxVersion.contains(file.getID())) {
                return mySyntaxVersion.get(file.getID());
            }

            if (!myIncludes.contains(file.getID())) {
                myIncludes.put(file.getID(), file.getID());
                myIncludedEntryVersions.put(file.getID(), file.getVersion());
                myCurrentIncludeFile = file;

                language.processVersion(file);

                myCurrentIncludeFile = null;

                long version = 0L;
                myIncludes.DEFAULT_ITERATOR.init(file.getID());

                while (myIncludes.DEFAULT_ITERATOR.hasMoreElements()) {
                    FileEntry includedFile = getFileEntry(myIncludes.DEFAULT_ITERATOR.nextValue());
                    version = version * 37L ^ includedFile.getVersion();
                }

                return version;
            }
        }
        return file.getVersion();
    }


    public SyntaxTree getSyntaxTree(FileEntry file, Language language) {
        return getSyntaxTree(file, language, true, true, true);
    }

    public SyntaxTree getSyntaxTree(FileEntry file, Language language, boolean errors, boolean parseCode, boolean parseComments) {
        return createSyntaxTree(file, language, errors, parseCode, parseComments);
    }

    protected SyntaxTree createSyntaxTree(FileEntry file, Language language, boolean errors, boolean parseCode, boolean parseComments) {
        try {
            Reader reader = file.getReader();
            final SyntaxTree[] syntaxTree = new SyntaxTree[1];
            language.createSyntaxTree(reader, file, file.getSyntaxVersion(), result -> syntaxTree[0]=result, errors,parseCode,parseComments);
            reader.close();
            return syntaxTree[0];
        } catch (IOException ignored) {
        }
        return null;
    }

    protected Reader createBomReader(InputStream inStream, String fallbackEncoding) throws IOException {
        int BOM_LENGTH = 4;
        PushbackInputStream pbInputStream = new PushbackInputStream(inStream, BOM_LENGTH);
        byte[] bomBytes = new byte[BOM_LENGTH];

        int totalCount;
        int count;
        for (totalCount = 0; totalCount < BOM_LENGTH; totalCount += count) {
            count = pbInputStream.read(bomBytes, totalCount, BOM_LENGTH - totalCount);
            if (count == -1) {
                break;
            }
        }

        String encoding = null;
        if (totalCount == BOM_LENGTH) {
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

}
