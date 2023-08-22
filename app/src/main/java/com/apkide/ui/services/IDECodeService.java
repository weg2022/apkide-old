package com.apkide.ui.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.apkide.common.AppLog;
import com.apkide.language.Assembly;
import com.apkide.language.service.CodeEngineService;
import com.apkide.language.service.ICodeAnayzingListener;
import com.apkide.language.service.ICodeCompletionListener;
import com.apkide.language.service.ICodeEngineService;
import com.apkide.language.service.ICodeHighlightingListener;
import com.apkide.language.service.ICodeNavigationListener;
import com.apkide.language.service.ICodeRefactoringListener;
import com.apkide.language.service.IFileEditor;
import com.apkide.ui.App;


public class IDECodeService implements AppService {
    private final ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ICodeEngineService.Stub.asInterface(service);
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (myService != null) {
                try {
                    myService.shutdown();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                myService = null;
            }
        }
    };
    
    private ICodeEngineService myService;
    
    public void configureAssembly(Assembly assembly) {
        if (!isConnected()) return;
        try {
            myService.configureAssembly(assembly);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void format(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) {
        if (!isConnected()) return;
        try {
            myService.format(filePath, startLine, startColumn, endLine, endColumn, editor);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void indent(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) {
        if (!isConnected()) return;
        try {
            myService.indent(filePath, startLine, startColumn, endLine, endColumn, editor);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void analyze(String filePath) {
        if (!isConnected()) return;
        try {
            myService.analyze(filePath);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setCodeAnalyzingListener(ICodeAnayzingListener listener) {
        if (!isConnected()) return;
        try {
            myService.setCodeAnalyzingListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void completion(String filePath, int line, int column, boolean allowTypes) {
        if (!isConnected()) return;
        try {
            myService.completion(filePath, line, column, allowTypes);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void setCodeCompletionListener(ICodeCompletionListener listener) {
        if (!isConnected()) return;
        try {
            myService.setCodeCompletionListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void highlight(String filePath) {
        if (!isConnected()) return;
        try {
            myService.highlight(filePath);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void setCodeHighlightingListener(ICodeHighlightingListener listener) {
        if (!isConnected()) return;
        try {
            myService.setCodeHighlightingListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void searchUsages(String filePath, int line, int column) {
        if (!isConnected()) return;
        try {
            myService.searchUsages(filePath, line, column);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void searchSymbol(String filePath, int line, int column, boolean includeDeclaration) {
        if (!isConnected()) return;
        try {
            myService.searchSymbol(filePath, line, column, includeDeclaration);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void setCodeNavigationListener(ICodeNavigationListener listener) {
        if (!isConnected()) return;
        try {
            myService.setCodeNavigationListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void optimizeImports(String filePath) {
        if (!isConnected()) return;
        try {
            myService.optimizeImports(filePath);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void prepareRename(String filePath, int line, int column, String newName) {
        if (!isConnected()) return;
        try {
            myService.prepareRename(filePath, line, column, newName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void rename(String filePath, int line, int column, String newName) {
        if (!isConnected()) return;
        try {
            myService.rename(filePath, line, column, newName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void prepareInline(String filePath, int line, int column) {
        if (!isConnected()) return;
        try {
            myService.prepareInline(filePath, line, column);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void inline(String filePath, int line, int column) {
        if (!isConnected()) return;
        try {
            myService.inline(filePath, line, column);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void comment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        if (!isConnected()) return;
        try {
            myService.comment(filePath, startLine, startColumn, endLine, endColumn);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void removeComment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        if (!isConnected()) return;
        try {
            myService.removeComment(filePath, startLine, startColumn, endLine, endColumn);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void safeDelete(String filePath, int line, int column) {
        if (!isConnected()) return;
        try {
            myService.safeDelete(filePath, line, column);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void surroundWith(String filePath, int line, int column) {
        if (!isConnected()) return;
        try {
            myService.surroundWith(filePath, line, column);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setCodeRefactoringListener(ICodeRefactoringListener listener) {
        if (!isConnected()) return;
        try {
            myService.setCodeRefactoringListener(listener);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void restart() {
        if (!isConnected()) return;
        try {
            myService.restart();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isShutdown() {
        if (isConnected()) {
            try {
                return myService.isShutdown();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
    
    @Override
    public void initialize() {
        bindService();
    }
    
    @Override
    public void shutdown() {
        unbindService();
    }
    
    public boolean isConnected() {
        return myService != null;
    }
    
    private void bindService() {
        if (App.getMainUI().bindService(
                new Intent(App.getMainUI(), CodeEngineService.class),
                myConnection, Context.BIND_AUTO_CREATE)) {
            return;
        }
        AppLog.s(this, "绑定服务失败");
    }
    
    private void unbindService() {
        App.getMainUI().unbindService(myConnection);
    }
}
