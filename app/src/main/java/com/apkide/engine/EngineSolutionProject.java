package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class EngineSolutionProject implements Parcelable {
    public String rootDir;
    public String buildDir;//build dir
    public String originalDir;//original dir
    public String metainfDir;//META-INF dir
    public String assetsDir;//assets dir
    public String libDir;//lib dir
    public String resDir;//res dir
    public List<String> smaliClassesDirs;//smali dir or smali_classes2... dir
    public String unknownDir;//unknown dir
    public String androidManifestFile;//AndroidManifest.xml
    public String configFile;//apktool.yml

    public EngineSolutionProject(String rootDir, String buildDir, String originalDir, String metainfDir, String assetsDir, String libDir, String resDir, List<String> smaliClassesDirs, String unknownDir, String androidManifestFile, String configFile) {
        this.rootDir = rootDir;
        this.buildDir = buildDir;
        this.originalDir = originalDir;
        this.metainfDir = metainfDir;
        this.assetsDir = assetsDir;
        this.libDir = libDir;
        this.resDir = resDir;
        this.smaliClassesDirs = smaliClassesDirs;
        this.unknownDir = unknownDir;
        this.androidManifestFile = androidManifestFile;
        this.configFile = configFile;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineSolutionProject that = (EngineSolutionProject) o;
        return rootDir.equals(that.rootDir) && buildDir.equals(that.buildDir) && originalDir.equals(that.originalDir) && metainfDir.equals(that.metainfDir) && assetsDir.equals(that.assetsDir) && libDir.equals(that.libDir) && resDir.equals(that.resDir) && smaliClassesDirs.equals(that.smaliClassesDirs) && unknownDir.equals(that.unknownDir) && androidManifestFile.equals(that.androidManifestFile) && configFile.equals(that.configFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootDir, buildDir, originalDir, metainfDir, assetsDir, libDir, resDir, smaliClassesDirs, unknownDir, androidManifestFile, configFile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rootDir);
        dest.writeString(this.buildDir);
        dest.writeString(this.originalDir);
        dest.writeString(this.metainfDir);
        dest.writeString(this.assetsDir);
        dest.writeString(this.libDir);
        dest.writeString(this.resDir);
        dest.writeStringList(this.smaliClassesDirs);
        dest.writeString(this.unknownDir);
        dest.writeString(this.androidManifestFile);
        dest.writeString(this.configFile);
    }

    public void readFromParcel(Parcel source) {
        this.rootDir = source.readString();
        this.buildDir = source.readString();
        this.originalDir = source.readString();
        this.metainfDir = source.readString();
        this.assetsDir = source.readString();
        this.libDir = source.readString();
        this.resDir = source.readString();
        this.smaliClassesDirs = source.createStringArrayList();
        this.unknownDir = source.readString();
        this.androidManifestFile = source.readString();
        this.configFile = source.readString();
    }

    protected EngineSolutionProject(Parcel in) {
        this.rootDir = in.readString();
        this.buildDir = in.readString();
        this.originalDir = in.readString();
        this.metainfDir = in.readString();
        this.assetsDir = in.readString();
        this.libDir = in.readString();
        this.resDir = in.readString();
        this.smaliClassesDirs = in.createStringArrayList();
        this.unknownDir = in.readString();
        this.androidManifestFile = in.readString();
        this.configFile = in.readString();
    }

    public static final Parcelable.Creator<EngineSolutionProject> CREATOR = new Parcelable.Creator<EngineSolutionProject>() {
        @Override
        public EngineSolutionProject createFromParcel(Parcel source) {
            return new EngineSolutionProject(source);
        }

        @Override
        public EngineSolutionProject[] newArray(int size) {
            return new EngineSolutionProject[size];
        }
    };
}
