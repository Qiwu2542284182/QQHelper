package me.qiwu.QQHelper.utils;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;

public class DialogUtil {
    public static void setAboutDialogStyle(AlertDialog alertDialog){
        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (button!=null){
            button.setTextSize(16.0f);
            button.setTextColor(0xff4284f3);
        }
        Button button1 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (button1!=null){
            button1.setTextSize(16.0f);
            button1.setTextColor(0xff4284f3);
        }
        Button button2 = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (button2!=null){
            button2.setTextSize(16.0f);
            button2.setTextColor(0xff4284f3);
        }

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            if (mTitleView!=null){
                mTitleView.setTextSize(20.0f);
            }
            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            if (mMessageView!=null){
                mMessageView.setTextSize(18.0f);
            }
        } catch (Exception e){
            XposedBridge.log(e);
        }
    }
}
