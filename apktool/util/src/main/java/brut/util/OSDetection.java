package brut.util;

import android.os.Build;

import java.util.Arrays;

public class OSDetection {
	private static final String[] architectures = Build.SUPPORTED_ABIS;


	public static boolean isAarch64() {
		return Arrays.stream(architectures)
				.anyMatch(arch -> arch.contains("arm64-v8a"));
	}
	
	public static boolean isAarch32() {
		return Arrays.stream(architectures)
				.anyMatch(arch -> arch.contains("armeabi-v7a"));
	}

	public static boolean isArm() {
		return Arrays.stream(architectures)
				.anyMatch(arch -> arch.contains("armeabi"));
	}
	
	public static boolean isX86() {
		return Arrays.stream(architectures)
				.anyMatch(arch -> arch.contains("x86"));
	}
	
	public static boolean isX86_64() {
		return Arrays.stream(architectures)
				.anyMatch(arch -> arch.contains("x86_64"));
	}
}
