package brut.androlib.options;

import java.util.Collection;

public abstract class BuildOptions {
	
	private static BuildOptions options;
	
	public static void set(BuildOptions options) {
		synchronized (BuildOptions.class) {
			BuildOptions.options = options;
		}
	}
	
	public static BuildOptions get() {
		synchronized (BuildOptions.class) {
			return options;
		}
	}
	
	public abstract boolean isForceBuildAll();
	
	public abstract boolean isForceDeleteFramework();
	
	public abstract boolean isDebugMode();
	
	public abstract boolean isNetSecConf();
	
	public abstract boolean isVerbose();
	
	public abstract boolean isCopyOriginalFiles();
	
	public abstract boolean isUpdateFiles();
	
	public abstract void setIsFramework(boolean isFramework);
	
	public abstract boolean isFramework();
	
	public abstract void setResourceAreCompressed(boolean resourceAreCompressed);
	
	public abstract boolean isResourcesAreCompressed();
	
	public abstract boolean isUseAapt2();
	
	public abstract boolean isNoCrunch();
	
	public abstract int getForceApi();
	
	public abstract void setDoNotCompressResources(Collection<String> doNotCompress);
	
	public abstract Collection<String> getDoNotCompress();
	
	public abstract void setFrameworkFolderLocation(String frameworkFolderLocation);
	
	public abstract String getFrameworkFolderLocation();
	
	public abstract void setFrameworkTag(String frameworkTag);
	
	public abstract String getFrameworkTag();
	
	public abstract String getAaptPath();
	
}
