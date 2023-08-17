package com.apkide.ui.browsers.file;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ui.R;

public class FileIcons {
	public static int getIcon(@NonNull String filePath){
		String ext= FileSystem.getExtensionName(filePath).toLowerCase();
		switch (ext){
			case ".java":
				return R.drawable.file_type_java;
			case ".xml":
				return R.drawable.file_type_xml;
			case ".c":
				return R.drawable.file_type_c;
			case ".cpp":
			case ".cxx":
				return R.drawable.file_type_cpp;
			case ".h":
			case ".hpp":
				return R.drawable.file_type_h;
			case ".html":
				return R.drawable.file_type_html;
			case ".css":
				return R.drawable.file_type_css;
			case ".js":
				return R.drawable.file_type_js;
			case ".txt":
			case ".log":
				return R.drawable.file_type_txt;
			case ".classpath":
			case ".project":
			case ".pom":
			case ".gradle":
				return R.drawable.project_properties;
			default:
				return R.drawable.file_type_unknown;
		}
	}
}
