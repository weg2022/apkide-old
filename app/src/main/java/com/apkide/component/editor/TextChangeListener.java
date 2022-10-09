package com.apkide.component.editor;

public interface TextChangeListener {
	void textSet(TextChangedEvent event);
	
	void textChanging(TextChangingEvent event);
	
	void textChanged(TextChangedEvent event);
}
