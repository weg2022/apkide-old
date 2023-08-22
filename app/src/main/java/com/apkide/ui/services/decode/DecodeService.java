package com.apkide.ui.services.decode;

import androidx.annotation.NonNull;

import com.apkide.apktool.engine.service.IProcessingCallback;
import com.apkide.ui.App;
import com.apkide.ui.services.AppService;

public class DecodeService implements AppService {

    private DecodeListener myListener;
    
    @Override
    public void initialize() {
    
    }
    
    public void setListener(DecodeListener listener) {
        myListener = listener;
    }
    
    public void decode(String apkFilePath, String outputPath) {
        App.getAPkService().decode(apkFilePath, outputPath, new IProcessingCallback() {
            @Override
            public void processPrepare(@NonNull String sourcePath) {
                if (myListener!=null)
                    myListener.decodeStarted(sourcePath);
            }
    
            @Override
            public void processing(@NonNull String msg) {
                if (myListener!=null)
                    myListener.decodeOutput(msg);
            }
    
            @Override
            public void processError(@NonNull Throwable error) {
                if (myListener!=null)
                    myListener.decodeFailed(error);
            }
    
            @Override
            public void processDone(@NonNull String outputPath) {
                if (myListener!=null)
                    myListener.decodeFinished(outputPath);
            }
        });
    }
    
    @Override
    public void shutdown() {
    
    }
}
