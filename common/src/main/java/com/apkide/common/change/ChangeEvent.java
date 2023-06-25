package com.apkide.common.change;

import java.util.EventObject;

public class ChangeEvent extends EventObject {
	
	private static final long serialVersionUID = -3776896837874240056L;
	
	public ChangeEvent(Object source) {
		super(source);
	}
}
