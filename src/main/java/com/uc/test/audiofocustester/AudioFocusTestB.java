// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.media.AudioManager;

import java.lang.reflect.Method;

public class AudioFocusTestB extends AudioFocusTest {
    public AudioFocusTestB(Context context, final Logger logger) {
        super(context, logger);
        MSG_PRE = "测试 B：";
        mRetryTimeout = 5 * 1000;
    }

    @Override
    public boolean requestAudioFocus() {
        if (!super.requestAudioFocus())
            return false;

        try {
            Class<?> OnAudioFocusChangeListenerClass = Class.forName("android.media.AudioManager$OnAudioFocusChangeListener");
            Method requestAudioFocusMethod = AudioManager.class.getMethod("requestAudioFocus", OnAudioFocusChangeListenerClass, Integer.TYPE, Integer.TYPE);

            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            int res = (Integer)requestAudioFocusMethod.invoke(audioManager, mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mLogger.i("获取音频焦点成功");
            } else {
                mLogger.i("获取音频焦点失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.e("请求音频焦点发生异常：" + e.getMessage());
        }

        return true;
    }
}
