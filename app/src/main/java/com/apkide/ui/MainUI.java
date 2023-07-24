package com.apkide.ui;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import static com.apkide.ui.browsers.BrowserPager.BUILD_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.FILE_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.FIND_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.PROBLEM_BROWSER;
import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.apkide.ui.browsers.BrowserPager;
import com.apkide.ui.databinding.UiMainBinding;

public class MainUI extends StyledUI implements
        OnSharedPreferenceChangeListener,
        View.OnClickListener,
        DrawerLayout.DrawerListener,
        ViewPager.OnPageChangeListener {

    private UiMainBinding mainBinding;

    private long lastBackPressedTimeMillis;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        App.initialize(this);
        AppPreferences.registerListener(this);
        super.onCreate(savedInstanceState);
        mainBinding = UiMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        setSupportActionBar(mainBinding.mainContentToolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle mainDrawerToggle = new ActionBarDrawerToggle(this, mainBinding.mainDrawerLayout,
                android.R.string.ok, android.R.string.cancel);
        mainBinding.mainDrawerLayout.addDrawerListener(mainDrawerToggle);
        mainDrawerToggle.syncState();

        mainBinding.mainContentToolbar.setNavigationOnClickListener(v -> {
            if (isDrawerOpened())
                closeDrawer();
            else
                openDrawer();
        });
        mainBinding.mainDrawerLayout.addDrawerListener(this);
        mainBinding.mainDrawerLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                closeDrawer();
            return false;
        });
        mainBinding.mainBrowserPager.addOnPageChangeListener(this);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (isDrawerOpened()) {
                    closeDrawer();
                    return;
                }

                if (currentTimeMillis() - lastBackPressedTimeMillis < 2000)
                    exitApp();
                else {
                    lastBackPressedTimeMillis = currentTimeMillis();
                    Toast.makeText(MainUI.this, "Press Exit again...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        restoreBrowser();
    }

    public void exitApp() {
        App.shutdown();
        finish();
    }

    public boolean isDrawerOpened() {
        return mainBinding.mainDrawerLayout.isOpen();
    }

    public void closeDrawer() {
        mainBinding.mainDrawerLayout.closeDrawers();
    }

    public void openDrawer() {
        mainBinding.mainDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void lockDrawer() {
        mainBinding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    public void unlockDrawer() {
        mainBinding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public boolean isLockDrawer() {
        return mainBinding.mainDrawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN;
    }

    private void restoreBrowser() {
        int current = mainBinding.mainBrowserPager.getLastBrowser();
        if (current >= 0)
            toggleBrowser(current, true);
    }


    public void toggleFileBrowser() {

        toggleBrowser(FILE_BROWSER, false);
    }

    public void toggleProblemBrowser() {

        toggleBrowser(PROBLEM_BROWSER, false);
    }

    public void toggleBuildBrowser() {

        toggleBrowser(BUILD_BROWSER, false);
    }

    public void toggleFindBrowser() {

        toggleBrowser(FIND_BROWSER, false);
    }


    public void toggleBrowser(int index, boolean refresh) {
        if (!isDrawerOpened()) {
            openDrawer();
        }
        getBrowserPager().toggle(index, refresh);
    }

    public BrowserPager getBrowserPager() {
        return mainBinding.mainBrowserPager;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!App.isShutdown()) {
            if (AppCommands.menuCommandPreExec(menu)) {
                return true;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (!App.isShutdown()) {
            if (AppCommands.menuCommandExec(item))
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.unregisterListener(this);
        if (mainBinding != null)
            mainBinding = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith("editor")){

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        lockDrawer();
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        unlockDrawer();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == getBrowserPager().getBrowserCount() - 1) {
            unlockDrawer();
        } else {
            if (!isLockDrawer())
                lockDrawer();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
