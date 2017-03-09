// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FullScreenHolder {
    private static Activity sActivity;
    private static Listener sListener;

    private static int sAcquireCount = 0;

    private static int sOriginalOrientation;
    private static int sWindowAttrFlags;
    private static int sSystemUiVisibility;

    public interface Listener {
        void onEnterFullScreen();
        void onExitFullScreen();
    }

    public static void setActivity(Activity activity) {
        sActivity = activity;
    }

    public static void setListener(Listener listener) {
        sListener = listener;
    }

    public static boolean acquired() {
        return sAcquireCount > 0;
    }

    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
    public static final int SCREEN_ORIENTATION_FULL_USER = 13;
    public static final int SCREEN_ORIENTATION_LOCKED = 14;

    public static void acquire(int requestedOrientation) {
        if (sActivity == null) {
            sAcquireCount = 0;
            return;
        }

        ++sAcquireCount;

        if (sAcquireCount > 1)
            return;

        if (sListener != null)
            sListener.onEnterFullScreen();

        if (requestedOrientation == -1)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

        Window win = sActivity.getWindow();

        // 0. set old status
        sOriginalOrientation = sActivity.getRequestedOrientation();
        sWindowAttrFlags = win.getAttributes().flags;
        sSystemUiVisibility = win.getDecorView().getSystemUiVisibility();

        // 1. set orientation
        sActivity.setRequestedOrientation(requestedOrientation);

        // 2. set window flags
        win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 3. set system ui visibility
        int visiFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            visiFlag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        win.getDecorView().setSystemUiVisibility(visiFlag);
    }

    public static void release() {
        if (sAcquireCount == 0)
            return;

        --sAcquireCount;

        if (sAcquireCount > 0)
            return;

        if (sActivity == null) {
            sAcquireCount = 0;
            return;
        }

        if (sListener != null)
            sListener.onExitFullScreen();

        Window win = sActivity.getWindow();

        // restore status

        // 1. orientation
        sActivity.setRequestedOrientation(sOriginalOrientation);

        // 2. set window flags
        WindowManager.LayoutParams attrs = win.getAttributes();
        attrs.flags = sWindowAttrFlags;
        win.setAttributes(attrs);

        // 3. set system ui visibility
        win.getDecorView().setSystemUiVisibility(sSystemUiVisibility);
    }
}
