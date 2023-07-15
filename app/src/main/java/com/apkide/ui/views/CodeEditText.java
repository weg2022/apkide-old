package com.apkide.ui.views;

import static java.lang.Math.max;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.ui.views.editor.Editor;
import com.apkide.ui.views.editor.EditorColor;
import com.apkide.ui.views.editor.TextAttribute;

public class CodeEditText extends ViewGroup {
    public CodeEditText(Context context) {
        super(context);
        initView();
    }

    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CodeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private String filePath;

    private void initView() {
        removeAllViews();
        addView(new EditorView(getContext()));
    }

    public void setFont(Typeface typeface){
        getEditorView().setTypeface(typeface);
    }
    public void setFontSize(int fontSize){
        getEditorView().setFontSize(fontSize);
    }

    public void setTabSize(int tabSize){
        getEditorView().setTabSize(tabSize);
    }

    public void syncConfigure(){
        getEditorView().syncConfigure();
    }
    public void setText(@NonNull CharSequence text){
        getEditorView().setText(text);
    }

    public String getFilePath() {
        return filePath;
    }

    public EditorView getEditorView() {
        return (EditorView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, r - l, b - t);
        getEditorView().redraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view=getChildAt(0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(),view.getMeasuredHeight());
    }

    public class EditorView extends Editor {
        private final WindowManager windowManager;

        public EditorView(Context context) {
            super(context);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        @Override
        protected void registerColorScheme() {
            setFontColor(new EditorColor(0xff212121));
            setCaretColor(new EditorColor(0xff212121));
            setCaretLineColor(new EditorColor(0xfff5f5f5));
            setLineNumberColor(new EditorColor(0xffd0d0d0));
            setWhitespaceColor(new EditorColor(0xffd0d0d0));
            setSelectionColor(new EditorColor(0xff0099cc));
            setSelectionBackgroundColor(new EditorColor(0xffa6d2ff));
            setKeywordTextAttribute(new TextAttribute(new EditorColor(0xff2c82c8), Typeface.BOLD));
            setOperatorTextAttribute(new TextAttribute(new EditorColor(0xff007c1f)));
            setSeparatorTextAttribute(new TextAttribute(new EditorColor(0xff212121)));
            setNumberLiteralTextAttribute(new TextAttribute(new EditorColor(0xffbc0000)));
            setStringLiteralTextAttribute(new TextAttribute(new EditorColor(0xffbc0000)));
            setMetadataTextAttribute(new TextAttribute(new EditorColor(0xff9E880D)));
            setIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff212121)));
            setNamespaceIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff5d5d5d),Typeface.ITALIC));
            setClassIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff0096ff)));
            setTypeIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff0096ff)));
            setVariableIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff212121)));
            setFunctionIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff174ad4)));
            setParameterIdentifierTextAttribute(new TextAttribute(new EditorColor(0xff212121)));
            setCommentTextAttribute(new TextAttribute(new EditorColor(0xff009b00)));
            setDocCommentTextAttribute(new TextAttribute(new EditorColor(0xff009b00), Typeface.ITALIC));
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
            AppLog.d("w:"+getMeasuredWidth()+" h:"+getMeasuredHeight());
        }
    }
}
