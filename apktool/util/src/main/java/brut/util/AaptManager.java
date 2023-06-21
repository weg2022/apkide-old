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
package brut.util;

import android.text.TextUtils;

import com.apkide.common.AssetsProvider;

import java.io.File;

import brut.common.BrutException;

public class AaptManager {

	public static File getAapt2() throws BrutException {
		return getAapt(2);
	}

	public static File getAapt1() throws BrutException {
		return getAapt(1);
	}

	private static File getAapt(Integer version) throws BrutException {
		File aaptBinary;
		String aaptVersion = getAaptBinaryName(version);
		aaptBinary = AssetsProvider.get().foundBinary(aaptVersion);

		if (!aaptBinary.exists())
			throw new BrutException("Can't found aapt binary");

		if (aaptBinary.canExecute())
			return aaptBinary;

		throw new BrutException("aapt binary are not executable.");
	}

	public static String getAaptExecutionCommand(String aaptPath, File aapt) throws BrutException {
		if (!TextUtils.isEmpty(aaptPath)) {
			File aaptFile = new File(aaptPath);
			if (aaptFile.canRead() && aaptFile.exists()) {
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
