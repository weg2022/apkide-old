package com.apkide.ui.browsers.file;

import com.apkide.ui.R;

public enum FileTypes {
    Text(R.mipmap.file_type_txt, ".txt", ".log"),
    Java(R.mipmap.file_type_java, ".java"),
    Class(R.mipmap.file_type_class, ".class"),
    Dex(R.mipmap.file_type_dex, ".dex"),
    Smali(R.mipmap.file_type_smali, ".smali"),
    Flex(R.mipmap.file_type_flex, ".flex"),
    Json(R.mipmap.file_type_json, ".json"),
    Yaml(R.mipmap.file_type_yaml, ".yaml"),
    PNG(R.mipmap.file_type_png, ".png"),
    JPG(R.mipmap.file_type_jpg, ".jpg"),
    Arsc(R.mipmap.file_type_arsc, ".arsc"),
    ;
    public final int icon;
    public final String[] extensions;

    FileTypes(int icon, String... extensions) {
        this.icon = icon;
        this.extensions = extensions;
    }

    public static int getIcon(String fileName) {
        for (FileTypes types : values()) {
            for (String extension : types.extensions) {
                if (fileName.endsWith(extension))
                    return types.icon;
            }
        }
        return R.mipmap.file_type_unknown;
    }
}
