package brut.util;

import java.io.File;
import java.io.InputStream;

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
	
	public abstract InputStream open(String fileName);
	
	public abstract File foundFile(String fileName);
	
	public abstract boolean exists(String fileName);
	
	public abstract File getTempDirectory();
}
