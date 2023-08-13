package com.apkide.openapi.ls.callback;

public interface StopCallback {

	boolean stopAPISearing();

	boolean stopCodeCompleting();

	boolean stopHighlighting();

	boolean stopRefactoring();

	boolean stopSymbolSearing();

	boolean stopUsageSearing();
}
