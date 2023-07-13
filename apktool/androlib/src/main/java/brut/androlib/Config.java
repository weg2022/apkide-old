package brut.androlib;

import com.apkide.common.ApplicationProvider;

import java.util.Collection;

import brut.androlib.err.AndrolibException;

public abstract class Config {
    public final static short DECODE_SOURCES_NONE = 0x0000;
    public final static short DECODE_SOURCES_SMALI = 0x0001;
    public final static short DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES = 0x0010;

    public final static short DECODE_RESOURCES_NONE = 0x0100;
    public final static short DECODE_RESOURCES_FULL = 0x0101;

    public final static short FORCE_DECODE_MANIFEST_NONE = 0x0000;
    public final static short FORCE_DECODE_MANIFEST_FULL = 0x0001;

    public final static short DECODE_ASSETS_NONE = 0x0000;
    public final static short DECODE_ASSETS_FULL = 0x0001;

    // Build options
    public boolean forceBuildAll = false;
    public boolean forceDeleteFramework = false;
    public boolean debugMode = false;
    public boolean netSecConf = false;
    public boolean verbose = false;
    public boolean copyOriginalFiles = false;
    public boolean updateFiles = false;
    public boolean isFramework = false;
    public boolean resourcesAreCompressed = false;
    public boolean useAapt2 = false;
    public boolean noCrunch = false;
    public int forceApi = 0;
    public Collection<String> doNotCompress;

    // Decode options
    public short decodeSources = DECODE_SOURCES_SMALI;
    public short decodeResources = DECODE_RESOURCES_FULL;
    public short forceDecodeManifest = FORCE_DECODE_MANIFEST_NONE;
    public short decodeAssets = DECODE_ASSETS_FULL;
    public int apiLevel = 0;
    public boolean analysisMode = false;
    public boolean forceDelete = false;
    public boolean keepBrokenResources = false;
    public boolean baksmaliDebugMode = true;

    // Common options
    public String frameworkDirectory = null;
    public String frameworkTag = null;
    public String aaptPath = "";
    public int aaptVersion = 1; // default to v1

    // Utility functions
    public boolean isAapt2() {
        return this.useAapt2 || this.aaptVersion == 2;
    }

    public Config() {
        setDefaultFrameworkDirectory();
    }

    private void setDefaultFrameworkDirectory() {
        frameworkDirectory = ApplicationProvider.get().foundFile("android.jar").getAbsolutePath();
    }

    public void setDecodeSources(short mode) throws AndrolibException {
        if (mode != DECODE_SOURCES_NONE && mode != DECODE_SOURCES_SMALI && mode != DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES) {
            throw new AndrolibException("Invalid decode sources mode: " + mode);
        }
        if (decodeSources == DECODE_SOURCES_NONE && mode == DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES) {
            ApkBuilder.LOGGER.info("--only-main-classes cannot be paired with -s/--no-src. Ignoring.");
            return;
        }
        decodeSources = mode;
        sync();
    }

    public void setDecodeResources(short mode) throws AndrolibException {
        if (mode != DECODE_RESOURCES_NONE && mode != DECODE_RESOURCES_FULL) {
            throw new AndrolibException("Invalid decode resources mode");
        }
        decodeResources = mode;
        sync();
    }

    public void setForceDecodeManifest(short mode) throws AndrolibException {
        if (mode != FORCE_DECODE_MANIFEST_NONE && mode != FORCE_DECODE_MANIFEST_FULL) {
            throw new AndrolibException("Invalid force decode manifest mode");
        }
        forceDecodeManifest = mode;
        sync();
    }

    public void setDecodeAssets(short mode) throws AndrolibException {
        if (mode != DECODE_ASSETS_NONE && mode != DECODE_ASSETS_FULL) {
            throw new AndrolibException("Invalid decode asset mode");
        }
        decodeAssets = mode;
        sync();
    }

    private static final Object myLock=new Object();
    private static Config myConfig =new Config() {
        @Override
        public void onChanged() {
            //ignore
        }
    };

    public void sync(){
        onChanged();
    }
    public abstract void onChanged();

    public static void set(Config config){
        synchronized (myLock){
            myConfig=config;
        }
    }

    public static Config get(){
        synchronized (myLock){
            return myConfig;
        }
    }
}