// Copyright 2017 UCWeb Co., Ltd.

package com.uc.test.audiofocustester;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class Util {
    public static void startActivity(Context context, Map<String, String> params) {
        Intent intent;

        if (params.containsKey("action"))
            intent = new Intent(unescape(context, params.get("action")));
        else
            intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (params.containsKey("flags")) {
            final String flags = params.get("flags");
            if (flags.startsWith("0x"))
                intent.setFlags(Integer.parseInt(flags.substring(2), 16));
            else
                intent.setFlags(Integer.parseInt(flags));
        }

        if (params.containsKey("pkgName") && params.containsKey("className"))
            intent.setClassName(params.get("pkgName"), params.get("className"));

        if (params.containsKey("data")) {
            if (params.containsKey("dataType"))
                intent.setDataAndType(Uri.parse(unescape(context, params.get("data"))), unescape(context, params.get("dataType")));
            else
                intent.setData(Uri.parse(unescape(context, params.get("data"))));
        }

        if (params.containsKey("category"))
            intent.addCategory(unescape(context, params.get("category")));

        if (params.containsKey("extra")) {
            Map<String, String> extras = toMap(params.get("extra"), ",", "/");
            for (Map.Entry<String, String> kv : extras.entrySet()) {
                String k = kv.getKey();
                String v = kv.getValue();
                if (k != null)
                    k = unescape(context, k).trim();
                if (v != null)
                    v = unescape(context, v).trim();
                if (notEmpty(k) && notEmpty(v))
                    intent.putExtra(k, v);
            }
        }

        try {
            // FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent);
        } catch (Throwable ex) {
        }
    }

    public static String unescape(String str) {
        return unescape(null, str);
    }

    public static String unescape(Context context, String str) {
        if (empty(str))
            return str;
        if (context != null) {
            str = str.replaceAll("\\$[pP][kK][gG][nN][aA][mM][eE]", context.getPackageName());
        }
        if (empty(str))
            return str;
        StringBuilder sb = new StringBuilder(str.length());
        char[] chars = str.toCharArray();
        final int count = chars.length - 1;
        int i = 0;
        while (i < count) {
            char c = chars[i];
            if (c == '%') {
                int j;
                int dc;
                int cc = chars[i + 1];
                if (cc == 'u') {
                    j = 2;
                    dc = 4;
                } else {
                    j = 1;
                    dc = 2;
                }
                if (i + j + dc < count + 2) {
                    dc += j;
                    cc = 0;
                    for (; j < dc; ++j)
                        cc = (cc << 4) | Character.digit(chars[i + j], 16);
                    sb.append((char) cc);
                    i += j;
                    continue;
                }
            }
            sb.append(c);
            ++i;
        }
        if (i == count)
            sb.append(chars[i]);

        return sb.toString();
    }

    public static boolean empty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean notEmpty(String str) {
        return str != null && str.length() != 0;
    }

    static Map<String, String> toMap(String string) {
        return toMap(string, ",", "=");
    }

    static Map<String, String> toMap(String string, String delimiter, String kvDelimiter) {
        HashMap<String, String> res = new HashMap<String, String>();
        if (empty(string))
            return res;
        final String strs[] = string.split(delimiter);
        if (strs != null && strs.length > 0) {
            for (String str : strs) {
                str = str.trim();
                if (empty(str))
                    continue;
                final String kn[] = str.split(kvDelimiter);
                if (kn != null && kn.length == 2) {
                    final String k = kn[0];
                    final String v = kn[1];
                    if (res.containsKey(k))
                        res.put(k, res.get(k) + ", " + v);
                    else
                        res.put(k, v);
                }
            }
        }
        return res;
    }
}
