package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkide.analysis.api.clm.Model;

import java.util.List;
import java.util.TreeMap;

public class EngineSolution {

    private List<Project> projects;
    private String encoding;
    private TreeMap<String, List<String>> filePatternMap;

    public EngineSolution(List<Project> projects, String encoding, TreeMap<String, List<String>> filePatternMap) {
        this.projects = projects;
        this.encoding = encoding;
        this.filePatternMap = filePatternMap;
    }

    public void setModel(Model model) {

    }

    public static class Project implements Parcelable {
        private String assembly;
        private String projectFilePath;
        private String rootNamespace;
        private List<File> projectFiles;
        private List<String> projectAssembles;
        private boolean checked;
        private String configuration;
        private String destinationPath;
        private String targetVersion;
        private boolean debugBuild;
        private boolean externallyBuild;
        private List<String> defaultImports;
        private List<String> definedSymbols;
        private List<String> tagLibPaths;

        public Project(String assembly, String projectFilePath, String rootNamespace, List<File> projectFiles, List<String> projectAssembles, boolean checked, String configuration, String destinationPath, String targetVersion, boolean debugBuild, boolean externallyBuild, List<String> defaultImports, List<String> definedSymbols, List<String> tagLibPaths) {
            this.assembly = assembly;
            this.projectFilePath = projectFilePath;
            this.rootNamespace = rootNamespace;
            this.projectFiles = projectFiles;
            this.projectAssembles = projectAssembles;
            this.checked = checked;
            this.configuration = configuration;
            this.destinationPath = destinationPath;
            this.targetVersion = targetVersion;
            this.debugBuild = debugBuild;
            this.externallyBuild = externallyBuild;
            this.defaultImports = defaultImports;
            this.definedSymbols = definedSymbols;
            this.tagLibPaths = tagLibPaths;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.assembly);
            dest.writeString(this.projectFilePath);
            dest.writeString(this.rootNamespace);
            dest.writeTypedList(this.projectFiles);
            dest.writeStringList(this.projectAssembles);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            dest.writeString(this.configuration);
            dest.writeString(this.destinationPath);
            dest.writeString(this.targetVersion);
            dest.writeByte(this.debugBuild ? (byte) 1 : (byte) 0);
            dest.writeByte(this.externallyBuild ? (byte) 1 : (byte) 0);
            dest.writeStringList(this.defaultImports);
            dest.writeStringList(this.definedSymbols);
            dest.writeStringList(this.tagLibPaths);
        }

        public void readFromParcel(Parcel source) {
            this.assembly = source.readString();
            this.projectFilePath = source.readString();
            this.rootNamespace = source.readString();
            this.projectFiles = source.createTypedArrayList(File.CREATOR);
            this.projectAssembles = source.createStringArrayList();
            this.checked = source.readByte() != 0;
            this.configuration = source.readString();
            this.destinationPath = source.readString();
            this.targetVersion = source.readString();
            this.debugBuild = source.readByte() != 0;
            this.externallyBuild = source.readByte() != 0;
            this.defaultImports = source.createStringArrayList();
            this.definedSymbols = source.createStringArrayList();
            this.tagLibPaths = source.createStringArrayList();
        }

        protected Project(Parcel in) {
            this.assembly = in.readString();
            this.projectFilePath = in.readString();
            this.rootNamespace = in.readString();
            this.projectFiles = in.createTypedArrayList(File.CREATOR);
            this.projectAssembles = in.createStringArrayList();
            this.checked = in.readByte() != 0;
            this.configuration = in.readString();
            this.destinationPath = in.readString();
            this.targetVersion = in.readString();
            this.debugBuild = in.readByte() != 0;
            this.externallyBuild = in.readByte() != 0;
            this.defaultImports = in.createStringArrayList();
            this.definedSymbols = in.createStringArrayList();
            this.tagLibPaths = in.createStringArrayList();
        }

        public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<>() {
            @Override
            public Project createFromParcel(Parcel source) {
                return new Project(source);
            }

            @Override
            public Project[] newArray(int size) {
                return new Project[size];
            }
        };
    }

    public static class File implements Parcelable {
        private String filePath;
        private String language;
        private String packageName;
        private boolean isResource;
        private boolean isBinary;

        public File(String filePath, String language, String packageName, boolean isResource, boolean isBinary) {
            this.filePath = filePath;
            this.language = language;
            this.packageName = packageName;
            this.isResource = isResource;
            this.isBinary = isBinary;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.filePath);
            dest.writeString(this.language);
            dest.writeString(this.packageName);
            dest.writeByte(this.isResource ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isBinary ? (byte) 1 : (byte) 0);
        }

        public void readFromParcel(Parcel source) {
            this.filePath = source.readString();
            this.language = source.readString();
            this.packageName = source.readString();
            this.isResource = source.readByte() != 0;
            this.isBinary = source.readByte() != 0;
        }

        protected File(Parcel in) {
            this.filePath = in.readString();
            this.language = in.readString();
            this.packageName = in.readString();
            this.isResource = in.readByte() != 0;
            this.isBinary = in.readByte() != 0;
        }

        public static final Parcelable.Creator<File> CREATOR = new Parcelable.Creator<>() {
            @Override
            public File createFromParcel(Parcel source) {
                return new File(source);
            }

            @Override
            public File[] newArray(int size) {
                return new File[size];
            }
        };
    }
}
