package com.apkide.ui.browsers.build;

import static android.view.LayoutInflater.from;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apkide.ui.App;
import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserBuildBinding;
import com.apkide.ui.services.build.BuildListener;
import com.apkide.ui.services.decode.DecodeListener;

public class BuildBrowser extends HeaderBrowserLayout implements BuildListener, DecodeListener {
    
    private BrowserBuildBinding myBinding;
    
    public BuildBrowser(@NonNull Context context) {
        super(context);
        myBinding = BrowserBuildBinding.inflate(from(getContext()), this, false);
        addView(myBinding.getRoot(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        
        getHeaderIcon().setImageResource(R.drawable.errors_no);
        getHeaderLabel().setText(R.string.browser_label_build);
        getHeaderHelp().setOnClickListener(view -> {
        
        });
        myBinding.buildProgressBar.setVisibility(GONE);
        myBinding.buildOutputView.getEditorView().setEditable(false);
        myBinding.buildOutputView.getEditorView().setCaretVisibility(true);
        App.getBuildService().setListener(this);
        App.getDecodeService().setListener(this);
    }
    
    @NonNull
    @Override
    public String getBrowserName() {
        return "BuildBrowser";
    }
    
    @Override
    public void buildStarted(@NonNull String rootPath) {
   
    }
    
    @Override
    public void buildOutput(@NonNull String message) {
 
    }
    
    @Override
    public void buildFailed(@NonNull Throwable err) {
    
    }
    
    @Override
    public void buildFinished(@NonNull String outputPath) {
    
    }
    
    @Override
    public void decodeStarted(@NonNull String rootPath) {
  
    }
    
    @Override
    public void decodeOutput(@NonNull String message) {
    
    }
    
    @Override
    public void decodeFailed(@NonNull Throwable err) {
    
    }
    
    @Override
    public void decodeFinished(@NonNull String outputPath) {
    
    }
    
}
