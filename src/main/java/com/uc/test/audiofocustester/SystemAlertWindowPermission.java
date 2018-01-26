// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.Map;

class SystemAlertWindowPermission {
    @TargetApi(19)
    private static boolean isOpAllowed19(Context context) {
        try {
            final int op = 24; /* AppOpsManager.OP_SYSTEM_ALERT_WINDOW */
            android.app.AppOpsManager manager = (android.app.AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class<android.app.AppOpsManager> clazz = android.app.AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{op, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});
            return android.app.AppOpsManager.MODE_ALLOWED == mode;
        } catch (Throwable ex) {
        }
        return false;
    }

    @TargetApi(23)
    private static boolean isOpAllowed23(Context context) {
        try {
            Class<?> cls = Class.forName("android.provider.Settings");
            Method dispatchMethod = cls.getMethod("canDrawOverlays", new Class[]{Context.class});
            return (Boolean) dispatchMethod.invoke(null, context);
        } catch (Throwable ex) {
            return false;
        }
    }

    static boolean isOpAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= 23 /* Build.VERSION_CODES.M */) {
            return isOpAllowed23(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return isOpAllowed19(context);
        } else {
            return true;
        }
    }

    static boolean canUseAlertWindowWithToastType() {
        return Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 24;
    }

    static void showPermisionGuide(Context context) {
        String guidParam;
        guidParam = RomUtils.BRAND == RomUtils.BRAND_MEIZU ?
                "type=startActivity, " +
                        "action=com.meizu.safe.security.SHOW_APPSEC, " +
                        "category=android.intent.category.DEFAULT, " +
                        "extra=packageName/$PkgName" :
                Build.VERSION.SDK_INT >= 23 /* Build.VERSION_CODES.M */ ?
                        "type=startActivity, " +
                                "action=android.settings.action.MANAGE_OVERLAY_PERMISSION, " +
                                "data=package:$PkgName"
                        :
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD /* 9 */ ?
                                "type=startActivity, " +
                                        "action=android.settings.APPLICATION_DETAILS_SETTINGS, " +
                                        "data=package:$PkgName"
                                :
                                "type=startActivity, " +
                                        "action=android.intent.action.VIEW, " +
                                        "pkgName=com.android.settings, " +
                                        "className=com.android.settings.InstalledAppDetails, " +
                                        "extra=" + (Build.VERSION.SDK_INT == 8 ? "pkg" : "com.android.settings.ApplicationPkgName") + "/$PkgName";
        Map<String, String> m = Util.toMap(guidParam);
        Util.startActivity(context, m);
    }
}
