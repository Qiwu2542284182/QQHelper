package me.qiwu.QQHelper.hooks.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/7/16.
 */

public class CheatHook extends ReflectHelper {
    private Class<?>mPngFrameUtil;
    private Class<?>mEmoticonPanelLinearLayout;
    private SettingUtils settingUtils=new SettingUtils();
    private int diceNum=0;
    private int morraNum=0;
    private ClassLoader classLoader;
    private final String[]diceItem={"1","2","3","4","5","6"};
    private final String[]morraItem={"石头","剪刀","布"};
    public CheatHook(ClassLoader classLoader) throws ClassNotFoundException {
        mEmoticonPanelLinearLayout =classLoader.loadClass(EmoticonPanelLinearLayout);
        mPngFrameUtil=classLoader.loadClass(PngFrameUtil);
        this.classLoader = classLoader;
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_CHEAT)){
            XposedHelpers.findAndHookMethod(mPngFrameUtil, "a", int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    int num=(int)param.args[0];
                    if (num==6){
                        param.setResult(diceNum);
                    }else if (num==3){
                        param.setResult(morraNum);
                    }}
            });

            /**XposedHelpers.findAndHookMethod(mEmoticonPanelLinearLayout, "a", View.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    View view=(View)methodHookParam.args[0];
                    Toast.makeText(view.getContext(),view.getTag().toString(),Toast.LENGTH_LONG).show();
                    if (view.getTag()!=null && view.getTag().toString().contains("随机骰子")){
                        showDiceDialog(view.getContext(),methodHookParam);
                        return null;
                    }else if (view.getTag().toString().contains("猜拳")){
                        showMorraDialog(view.getContext(),methodHookParam);
                        return null;
                    }
                    return XposedBridge.invokeOriginalMethod(methodHookParam.method,methodHookParam.thisObject,methodHookParam.args);
                }
            });**/
            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.emoticonview.PicEmoticonInfo", classLoader, "a", QQAppInterface, Context.class, SessionInfo, "com.tencent.mobileqq.data.Emoticon",
                    "com.tencent.mobileqq.emoticon.EmojiStickerManager$StickerInfo",new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Context context = (Context) param.args[1];
                            Object emoticon = param.args[3];
                            String name = (String) XposedHelpers.getObjectField(emoticon,"name");
                            if ("随机骰子".equals(name)){
                                param.setResult(null);
                                showDiceDialog(context,param);
                            } else if ("猜拳".equals(name)){
                                param.setResult(null);
                                showMorraDialog(context,param);
                            }
                        }
                    });
        }
    }

    private void showDiceDialog(Context context,final XC_MethodHook.MethodHookParam param){
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("自定义骰子")
                .setSingleChoiceItems(diceItem, diceNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        diceNum=which;
                    }
                })
                .setNegativeButton("取消",null)
                .setNeutralButton("随机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        diceNum=Math.abs(new Random().nextInt(6));
                        try {
                            XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                        } catch (Exception e){
                            XposedBridge.log(e);
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                        } catch (Exception e){
                            XposedBridge.log(e);
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    private void showMorraDialog(Context context,final XC_MethodHook.MethodHookParam param){
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("自定义猜拳")
                .setSingleChoiceItems(morraItem, morraNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        morraNum=which;
                    }
                })
                .setNegativeButton("取消",null)
                .setNeutralButton("随机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        morraNum=Math.abs(new Random().nextInt(3));
                        try {
                            XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                        } catch (Exception e){
                            XposedBridge.log(e);
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                        } catch (Exception e){
                            XposedBridge.log(e);
                        }
                    }
                })
                .create();
        alertDialog.show();
    }
}
