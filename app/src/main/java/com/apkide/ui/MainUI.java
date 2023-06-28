package com.apkide.ui;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.apkide.ui.browsers.BrowserPager.BUILD_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.FILE_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.FIND_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.GIT_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.PROBLEM_BROWSER;
import static com.apkide.ui.browsers.BrowserPager.PROJECT_BROWSER;
import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
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
import com.apkide.ui.services.FileSystem;
import com.apkide.ui.services.navigate.FileSpan;

public class MainUI extends ThemeUI implements SharedPreferences.OnSharedPreferenceChangeListener {

    private UiMainBinding mainBinding;

    private ActionBarDrawerToggle mainDrawerToggle;

    private SharedPreferences browserPreferences;
    private long lastBackPressedTimeMillis;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        App.init(this);
        super.onCreate(savedInstanceState);
        AppPreferences.getPreferences().registerOnSharedPreferenceChangeListener(this);
        mainBinding = UiMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setSupportActionBar(mainBinding.mainContentToolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainDrawerToggle = new ActionBarDrawerToggle(this, mainBinding.mainDrawerLayout,
                android.R.string.ok, android.R.string.cancel);
        mainBinding.mainDrawerLayout.addDrawerListener(mainDrawerToggle);
        mainDrawerToggle.syncState();

        mainBinding.mainContentToolbar.setNavigationOnClickListener(v -> {
            if (isDrawerOpened())
                closeDrawer();
            else
                openDrawer();
        });
        mainBinding.mainDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                lockDrawer();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                unlockDrawer();
            }
        });

        mainBinding.mainDrawerLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                closeDrawer();
            return false;
        });
        mainBinding.mainBrowserPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == GIT_BROWSER) {
                    unlockDrawer();
                } else {
                    if (!isLockDrawer())
                        lockDrawer();
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                while (gotoBackward()) {

                }

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
        checkPermissions();

        initializeBrowser();
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

    private void initializeBrowser() {
        browserPreferences = getSharedPreferences("browser_view", MODE_PRIVATE);
        int current = browserPreferences.getInt("current", -1);
        if (current >= 0)
            toggleBrowser(current, true);
    }


    public void toggleFileBrowser() {

        toggleBrowser(FILE_BROWSER, false);
    }

    public void toggleProjectBrowser() {

        toggleBrowser(PROJECT_BROWSER, false);
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

    public void toggleGitBrowser() {

        toggleBrowser(GIT_BROWSER, false);
    }

    public void toggleBrowser(int index, boolean refresh) {
        //TODO: 加入是否需要自动打开选项
        if (!isDrawerOpened()) {
            openDrawer();
        }
        getBrowserPager().toggle(index, refresh);
    }

    public void saveCurrentBrowser(int index) {
        browserPreferences.edit().putInt("current", index).apply();
    }


    public BrowserPager getBrowserPager() {
        return mainBinding.mainBrowserPager;
    }


    public void gotoForward() {
        FileSpan span = App.getNavigateService().forward();
        if (span != null) {
            App.getNavigateService().setEnabled(false);
            navigateTo(span);
        }
    }

    public boolean gotoBackward() {
        FileSpan span = App.getNavigateService().backward();
        if (span != null) {
            App.getNavigateService().setEnabled(false);
            navigateTo(span);
            return true;
        }
        return false;
    }

    public void navigateTo(FileSpan span) {
        navigateTo(span, true);
    }

    public void navigateTo(FileSpan span, boolean focus) {
        if (span == null || !FileSystem.exists(span.filePath)) {
            return;
        }
        App.getNavigateService().setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (AppCommands.menuCommandPreExec(menu)) {

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        boolean commandExec = AppCommands.menuCommandExec(item);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        if (mainBinding != null)
            mainBinding = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            changeTheme(newConfig.isNightModeActive());
        else
            changeTheme(isNightModeActive(newConfig.uiMode));
    }

    private void changeTheme(boolean dark) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private static final int REQUEST_PERMISSION_CODE = 10014;

    private void checkPermissions() {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Lack of access to file");
                builder.setMessage("Next, you need to grant access to the file...");
                builder.setPositiveButton("yes", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                });
                builder.setNegativeButton("refuse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } else {*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        //}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PERMISSION_GRANTED) {

                        return;
                    } else {
                        Toast.makeText(this, "Failed to get access to external storage...", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }
    }
}
