package com.apkide.ui.browsers.build;

import static android.view.LayoutInflater.from;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apkide.ui.R;
import com.apkide.ui.browsers.HeaderBrowserLayout;
import com.apkide.ui.databinding.BrowserBuildBinding;
import com.apkide.ui.services.build.BuildListener;

public class BuildBrowser extends HeaderBrowserLayout implements BuildListener {
    
    private BrowserBuildBinding myBinding;
    
    public BuildBrowser(@NonNull Context context) {
        super(context);
        myBinding = BrowserBuildBinding.inflate(from(getContext()), this, false);
        addView(myBinding.getRoot());
        
        getHeaderIcon().setImageResource(R.drawable.errors_no);
        getHeaderIcon().setVisibility(INVISIBLE);
        getHeaderLabel().setText(R.string.browser_label_build);
        getHeaderHelp().setOnClickListener(view -> {
        
        });
    }
    
    @NonNull
    @Override
    public String getBrowserName() {
        return "BuildBrowser";
    }
    
    @Override
    public void buildStarted(@NonNull String rootPath) {
        myBinding.buildProgressBar.setVisibility(VISIBLE);
        myBinding.buildOutputView.getBuildOutputModel().setText("");
    }
    
    @Override
    public void buildOutput(@NonNull String message) {
        myBinding.buildOutputView.append(message);
        myBinding.buildOutputView.appendLineBreak();
    }
    
    @Override
    public void buildFailed(@NonNull Throwable err) {
        myBinding.buildProgressBar.setVisibility(INVISIBLE);
		getHeaderIcon().setImageResource(R.drawable.errors);
    }
    
    @Override
    public void buildFinished(@NonNull String outputPath) {
        myBinding.buildProgressBar.setVisibility(INVISIBLE);
		getHeaderIcon().setImageResource(R.drawable.errors_no);
    }
}
