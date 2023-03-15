package brut.util;

import java.io.File;

public abstract class SyncAssets {
	
	private static SyncAssets assets;
	
	public static SyncAssets get(){
		synchronized (SyncAssets.class){
			return assets;
		}
	}
	
	public static void set(SyncAssets assets){
		synchronized (SyncAssets.class){
			SyncAssets.assets=assets;
		}
	}

	public abstract File foundFile(String fileName);
	
	public abstract File getTempDirectory();
}
