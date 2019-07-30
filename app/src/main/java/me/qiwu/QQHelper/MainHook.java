package me.qiwu.QQHelper;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.qiwu.QQHelper.hooks.StartHook;

/**
 * Created by Deng on 2018/4/19.
 */

public class MainHook implements IXposedHookLoadPackage{
    public static final String QQ="com.tencent.mobileqq";
    private boolean isHookQQ=false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(QQ))return;
        Class<?> application = XposedHelpers.findClass(loadPackageParam.appInfo.className,loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod(application.getSuperclass(), "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!isHookQQ){
                    isHookQQ=true;
                    final Application application = (Application) param.thisObject;
                    Context context=application.getApplicationContext();
                    ClassLoader classLoader = application.getClassLoader();
                    new StartHook(classLoader,context).hook();

                }
            }
        });

    }





}
