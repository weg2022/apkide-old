package com.apkide.common;

import androidx.annotation.NonNull;

import java.util.EventObject;

public class ChangeEvent extends EventObject {
	
	private static final long serialVersionUID = -3776896837874240056L;

	public ChangeEvent(@NonNull Object source) {
		super(source);
	}

}
