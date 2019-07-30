package me.qiwu.QQHelper.hooks.other;

import android.view.View;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.MainHook;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/6/9.
 */

public class UpgradeManager extends ReflectHelper {
    private Class<?>mUpgradeController;
    private Class<?>mBannerManager;
    private SettingUtils settingUtils=new SettingUtils();
    public UpgradeManager(ClassLoader classLoader) throws ClassNotFoundException {
        mUpgradeController=classLoader.loadClass(UpgradeController);
        mBannerManager=classLoader.loadClass(BannerManager);
    }
    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_PREUPDATA)){
            Method method=getMethod(mUpgradeController,"a","UpgradeDetailWrapper");
            if (method!=null){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(null);

                    }
                });
            }
            Method method1=getMethod(mBannerManager,"n", View.class);
            if (method1 != null) {
                XposedBridge.hookMethod(method1, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(null);
                    }
                });
            }
        }


    }
}
