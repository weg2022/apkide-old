package com.apkide.ui;

import androidx.multidex.MultiDexApplication;

import com.apkide.common.IOUtils;
import com.apkide.language.api.LanguageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import brut.androlib.options.BuildOptions;
import brut.util.SyncAssets;

public class IDEApplication extends MultiDexApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		LanguageManager.registerDefaults();
		AppPreferences.init(getApplicationContext());
		SyncAssets.set(new SyncAssets() {
			@Override
			public InputStream open(String fileName) {
				try {
					return getAssets().open(fileName);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public File foundFile(String fileName) {
				String targetName = fileName;
				//Compatible with binaries
				int index = targetName.lastIndexOf(File.separator);
				if (index >= 0)
					targetName=targetName.substring(index + 1);
				
				File file = new File(getFilesDir(), targetName);
				if (!file.exists()) {
					InputStream in = open(fileName);
					FileOutputStream out;
					try {
						out = new FileOutputStream(file);
						IOUtils.copyBytes(in, out);
					} catch (IOException e) {
						IOUtils.safeClose(in);
						throw new RuntimeException(e);
					}
					
					IOUtils.safeClose(in);
					IOUtils.safeClose(out);
				}
				//TODO Check that the file is up to date
				return file;
			}
			
			@Override
			public boolean exists(String fileName) {
				String targetName = fileName;
				//Compatible with binaries
				int index = targetName.lastIndexOf(File.separator);
				if (index >= 0)
					targetName=targetName.substring(index + 1);
				return new File(getFilesDir(), targetName).exists();
			}
			
			@Override
			public File getTempDirectory() {
				File file = new File(getFilesDir(), "temp");
				if (!file.exists())
					file.mkdir();
				return file;
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
				return AppPreferences.isUseAapt2();
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
	}
}
