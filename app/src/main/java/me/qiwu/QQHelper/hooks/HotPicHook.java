package me.qiwu.QQHelper.hooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/4/26.
 */

public class HotPicHook extends ReflectHelper{
    private Class<?>mMessageRecord;
    private Class<?>mFlashPicHelper;
    private Class<?>mPicItemBuilder;
    private Class<?>mBaseBubbleBuilder$ViewHolder;
    private SettingUtils settingUtils=new SettingUtils();
    public HotPicHook(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        mMessageRecord=classLoader.loadClass(MessageRecord);
        mFlashPicHelper=classLoader.loadClass(FlashPicHelper);
        mPicItemBuilder=classLoader.loadClass(PicItemBuilder);
        mBaseBubbleBuilder$ViewHolder=classLoader.loadClass(BaseBubbleBuilder$ViewHolder);
    }

    public void init() throws ClassNotFoundException {
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_SHOWASPIC)){
            showAsPic();
        }
    }

    public void showAsPic() throws ClassNotFoundException {
        final Method method = getMethod(mFlashPicHelper, "a", boolean.class, mMessageRecord);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isCallingFrom("ItemBuilderFactory")||isCallingFrom("BasePicDownloadProcessor")) {
                    param.setResult(false);

                }
            }
        });
        if (!settingUtils.getBoolean(SettingUtils.SETTING_KEY_BANHOTPICMASK)){
            XposedHelpers.findAndHookMethod(mPicItemBuilder, "a", ChatMessage,
                    BaseBubbleBuilder$ViewHolder,
                    View.class,
                    BaseChatItemLayout,
                    OnLongClickAndTouchListener, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object viewHolder=param.args[1];
                            if (viewHolder==null)return;
                            Object baseChatItemLayout=getObjectFieldByClass(mBaseBubbleBuilder$ViewHolder,viewHolder,"a","BaseChatItemLayout");
                            boolean isFlashPic=(boolean)XposedBridge.invokeOriginalMethod(method,null,new Object[]{param.args[0]});
                            XposedHelpers.callMethod(baseChatItemLayout,"setTailMessage",isFlashPic,"闪照",null);
                        }
                    });
        }

    }




}
