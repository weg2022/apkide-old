package com.apkide.engine.service;

import com.apkide.engine.service.IEngineListener;
import com.apkide.engine.service.IErrorListener;
import com.apkide.engine.service.IHighlightingListener;
import com.apkide.engine.service.IFindUsageListener;
import com.apkide.engine.service.IGotoSymbolListener;
import com.apkide.engine.service.INavigationListener;
import com.apkide.engine.service.IRefactoringListener;
import com.apkide.engine.service.ICodeCompletionListener;

interface IEngineService{



	void shutdown();

	void setEngineListener(IEngineListener listener);

	void setErrorListener(IErrorListener listener);

	void setHighlightingListener(IHighlightingListener listener);

	void setFindUsageListener(IFindUsageListener listener);

	void setGotoSymbolListener(IGotoSymbolListener listener);

	void setNavigationListener(INavigationListener listener);

	void setRefactoringListener(IRefactoringListener listener);

	void setCodeCompletionListener(ICodeCompletionListener listener);

}
