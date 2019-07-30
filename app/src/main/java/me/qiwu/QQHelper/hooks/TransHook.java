package me.qiwu.QQHelper.hooks;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;
import me.qiwu.QQHelper.utils.TransApi;

/**
 * Created by Deng on 2019/3/4.
 */

public class TransHook extends ReflectHelper {
    private String appid;
    private String securityKey;
    private String lang;
    private boolean transUp = false;
    private boolean startTrans = false;
    private Method method;
    private ClassLoader classLoader;
    public TransHook(ClassLoader classLoader, Context context){
        this.classLoader = classLoader;
        appid = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString(SettingUtils.SETTING_KEY_TRANSAPPID,"");
        securityKey = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString(SettingUtils.SETTING_KEY_TRANSKEY,"");
        lang = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString(SettingUtils.SETTING_KEY_TRANSLANG,"en");
        transUp = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getBoolean(SettingUtils.SETTING_KEY_TRANSUP,false);
        startTrans = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getBoolean(SettingUtils.SETTING_KEY_STARTTRANS,false);
    }

    public void init(){
        if (startTrans){
            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.data.MessageForText", classLoader, "doParse", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final String originMsg = (String) XposedHelpers.callMethod(param.thisObject,"getSummaryMsg");
                    Object qqtext = XposedHelpers.getObjectField(param.thisObject,"sb");
                    final String msg = getOriginMsg(qqtext);
                    if (!"".equals(msg)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                TransApi api = new TransApi(appid, securityKey);
                                Object newText = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mobileqq.text.QQText",classLoader),getDst(api.getTransResult(msg, "auto", lang),originMsg),13,32,param.thisObject);
                                XposedHelpers.setObjectField(param.thisObject,"sb",newText );

                            }
                        }).start();
                    }
                }
            });
        }


    }

    private String getDst(String json,String msg){
        String transMsg = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            transMsg = ((JSONObject)jsonArray.get(0)).getString("dst");
        } catch (Exception e) {
            transMsg = e.toString();
        }
        return transUp ? transMsg + "\n" + "----------\n" + msg : msg + "\n" + "----------\n" + transMsg;
    }

    private String getOriginMsg(Object qqtext){
        try {
            if (method==null){
                method = findMethodIfExists(XposedHelpers.findClass("com.tencent.mobileqq.text.QQText",classLoader),String.class,"a");
            }
            return (String) method.invoke(qqtext);
        } catch (Exception e){
            return qqtext.toString();
        }
    }
}
