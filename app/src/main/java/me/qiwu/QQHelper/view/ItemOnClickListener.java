package me.qiwu.QQHelper.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.R;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/7/15.
 */

public class ItemOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        final Context context=v.getContext();
        String msg=(String)v.getTag(R.id.tag_first);
        String title=msg.equals("设置字体大小")?"设置字体大小(数字越大，字体越小)":msg;
        final String key=(String)v.getTag(R.id.tag_second);
        String defealtValue=(String)v.getTag(R.id.tag_third);
        final LinearLayout linearLayout=getEditView(context,key,defealtValue);
        AlertDialog alertDialog=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(title)
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettingUtils.setString(context,key,((EditText)linearLayout.getChildAt(0)).getText().toString());
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        alertDialog.show();
        setEditDialogStyle(alertDialog);
    }

    private LinearLayout getEditView(Context context,String key,String defealtValue){
        int padding= DensityUtil.dip2px(context,20.0f);
        //去除editView焦点
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        EditText editText=new EditText(context);
        editText.setTextColor(Color.BLACK);
        if (key.equals(SettingUtils.SETTING_KEY_FONTSIZE)||key.equals(SettingUtils.SETTING_KEY_NOTIFYMAXNUM)){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setText(context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString(key,defealtValue));
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(context,40.0f));
        layoutParams.setMargins(padding,DensityUtil.dip2px(context,10.0f),padding,10);
        editText.setLayoutParams(layoutParams);
        linearLayout.addView(editText);
        return linearLayout;
    }
    private void setEditDialogStyle(AlertDialog alertDialog){
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(20.0f);
        } catch (Exception e){
            XposedBridge.log(e);
        }
    }
}
