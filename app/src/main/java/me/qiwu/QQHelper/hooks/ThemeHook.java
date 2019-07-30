package me.qiwu.QQHelper.hooks;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/9/11.
 */

public class ThemeHook extends ReflectHelper {
    private Class<?>mThemeUtil;
    private Class<?>mSkinEngine;
    private Class<?>mMainFragment;
    private Class<?>mFrame;
    private Class<?>mIphoneTitleBarActivity;
    private Class<?>mImmersiveTitleBar2;
    private Class<?>mSystemBarCompact;
    private Class<?>mPluginProxyActivity;
    private Class<?>mBaseActivity;
    private Context context;
    public ThemeHook(ClassLoader classLoader,Context context){
        mThemeUtil= XposedHelpers.findClass("com.tencent.mobileqq.theme.ThemeUtil",classLoader);
        mSkinEngine=XposedHelpers.findClass("com.tencent.theme.SkinEngine",classLoader);
        mMainFragment=XposedHelpers.findClass("com.tencent.mobileqq.activity.MainFragment",classLoader);
        mFrame=XposedHelpers.findClass("com.tencent.mobileqq.app.Frame",classLoader);
        mIphoneTitleBarActivity=XposedHelpers.findClass("com.tencent.mobileqq.app.IphoneTitleBarActivity",classLoader);
        mImmersiveTitleBar2=XposedHelpers.findClass("com.tencent.widget.immersive.ImmersiveTitleBar2",classLoader);
        mSystemBarCompact=XposedHelpers.findClass("com.tencent.widget.immersive.SystemBarCompact",classLoader);
        mPluginProxyActivity=XposedHelpers.findClass("com.tencent.mobileqq.pluginsdk.PluginProxyActivity",classLoader);
        mBaseActivity=XposedHelpers.findClass("com.tencent.mobileqq.app.BaseActivity",classLoader);
        this.context=context;
    }

    public void init(){
        if (!SettingUtils.getBoolean(context,SettingUtils.SETTING_KEY_THEME))return;
        XposedBridge.hookAllMethods(mThemeUtil,"getCurrentThemeId",XC_MethodReplacement.returnConstant("2097"));
        XposedBridge.hookAllMethods(mThemeUtil, "initTheme", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                Object skinEngine=XposedHelpers.callStaticMethod(mSkinEngine,"getInstances");
                XposedHelpers.callMethod(skinEngine,"setSkinRootPath",context,Environment.getExternalStorageDirectory().getPath()+"/QQColor/theme");
                return null;
            }
        });
        /**
        XposedHelpers.findAndHookMethod(mFrame, "a", View.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (methodHookParam.args[0]!=null){
                    View view=(View)methodHookParam.args[0];
                    view.setBackgroundColor(0xff009688);
                }
                return null;
            }
        });
        XposedBridge.hookAllMethods(mIphoneTitleBarActivity, "setContentView", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view=(View)XposedHelpers.callMethod(param.thisObject,"getTitleBarView");
                if (view!=null){
                    view.setBackgroundColor(0xff009688);
                }
            }
        });
        XposedHelpers.findAndHookMethod(mImmersiveTitleBar2, "a", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                ((View)param.thisObject).setBackgroundColor(0xff009688);
            }
        });
        XposedHelpers.findAndHookMethod(mSystemBarCompact, "setStatusDrawable", Drawable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (param.args[0]!=null)
                param.args[0]=new ColorDrawable(0xff009688);
            }
        });
        XposedHelpers.findAndHookMethod(mSystemBarCompact, "setStatusBarDrawable", Drawable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (param.args[0]!=null)
                param.args[0]=new ColorDrawable(0xff009688);
            }
        });**/
        /**
        XposedBridge.hookAllMethods(mThemeUtil,"isNowThemeIsDefaultCache",XC_MethodReplacement.returnConstant(false));
        XposedBridge.hookAllMethods(mThemeUtil,"isDefaultOrDIYTheme", XC_MethodReplacement.returnConstant(false));
        XposedBridge.hookAllMethods(mThemeUtil,"isDefaultTheme",XC_MethodReplacement.returnConstant(false));
        XposedBridge.hookAllMethods(mThemeUtil,"isNowThemeIsDefault",XC_MethodReplacement.returnConstant(false));
        final int headerBgId=context.getResources().getIdentifier("rlCommenTitle","id",context.getPackageName());
        XposedHelpers.findAndHookMethod(View.class, "setBackgroundDrawable", Drawable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View view=(View)param.thisObject;
                if (view.getId()==headerBgId){
                    param.args[0]=new ColorDrawable(0xff009688);
                }
            }
        });
        final int headerBanId=context.getResources().getIdentifier("skin_color_title_immersive_bar","color",context.getPackageName());
        XposedHelpers.findAndHookMethod(Resources.class, "getColor", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int id=(int)param.args[0];
                if (id==headerBanId){
                    param.setResult(0xff009688);
                }
            }
        });
        final int headerId=context.getResources().getIdentifier("skin_header_bar_bg","drawable",context.getPackageName());

        XposedHelpers.findAndHookMethod(Resources.class, "getDrawable", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                int id=(int)methodHookParam.args[0];
                if (id==headerBgId){
                    return new ColorDrawable(0xff009688);
                }
                return XposedBridge.invokeOriginalMethod(methodHookParam.method,methodHookParam.thisObject,methodHookParam.args);
            }
        });**/
        /**
        XposedHelpers.findAndHookMethod(View.class, "setLayoutParams", ViewGroup.LayoutParams.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final View view=(View) param.thisObject;
                if (view.getId()==headerBgId){
                    view.setBackgroundColor(0xff009688);
                }
            }
        });






        **/
    }
}
