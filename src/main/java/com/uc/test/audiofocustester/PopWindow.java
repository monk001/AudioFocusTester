// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

class PopWindow {
    private static WindowManager sWindowManager;

    private static FrameLayout sBaseLayer;

    public static boolean togglePopWindow(Context context) {
        if (sBaseLayer != null) {
            sWindowManager.removeView(sBaseLayer);
            sBaseLayer = null;
            return false;
        }

        context = context.getApplicationContext();

        if (sWindowManager == null)
            sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (sWindowManager == null)
            return false;

        sBaseLayer = new FrameLayout(context);
        sBaseLayer.setBackgroundColor(0x800000FF);

        TextView textView = new TextView(context);
        textView.setText("Hello World");
        textView.setTextColor(Color.RED);

        sBaseLayer.addView(textView);

        WindowManager.LayoutParams lp = createLayoutParams();

        sWindowManager.addView(sBaseLayer, lp);
        sWindowManager.updateViewLayout(sBaseLayer, lp);

        return true;
    }

    private static final int sWindowType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

    private static WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams;

        layoutParams = new WindowManager.LayoutParams(
                sWindowType,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        layoutParams.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.x = 100;
        layoutParams.y = 300;
        layoutParams.width = 300;
        layoutParams.height = 80;

        return layoutParams;
    }
}
