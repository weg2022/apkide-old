package com.apkide.core;

import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.apkide.core.androlib.util.BrutIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressLint("StaticFieldLeak")
public final class Core {
	private static final Logger LOGGER=Logger.getLogger(Core.class.getName());
	private static Context sContext;
	
	
	public static void setContext(Context context) {
		sContext = context;
	}
	
	public static Context getContext() {
		return sContext;
	}
	
	public static InputStream fileAsInputStream(File file) throws IOException {
		return new FileInputStream(file);
	}
	
	public static InputStream openAssets(String path) throws IOException {
		return getContext().getAssets().open(path);
	}
	
	public static InputStream openAssets(String path, boolean executable, String arch, String compatibleArch) throws IOException {
		if (executable) {
			InputStream inputStream;
			try {
				inputStream = openAssets(arch + separator + path);
			} catch (IOException e) {
				return openAssets(compatibleArch + separator + path);
			}
			return inputStream;
		}
		return openAssets(path);
	}
	
	public static String[] getCompatibleArch() throws IllegalArgumentException {
		var array = Build.SUPPORTED_ABIS;
		for (String arch : array) {
			switch (arch) {
				case "arm64-v8a":
					return new String[]{"arm64", "arm-pie"};
				case "armeabi-v7a":
					return new String[]{"arm-pie"};
				case "x86":
					return new String[]{"x86", "arm-pie"};
				case "x86_64":
					return new String[]{"x86-pie", "x86"};
			}
		}
		throw new IllegalArgumentException("The cpu architecture of the current device is not yet supported．");
	}
	
	
	public static void extract(InputStream inputStream, OutputStream outputStream) {
		try {
			BrutIO.copyAndClose(inputStream, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void extractExecutable(String executable, File target) throws IOException {
		var abis = getCompatibleArch();
		extractExecutable(executable, abis[0], abis.length == 2 ? abis[1] : abis[0], target);
	}
	
	private static void extractExecutable(String executable, String arch, String compatibleArch, File target) throws IOException {
		var inputStream = openAssets(executable, true, arch, compatibleArch);
		try {
			if (!target.exists()) {
				target.createNewFile();
			}
			extract(inputStream, new FileOutputStream(target));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!target.setExecutable(true))
			target.setExecutable(true, true);
	}
	
	public static String getExecutableFilePath(String executable) throws IOException {
		return getExecutableFile(executable).getAbsolutePath();
	}
	
	public static File getExecutableFile(String executable) throws IOException {
		File file = new File(getExecutableFileDir(), executable);
		if (!file.exists() || file.isDirectory())
			extractExecutable(executable, file);
		return file;
	}
	
	public static File getExecutableFileDir() {
		return mkdirIfNot(new File(getContext().getFilesDir(), "bin"));
	}
	
	public static File getFrameworkFileDir() {
		return mkdirIfNot(new File(getContext().getFilesDir(), "framework"));
	}
	
	public static File getAndroidFrameworkFile() throws IOException {
		File file = new File(getFrameworkFileDir(), "android-framework.jar");
		if (!file.exists() || file.isDirectory()) {
			if (!file.exists())
				file.createNewFile();
			extract(openAssets("android-framework.jar"), new FileOutputStream(file));
		}
		return file;
		
	}
	
	public static File mkdirIfNot(File file) {
		if (!file.exists() || file.isFile())
			file.mkdirs();
		return file;
	}
	
	
}
