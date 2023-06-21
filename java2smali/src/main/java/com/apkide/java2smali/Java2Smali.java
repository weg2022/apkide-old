package com.apkide.java2smali;

import com.android.dx.command.dexer.DxContext;
import com.android.dx.command.dexer.Main;
import com.android.tools.smali.baksmali.Baksmali;
import com.android.tools.smali.baksmali.BaksmaliOptions;
import com.android.tools.smali.dexlib2.DexFileFactory;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.iface.DexFile;
import com.apkide.common.LogOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Java2Smali {


    public static boolean dexClassFile(List<String> inputClassFilePaths, String outDexPath) {
        DxContext dxContext = new DxContext(
                new LogOutputStream("Dex-Classes", LogOutputStream.LEVEL_INFO),
                new LogOutputStream("Dex-Classes", LogOutputStream.LEVEL_ERROR));

        Main.Arguments arguments = new Main.Arguments();
        arguments.outName = outDexPath;
        arguments.strictNameCheck = false;
        arguments.fileNames = inputClassFilePaths.toArray(new String[0]);
        try {
            int exitCode = new Main(dxContext).runDx(arguments);
            if (exitCode == 0) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void disassembleDexFile(String dexFilePath, String outDir) throws IOException {
        Opcodes opCodes = Opcodes.getDefault();
        DexFile file = DexFileFactory.loadDexFile(dexFilePath, opCodes);
        BaksmaliOptions options = new BaksmaliOptions();
        options.apiLevel = opCodes.api;
        Baksmali.disassembleDexFile(file, new File(outDir), 6, options);
    }
}
