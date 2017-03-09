// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusTestA extends AudioFocusTest {
    public AudioFocusTestA(Context context, final Logger logger) {
        super(context, logger);
        MSG_PRE = "测试 A：";
        mRetryTimeout = 5 * 1000;
    }

    @Override
    public boolean requestAudioFocus() {
        if (!super.requestAudioFocus())
            return false;
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int res = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mLogger.i("获取音频焦点成功");
        } else {
            mLogger.i("获取音频焦点失败");
        }
        return true;
    }
}
