package com.apkide.ui;

import androidx.annotation.NonNull;

import com.apkide.common.Command;
import com.apkide.ui.commands.CloseFileCommand;
import com.apkide.ui.commands.ExitCommand;
import com.apkide.ui.commands.GotoSettingsCommand;
import com.apkide.ui.commands.SaveAllCommand;
import com.apkide.ui.commands.actionbar.CopyCommand;
import com.apkide.ui.commands.actionbar.CutCommand;
import com.apkide.ui.commands.actionbar.EditModeCommand;
import com.apkide.ui.commands.actionbar.NavigateModeCommand;
import com.apkide.ui.commands.actionbar.PasteCommand;
import com.apkide.ui.commands.actionbar.PickColorCommand;
import com.apkide.ui.commands.actionbar.RedoCommand;
import com.apkide.ui.commands.actionbar.RunCommand;
import com.apkide.ui.commands.actionbar.SelectAllCommand;
import com.apkide.ui.commands.actionbar.UndoCommand;
import com.apkide.ui.commands.code.GenerateCommand;
import com.apkide.ui.commands.code.IndentLinesCommand;
import com.apkide.ui.commands.code.OptimizeImportsCommand;
import com.apkide.ui.commands.code.OutBlockCommentCommand;
import com.apkide.ui.commands.code.OutLineCommentCommand;
import com.apkide.ui.commands.code.ReformatCodeCommand;
import com.apkide.ui.commands.code.SurroundWithCommand;
import com.apkide.ui.commands.code.UnOutComment;
import com.apkide.ui.commands.edit.FindCommand;
import com.apkide.ui.commands.edit.FindInFileCommand;
import com.apkide.ui.commands.edit.FindReplaceCommand;
import com.apkide.ui.commands.edit.FindReplaceInFileCommand;
import com.apkide.ui.commands.edit.FindUsagesCommand;
import com.apkide.ui.commands.navigate.BackwardCommand;
import com.apkide.ui.commands.navigate.ForwardCommand;
import com.apkide.ui.commands.navigate.GotoCommand;
import com.apkide.ui.commands.navigate.GotoDefinitionCommand;
import com.apkide.ui.commands.project.CleanProjectCommand;
import com.apkide.ui.commands.project.CloseProjectCommand;
import com.apkide.ui.commands.project.MakeProjectCommand;
import com.apkide.ui.commands.project.RebuildProjectCommand;
import com.apkide.ui.commands.refactor.InlineCommand;
import com.apkide.ui.commands.refactor.RenameCommand;
import com.apkide.ui.commands.refactor.SafeDeleteCommand;
import com.apkide.ui.commands.view.ViewBuildCommand;
import com.apkide.ui.commands.view.ViewErrorCommand;
import com.apkide.ui.commands.view.ViewFileCommand;
import com.apkide.ui.commands.view.ViewSearchCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class AppCommands {

	private static final HashSet<Class<?>> sClasses = new HashSet<>(50);
	private static final List<Command> sCommands = new ArrayList<>(50);


	static {
		//Main
		addAll(new Command[]{
				new UndoCommand(),
				new RedoCommand(),
				new RunCommand(),
				new EditModeCommand(),
				new NavigateModeCommand()
		});

		addAll(new Command[]{
				new FindCommand(),
				new FindInFileCommand(),
				new FindReplaceCommand(),
				new FindReplaceInFileCommand(),
				new FindUsagesCommand()
		});

		addAll(new Command[]{
				new ViewFileCommand(),
				new ViewErrorCommand(),
				new ViewSearchCommand(),
				new ViewBuildCommand()
		});

		addAll(new Command[]{
				new BackwardCommand(),
				new ForwardCommand(),
				new GotoCommand(),
				new GotoDefinitionCommand()
		});

		addAll(new Command[]{
				new OptimizeImportsCommand(),
				new ReformatCodeCommand(),
				new IndentLinesCommand(),
				new GenerateCommand(),
				new SurroundWithCommand(),
				new OutLineCommentCommand(),
				new OutBlockCommentCommand(),
				new UnOutComment(),
		});

		addAll(new Command[]{
				new RenameCommand(),
				new InlineCommand(),
				new SafeDeleteCommand()
		});

		addAll(new Command[]{
				new MakeProjectCommand(),
				new CleanProjectCommand(),
				new RebuildProjectCommand(),
				new CloseProjectCommand()
		});

		addAll(new Command[]{
				new CloseFileCommand(),
				new SaveAllCommand(),
				new GotoSettingsCommand(),
				new ExitCommand()
		});
		//END Main

		//EditorView

		addAll(new Command[]{
				new PickColorCommand(),
				new CutCommand(),
				new CopyCommand(),
				new PasteCommand(),
				new SelectAllCommand()
		});
		//END EditorView
	}


	public static void addAll(@NonNull Command[] commands) {
		for (Command command : commands) {
			if (command != null) {
				if (!sClasses.contains(command.getClass())) {
					sClasses.add(command.getClass());
					sCommands.add(command);
				}
			}
		}
	}

	public static void add(@NonNull Command command) {
		if (sClasses.contains(command.getClass())) {
			return;
		}
		sClasses.add(command.getClass());
		sCommands.add(command);
	}

	public static void remove(@NonNull Command command) {
		if (sClasses.contains(command.getClass())) {
			sCommands.remove(command);
			sClasses.remove(command.getClass());
		}
	}

	public static void remove(int index) {
		Command command = sCommands.get(index);
		if (command != null) {
			sCommands.remove(index);
			sClasses.remove(command.getClass());
		}
	}

	public static Command getCommand(int index) {
		return sCommands.get(index);
	}

	@NonNull
	public static List<Command> getCommands() {
		return Collections.unmodifiableList(sCommands);
	}

	public int getCommandCount() {
		return sCommands.size();
	}

}
