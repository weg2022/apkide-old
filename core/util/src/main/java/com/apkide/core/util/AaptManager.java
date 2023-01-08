/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.apkide.core.util;

import static java.io.File.separator;

import android.content.Context;

import com.apkide.core.common.BrutException;

import java.io.File;

public class AaptManager {

	public static File getAapt2(Context context) throws BrutException {
		return getAapt(context, 2);
	}

	public static File getAapt1(Context context) throws BrutException {
		return getAapt(context, 1);
	}

	private static File getAapt(Context context, Integer version) throws BrutException {
		String aaptVersion = getAaptBinaryName(version);
		String path = context.getApplicationInfo().nativeLibraryDir + separator + "lib" + aaptVersion + ".so";
		File aaptBinary = new File(path);
		if (!aaptBinary.exists())
			throw new BrutException("not found aapt binary in" + path);
		if (!aaptBinary.canExecute()) {
			aaptBinary.setExecutable(true);
		}
		return aaptBinary;
	}

	public static String getAaptExecutionCommand(String aaptPath, File aapt) throws BrutException {
		if (!aaptPath.isEmpty()) {
			File aaptFile = new File(aaptPath);
			if (aaptFile.canRead() && aaptFile.exists()) {
				if (!aaptFile.canExecute())
					aaptFile.setExecutable(true);
				return aaptFile.getPath();
			} else {
				throw new BrutException("binary could not be read: " + aaptFile.getAbsolutePath());
			}
		} else {
			return aapt.getAbsolutePath();
		}
	}

	public static String getAaptBinaryName(Integer version) {
		return "aapt" + (version == 2 ? "2" : "");
	}

}
