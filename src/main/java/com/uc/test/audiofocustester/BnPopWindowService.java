// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.uc.test.audiofocustester.IPopWindowService.Stub;

import java.lang.ref.WeakReference;

public class BnPopWindowService extends Stub {
    private static final int MSG_togglePopWindow = 1;

    private Context mContext;
    private HandlerThread mEventHandlerThread;
    private EventHandler mEventHandler;

    BnPopWindowService(Context context) {
        mContext = context.getApplicationContext();
        mEventHandlerThread = new HandlerThread("BnPopWindowService");
        mEventHandlerThread.start();
        mEventHandler = new EventHandler(this, mEventHandlerThread.getLooper());
    }

    @Override
    public boolean togglePopWindow() throws RemoteException {
        Boolean res[] = new Boolean[] { false };
        sendMessageSync(mEventHandler.obtainMessage(MSG_togglePopWindow, res));
        return res[0];
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_togglePopWindow: {
                boolean res = PopWindow.togglePopWindow(mContext);
                synchronized (BnPopWindowService.class) {
                    ((Boolean[]) msg.obj)[0] = res;
                    BnPopWindowService.class.notify();
                }
                break;
            }
        }
    }

    private void sendMessageSync(Message msg) {
        synchronized (BnPopWindowService.class) {
            msg.sendToTarget();
            try {
                BnPopWindowService.class.wait(2000);
            } catch (InterruptedException e) {
            }
        }
    }

    private static class EventHandler extends Handler {
        private WeakReference<BnPopWindowService> mOwner;

        EventHandler(BnPopWindowService owner, Looper looper) {
            super(looper);
            mOwner = new WeakReference<BnPopWindowService>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            BnPopWindowService owner = mOwner.get();
            if (owner == null)
                return;
            owner.handleMessage(msg);
        }
    }
}
