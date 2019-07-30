package me.qiwu.QQHelper.hooks;


import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.hooks.other.CheatHook;
import me.qiwu.QQHelper.hooks.other.Doutu;
import me.qiwu.QQHelper.hooks.other.FavEmoHandler;
import me.qiwu.QQHelper.hooks.other.FontManager;
import me.qiwu.QQHelper.hooks.other.QRLoginHook;
import me.qiwu.QQHelper.hooks.other.UpgradeManager;
import me.qiwu.QQHelper.utils.QQHelper;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.view.SettingView;

/**
 * Created by Deng on 2018/4/26.
 */

public class StartHook extends ReflectHelper{
    private ClassLoader classLoader;
    private Context context;
    public StartHook(ClassLoader classLoader,Context context) throws ClassNotFoundException {
        this.classLoader=classLoader;
        this.context=context;
    }

    public void hook() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        QQHelper.init(classLoader);
        new ChatFragmentHook(classLoader).init();

        new RevokeMsg(classLoader).preventRevoke();
        new HotPicHook(classLoader).init();
        new QQNotificationManager(classLoader).init();
        new QRLoginHook(classLoader).init();
        new UpgradeManager(classLoader).init();
        new FontManager(classLoader).init();
        new CheatHook(classLoader).init();

        new FavEmoHandler(classLoader).init();
        new Doutu(classLoader).init();
        new FingerPrint(classLoader).init();
        new TransHook(classLoader,context).init();
        new SettingView(classLoader).addSettingView();
        new MessageHook(classLoader).init();
    }



}
