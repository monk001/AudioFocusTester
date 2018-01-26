// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

class BpPopWindowService {
    private static final int SVC_STATE_BINDFAILURE = -1;
    private static final int SVC_STATE_UNINIT = 0;
    private static final int SVC_STATE_BINDING = 1;
    private static final int SVC_STATE_CONNECTED = 2;
    private static final int SVC_STATE_DISCONNECTED = 3;
    private static String TAG = "BpPopWindowService";

    interface Listener {
        void onConnected();
        void onDisConnected();
    }

    private static int sSvcState = SVC_STATE_UNINIT;

    private static Listener sListener;
    private static IPopWindowService sPopWindowService;

    static void init(Context ctx, Listener listener) {
        if (sSvcState == SVC_STATE_UNINIT) {
            try {
                sSvcState = SVC_STATE_BINDING;
                sListener = listener;
                new Thread(new BindServiceTask(ctx.getApplicationContext())).start();
            } catch (Throwable ex) {
                ex.printStackTrace();
                sSvcState = SVC_STATE_BINDFAILURE;
            }
        }
    }

    private static class BindServiceTask implements Runnable {
        Context mCtx;

        BindServiceTask(Context ctx) {
            mCtx = ctx;
        }

        @Override
        public void run() {
            bindService(mCtx);
        }
    }

    private static void bindService(Context ctx) {
        final String svcName = PopWindowService.class.getName();
        try {
            ServiceConnectionImpl conn = new ServiceConnectionImpl();
            Intent intent = new Intent(ctx, PopWindowService.class);
            Log.v(TAG, "try to bind " + svcName + " ...");
            if (ctx.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
                Log.v(TAG, "exec bindService for " + svcName + " done.");
            } else {
                Log.w(TAG, "try to bind " + svcName + " failure.");
            }
        } catch (Throwable throwable) {
            Log.w(TAG, "try to bind " + svcName + " exception: " + throwable);
        }
    }

    private static class ServiceConnectionImpl implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            Log.v(TAG, "onServiceConnected, name/binder " + name + "/" + service);
            if (Looper.getMainLooper() != Looper.myLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onServiceConnected(service);
                    }
                });
            } else {
                onServiceConnected(service);
            }
        }

        private void onServiceConnected(IBinder service) {
            sPopWindowService = IPopWindowService.Stub.asInterface(service);
            sSvcState = SVC_STATE_CONNECTED;
            if (sListener != null)
                sListener.onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected, name " + name);
            if (Looper.getMainLooper() != Looper.myLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onServiceDisconnectedImpl();
                    }
                });
            } else {
                onServiceDisconnectedImpl();
            }
        }

        private void onServiceDisconnectedImpl() {
            sSvcState = SVC_STATE_DISCONNECTED;
            if (sListener != null)
                sListener.onDisConnected();
        }
    }

    static boolean togglePopWindow() {
        if (sPopWindowService != null) {
            try {
                return sPopWindowService.togglePopWindow();
            } catch (Throwable e) {
            }
        }
        return false;
    }
}
