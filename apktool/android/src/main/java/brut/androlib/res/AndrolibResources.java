/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib.res;

import static brut.android.BrutContext.getString;

import com.apktool.android.R;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import brut.android.BrutContext;
import brut.androlib.AndrolibException;
import brut.androlib.err.CantFindFrameworkResException;
import brut.androlib.meta.MetaInfo;
import brut.androlib.meta.PackageInfo;
import brut.androlib.meta.VersionInfo;
import brut.androlib.options.BuildOptions;
import brut.androlib.res.data.ResConfigFlags;
import brut.androlib.res.data.ResPackage;
import brut.androlib.res.data.ResResSpec;
import brut.androlib.res.data.ResResource;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.data.ResValuesFile;
import brut.androlib.res.decoder.ARSCDecoder;
import brut.androlib.res.decoder.ARSCDecoder.ARSCData;
import brut.androlib.res.decoder.ARSCDecoder.FlagsOffset;
import brut.androlib.res.decoder.AXmlResourceParser;
import brut.androlib.res.decoder.AndroidManifestResourceParser;
import brut.androlib.res.decoder.Res9patchStreamDecoder;
import brut.androlib.res.decoder.ResAttrDecoder;
import brut.androlib.res.decoder.ResFileDecoder;
import brut.androlib.res.decoder.ResRawStreamDecoder;
import brut.androlib.res.decoder.ResStreamDecoderContainer;
import brut.androlib.res.decoder.XmlPullStreamDecoder;
import brut.androlib.res.util.ExtMXSerializer;
import brut.androlib.res.util.ExtXmlSerializer;
import brut.androlib.res.xml.ResValuesXmlSerializable;
import brut.androlib.res.xml.ResXmlPatcher;
import brut.common.BrutException;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.ExtFile;
import brut.directory.FileDirectory;
import brut.directory.ZipUtils;
import brut.util.AaptManager;
import brut.util.Duo;
import brut.util.Jar;
import brut.util.Logger;
import brut.util.OS;
import brut.util.OSDetection;

final public class AndrolibResources {
    private final static Logger LOGGER = Logger.getInstance(AndrolibResources.class.getName());
    private final static String[] IGNORED_PACKAGES = new String[]{
            "android", "com.htc", "com.lge", "com.lge.internal", "yi", "flyme", "air.com.adobe.appentry",
            "FFFFFFFFFFFFFFFFFFFFFF"};
    // TODO: dirty static hack. I have to refactor decoding mechanisms.
    public static boolean sKeepBroken = false;
    public BuildOptions buildOptions;
    private File mFrameworkDirectory = null;
    private ExtFile mFramework = null;
    private String mMinSdkVersion = null;
    private String mMaxSdkVersion = null;
    private String mTargetSdkVersion = null;
    private String mVersionCode = null;
    private String mVersionName = null;
    private String mPackageRenamed = null;
    private String mPackageId = null;
    private boolean mSharedLibrary = false;
    private boolean mSparseResources = false;

    public ResTable getResTable(ExtFile apkFile) throws AndrolibException {
        return getResTable(apkFile, true);
    }

    public ResTable getResTable(ExtFile apkFile, boolean loadMainPkg)
            throws AndrolibException {
        ResTable resTable = new ResTable(this);
        if (loadMainPkg) {
            loadMainPkg(resTable, apkFile);
        }
        return resTable;
    }

    public ResPackage loadMainPkg(ResTable resTable, ExtFile apkFile)
            throws AndrolibException {
        LOGGER.info(getString(R.string.androlibresources_loading_resource_table));
        ResPackage[] pkgs = getResPackagesFromApk(apkFile, resTable, sKeepBroken);
        ResPackage pkg;

        switch (pkgs.length) {
            case 1:
                pkg = pkgs[0];
                break;
            case 2:
                LOGGER.warning(getString(R.string.androlibresources_skipping_package_group) + pkgs[0].getName());
                pkg = pkgs[1];
                break;
            default:
                pkg = selectPkgWithMostResSpecs(pkgs);
                break;
        }

        if (pkg == null) {
            throw new AndrolibException(getString(R.string.androlibresources_arsc_file_found));
        }

        resTable.addPackage(pkg, true);
        return pkg;
    }

    public ResPackage selectPkgWithMostResSpecs(ResPackage[] pkgs) {
        int id = 0;
        int value = 0;
        int index = 0;

        for (int i = 0; i < pkgs.length; i++) {
            ResPackage resPackage = pkgs[i];
            if (resPackage.getResSpecCount() > value && !resPackage.getName().equalsIgnoreCase("android")) {
                value = resPackage.getResSpecCount();
                id = resPackage.getId();
                index = i;
            }
        }

        // if id is still 0, we only have one pkgId which is "android" -> 1
        return (id == 0) ? pkgs[0] : pkgs[index];
    }

    public ResPackage loadFrameworkPkg(ResTable resTable, int id, String frameTag)
            throws AndrolibException {
        File apk = getFrameworkApk(id, frameTag);

        LOGGER.info(getString(R.string.androlibresources_loading_resource_table_from_file) + apk);
        mFramework = new ExtFile(apk);
        ResPackage[] pkgs = getResPackagesFromApk(mFramework, resTable, true);

        ResPackage pkg;
        if (pkgs.length > 1) {
            pkg = selectPkgWithMostResSpecs(pkgs);
        } else if (pkgs.length == 0) {
            throw new AndrolibException(getString(R.string.androlibresources_arsc_files_with_zero));
        } else {
            pkg = pkgs[0];
        }

        if (pkg.getId() != id) {
            throw new AndrolibException(getString(R.string.androlibresources_expected_pkg_of_id) + id + getString(R.string.androlibresources_got) + pkg.getId());
        }

        resTable.addPackage(pkg, false);
        return pkg;
    }

    public void decodeManifest(ResTable resTable, ExtFile apkFile, File outDir)
            throws AndrolibException {

        Duo<ResFileDecoder, AXmlResourceParser> duo = getManifestFileDecoder(false);
        ResFileDecoder fileDecoder = duo.m1;

        // Set ResAttrDecoder
        duo.m2.setAttrDecoder(new ResAttrDecoder());
        ResAttrDecoder attrDecoder = duo.m2.getAttrDecoder();

        // Fake ResPackage
        attrDecoder.setCurrentPackage(new ResPackage(resTable, 0, null));

        Directory inApk, out;
        try {
            inApk = apkFile.getDirectory();
            out = new FileDirectory(outDir);

            LOGGER.info(getString(R.string.androlibresources_decoding_androidmanifest_with_only_franework_resources));
            fileDecoder.decodeManifest(inApk, "AndroidManifest.xml", out, "AndroidManifest.xml");

        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void adjustPackageManifest(ResTable resTable, String filePath)
            throws AndrolibException {

        // compare resources.arsc package name to the one present in AndroidManifest
        ResPackage resPackage = resTable.getCurrentResPackage();
        String pkgOriginal = resPackage.getName();
        mPackageRenamed = resTable.getPackageRenamed();

        resTable.setPackageId(resPackage.getId());
        resTable.setPackageOriginal(pkgOriginal);

        // 1) Check if pkgOriginal === mPackageRenamed
        // 2) Check if pkgOriginal is ignored via IGNORED_PACKAGES
        if (pkgOriginal.equalsIgnoreCase(mPackageRenamed) || (Arrays.asList(IGNORED_PACKAGES).contains(pkgOriginal))) {
            LOGGER.info(getString(R.string.androlibresources_regular_manifest_package));
        } else {
            LOGGER.info(getString(R.string.androlibresources_renamed_manifest_package_found) + mPackageRenamed + getString(R.string.androlibresources_with) + pkgOriginal);
            ResXmlPatcher.renameManifestPackage(new File(filePath), pkgOriginal);
        }
    }

    public void decodeManifestWithResources(ResTable resTable, ExtFile apkFile, File outDir)
            throws AndrolibException {

        Duo<ResFileDecoder, AXmlResourceParser> duo = getManifestFileDecoder(true);
        ResFileDecoder fileDecoder = duo.m1;
        ResAttrDecoder attrDecoder = duo.m2.getAttrDecoder();

        attrDecoder.setCurrentPackage(resTable.listMainPackages().iterator().next());

        Directory inApk, in = null, out;
        try {
            inApk = apkFile.getDirectory();
            out = new FileDirectory(outDir);
            LOGGER.info(getString(R.string.androlibresources_decoding_androidmanifest_with_resources));

            fileDecoder.decodeManifest(inApk, "AndroidManifest.xml", out, "AndroidManifest.xml");

            // Remove versionName / versionCode (aapt API 16)
            if (!resTable.getAnalysisMode()) {

                // check for a mismatch between resources.arsc package and the package listed in AndroidManifest
                // also remove the android::versionCode / versionName from manifest for rebuild
                // this is a required change to prevent aapt warning about conflicting versions
                // it will be passed as a parameter to aapt like "--min-sdk-version" via apktool.yml
                adjustPackageManifest(resTable, outDir.getAbsolutePath() + File.separator + "AndroidManifest.xml");

                ResXmlPatcher.removeManifestVersions(new File(
                        outDir.getAbsolutePath() + File.separator + "AndroidManifest.xml"));

                mPackageId = String.valueOf(resTable.getPackageId());
            }
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void decode(ResTable resTable, ExtFile apkFile, File outDir)
            throws AndrolibException {
        Duo<ResFileDecoder, AXmlResourceParser> duo = getResFileDecoder();
        ResFileDecoder fileDecoder = duo.m1;
        ResAttrDecoder attrDecoder = duo.m2.getAttrDecoder();

        attrDecoder.setCurrentPackage(resTable.listMainPackages().iterator().next());
        Directory inApk, in = null, out;

        try {
            out = new FileDirectory(outDir);

            inApk = apkFile.getDirectory();
            out = out.createDir("res");
            if (inApk.containsDir("res")) {
                in = inApk.getDir("res");
            }
            if (in == null && inApk.containsDir("r")) {
                in = inApk.getDir("r");
            }
            if (in == null && inApk.containsDir("R")) {
                in = inApk.getDir("R");
            }
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }

        ExtMXSerializer xmlSerializer = getResXmlSerializer();
        for (ResPackage pkg : resTable.listMainPackages()) {
            attrDecoder.setCurrentPackage(pkg);

            LOGGER.info(getString(R.string.androlibresources_decoding_file_resources));
            for (ResResource res : pkg.listFiles()) {
                fileDecoder.decode(res, in, out);
            }

            LOGGER.info(getString(R.string.androlibresources_decoding_values_xmls));
            for (ResValuesFile valuesFile : pkg.listValuesFiles()) {
                generateValuesFile(valuesFile, out, xmlSerializer);
            }
            generatePublicXml(pkg, out, xmlSerializer);
        }

        AndrolibException decodeError = duo.m2.getFirstError();
        if (decodeError != null) {
            throw decodeError;
        }
    }

    public void setSdkInfo(Map<String, String> map) {
        if (map != null) {
            mMinSdkVersion = map.get("minSdkVersion");
            mTargetSdkVersion = map.get("targetSdkVersion");
            mMaxSdkVersion = map.get("maxSdkVersion");
        }
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        if (versionInfo != null) {
            mVersionCode = versionInfo.versionCode;
            mVersionName = versionInfo.versionName;
        }
    }

    public void setPackageRenamed(PackageInfo packageInfo) {
        if (packageInfo != null) {
            mPackageRenamed = packageInfo.renameManifestPackage;
        }
    }

    public void setPackageId(PackageInfo packageInfo) {
        if (packageInfo != null) {
            mPackageId = packageInfo.forcedPackageId;
        }
    }

    public void setSharedLibrary(boolean flag) {
        mSharedLibrary = flag;
    }

    public void setSparseResources(boolean flag) {
        mSparseResources = flag;
    }

    public String checkTargetSdkVersionBounds() {
        int target = mapSdkShorthandToVersion(mTargetSdkVersion);

        int min = (mMinSdkVersion != null) ? mapSdkShorthandToVersion(mMinSdkVersion) : 0;
        int max = (mMaxSdkVersion != null) ? mapSdkShorthandToVersion(mMaxSdkVersion) : target;

        target = Math.min(max, target);
        target = Math.max(min, target);
        return Integer.toString(target);
    }

    private File createDoNotCompressExtensionsFile(BuildOptions buildOptions) throws AndrolibException {
        if (buildOptions.doNotCompress == null || buildOptions.doNotCompress.isEmpty()) {
            return null;
        }

        File doNotCompressFile;
        try {
            doNotCompressFile = File.createTempFile("APKTOOL", null);
            doNotCompressFile.deleteOnExit();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(doNotCompressFile));
            for (String extension : buildOptions.doNotCompress) {
                fileWriter.write(extension);
                fileWriter.newLine();
            }
            fileWriter.close();

            return doNotCompressFile;
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        }
    }

    private void aapt2Package(File apkFile, File manifest, File resDir, File rawDir, File assetDir, File[] include,
                              List<String> cmd, boolean customAapt)
            throws AndrolibException {

        List<String> compileCommand = new ArrayList<>(cmd);
        File resourcesZip = null;

        if (resDir != null) {
            File buildDir = new File(resDir.getParent(), "build");
            resourcesZip = new File(buildDir, "resources.zip");
        }

        if (resDir != null && !resourcesZip.exists()) {

            // Compile the files into flat arsc files
            cmd.add("compile");

            cmd.add("--dir");
            cmd.add(resDir.getAbsolutePath());

            // Treats error that used to be valid in aapt1 as warnings in aapt2
            cmd.add("--legacy");

            File buildDir = new File(resDir.getParent(), "build");
            resourcesZip = new File(buildDir, "resources.zip");

            cmd.add("-o");
            cmd.add(resourcesZip.getAbsolutePath());

            if (buildOptions.verbose) {
                cmd.add("-v");
            }

            if (buildOptions.noCrunch) {
                cmd.add("--no-crunch");
            }

            try {
                OS.exec(cmd.toArray(new String[0]));
                LOGGER.verbose(getString(R.string.androlibresources_aapt2_compile_command_ran));
                LOGGER.verbose(cmd.toString());
            } catch (BrutException ex) {
                throw new AndrolibException(ex);
            }
        }

        if (manifest == null) {
            return;
        }

        // Link them into the final apk, reusing our old command after clearing for the aapt2 binary
        cmd = new ArrayList<>(compileCommand);
        cmd.add("link");

        cmd.add("-o");
        cmd.add(apkFile.getAbsolutePath());

        if (mPackageId != null && !mSharedLibrary) {
            cmd.add("--package-id");
            cmd.add(mPackageId);
        }

        if (mSharedLibrary) {
            cmd.add("--shared-lib");
        }

        if (mMinSdkVersion != null) {
            cmd.add("--min-sdk-version");
            cmd.add(mMinSdkVersion);
        }

        if (mTargetSdkVersion != null) {
            cmd.add("--target-sdk-version");
            cmd.add(checkTargetSdkVersionBounds());
        }

        if (mPackageRenamed != null) {
            cmd.add("--rename-manifest-package");
            cmd.add(mPackageRenamed);

            cmd.add("--rename-instrumentation-target-package");
            cmd.add(mPackageRenamed);
        }

        if (mVersionCode != null) {
            cmd.add("--version-code");
            cmd.add(mVersionCode);
        }

        if (mVersionName != null) {
            cmd.add("--version-name");
            cmd.add(mVersionName);
        }

        // Disable automatic changes
        cmd.add("--no-auto-version");
        cmd.add("--no-version-vectors");
        cmd.add("--no-version-transitions");
        cmd.add("--no-resource-deduping");

        cmd.add("--allow-reserved-package-id");

        if (mSparseResources) {
            cmd.add("--enable-sparse-encoding");
        }

        if (buildOptions.isFramework) {
            cmd.add("-x");
        }

        if (buildOptions.doNotCompress != null && !customAapt) {
            // Use custom -e option to avoid limits on commandline length.
            // Can only be used when custom aapt binary is not used.
            String extensionsFilePath = createDoNotCompressExtensionsFile(buildOptions).getAbsolutePath();
            cmd.add("-e");
            cmd.add(extensionsFilePath);
        } else if (buildOptions.doNotCompress != null) {
            for (String file : buildOptions.doNotCompress) {
                cmd.add("-0");
                cmd.add(file);
            }
        }

        if (!buildOptions.resourcesAreCompressed) {
            cmd.add("-0");
            cmd.add("arsc");
        }

        if (include != null) {
            for (File file : include) {
                cmd.add("-I");
                cmd.add(file.getPath());
            }
        }

        cmd.add("--manifest");
        cmd.add(manifest.getAbsolutePath());

        if (assetDir != null) {
            cmd.add("-A");
            cmd.add(assetDir.getAbsolutePath());
        }

        if (rawDir != null) {
            cmd.add("-R");
            cmd.add(rawDir.getAbsolutePath());
        }

        if (buildOptions.verbose) {
            cmd.add("-v");
        }

        if (resourcesZip != null) {
            cmd.add(resourcesZip.getAbsolutePath());
        }

        try {
            OS.exec(cmd.toArray(new String[0]));
            LOGGER.verbose(getString(R.string.androlibresources_aapt2_link_command_ran));
            LOGGER.verbose(cmd.toString());
        } catch (BrutException ex) {
            throw new AndrolibException(ex);
        }
    }

    private void aapt1Package(File apkFile, File manifest, File resDir, File rawDir, File assetDir, File[] include,
                              List<String> cmd, boolean customAapt)
            throws AndrolibException {

        cmd.add("p");

        if (buildOptions.verbose) { // output aapt verbose
            cmd.add("-v");
        }
        if (buildOptions.updateFiles) {
            cmd.add("-u");
        }
        if (buildOptions.debugMode) { // inject debuggable="true" into manifest
            cmd.add("--debug-mode");
        }
        if (buildOptions.noCrunch) {
            cmd.add("--no-crunch");
        }
        // force package id so that some frameworks build with correct id
        // disable if user adds own aapt (can't know if they have this feature)
        if (mPackageId != null && !customAapt && !mSharedLibrary) {
            cmd.add("--forced-package-id");
            cmd.add(mPackageId);
        }
        if (mSharedLibrary) {
            cmd.add("--shared-lib");
        }
        if (mMinSdkVersion != null) {
            cmd.add("--min-sdk-version");
            cmd.add(mMinSdkVersion);
        }
        if (mTargetSdkVersion != null) {
            cmd.add("--target-sdk-version");

            // Ensure that targetSdkVersion is between minSdkVersion/maxSdkVersion if
            // they are specified.
            cmd.add(checkTargetSdkVersionBounds());
        }
        if (mMaxSdkVersion != null) {
            cmd.add("--max-sdk-version");
            cmd.add(mMaxSdkVersion);

            // if we have max sdk version, set --max-res-version
            // so we can ignore anything over that during build.
            cmd.add("--max-res-version");
            cmd.add(mMaxSdkVersion);
        }
        if (mPackageRenamed != null) {
            cmd.add("--rename-manifest-package");
            cmd.add(mPackageRenamed);
        }
        if (mVersionCode != null) {
            cmd.add("--version-code");
            cmd.add(mVersionCode);
        }
        if (mVersionName != null) {
            cmd.add("--version-name");
            cmd.add(mVersionName);
        }
        cmd.add("--no-version-vectors");
        cmd.add("-F");
        cmd.add(apkFile.getAbsolutePath());

        if (buildOptions.isFramework) {
            cmd.add("-x");
        }

        if (buildOptions.doNotCompress != null && !customAapt) {
            // Use custom -e option to avoid limits on commandline length.
            // Can only be used when custom aapt binary is not used.
            String extensionsFilePath = createDoNotCompressExtensionsFile(buildOptions).getAbsolutePath();
            cmd.add("-e");
            cmd.add(extensionsFilePath);
        } else if (buildOptions.doNotCompress != null) {
            for (String file : buildOptions.doNotCompress) {
                cmd.add("-0");
                cmd.add(file);
            }
        }

        if (!buildOptions.resourcesAreCompressed) {
            cmd.add("-0");
            cmd.add("arsc");
        }

        if (include != null) {
            for (File file : include) {
                cmd.add("-I");
                cmd.add(file.getPath());
            }
        }
        if (resDir != null) {
            cmd.add("-S");
            cmd.add(resDir.getAbsolutePath());
        }
        if (manifest != null) {
            cmd.add("-M");
            cmd.add(manifest.getAbsolutePath());
        }
        if (assetDir != null) {
            cmd.add("-A");
            cmd.add(assetDir.getAbsolutePath());
        }
        if (rawDir != null) {
            cmd.add(rawDir.getAbsolutePath());
        }
        try {
            OS.exec(cmd.toArray(new String[0]));
            LOGGER.verbose("command ran: ");
            LOGGER.verbose(cmd.toString());
        } catch (BrutException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void aaptPackage(File apkFile, File manifest, File resDir, File rawDir, File assetDir, File[] include)
            throws AndrolibException {

        String aaptPath = buildOptions.aaptPath;
        boolean customAapt = !aaptPath.isEmpty();
        List<String> cmd = new ArrayList<>();

        try {
            String aaptCommand = AaptManager.getAaptExecutionCommand(aaptPath, getAaptBinaryFile());
            cmd.add(aaptCommand);
        } catch (BrutException ex) {
            LOGGER.warning("aapt: " + ex.getMessage() + " (defaulting to $PATH binary)");
            cmd.add(AaptManager.getAaptBinaryName(getAaptVersion()));
        }

        if (buildOptions.isAapt2()) {
            aapt2Package(apkFile, manifest, resDir, rawDir, assetDir, include, cmd, customAapt);
            return;
        }
        aapt1Package(apkFile, manifest, resDir, rawDir, assetDir, include, cmd, customAapt);
    }

    public void zipPackage(File apkFile, File rawDir, File assetDir)
            throws AndrolibException {

        try {
            ZipUtils.zipFolders(rawDir, apkFile, assetDir, buildOptions.doNotCompress);
        } catch (IOException | BrutException ex) {
            throw new AndrolibException(ex);
        }
    }

    public int getMinSdkVersionFromAndroidCodename(MetaInfo meta, String sdkVersion) {
        int sdkNumber = mapSdkShorthandToVersion(sdkVersion);

        if (sdkNumber == ResConfigFlags.SDK_BASE) {
            return Integer.parseInt(meta.sdkInfo.get("minSdkVersion"));
        }
        return sdkNumber;
    }

    private int mapSdkShorthandToVersion(String sdkVersion) {
        switch (sdkVersion.toUpperCase()) {
            case "M":
                return ResConfigFlags.SDK_MNC;
            case "N":
                return ResConfigFlags.SDK_NOUGAT;
            case "O":
                return ResConfigFlags.SDK_OREO;
            case "P":
                return ResConfigFlags.SDK_P;
            case "Q":
                return ResConfigFlags.SDK_Q;
            case "R":
                return ResConfigFlags.SDK_R;
            case "S":
                return ResConfigFlags.SDK_S;
            case "SV2":
                return ResConfigFlags.SDK_S_V2;
            case "T":
            case "TIRAMISU":
                return ResConfigFlags.SDK_DEVELOPMENT;
            default:
                return Integer.parseInt(sdkVersion);
        }
    }

    public boolean detectWhetherAppIsFramework(File appDir)
            throws AndrolibException {
        File publicXml = new File(appDir, "res/values/public.xml");
        if (!publicXml.exists()) {
            return false;
        }

        Iterator<String> it;
        try {
            it = IOUtils.lineIterator(new FileReader(new File(appDir,
                    "res/values/public.xml")));
        } catch (FileNotFoundException ex) {
            throw new AndrolibException(
                    getString(R.string.androlibresources_could_not_detect_wether_app), ex);
        }
        it.next();
        it.next();
        return it.next().contains("0x01");
    }

    public Duo<ResFileDecoder, AXmlResourceParser> getResFileDecoder() {
        ResStreamDecoderContainer decoders = new ResStreamDecoderContainer();
        decoders.setDecoder("raw", new ResRawStreamDecoder());
        decoders.setDecoder("9patch", new Res9patchStreamDecoder());

        AXmlResourceParser axmlParser = new AXmlResourceParser();
        axmlParser.setAttrDecoder(new ResAttrDecoder());
        decoders.setDecoder("xml", new XmlPullStreamDecoder(axmlParser, getResXmlSerializer()));

        return new Duo<>(new ResFileDecoder(decoders), axmlParser);
    }

    public Duo<ResFileDecoder, AXmlResourceParser> getManifestFileDecoder(boolean withResources) {
        ResStreamDecoderContainer decoders = new ResStreamDecoderContainer();

        AXmlResourceParser axmlParser = new AndroidManifestResourceParser();
        if (withResources) {
            axmlParser.setAttrDecoder(new ResAttrDecoder());
        }
        decoders.setDecoder("xml", new XmlPullStreamDecoder(axmlParser, getResXmlSerializer()));

        return new Duo<>(new ResFileDecoder(decoders), axmlParser);
    }

    public ExtMXSerializer getResXmlSerializer() {
        ExtMXSerializer serial = new ExtMXSerializer();
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_INDENTATION, "    ");
        serial.setProperty(ExtXmlSerializer.PROPERTY_SERIALIZER_LINE_SEPARATOR, System.getProperty("line.separator"));
        serial.setProperty(ExtXmlSerializer.PROPERTY_DEFAULT_ENCODING, "utf-8");
        serial.setDisabledAttrEscape(true);
        return serial;
    }

    private void generateValuesFile(ResValuesFile valuesFile, Directory out,
                                    ExtXmlSerializer serial) throws AndrolibException {
        try {
            OutputStream outStream = out.getFileOutput(valuesFile.getPath());
            serial.setOutput((outStream), null);
            serial.startDocument(null, null);
            serial.startTag(null, "resources");

            for (ResResource res : valuesFile.listResources()) {
                if (valuesFile.isSynthesized(res)) {
                    continue;
                }
                ((ResValuesXmlSerializable) res.getValue()).serializeToResValuesXml(serial, res);
            }

            serial.endTag(null, "resources");
            serial.newLine();
            serial.endDocument();
            serial.flush();
            outStream.close();
        } catch (IOException | DirectoryException ex) {
            throw new AndrolibException(getString(R.string.androlibresources_could_not_generate) + valuesFile.getPath(), ex);
        }
    }

    private void generatePublicXml(ResPackage pkg, Directory out,
                                   XmlSerializer serial) throws AndrolibException {
        try {
            OutputStream outStream = out.getFileOutput("values/public.xml");
            serial.setOutput(outStream, null);
            serial.startDocument(null, null);
            serial.startTag(null, "resources");

            for (ResResSpec spec : pkg.listResSpecs()) {
                serial.startTag(null, "public");
                serial.attribute(null, "type", spec.getType().getName());
                serial.attribute(null, "name", spec.getName());
                serial.attribute(null, "id", String.format("0x%08x", spec.getId().id));
                serial.endTag(null, "public");
            }

            serial.endTag(null, "resources");
            serial.endDocument();
            serial.flush();
            outStream.close();
        } catch (IOException | DirectoryException ex) {
            throw new AndrolibException(getString(R.string.androlibresources_could_not_generate_public_xml), ex);
        }
    }

    private ResPackage[] getResPackagesFromApk(ExtFile apkFile, ResTable resTable, boolean keepBroken)
            throws AndrolibException {
        try {
            Directory dir = apkFile.getDirectory();
            try (BufferedInputStream bfi = new BufferedInputStream(dir.getFileInput("resources.arsc"))) {
                return ARSCDecoder.decode(bfi, false, keepBroken, resTable).getPackages();
            }
        } catch (DirectoryException | IOException ex) {
            throw new AndrolibException(getString(R.string.androlibresources_could_not_load_resources_arsc_from_file) + apkFile, ex);
        }
    }

    public File getFrameworkApk(int id, String frameTag)
            throws AndrolibException {
        File dir = getFrameworkDir();
        File apk;

        if (frameTag != null) {
            apk = new File(dir, String.valueOf(id) + '-' + frameTag + ".apk");
            if (apk.exists()) {
                return apk;
            }
        }

        apk = new File(dir, id + ".apk");
        if (apk.exists()) {
            return apk;
        }

        if (id == 1) {
            try (InputStream in = getAndroidFrameworkResourcesAsStream();
                 OutputStream out = new FileOutputStream(apk)) {
                IOUtils.copy(in, out);
                return apk;
            } catch (IOException ex) {
                throw new AndrolibException(ex);
            }
        }

        throw new CantFindFrameworkResException(id);
    }

    public void emptyFrameworkDirectory() throws AndrolibException {
        File dir = getFrameworkDir();
        File apk;

        apk = new File(dir, "1.apk");

        if (!apk.exists()) {
            LOGGER.warning(getString(R.string.androlibresources_cant_empty_framework_dir_no_file_found_at) + apk.getAbsolutePath());
        } else {
            try {
                if (apk.exists() && dir.listFiles().length > 1 && !buildOptions.forceDeleteFramework) {
                    LOGGER.warning(getString(R.string.androlibresources_more_than_default_framework_detected));
                } else {
                    for (File file : dir.listFiles()) {
                        if (file.isFile() && file.getName().endsWith(".apk")) {
                            LOGGER.info(getString(R.string.androlibresources_removing) + file.getName() + getString(R.string.androlibresources_framework_file));
                            file.delete();
                        }
                    }
                }
            } catch (NullPointerException e) {
                throw new AndrolibException(e);
            }
        }
    }

    public void listFrameworkDirectory() throws AndrolibException {
        File dir = getFrameworkDir();
        if (dir == null) {
            LOGGER.error(getString(R.string.androlibresources_no_framework_dir_found));
            return;
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".apk")) {
                LOGGER.info(file.getName());
            }
        }
    }

    public void installFramework(File frameFile) throws AndrolibException {
        installFramework(frameFile, buildOptions.frameworkTag);
    }

    public void installFramework(File frameFile, String tag)
            throws AndrolibException {
        InputStream in = null;
        ZipOutputStream out = null;
        try {
            ZipFile zip = new ZipFile(frameFile);
            ZipEntry entry = zip.getEntry("resources.arsc");

            if (entry == null) {
                throw new AndrolibException(getString(R.string.androlibresources_cant_found_resources_arsc));
            }

            in = zip.getInputStream(entry);
            byte[] data = IOUtils.toByteArray(in);

            ARSCData arsc = ARSCDecoder.decode(new ByteArrayInputStream(data), true, true);
            publicizeResources(data, arsc.getFlagsOffsets());

            File outFile = new File(getFrameworkDir(), arsc
                    .getOnePackage().getId()
                    + (tag == null ? "" : '-' + tag)
                    + ".apk");

            out = new ZipOutputStream(new FileOutputStream(outFile));
            out.setMethod(ZipOutputStream.STORED);
            CRC32 crc = new CRC32();
            crc.update(data);
            entry = new ZipEntry("resources.arsc");
            entry.setSize(data.length);
            entry.setMethod(ZipOutputStream.STORED);
            entry.setCrc(crc.getValue());
            out.putNextEntry(entry);
            out.write(data);
            out.closeEntry();

            //Write fake AndroidManifest.xml file to support original aapt
            entry = zip.getEntry("AndroidManifest.xml");
            if (entry != null) {
                in = zip.getInputStream(entry);
                byte[] manifest = IOUtils.toByteArray(in);
                CRC32 manifestCrc = new CRC32();
                manifestCrc.update(manifest);
                entry.setSize(manifest.length);
                entry.setCompressedSize(-1);
                entry.setCrc(manifestCrc.getValue());
                out.putNextEntry(entry);
                out.write(manifest);
                out.closeEntry();
            }

            zip.close();
            LOGGER.info(getString(R.string.androlibresources_framework_installed_to) + outFile);
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    public void publicizeResources(File arscFile) throws AndrolibException {
        byte[] data = new byte[(int) arscFile.length()];

        try (InputStream in = new FileInputStream(arscFile);
             OutputStream out = new FileOutputStream(arscFile)) {
            in.read(data);
            publicizeResources(data);
            out.write(data);
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void publicizeResources(byte[] arsc) throws AndrolibException {
        publicizeResources(arsc, ARSCDecoder.decode(new ByteArrayInputStream(arsc), true, true).getFlagsOffsets());
    }

    public void publicizeResources(byte[] arsc, FlagsOffset[] flagsOffsets) {
        for (FlagsOffset flags : flagsOffsets) {
            int offset = flags.offset + 3;
            int end = offset + 4 * flags.count;
            while (offset < end) {
                arsc[offset] |= (byte) 0x40;
                offset += 4;
            }
        }
    }

    public File getFrameworkDir() throws AndrolibException {
        if (mFrameworkDirectory != null) {
            return mFrameworkDirectory;
        }

        String path;

        // if a framework path was specified on the command line, use it
        if (buildOptions.frameworkFolderLocation != null) {
            path = buildOptions.frameworkFolderLocation;
        } else {
            File parentPath = new File(System.getProperty("user.home"));
            path = parentPath.getAbsolutePath() + String.format("%1$s.local%1$sshare%1$sapktool%1$sframework", File.separatorChar);
        }

        File dir = new File(path);

        if (!dir.isDirectory() && dir.isFile()) {
            throw new AndrolibException(getString(R.string.androlibresources_frame_path_not_a_dir));
        }

        if (dir.getParentFile() != null && dir.getParentFile().isFile()) {
            throw new AndrolibException(getString(R.string.androlibresources_please_remove_file_at) + dir.getParentFile());
        }

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                if (buildOptions.frameworkFolderLocation != null) {
                    LOGGER.error(getString(R.string.androlibresources_cant_create_framework_dir) + dir);
                }
                throw new AndrolibException(String.format(getString(R.string.androlibresources_cant_create_dir_pass_a_writable_path_with), dir));
            }
        }

        if (buildOptions.frameworkFolderLocation == null) {
            if (!dir.canWrite()) {
                LOGGER.error(String.format(getString(R.string.androlibresources_warning_could_write_to_using), dir.getAbsolutePath(), System.getProperty("java.io.tmpdir")));
                LOGGER.error(getString(R.string.androlibresources_please_be_aware_this) + getString(R.string.androlibresources_please_utilize));

                dir = new File(System.getProperty("java.io.tmpdir"));
            }
        }

        mFrameworkDirectory = dir;
        return dir;
    }

    private File getAaptBinaryFile() throws AndrolibException {
        try {
            if (getAaptVersion() == 2) {
                return AaptManager.getAapt2();
            }
            return AaptManager.getAapt1();
        } catch (BrutException ex) {
            throw new AndrolibException(ex);
        }
    }

    private int getAaptVersion() {
        return buildOptions.isAapt2() ? 2 : 1;
    }

    public InputStream getAndroidFrameworkResourcesAsStream() throws IOException {
        return BrutContext.getContext().getAssets().open("android-framework.jar");
    }

    public void close() throws IOException {
        if (mFramework != null) {
            mFramework.close();
        }
    }
}
