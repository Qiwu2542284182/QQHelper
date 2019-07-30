package me.qiwu.QQHelper.hooks.other;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.DisplayMetrics;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/6/11.
 */

public class FontManager extends ReflectHelper {
    private Class<?>mFontSettingManager;
    private SettingUtils settingUtils=new SettingUtils();
    private Context context= AndroidAppHelper.currentApplication().getApplicationContext();
    public FontManager(ClassLoader classLoader) throws ClassNotFoundException {
        mFontSettingManager=classLoader.loadClass(FontSettingManager);
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_DIYFONT)){
            setDip();
            XposedHelpers.findAndHookMethod(mFontSettingManager, "b", Context.class, boolean.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    setDip();
                }
            });
        }
    }

    private DisplayMetrics getDisplayMetrics(float dip){
        DisplayMetrics disa=(DisplayMetrics)getObjectFieldByClass(mFontSettingManager,null,"a","DisplayMetrics");
        float f2 = dip / settingUtils.getFontSize();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        displayMetrics.density = disa.density * f2;
        displayMetrics.scaledDensity = disa.density * f2;
        displayMetrics.densityDpi = (int) (f2 * ((float) disa.densityDpi));
        return displayMetrics;
    }

    private void setDip(){
        DisplayMetrics disb=(DisplayMetrics)getObjectFieldByClass(mFontSettingManager,null,"b","DisplayMetrics");
        float dip=(float)getObjectFieldByClass(mFontSettingManager,null,"a",float.class);
        disb.setTo(getDisplayMetrics(dip));
    }
}
