package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EngineSolution implements Parcelable {
    public Map<String,List<String>> bootClassesMap;
    public Map<String, List<String>> smaliClassesMap;

    public EngineSolution(Map<String, List<String>> bootClassesMap, Map<String, List<String>> smaliClassesMap) {
        this.bootClassesMap = bootClassesMap;
        this.smaliClassesMap = smaliClassesMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineSolution that = (EngineSolution) o;
        return bootClassesMap.equals(that.bootClassesMap) && smaliClassesMap.equals(that.smaliClassesMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bootClassesMap, smaliClassesMap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bootClassesMap.size());
        for (Map.Entry<String, List<String>> entry : this.bootClassesMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeStringList(entry.getValue());
        }
        dest.writeInt(this.smaliClassesMap.size());
        for (Map.Entry<String, List<String>> entry : this.smaliClassesMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeStringList(entry.getValue());
        }
    }

    public void readFromParcel(Parcel source) {
        int bootClassesMapSize = source.readInt();
        this.bootClassesMap = new HashMap<String, List<String>>(bootClassesMapSize);
        for (int i = 0; i < bootClassesMapSize; i++) {
            String key = source.readString();
            List<String> value = source.createStringArrayList();
            this.bootClassesMap.put(key, value);
        }
        int smaliClassesMapSize = source.readInt();
        this.smaliClassesMap = new HashMap<String, List<String>>(smaliClassesMapSize);
        for (int i = 0; i < smaliClassesMapSize; i++) {
            String key = source.readString();
            List<String> value = source.createStringArrayList();
            this.smaliClassesMap.put(key, value);
        }
    }

    protected EngineSolution(Parcel in) {
        int bootClassesMapSize = in.readInt();
        this.bootClassesMap = new HashMap<String, List<String>>(bootClassesMapSize);
        for (int i = 0; i < bootClassesMapSize; i++) {
            String key = in.readString();
            List<String> value = in.createStringArrayList();
            this.bootClassesMap.put(key, value);
        }
        int smaliClassesMapSize = in.readInt();
        this.smaliClassesMap = new HashMap<String, List<String>>(smaliClassesMapSize);
        for (int i = 0; i < smaliClassesMapSize; i++) {
            String key = in.readString();
            List<String> value = in.createStringArrayList();
            this.smaliClassesMap.put(key, value);
        }
    }

    public static final Creator<EngineSolution> CREATOR = new Creator<EngineSolution>() {
        @Override
        public EngineSolution createFromParcel(Parcel source) {
            return new EngineSolution(source);
        }

        @Override
        public EngineSolution[] newArray(int size) {
            return new EngineSolution[size];
        }
    };
}
