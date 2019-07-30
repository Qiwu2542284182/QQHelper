package me.qiwu.QQHelper.hooks.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/6/9.
 */

public class QRLoginHook extends ReflectHelper{
    private ClassLoader classLoader;
    private Class<?>mQRLoginActivity;
    private Class<?>mDevlockQuickLoginActivity;
    private SettingUtils settingUtils=new SettingUtils();
    public QRLoginHook(ClassLoader classLoader) throws ClassNotFoundException {
        this.classLoader=classLoader;
        mQRLoginActivity=classLoader.loadClass(QRLoginActivity);
        mDevlockQuickLoginActivity=classLoader.loadClass(DevlockQuickLoginActivity);
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_QRLOGIN)){
            QRlogin();
            DevlockQuickLogin();
        }
    }
    public void QRlogin(){
        XposedHelpers.findAndHookMethod(mQRLoginActivity, "doOnCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final Button button1=(Button)getObjectFieldByClass(mQRLoginActivity,param.thisObject,"a","Button");
                Handler handler=(Handler) getObjectFieldByClass(mQRLoginActivity,param.thisObject,"a","Handler");
                XposedHelpers.findAndHookMethod(handler.getClass().getCanonicalName(), classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (button1.getText().toString().contains("允许登录")){
                            button1.performClick();
                        }
                    }
                });
            }
        });

    }

    public void DevlockQuickLogin(){
        XposedHelpers.findAndHookMethod(mDevlockQuickLoginActivity, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final Button button1=(Button)getObjectFieldByClass(mDevlockQuickLoginActivity,param.thisObject,"a","Button");
                if (button1.getText().toString().contains("允许登录")&&settingUtils.getString("DevlockQuickQRLoginway").equals("授权永久登录")){
                    button1.performClick();

                }
            }
        });
    }
}
