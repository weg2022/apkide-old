package weg.test.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.apkide.language.Completion;
import com.apkide.language.FileHighlighting;
import com.apkide.language.Problem;
import com.apkide.language.service.CodeEngineService;
import com.apkide.language.service.ICodeAnayzingListener;
import com.apkide.language.service.ICodeCompletionListener;
import com.apkide.language.service.ICodeEngineService;
import com.apkide.language.service.ICodeHighlightingListener;

import java.util.List;

import weg.test.ui.databinding.MainBinding;

public class MainUI extends AppCompatActivity implements ServiceConnection {
    private static final String LOG_TAG = "MainUI";
    private ICodeEngineService myService;
    private MainBinding myBinding;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBinding = MainBinding.inflate(getLayoutInflater());
        setContentView(myBinding.getRoot());
       
      
        myBinding.startService.setOnClickListener(v -> startService());
        myBinding.stopService.setOnClickListener(v -> stopService());
        myBinding.analyze.setOnClickListener(v -> {
            if (myService != null) {
                try {
                    myService.analyze("1.test");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        myBinding.completion.setOnClickListener(v -> {
            if (myService != null) {
                try {
                    myService.completion("1.test", 0, 0, true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        myBinding.highlight.setOnClickListener(v -> {
            if (myService != null) {
                try {
                    myService.highlight("1.test");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private void startService() {
        if (bindService(new Intent(this, CodeEngineService.class),
                this, Context.BIND_AUTO_CREATE)) {
            toast("绑定引擎服务");
        } else {
            toast("绑定引擎服务失败");
        }
    }
    
    private void stopService() {
        unbindService(this);
        toast("结束引擎服务");
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        myService = ICodeEngineService.Stub.asInterface(service);
        if (myService != null) {
            toast("引擎服务已连接");
    
            try {
                myService.setCodeAnalyzingListener(new ICodeAnayzingListener.Stub() {
                    @Override
                    public void analyzeFinished(String filePath, List<Problem> list) throws RemoteException {
                        Log.d(LOG_TAG, "analyzeFinished: ");
                    }
                    
                });
                myService.setCodeCompletionListener(new ICodeCompletionListener.Stub() {
                    @Override
                    public void completionFinished(String filePath, int line, int column, boolean allowTypes, List<Completion> list) throws RemoteException {
                        Log.d(LOG_TAG, "completionFinished: ");
                    }
                    
                });
                myService.setCodeHighlightingListener(new ICodeHighlightingListener.Stub() {
                    @Override
                    public void highlightingFinished(FileHighlighting highlight) throws RemoteException {
                        Log.d(LOG_TAG, "highlightingFinished: ");
                    }
    
                    @Override
                    public void semanticHighlightingFinished(FileHighlighting highlight) throws RemoteException {
                        Log.d(LOG_TAG, "semanticHighlightingFinished: ");
                    }
                });
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        myService = null;
        toast("引擎服务销毁");
    }
    
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
