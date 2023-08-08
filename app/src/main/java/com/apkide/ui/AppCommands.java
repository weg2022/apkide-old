package com.apkide.ui;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.apkide.common.command.Command;
import com.apkide.common.keybinding.KeyStroke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AppCommands {

	public interface MenuCommand extends Command {
		int getId();

		boolean isEnabled();

		boolean run();
	}

	public interface VisibleMenuCommand extends MenuCommand {
		boolean getVisible(boolean preview);
	}

	public interface TitleMenuCommand extends MenuCommand {
		@NonNull
		String getTitle();
	}

	public interface ActionBarCommand extends Command {
		int getId();

		@NonNull
		String getLabel();

		boolean isEnabled();

		boolean isVisible();

		boolean run();
	}

	public interface BrowserCommand extends Command {

		boolean getVisible(boolean preview);
		@DrawableRes
		int getIcon();

		@StringRes
		int getLabel();

		boolean isEnabled();

		boolean run();
	}

	public interface ShortcutKeyCommand extends Command {
		@NonNull
		String getName();


		boolean isEnabled();

		boolean run();

		@NonNull
		KeyStroke getKeyStroke();
	}


	private static final HashSet<Class<?>> sCommandClasses = new HashSet<>(50);
	private static final List<Command> sCommands = new ArrayList<>(50);

	static {

	}

	private static void addCommands(Command[] commands, List<ShortcutKeyCommand> keyCommands) {
		for (Command command : commands) {
			if (command instanceof ShortcutKeyCommand) {
				if (!keyCommands.contains(command))
					keyCommands.add((ShortcutKeyCommand) command);
			}
			if (!sCommandClasses.contains(command.getClass())) {
				sCommandClasses.add(command.getClass());
				sCommands.add(command);
			}
		}
	}

	public static List<Command> getCommands() {
		return sCommands;
	}

	public static List<BrowserCommand> getBrowserCommands() {
		ArrayList<BrowserCommand> commands = new ArrayList<>();
		for (Command command : getCommands()) {
			if (command instanceof BrowserCommand) {
				commands.add((BrowserCommand) command);
			}
		}
		return commands;
	}

	public static MenuCommand foundMenuCommand(int id) {
		for (Command command : getCommands()) {
			if (command instanceof MenuCommand && ((MenuCommand) command).getId() == id) {
				return (MenuCommand) command;
			}
		}
		return null;
	}

	public static ActionBarCommand foundActionBarCommand(int id) {
		for (Command command : getCommands()) {
			if (command instanceof ActionBarCommand && ((ActionBarCommand) command).getId() == id) {
				return (ActionBarCommand) command;
			}
		}
		return null;
	}
}
