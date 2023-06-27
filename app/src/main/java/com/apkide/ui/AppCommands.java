package com.apkide.ui;

import static java.lang.System.arraycopy;

import android.view.Menu;
import android.view.MenuItem;

import com.apkide.common.AppLog;
import com.apkide.common.Command;
import com.apkide.ui.commands.ExitApp;
import com.apkide.ui.commands.GotoSettings;
import com.apkide.ui.commands.MakeProject;
import com.apkide.ui.commands.Redo;
import com.apkide.ui.commands.Undo;
import com.apkide.ui.commands.navigate.Backward;
import com.apkide.ui.commands.navigate.Forward;
import com.apkide.ui.commands.view.ViewBuild;
import com.apkide.ui.commands.view.ViewFiles;
import com.apkide.ui.commands.view.ViewFind;
import com.apkide.ui.commands.view.ViewGit;
import com.apkide.ui.commands.view.ViewProblem;
import com.apkide.ui.commands.view.ViewProject;
import com.apkide.ui.util.MenuCommand;

public class AppCommands {

    private static final Command[] commands;
    private static final MenuCommand[] menuCommands;


    static {
        menuCommands = new MenuCommand[]{
                new Undo(),
                new Redo(),
                new MakeProject(),
                new GotoSettings(),
                new ExitApp(),

                //View
                new ViewFiles(),
                new ViewProject(),
                new ViewProblem(),
                new ViewBuild(),
                new ViewFind(),
                new ViewGit(),

                //Navigation
                new Backward(),
                new Forward(),

        };

        commands = new Command[menuCommands.length];
        arraycopy(menuCommands, 0, commands, 0, menuCommands.length);
    }


    public static boolean menuCommandPreExec(Menu menu) {
        long startTime=System.currentTimeMillis();
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
        long endTime=System.currentTimeMillis();
        AppLog.s("menuCommandPreExec:"+(endTime-startTime));
        return apply;
    }

    public static boolean menuCommandExec(MenuItem item) {
        for (Command command : commands) {
            if (command instanceof MenuCommand) {
                if (item.getItemId() == ((MenuCommand) command).getId()) {
                    AppLog.s("menuCommandExec:"+item.getTitle());
                    return command.commandPerformed();
                }
            }
        }
        return false;
    }
}
