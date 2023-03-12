package com.apkide.engine.service;

import com.apkide.engine.FileHighlights;

interface IHighlightingListener{
	void highlighting(inout FileHighlights highlights);
}
