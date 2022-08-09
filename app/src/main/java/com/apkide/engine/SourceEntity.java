package com.apkide.engine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Code completion item
 * {@link com.apkide.views.CodeCompletionListView}
 */
public class SourceEntity {
    private String packageName;
    private String name;
    
    private List<SourceEntity> children;
    private Type type;


    public void setChildren(@NonNull List<SourceEntity> children) {
        this.children = children;
    }

    public void addChild(@NonNull SourceEntity entity){
        if (!children.contains(entity)) {
            children.add(entity);
        }
    }

    public void removeChild(@NonNull SourceEntity entity){
        children.remove(entity);
    }

    public void removeChild(int index){
        children.remove(index);
    }

    public boolean hasChild() {
        return children != null && children.size() > 0;
    }

    public int childCount() {
        return children.size();
    }

    @Nullable
    public SourceEntity childAt(int index) {
        return children.get(index);
    }

    @Nullable
    public SourceEntity getFirstChild() {
        return children.get(0);
    }

    @Nullable
    public SourceEntity getLastChild() {
        return children.get(children.size() - 1);
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public enum Type {
        Method,
        Field,
        Property,
        Event,
        Class,
        Type,
        Package,
        Variable,
        Parameter,
        Keyword,
        UnknownIdentifier,
        Constructor,
        File;
    }
}
