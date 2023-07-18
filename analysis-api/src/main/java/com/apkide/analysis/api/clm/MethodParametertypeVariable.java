package com.apkide.analysis.api.clm;

import androidx.annotation.NonNull;

import com.apkide.analysis.api.cle.Language;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;

import java.io.IOException;

public final class MethodParametertypeVariable extends Type {
    private EntitySpace space;
    private int entity;
    private MethodParameterType methodparametertype;

    protected MethodParametertypeVariable(FileSpace fileSpace, EntitySpace space) {
        super(fileSpace, space, 16);
        this.space = space;
    }

    protected MethodParametertypeVariable(FileSpace fileSpace, EntitySpace space, MethodParameterType methodparametertype) {
        super(fileSpace, space, 16);
        this.space = space;
        this.entity = space.declareEntity(this);
        this.methodparametertype = methodparametertype;
    }

    @Override
    protected void load(@NonNull StoreInputStream stream) throws IOException {
        super.load(stream);
        this.entity = stream.readInt();
        this.methodparametertype = (MethodParameterType) this.space.getEntity(stream.readInt());
    }

    @Override
    public int getID() {
        return this.entity;
    }

    @Override
    protected void store(@NonNull StoreOutputStream stream) throws IOException {
        super.store(stream);
        stream.writeInt(this.entity);
        stream.writeInt(this.space.getID(this.methodparametertype));
    }

    @Override
    public Language getLanguage() {
        return this.getMethodparametertype().getLanguage();
    }

    public MethodParameterType getMethodparametertype() {
        return this.methodparametertype;
    }
}
