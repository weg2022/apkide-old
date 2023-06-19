package com.apkide.common;

import java.util.EventObject;

public class ActionEvent extends EventObject {
    public String actionCommand;
    public ActionEvent(Object source,String actionCommand) {
        super(source);
        this.actionCommand=actionCommand;
    }

    public String getActionCommand() {
        return actionCommand;
    }
}
