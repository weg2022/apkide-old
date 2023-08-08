package com.apkide.ui.browsers.file;


import com.apkide.common.FileSystem;
import com.apkide.ui.R;

public class FileIcons {


	public static int getIcon(String name){
		String ext= FileSystem.getExtension(name).toLowerCase();
		switch (ext){
			case ".java":
				return R.mipmap.file_type_java;
			case ".xml":
				return R.mipmap.file_type_xml;
			case ".c":
				return R.mipmap.file_type_c;
			case ".cpp":
			case ".cxx":
				return R.mipmap.file_type_cpp;
			case ".h":
			case ".hpp":
				return R.mipmap.file_type_h;
			case ".html":
				return R.mipmap.file_type_html;
			case ".css":
				return R.mipmap.file_type_css;
			case ".js":
				return R.mipmap.file_type_js;
			case ".txt":
			case ".log":
				return R.mipmap.file_type_txt;
			case ".classpath":
			case ".project":
			case ".pom":
			case ".gradle":
				return R.mipmap.project_properties;
			default:
				return R.mipmap.file_type_unknown;
		}
	}
}
