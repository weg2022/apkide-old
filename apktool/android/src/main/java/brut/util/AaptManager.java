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

import static brut.android.BrutContext.getString;

import android.annotation.SuppressLint;
import android.content.Context;

import com.apktool.android.R;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import brut.android.BrutContext;
import brut.common.BrutException;

@SuppressLint("StaticFieldLeak")
public class AaptManager {


    public static File getAapt2() throws BrutException {
        return getAapt(2);
    }

    public static File getAapt1() throws BrutException {
        return getAapt(1);
    }

    private static File getAapt(Integer version) throws BrutException {
        String aaptVersion = getAaptBinaryName(version);
        File dir=new File(BrutContext.getContext().getFilesDir(),"aapt");
        if (!dir.exists())dir.mkdirs();

        File aaptBinary = new File(dir, aaptVersion);

        if (aaptBinary.exists() && aaptBinary.length() > 0 && aaptBinary.canExecute()) {
            return aaptBinary;
        }

        String abi = OSDetection.getSupportedAbi();
        if (abi == null) {
            throw new BrutException(getString(R.string.aaptmanager_not_supported));
        }

        InputStream in;
        FileOutputStream out;
        try {
            in = BrutContext.getContext().getAssets().open("bin" + File.separator + abi + File.separator + aaptVersion);
            out = new FileOutputStream(aaptBinary);
            BrutIO.copyAndClose(in,out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (aaptBinary.setExecutable(true)) {
            return aaptBinary;
        }

        throw new BrutException(getString(R.string.aaptmanager_cant_executable));
    }

    public static String getAaptExecutionCommand(String aaptPath, File aapt) throws BrutException {
        if (!aaptPath.isEmpty()) {
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

    public static int getAaptVersion(String aaptLocation) throws BrutException {
        return getAaptVersion(new File(aaptLocation));
    }

    public static String getAaptBinaryName(Integer version) {
        return "aapt" + (version == 2 ? "2" : "");
    }

    public static int getAppVersionFromString(String version) throws BrutException {
        if (version.startsWith("Android Asset Packaging Tool (aapt) 2:")) {
            return 2;
        } else if (version.startsWith("Android Asset Packaging Tool (aapt) 2.")) {
            return 2; // Prior to Android SDK 26.0.2
        } else if (version.startsWith("Android Asset Packaging Tool, v0.")) {
            return 1;
        }
        throw new BrutException("aapt version could not be identified: " + version);
    }

    public static int getAaptVersion(File aapt) throws BrutException {
        if (!aapt.isFile()) {
            throw new BrutException("Could not identify aapt binary as executable.");
        }
        aapt.setExecutable(true);

        List<String> cmd = new ArrayList<>();
        cmd.add(aapt.getAbsolutePath());
        cmd.add("version");

        String version = OS.execAndReturn(cmd.toArray(new String[0]));

        if (version == null) {
            throw new BrutException("Could not execute aapt binary at location: " + aapt.getAbsolutePath());
        }

        return getAppVersionFromString(version);
    }
}
