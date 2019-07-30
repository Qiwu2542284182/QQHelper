package me.qiwu.QQHelper.view;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;
import me.qiwu.QQHelper.BuildConfig;

/**
 * Created by Deng on 2018/7/14.
 */

public class SwitchListener implements CompoundButton.OnCheckedChangeListener {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String msg=(String) buttonView.getTag();
        Context context=buttonView.getContext();
        context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putBoolean(msg,isChecked).apply();
    }
}
