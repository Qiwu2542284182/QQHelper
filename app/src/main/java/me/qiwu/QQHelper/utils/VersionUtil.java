package me.qiwu.QQHelper.utils;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.XposedHelpers;

import static me.qiwu.QQHelper.MainHook.QQ;

/**
 * Created by Deng on 2018/9/24.
 */

public class VersionUtil {
    public static boolean isMore780(){
        return getQQVersionName(getSystemContext()).compareTo("7.8.0")>=0;
    }

    public static boolean isMore755(){
        return getQQVersionName(getSystemContext()).compareTo("7.5.5")>=0;
    }

    public static Context getSystemContext() {
        return (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
    }

    public static String getQQVersionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(QQ, 0).versionName;
        } catch (Throwable e) {
            return "unknown";
        }
    }
}
