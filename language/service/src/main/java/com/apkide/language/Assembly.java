package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Assembly implements Parcelable {
    public static final Parcelable.Creator<Assembly> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Assembly createFromParcel(Parcel source) {
            return new Assembly(source);
        }
        
        @Override
        public Assembly[] newArray(int size) {
            return new Assembly[size];
        }
    };
    @NonNull
    private String name;
    @NonNull
    private String rootPath;
    @NonNull
    private String buildPath;
    @NonNull
    private List<String> files;
    @NonNull
    private List<Assembly> assembles;
    private String configuration;
    @NonNull
    private String sourceCompatibility;
    @NonNull
    private String targetCompatibility;
    
    public Assembly(@NonNull String name, @NonNull String rootPath, @NonNull String buildPath, @NonNull List<String> files, @NonNull List<Assembly> assembles, String configuration, @NonNull String sourceCompatibility, @NonNull String targetCompatibility) {
        this.name = name;
        this.rootPath = rootPath;
        this.buildPath = buildPath;
        this.files = files;
        this.assembles = assembles;
        this.configuration = configuration;
        this.sourceCompatibility = sourceCompatibility;
        this.targetCompatibility = targetCompatibility;
    }
    
    protected Assembly(Parcel in) {
        this.name = Objects.requireNonNull(in.readString());
        this.rootPath = Objects.requireNonNull(in.readString());
        this.buildPath = Objects.requireNonNull(in.readString());
        this.files = Objects.requireNonNull(in.createStringArrayList());
        this.assembles = new ArrayList<>();
        in.readList(this.assembles, Assembly.class.getClassLoader());
        this.configuration = in.readString();
        this.sourceCompatibility = Objects.requireNonNull(in.readString());
        this.targetCompatibility = Objects.requireNonNull(in.readString());
    }
    
    protected com.apkide.language.api.Assembly toAssembly() {
        List<com.apkide.language.api.Assembly> assemblies = new ArrayList<>();
        for (Assembly assemble : assembles) {
            assemblies.add(assemble.toAssembly());
        }
        return new com.apkide.language.api.Assembly(
                name, rootPath, buildPath,
                files, assemblies, configuration,
                sourceCompatibility, targetCompatibility
        );
    }
    
    
    @NonNull
    public String getName() {
        return name;
    }
    
    @NonNull
    public String getRootPath() {
        return rootPath;
    }
    
    @NonNull
    public String getBuildPath() {
        return buildPath;
    }
    
    @NonNull
    public List<String> getFiles() {
        return files;
    }
    
    @NonNull
    public List<Assembly> getAssembles() {
        return assembles;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    @NonNull
    public String getSourceCompatibility() {
        return sourceCompatibility;
    }
    
    @NonNull
    public String getTargetCompatibility() {
        return targetCompatibility;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, rootPath, buildPath, files, assembles, configuration, sourceCompatibility, targetCompatibility);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assembly assembly = (Assembly) o;
        return Objects.equals(name, assembly.name) && Objects.equals(rootPath, assembly.rootPath) && Objects.equals(buildPath, assembly.buildPath) && Objects.equals(files, assembly.files) && Objects.equals(assembles, assembly.assembles) && Objects.equals(configuration, assembly.configuration) && Objects.equals(sourceCompatibility, assembly.sourceCompatibility) && Objects.equals(targetCompatibility, assembly.targetCompatibility);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.rootPath);
        dest.writeString(this.buildPath);
        dest.writeStringList(this.files);
        dest.writeList(this.assembles);
        dest.writeString(this.configuration);
        dest.writeString(this.sourceCompatibility);
        dest.writeString(this.targetCompatibility);
    }
    
    public void readFromParcel(Parcel source) {
        this.name = Objects.requireNonNull(source.readString());
        this.rootPath = Objects.requireNonNull(source.readString());
        this.buildPath = Objects.requireNonNull(source.readString());
        this.files = Objects.requireNonNull(source.createStringArrayList());
        this.assembles = new ArrayList<>();
        source.readList(this.assembles, Assembly.class.getClassLoader());
        this.configuration = source.readString();
        this.sourceCompatibility = Objects.requireNonNull(source.readString());
        this.targetCompatibility = Objects.requireNonNull(source.readString());
    }
    
    
}

