// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final String TAG = "音频焦点测试";

    private Logger mLogger;
    private ListBox<String> mLog;
    private Button mTestABtn;
    private Button mTestBBtn;
    private Button mFullScreenBtn;
    private Button mClearLogBtn;

    private AudioFocusTest mAudioFocusTestA;
    private AudioFocusTest mAudioFocusTestB;

    private void onTest(Button a, Button b) {
        a.setEnabled(false);
        a.setText("测试中...");
        a.setTextColor(Color.GREEN);

        b.setEnabled(true);
        b.setText(getResources().getString(a == mTestABtn ? R.string.testb : R.string.testa));
        b.setTextColor(Color.BLACK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);

        setContentView(R.layout.activity_main);

        FullScreenHolder.setActivity(this);

        mLog = (ListBox<String>) findViewById(R.id.log_view);
        mTestABtn = (Button) findViewById(R.id.testa_btn);
        mTestBBtn = (Button) findViewById(R.id.testb_btn);
        mFullScreenBtn = (Button) findViewById(R.id.fullscreen_btn);
        mClearLogBtn = (Button) findViewById(R.id.clear_log_btn);

        mFullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FullScreenHolder.acquired()) {
                    FullScreenHolder.release();
                } else {
                    FullScreenHolder.acquire(FullScreenHolder.SCREEN_ORIENTATION_SENSOR);
                }
                mFullScreenBtn.setText(getResources().getString(
                        FullScreenHolder.acquired() ? R.string.exit_fullscreen : R.string.enter_fullscreen));
            }
        });

        mClearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog.clear();
            }
        });

        mTestABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioFocusTestA.test();
                onTest(mTestABtn, mTestBBtn);
            }
        });

        mTestBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioFocusTestB.test();
                onTest(mTestBBtn, mTestABtn);
            }
        });

        mLogger = new Logger() {
            public String getCurrentTimeStamp() {
                return new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());
            }

            @Override
            public void i(String msg) {
                mLog.insert(getCurrentTimeStamp() + " I " + msg, 0);
                Log.i(TAG, msg);
            }

            @Override
            public void e(String msg) {
                mLog.insert(getCurrentTimeStamp() + " E " + msg, 0);
                Log.e(TAG, msg);
            }
        };

        mAudioFocusTestA = new AudioFocusTestA(this, mLogger);
        mAudioFocusTestB = new AudioFocusTestB(this, mLogger);

        if (!AndroidPermission.requestPermissions(this, Manifest.permission.READ_PHONE_STATE))
            mLog.add("获取权限" + (AndroidPermission.havePermission(this, Manifest.permission.READ_PHONE_STATE) ? "成功" : "失败"));
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        AndroidPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLog.add("获取权限" + (AndroidPermission.havePermission(this, Manifest.permission.READ_PHONE_STATE) ? "成功" : "失败"));
    }

    private long mPreBackPressedTime = 0;

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (mPreBackPressedTime == 0 || curTime - mPreBackPressedTime > 2 * 1000) {
            mPreBackPressedTime = curTime;
            Toast.makeText(getBaseContext(), "再按一次返回键，退出本应用", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
