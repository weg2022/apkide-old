package com.apkide.ls.api.callback;

public interface StopCallback {

	boolean stopAPISearing();

	boolean stopCodeCompleting();

	boolean stopHighlighting();

	boolean stopRefactoring();

	boolean stopSymbolSearing();

	boolean stopUsageSearing();
}
