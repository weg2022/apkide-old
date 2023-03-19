package com.apkide.ui;

import static com.apkide.common.IOUtils.copyBytes;
import static com.apkide.common.IOUtils.safeClose;
import static java.io.File.separator;

import android.os.Build;

import androidx.multidex.MultiDexApplication;

import com.apkide.common.FileUtils;
import com.apkide.language.api.LanguageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import brut.androlib.options.BuildOptions;
import brut.util.AssetsProvider;
import brut.util.OSDetection;

public class IDEApplication extends MultiDexApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		AppPreferences.init(getApplicationContext());
		App.initApp(getApplicationContext());
		AssetsProvider.set(new AssetsProvider() {
			@Override
			public File foundBinary(String binaryName) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					return new File(getApplicationContext().getApplicationInfo().nativeLibraryDir
							+ separator + "lib" + binaryName + ".so");
				}
				String targetName = FileUtils.getFileName(binaryName);
				String arch = "";
				if (OSDetection.isAarch64())
					arch = "aarch64";
				else if (OSDetection.isAarch32())
					arch = "aarch32";
				else if (OSDetection.isX86_64())
					arch = "x64";
				else if (OSDetection.isX86())
					arch = "x86";

				File targetFile = new File(getApplicationContext().getFilesDir(), targetName);
				if (targetFile.exists()) {
					if (!targetFile.canExecute())
						targetFile.setExecutable(true);
					return targetFile;
				}

				String fullFileName = "bin" + separator + arch + separator + binaryName;
				InputStream inputStream = null;
				FileOutputStream outputStream = null;

				try {
					targetFile.createNewFile();
					inputStream = getApplicationContext().getAssets().open(fullFileName);
					outputStream = new FileOutputStream(targetFile);
					copyBytes(inputStream, outputStream);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					safeClose(inputStream);
					safeClose(outputStream);
				}

				if (!targetFile.canExecute())
					targetFile.setExecutable(true);
				return targetFile;
			}

			@Override
			public File foundFile(String fileName) {
				String targetName = FileUtils.getFileName(fileName);
				File targetFile = new File(getApplicationContext().getExternalFilesDir(null), targetName);
				if (targetFile.exists()) {
					//TODO: check size of file
					return targetFile;
				}

				InputStream inputStream = null;
				FileOutputStream outputStream = null;
				try {
					targetFile.createNewFile();
					inputStream = getApplicationContext().getAssets().open(fileName);
					outputStream = new FileOutputStream(targetFile);
					copyBytes(inputStream, outputStream);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					safeClose(inputStream);
					safeClose(outputStream);
				}

				return targetFile;
			}

			@Override
			public File getTempDirectory() {
				return getApplicationContext().getExternalFilesDir(".temp");
			}

			@Override
			public String getString(int resId) {
				return getApplicationContext().getString(resId);
			}
		});

		BuildOptions.set(new BuildOptions() {
			private boolean resourceAreCompressed;
			private Collection<String> doNotCompress;
			private boolean isFramework;
			private String frameworkTag;
			private String frameworkFolderLocation;

			@Override
			public boolean isForceBuildAll() {
				return AppPreferences.isForceBuildAll();
			}

			@Override
			public boolean isForceDeleteFramework() {
				return AppPreferences.isForceDeleteFramework();
			}

			@Override
			public boolean isDebugMode() {
				return AppPreferences.isDebugMode();
			}

			@Override
			public boolean isNetSecConf() {
				return AppPreferences.isNetSecConf();
			}

			@Override
			public boolean isVerbose() {
				return AppPreferences.isVerbose();
			}

			@Override
			public boolean isCopyOriginalFiles() {
				return AppPreferences.isCopyOriginalFiles();
			}

			@Override
			public boolean isUpdateFiles() {
				return AppPreferences.isUpdateFiles();
			}

			@Override
			public void setIsFramework(boolean isFramework) {
				this.isFramework = isFramework;
			}

			@Override
			public boolean isFramework() {
				return isFramework;
			}

			@Override
			public void setResourceAreCompressed(boolean resourceAreCompressed) {
				this.resourceAreCompressed = resourceAreCompressed;
			}

			@Override
			public boolean isResourcesAreCompressed() {
				return resourceAreCompressed;
			}

			@Override
			public boolean isUseAapt2() {
				return true;//Always use AAPT 2
			}

			@Override
			public boolean isNoCrunch() {
				return AppPreferences.isNoCrunch();
			}

			@Override
			public int getForceApi() {
				return AppPreferences.getForceApi();
			}

			@Override
			public void setDoNotCompressResources(Collection<String> doNotCompress) {
				this.doNotCompress = doNotCompress;
			}

			@Override
			public Collection<String> getDoNotCompress() {
				return this.doNotCompress;
			}

			@Override
			public void setFrameworkFolderLocation(String frameworkFolderLocation) {
				this.frameworkFolderLocation = frameworkFolderLocation;
			}

			@Override
			public String getFrameworkFolderLocation() {
				return frameworkFolderLocation;
			}

			@Override
			public void setFrameworkTag(String frameworkTag) {
				this.frameworkTag = frameworkTag;
			}

			@Override
			public String getFrameworkTag() {
				return frameworkTag;
			}

			@Override
			public String getAaptPath() {
				//No external AAPT is required
				return null;
			}
		});

		LanguageManager.registerDefaults();
	}
}
