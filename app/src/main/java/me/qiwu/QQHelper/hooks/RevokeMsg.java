package me.qiwu.QQHelper.hooks;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/5/26.
 */

public class RevokeMsg extends ReflectHelper{
    private ClassLoader classLoader;
    private Object OQQAppInterface;
    private Class<?>mQQMessageFacade;
    private Class<?>mMessageRecord;
    private Class<?>mUniteGrayTipParam;
    private SettingUtils settingUtils=new SettingUtils();
    public RevokeMsg(ClassLoader classLoader) throws ClassNotFoundException {
        this.classLoader=classLoader;
        mQQMessageFacade=classLoader.loadClass(QQMessageFacade);
        mMessageRecord=classLoader.loadClass(MessageRecord);
        mUniteGrayTipParam=classLoader.loadClass(UniteGrayTipParam);
    }
    public void preventRevoke(){
        if (!settingUtils.getBoolean(SettingUtils.SETTING_KEY_STARTREVOKE))return;
        XposedHelpers.findAndHookMethod(mQQMessageFacade, "a", String.class, int.class, long.class, long.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (isCallingFrom("BaseMessageManager")){
                    List list=(List)param.getResult();
                    Object msg = list.get(0);
                    int msgtype = (int) getObjectFieldByClass(mMessageRecord,msg, "msgtype", int.class);
                    if (msgtype == -2006) {
                        param.setResult(list);
                        return;
                    }
                    param.setResult(null);
                }

            }
        });
        XposedHelpers.findAndHookMethod(mQQMessageFacade, "a", ArrayList.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                load(param);
                ArrayList arrayList=(ArrayList)param.args[0];
                Object revokeMsg=arrayList.get(0);
                String fromuin=(String) getObjectField(revokeMsg,"b","String");

                if (isCallingFrom("C2CMessageProcessor")){
                    param.args[0]=null;
                }else if (fromuin.equals(getmyselfuin())){
                    param.args[0]=null;
                }else if (!settingUtils.getBoolean(SettingUtils.SETTING_KEY_SHOWREVOKEMSG)){
                    param.args[0]=null;
                }

            }
        });
        XposedBridge.hookAllConstructors(mUniteGrayTipParam, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!settingUtils.getBoolean(SettingUtils.SETTING_KEY_STARTREVOKE))return;
                String selfuin=getmyselfuin();
                String fromuin=(String)param.args[1];
                String message=(String)param.args[2];
                if (selfuin!=null&&!selfuin.isEmpty()&&message.contains("撤回")){
                    if (!selfuin.equals(fromuin)){
                        param.args[2]=getShowMsg(message);
                    }
                }
            }
        });
    }

    private void load(XC_MethodHook.MethodHookParam param){
        if (OQQAppInterface==null)OQQAppInterface = getObjectField(param.thisObject, "a", "QQAppInterface");
    }

    private String getmyselfuin(){
        if (OQQAppInterface==null)return null;
        return (String)XposedHelpers.callMethod(OQQAppInterface,"getCurrentAccountUin");
    }

    private String getShowMsg(String message){
        if (message.length()>=7){
            if (message.substring(message.length()-7,message.length()).equals("撤回了一条消息")){
                return message.substring(0,message.length()-7)+settingUtils.getString(SettingUtils.SETTING_KEY_REVOKEMSG,"尝试撤回一条消息");
            }
        }
        if (message.length()>=9){
            if (message.substring(message.length()-9,message.length()).equals("撤回了一条成员消息")){
                return message.substring(0,message.length()-9)+settingUtils.getString(SettingUtils.SETTING_KEY_REVOKEADMMSG,"尝试撤回一条成员消息");
            }
        }
        return message;
    }
}
