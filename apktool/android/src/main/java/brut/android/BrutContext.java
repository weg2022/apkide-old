package brut.android;

import android.content.Context;

public final class BrutContext {
    public static Context getContext(){
        return AppContextProvider.getInstance().getContext();
    }

    public static String getString(int id){
        return getContext().getString(id);
    }


}
