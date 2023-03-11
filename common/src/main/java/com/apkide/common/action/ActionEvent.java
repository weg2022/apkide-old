package com.apkide.common.action;

import static android.view.KeyEvent.META_ALT_ON;
import static android.view.KeyEvent.META_CTRL_ON;
import static android.view.KeyEvent.META_SHIFT_ON;

import androidx.annotation.NonNull;

import java.util.EventObject;

public class ActionEvent extends EventObject {
	
	private static final long serialVersionUID = -7011004513636683611L;
	private final String command;
	private final int metaState;
	private final long timestamp;
	
	public ActionEvent(@NonNull Object source, @NonNull String command, int metaState, long timestamp) {
		super(source);
		this.command = command;
		this.metaState = metaState;
		this.timestamp = timestamp;
	}
	
	@NonNull
	@Override
	public Object getSource() {
		return super.getSource();
	}
	
	@NonNull
	public String getCommand() {
		return command;
	}
	
	public int getMetaState() {
		return metaState;
	}
	
	public boolean isShiftPressed() {
		return (metaState & META_SHIFT_ON) != 0;
	}
	
	public boolean isCtrlPressed() {
		return (metaState & META_CTRL_ON) != 0;
	}
	
	public boolean isAltPressed() {
		return (metaState & META_ALT_ON) != 0;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		ActionEvent that = (ActionEvent) o;
		
		if (metaState != that.metaState) return false;
		if (timestamp != that.timestamp) return false;
		return command.equals(that.command);
	}
	
	@Override
	public int hashCode() {
		int result = command.hashCode();
		result = 31 * result + metaState;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}
}
