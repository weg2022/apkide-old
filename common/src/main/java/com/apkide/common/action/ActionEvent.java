package com.apkide.common.action;

import androidx.annotation.NonNull;

import java.util.EventObject;

public class ActionEvent extends EventObject {
    public static final String Default="Default";
    private final String actionCommand;
    public ActionEvent(@NonNull Object source,@NonNull String actionCommand) {
        super(source);
        this.actionCommand=actionCommand;
    }

    @NonNull
    @Override
    public Object getSource() {
        return super.getSource();
    }

    @NonNull
    public String getActionCommand() {
        return actionCommand;
    }
}
