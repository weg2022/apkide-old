package com.apkide.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Completion implements Parcelable {
    public static final Parcelable.Creator<Completion> CREATOR = new Parcelable.Creator<>() {
        @Override
        public Completion createFromParcel(Parcel source) {
            return new Completion(source);
        }
    
        @Override
        public Completion[] newArray(int size) {
            return new Completion[size];
        }
    };
    private int kind;
    @NonNull
    private String label;
    
    @Nullable
    private String details;
    @Nullable
    private String documentation;
    private boolean deprecated;
    private boolean preselect;
    @Nullable
    private String sortText;
    @NonNull
    private String insertText;
    private boolean snippet;
    
    public Completion(int kind, @NonNull String label, @Nullable String details, @Nullable String documentation, boolean deprecated, boolean preselect, @Nullable String sortText, @NonNull String insertText, boolean snippet) {
        this.kind = kind;
        this.label = label;
        this.details = details;
        this.documentation = documentation;
        this.deprecated = deprecated;
        this.preselect = preselect;
        this.sortText = sortText;
        this.insertText = insertText;
        this.snippet = snippet;
    }
    
    protected Completion(Parcel in) {
        this.kind = in.readInt();
        this.label = Objects.requireNonNull(in.readString());
        this.details = in.readString();
        this.documentation = in.readString();
        this.deprecated = in.readByte() != 0;
        this.preselect = in.readByte() != 0;
        this.sortText = in.readString();
        this.insertText = Objects.requireNonNull(in.readString());
        this.snippet = in.readByte() != 0;
    }
    
    public int getKind() {
        return kind;
    }
    
    @NonNull
    public String getLabel() {
        return label;
    }
    
    @Nullable
    public String getDetails() {
        return details;
    }
    
    @Nullable
    public String getDocumentation() {
        return documentation;
    }
    
    public boolean isDeprecated() {
        return deprecated;
    }
    
    public boolean isPreselect() {
        return preselect;
    }
    
    @Nullable
    public String getSortText() {
        return sortText;
    }
    
    @NonNull
    public String getInsertText() {
        return insertText;
    }
    
    public boolean isSnippet() {
        return snippet;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(kind, label, details, documentation, deprecated, preselect, sortText, insertText, snippet);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Completion that = (Completion) o;
        return kind == that.kind && deprecated == that.deprecated && preselect == that.preselect && snippet == that.snippet && label.equals(that.label) && Objects.equals(details, that.details) && Objects.equals(documentation, that.documentation) && Objects.equals(sortText, that.sortText) && insertText.equals(that.insertText);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.kind);
        dest.writeString(this.label);
        dest.writeString(this.details);
        dest.writeString(this.documentation);
        dest.writeByte(this.deprecated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.preselect ? (byte) 1 : (byte) 0);
        dest.writeString(this.sortText);
        dest.writeString(this.insertText);
        dest.writeByte(this.snippet ? (byte) 1 : (byte) 0);
    }
    
    public void readFromParcel(Parcel source) {
        this.kind = source.readInt();
        this.label = Objects.requireNonNull(source.readString());
        this.details = source.readString();
        this.documentation = source.readString();
        this.deprecated = source.readByte() != 0;
        this.preselect = source.readByte() != 0;
        this.sortText = source.readString();
        this.insertText = Objects.requireNonNull(source.readString());
        this.snippet = source.readByte() != 0;
    }
}
