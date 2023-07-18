package com.apkide.analysis.api.clm;


import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public abstract class Entity {
    private final FileSpace myFileSpace;
    private final EntitySpace mySpace;
    private int myLanguageID;

    public Entity(FileSpace fileSpace, EntitySpace space) {
        this.myFileSpace = fileSpace;
        this.mySpace = space;
    }

    public void configureLanguage(@NonNull Language language) {
        myLanguageID = myFileSpace.getLanguageID(language);
    }

    protected void load(@NonNull StoreInputStream stream) throws IOException {
        myLanguageID = stream.readInt();
    }

    protected void store(@NonNull StoreOutputStream stream) throws IOException {
        stream.writeInt(myLanguageID);
    }

    public boolean isPrivateVisibleFrom(Entity entity) {
        if (this.getFile() != entity.getFile()) {
            return false;
        } else {
            ClassType classType1 = this.getEnclosingClassType();
            ClassType classType2 = entity.getEnclosingClassType();
            return classType1.isEnclosedBy(classType2);
        }
    }

    public String getHTMLString() {
        return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getHTMLString(this) : "";
    }

    public int getIdentifier() {
        return 0;
    }

    public boolean isType() {
        return this instanceof Type;
    }

    public boolean isNamespace() {
        return this instanceof Namespace;
    }

    public boolean isMethodParametertypeVariable() {
        return this instanceof MethodParametertypeVariable;
    }

    public boolean isMember() {
        return this instanceof Member;
    }

    public boolean isArrayType() {
        return this instanceof ArrayType;
    }

    public boolean isPointerType() {
        return this instanceof PointerType;
    }

    public boolean isPrimitiveType() {
        return this instanceof PrimitiveType;
    }

    public boolean isClassType() {
        return this instanceof ClassType;
    }

    public boolean isParameterType() {
        return this instanceof ParameterType;
    }

    public boolean isMethodParameterType() {
        return this instanceof MethodParameterType;
    }

    public boolean isParameterizedType() {
        return this instanceof ParameterizedType;
    }

    public int getID() {
        return 0;
    }

    public int getModifiers() {
        return 0;
    }

    public boolean isPrivate() {
        return Modifiers.isJavaPrivate(this.getModifiers()) || Modifiers.isCSPrivate(this.getModifiers());
    }

    public boolean isFilePrivate() {
        return this.isPrivate();
    }

    public ClassType getEnclosingClassType() {
        return null;
    }

    public Language getLanguage() {
        return myFileSpace.getLanguage(myLanguageID);
    }

    public ClassType getEnclosingTopLevelClassType() {
        return null;
    }

    public String getDocumentationString() {
        return this.mySpace.getDocumentationString(this);
    }

    public String getHTMLDocumentationString() {
        return this.mySpace.getHTMLDocumentationString(this);
    }

    public FileEntry getFile() {
        return null;
    }

    public int getDeclarationNumber() {
        return -1;
    }

    public String getFullyQualifiedNameString() {
        return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getFullyQualifiedNameString(this) : "";
    }

    public String getNameString() {
        return this.getLanguage().getRenderer() != null ? this.getLanguage().getRenderer().getNameString(this) : "";
    }

    public int getIdentifierLine() {
        return 0;
    }

    public int getIdentifierEndColumn() {
        return 0;
    }

    public int getIdentifierStartColumn() {
        return 0;
    }

    public int getModifiersStartLine() {
        return 0;
    }

    public int getModifiersEndLine() {
        return 0;
    }

    public int getModifiersEndColumn() {
        return 0;
    }

    public int getModifiersStartColumn() {
        return 0;
    }

    public int getEndColumn() {
        return 0;
    }

    public int getEndLine() {
        return 0;
    }

    public int getStartColumn() {
        return 0;
    }

    public int getStartLine() {
        return 0;
    }
}
