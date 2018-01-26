// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopWindowService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new BnPopWindowService(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean res = super.onUnbind(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        return res;
    }
}
