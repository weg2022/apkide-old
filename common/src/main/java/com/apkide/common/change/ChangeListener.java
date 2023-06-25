package com.apkide.common.change;

import androidx.annotation.NonNull;

import java.util.EventListener;

/**
 * Defines an object which listens for ChangeEvents.
 */
public interface ChangeListener extends EventListener {
	/**
	 * Invoked when the target of the listener has changed its state.
	 *
	 * @param e a ChangeEvent object
	 */
	void stateChanged(@NonNull ChangeEvent event);
}
