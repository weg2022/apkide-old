package brut.util;

import java.io.File;

public abstract class AssetsProvider {

	public abstract File foundBinary(String binaryName);

	public abstract File foundFile(String fileName);

	public abstract File getTempDirectory();

	private static final Object lock = new Object();

	private static AssetsProvider assetsProvider;

	public static void set(AssetsProvider provider) {
		synchronized (lock) {
			assetsProvider = provider;
		}
	}

	public static AssetsProvider get() {
		synchronized (lock) {
			return assetsProvider;
		}
	}

}
