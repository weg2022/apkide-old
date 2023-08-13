package com.apkide.openapi.ls;

import com.apkide.common.collection.List;

import java.io.Serializable;

public class Assemble implements Serializable {

	public String rootPath;
	public String buildPath;

	public List<AssembleFile> files;

	public List<String> includeAssembles;
	public String sourceCompatibility;
	public String targetCompatibility;

}
