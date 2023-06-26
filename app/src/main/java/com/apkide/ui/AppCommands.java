package com.apkide.ui;

import static java.lang.System.arraycopy;

import android.view.Menu;
import android.view.MenuItem;

import com.apkide.common.Command;
import com.apkide.ui.commands.SettingsMenuCommand;
import com.apkide.ui.commands.UndoMenuCommand;
import com.apkide.ui.util.MenuCommand;

public class AppCommands {

    private static final Command[] commands;
    private static final MenuCommand[] menuCommands;


    static {
        menuCommands = new MenuCommand[]{
                new UndoMenuCommand(),

                new SettingsMenuCommand()
        };

        commands = new Command[menuCommands.length];
        arraycopy(menuCommands, 0, commands, 0, menuCommands.length);
    }


    public static boolean menuCommandPreExec(Menu menu) {
        boolean apply = false;
        for (Command command : commands) {
            if (command instanceof MenuCommand) {
                MenuItem item = menu.findItem(((MenuCommand) command).getId());
                if (item != null) {
                    item.setEnabled(command.isEnabled());
                    item.setVisible(((MenuCommand) command).isVisible());
                    apply = true;
                }
            }
        }
        return apply;
    }

    public static boolean menuCommandExec(MenuItem item) {
        for (Command command : commands) {
            if (command instanceof MenuCommand) {
                if (item.getItemId() == ((MenuCommand) command).getId()) {
                    if (command.isEnabled()) {
                        return command.commandPerformed();
                    }
                }
            }
        }
        return false;
    }
}
