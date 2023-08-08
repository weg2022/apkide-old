package com.apkide.ui;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import static com.apkide.ui.AppCommands.ActionBarCommand;
import static com.apkide.ui.AppCommands.MenuCommand;
import static com.apkide.ui.AppCommands.TitleMenuCommand;
import static com.apkide.ui.AppCommands.VisibleMenuCommand;
import static com.apkide.ui.AppCommands.foundActionBarCommand;
import static com.apkide.ui.AppCommands.foundMenuCommand;
import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
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

import com.apkide.common.keybinding.KeyStrokeDetector;
import com.apkide.ui.browsers.BrowserPager;
import com.apkide.ui.databinding.UiMainBinding;

public class MainUI extends StyledUI implements
        OnSharedPreferenceChangeListener,
        View.OnClickListener,
        DrawerLayout.DrawerListener,
        ViewPager.OnPageChangeListener {

    private UiMainBinding mainBinding;

    private long lastBackPressedTimeMillis;
    private KeyStrokeDetector myKeyStrokeDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        App.initialize(this);
        AppPreferences.registerListener(this);
        super.onCreate(savedInstanceState);
        myKeyStrokeDetector =new KeyStrokeDetector(this);
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
                    Toast.makeText(MainUI.this, "再按一次退出", Toast.LENGTH_SHORT).show();
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
        boolean r = super.onPrepareOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.main_options, menu);
        if (!App.isShutdown()) {
            applyMenu(menu);
        }
        return r;
    }

    private void applyMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            MenuCommand menuCommand = foundMenuCommand(item.getItemId());
            if (menuCommand != null) {
                item.setEnabled(menuCommand.isEnabled());
                if (menuCommand instanceof VisibleMenuCommand)
                    item.setVisible(((VisibleMenuCommand) menuCommand).getVisible(false));

                if (menuCommand instanceof TitleMenuCommand)
                    item.setTitle(((TitleMenuCommand) menuCommand).getTitle());
            }

            ActionBarCommand actionBarCommand = foundActionBarCommand(item.getItemId());
            if (actionBarCommand != null) {
                item.setEnabled(actionBarCommand.isEnabled());
                item.setVisible(actionBarCommand.isVisible());
            }

            if (item.hasSubMenu()) {
                applyMenu(item.getSubMenu());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuCommand menuCommand = foundMenuCommand(item.getItemId());
        if (menuCommand != null && menuCommand.isEnabled()) {
            menuCommand.run();
            return true;
        }
        ActionBarCommand actionBarCommand = foundActionBarCommand(item.getItemId());
        if (actionBarCommand != null && actionBarCommand.isVisible()) {
            actionBarCommand.run();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        myKeyStrokeDetector.onActivityKeyDown(keyCode,event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        myKeyStrokeDetector.onActivityKeyUp(keyCode,event);
        return super.onKeyUp(keyCode, event);
    }

    public KeyStrokeDetector getKeyStrokeDetector() {
        return myKeyStrokeDetector;
    }

    public IDEEditorPager getEditorPager(){
        return mainBinding.mainEditorPager;
    }
}
