package com.apkide.ui.editors;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apkide.common.AppLog;
import com.apkide.common.FileSystem;
import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.services.openfile.OpenFileModel;
import com.apkide.ui.services.openfile.OpenFileModelFactory;
import com.apkide.ui.services.openfile.OpenFileServiceListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditorPager extends ViewPager implements OpenFileModelFactory, OpenFileServiceListener {
    public EditorPager(Context context) {
        super(context);
        initView();
    }
    
    public EditorPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    private final List<View> myViews = new ArrayList<>();
    
    private void initView() {
        try {
            Field declaredField = requireNonNull(getClass().getSuperclass()).getDeclaredField("mTouchSlop");
            declaredField.setAccessible(true);
            declaredField.setInt(this, 15);
        } catch (Exception ignored) {
        }
        
        setPageMargin((int) getContext().getResources().getDisplayMetrics().density);
        setPageMarginDrawable(new ColorDrawable(getContext().getColor(R.color.colorSecondaryVariant)));
        
        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return myViews.size();
            }
            
            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view.equals(object);
            }
            
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = myViews.get(position);
                container.addView(view);
                return view;
            }
            
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
            
            private int index = -1;
            
            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                if (this.index != position) {
                    this.index = position;
                }
            }
    
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                IDEEditor editor =getEditor(position);
                if (editor!=null){
                    return FileSystem.getName(editor.getIDEEditorModel().getFilePath());
                }
                return super.getPageTitle(position);
            }
        });
        
        App.getOpenFileService().addOpenFileModelFactory(this);
        App.getOpenFileService().addListener(this);
    }
    
    @SuppressLint("InflateParams")
    @Override
    public void fileOpened(@NonNull String filePath, @NonNull OpenFileModel fileModel) {
        AppLog.s(this, "fileOpened: " + filePath);
        
        for (IDEEditor editor : getEditors()) {
            if (editor.getIDEEditorModel().getFilePath().equals(filePath)) {
                int index = foundEditor(filePath);
                
                setCurrentItem(index);
                return;
            }
        }
        
        List<IDEEditor> editors = getEditors();
        for (int i = 0; i < editors.size(); i++) {
            if (editors.get(i).getIDEEditorModel().getFilePath().equals(filePath)) {
                myViews.remove(i);
                requireNonNull(getAdapter()).notifyDataSetChanged();
                requestLayout();
                return;
            }
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.editor, null);
        IDEEditor editor = view.findViewById(R.id.editor);
        myViews.add(view);
        editor.setModel((IDEEditorModel) fileModel);
        requireNonNull(getAdapter()).notifyDataSetChanged();
        openEditor(filePath);
        requestLayout();
        postDelayed(() -> {
            editor.getScrollView().scrollToCaretVisible();
            editor.getEditorView().redraw();
        }, 100L);
    }
    
    @Override
    public void fileClosed(@NonNull String filePath, @NonNull OpenFileModel fileModel) {
        AppLog.s(this, "fileClosed: " + filePath);
        List<IDEEditor> editors = getEditors();
        for (int i = 0; i < editors.size(); i++) {
            if (editors.get(i).getIDEEditorModel().getFilePath().equals(filePath)) {
                myViews.remove(i);
                requireNonNull(getAdapter()).notifyDataSetChanged();
                requestLayout();
                return;
            }
        }
    }
    
    @NonNull
    @Override
    public String getName() {
        return "IDEEditor";
    }
    
    @Override
    public boolean isSupportedFile(@NonNull String filePath) {
        if (FileSystem.isBinary(filePath)) {
            return false;
        }
        return true;
    }
    
    @NonNull
    @Override
    public OpenFileModel createFileModel(@NonNull String filePath) throws IOException {
        IDEEditorModel model = new IDEEditorModel(filePath);
        model.sync();
        return model;
    }
    
    public void openEditor(String filePath) {
        int index = foundEditor(filePath);
        setCurrentItem(index);
        
    }
    
    
    public int foundEditor(String filePath) {
        for (int i = 0; i < myViews.size(); i++) {
            if (getEditor(i).getIDEEditorModel().getFilePath().equals(filePath)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getEditorCount() {
        return myViews.size();
    }
    
    @Nullable
    public IDEEditor getCurrentEditor() {
        int currentItem = getCurrentItem();
        if (currentItem < 0) {
            return null;
        }
        return getEditor(currentItem);
    }
    
    public IDEEditor getEditor(int index) {
        if (index < 0 || index >= myViews.size()) {
            return null;
        }
        return myViews.get(index).findViewById(R.id.editor);
    }
    
    public List<IDEEditor> getEditors() {
        List<IDEEditor> editors = new ArrayList<>(myViews.size());
        for (int i = 0; i < myViews.size(); i++) {
            editors.add(getEditor(i));
        }
        return editors;
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && getCurrentEditor() != null &&
                getCurrentEditor().isTouchEventInsideHandle(ev)) {
            return false;
        }
        
        return super.onInterceptTouchEvent(ev);
    }
}
