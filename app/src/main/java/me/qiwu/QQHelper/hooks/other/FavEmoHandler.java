package me.qiwu.QQHelper.hooks.other;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/6/29.
 */

public class FavEmoHandler extends ReflectHelper{
    private ClassLoader classLoader;
    private Class<?>mFavEmoConstant;
    private Class<?>mFavEmoRoamingHandler;
    private Class<?>mEmoAddedAuthCallback;
    private Class<?>mUpCallBack$SendResult;
    private SettingUtils settingUtils=new SettingUtils();
    public FavEmoHandler(ClassLoader classLoader) throws ClassNotFoundException {
        this.classLoader=classLoader;
        mFavEmoConstant=classLoader.loadClass(FavEmoConstant);
        mFavEmoRoamingHandler=classLoader.loadClass(FavEmoRoamingHandler);
        mEmoAddedAuthCallback=classLoader.loadClass(EmoAddedAuthCallback);
        mUpCallBack$SendResult=classLoader.loadClass(UpCallBack$SendResult);
    }

    public void init(){
        if (mEmoAddedAuthCallback==null){
            if (mFavEmoRoamingHandler==null){
                setEmoNum();
            }else {
                XposedHelpers.findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        setEmoNum();
                    }
                });
            }

        }else {
            if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_SAVEMOREPIC)){
                XposedHelpers.findAndHookMethod(mEmoAddedAuthCallback, "b", mUpCallBack$SendResult, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object msg=param.args[0];
                        setObjectField(msg,"a",int.class,0);
                    }
                });
                XposedHelpers.findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        setEmoNum();
                    }
                });
            }
        }


    }

    private void setEmoNum(){
        XposedHelpers.setStaticIntField(mFavEmoConstant,"a",999);
        XposedHelpers.setStaticIntField(mFavEmoConstant,"b",999);
    }
}
