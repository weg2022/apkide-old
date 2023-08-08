package com.apkide.ui.views;

import static java.lang.Math.max;

import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

import com.apkide.common.app.AppLog;
import com.apkide.ui.views.editor.Editor;

public class EditorView extends Editor {
    private final WindowManager windowManager;

    public EditorView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCaretVisibility(true);
    }

    public CodeEditTextEditorModel getCodeEditTextEditorModel() {
        return (CodeEditTextEditorModel) getEditorModel();
    }

    private CodeEditText getCodeEditText(){
        return (CodeEditText) getParent();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            width = windowManager.getCurrentWindowMetrics().getBounds().width();
            height = windowManager.getCurrentWindowMetrics().getBounds().height();
        } else {
            width = windowManager.getDefaultDisplay().getWidth();
            height = windowManager.getDefaultDisplay().getHeight();
        }
        setMeasuredDimension(max(getMeasuredWidth(), width), max(getMeasuredHeight(), height));
        AppLog.d("w:" + getMeasuredWidth() + " h:" + getMeasuredHeight());
    }
}
