// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Method;

public class AndroidPermission {
    private final static String METHOD_NAME_CHECK_SELF_PERMISSION = "checkSelfPermission";
    private final static String METHOD_NAME_REQUEST_PERMISSIONS = "requestPermissions";
    private final static String METHOD_NAME_SHOULDSHOW_REQUEST_PERMISSION_RATIONALE = "shouldShowRequestPermissionRationale";

    private static final int REQUEST_CODE = 1;

    public static boolean requestPermissions(Context context, String...permissions) {
        if (!isAndroidMVersionOrAbove())
            return false;
        try {
            Method method = context.getClass().getMethod(METHOD_NAME_REQUEST_PERMISSIONS, String[].class, int.class);
            method.setAccessible(true);
            method.invoke(context, new Object[] {permissions, 1});
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
    }

    public static boolean havePermission(Context context, String permission) {
        if (!isAndroidMVersionOrAbove())
            return true;

        try {
            Method method = context.getClass().getMethod(METHOD_NAME_CHECK_SELF_PERMISSION, String.class);
            method.setAccessible(true);
            return PackageManager.PERMISSION_GRANTED == (Integer) method.invoke(context, new Object[] {permission});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isAndroidMVersionOrAbove() {
        return "MNC".equals(Build.VERSION.CODENAME)
                || Build.VERSION.SDK_INT >= 23;
    }
}
