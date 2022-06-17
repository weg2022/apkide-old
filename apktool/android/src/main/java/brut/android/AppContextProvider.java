package brut.android;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("StaticFieldLeak")
public class AppContextProvider {
    private static AppContextProvider INSTANCE;
    private final Context fContext;

    public AppContextProvider(Context context) {
        fContext=context;
    }

    public static AppContextProvider getInstance(){
        if (INSTANCE==null){
            synchronized (AppContextProvider.class){
                Context context=ContentProvider.sContext;
                if (context==null){
                    throw new IllegalArgumentException("Context is null.");
                }
                INSTANCE=new AppContextProvider(context);
            }
        }
        return INSTANCE;
    }

    public Context getContext() {
        return fContext;
    }

}
