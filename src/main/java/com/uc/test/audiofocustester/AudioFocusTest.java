// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

class AudioFocusTest {
    static AudioFocusTest sTestingInstance;

    String MSG_PRE;
    int mRetryTimeout;

    Context mContext;
    Logger mLogger;
    boolean mIsTesting;

    public AudioFocusTest(Context context, final Logger logger) {
        mContext = context;
        mLogger = new Logger() {
            @Override
            public void i(String msg) {
                logger.i(MSG_PRE + msg);
            }

            @Override
            public void e(String msg) {
                logger.e(MSG_PRE + msg);
            }
        };
    }

    public void test() {
        mIsTesting = true;
        if (sTestingInstance != null)
            sTestingInstance.mIsTesting = false;
        sTestingInstance = this;
        requestAudioFocus();
    }

    boolean requestAudioFocus() {
        if (!mIsTesting)
            return false;
        Toast.makeText(mContext, MSG_PRE + "请求音频焦点", Toast.LENGTH_SHORT).show();
        return true;
    }

    void onAudioFocusChange(final int focusChange) {
        final boolean callbackOnMainLooper = Looper.myLooper() == Looper.getMainLooper();

        mLogger.i("音频焦点修改为 " + stateDesc(focusChange) + (callbackOnMainLooper ? "" : "[在非UI线程被回调]"));

        if (focusChange != AudioManager.AUDIOFOCUS_GAIN) {
            final String msg = "音频焦点 " + stateDesc(focusChange) + (mIsTesting ? ", " + (mRetryTimeout/1000f) + " 秒后重新请求" : "");

            mLogger.i(msg);

            if (callbackOnMainLooper) {
                Toast.makeText(mContext, MSG_PRE + "音频焦点 " + msg, Toast.LENGTH_SHORT).show();
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, MSG_PRE + "音频焦点 " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mIsTesting)
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestAudioFocus();
                    }
                }, mRetryTimeout);
        }
    }

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            AudioFocusTest.this.onAudioFocusChange(focusChange);
        }
    };


    static String stateDesc(int state) {
        switch (state) {
            case AudioManager.AUDIOFOCUS_GAIN:
                return "gain";
            case AudioManager.AUDIOFOCUS_LOSS:
                return "loss";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                return "loss transient";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                return "loss transient can duck";
            default:
                return Integer.valueOf(state).toString();
        }
    }
}
