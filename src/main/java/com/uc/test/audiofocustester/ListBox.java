// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListBox<T> extends ListView {
    public ListBox(Context context) {
        super(context);
        init(context, new ArrayList<T>());
    }

    public ListBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, new ArrayList<T>());
    }

    public ListBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, new ArrayList<T>());
    }

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, new ArrayList<T>());
    }
    */

    private void init(Context ctx, List<T> items) {
        setAdapter(new CustomAdapter<T>(ctx, items));
    }

    @SuppressWarnings("unchecked")
    public void add(final T item) {
        if (Looper.myLooper() != Looper.getMainLooper())
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((CustomAdapter<T>) getAdapter()).add(item);
                }
            });
        else
            ((CustomAdapter<T>) getAdapter()).add(item);
    }

    @SuppressWarnings("unchecked")
    public void insert(final T item, final int index) {
        if (Looper.myLooper() != Looper.getMainLooper())
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((CustomAdapter<T>) getAdapter()).insert(item, index);
                }
            });
        else
            ((CustomAdapter<T>) getAdapter()).insert(item, index);
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        if (Looper.myLooper() != Looper.getMainLooper())
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ((CustomAdapter<T>) getAdapter()).clear();
                }
            });
        else
            ((CustomAdapter<T>) getAdapter()).clear();
    }

    private static class CustomAdapter<T> extends BaseAdapter {
        private final Object mLock = new Object();
        Context mContext;
        List<T> mItems;

        CustomAdapter(Context ctx, List<T> items) {
            mContext = ctx;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(mContext);
            tv.setText("" + mItems.get(position));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            return tv;
        }

        void add(T object) {
            synchronized (mLock) {
                mItems.add(object);
            }
            notifyDataSetChanged();
        }

        void insert(T object, int index) {
            synchronized (mLock) {
                mItems.add(index, object);
            }
            notifyDataSetChanged();
        }

        void clear() {
            synchronized (mLock) {
                mItems.clear();
            }
            notifyDataSetChanged();
        }
    }
}
