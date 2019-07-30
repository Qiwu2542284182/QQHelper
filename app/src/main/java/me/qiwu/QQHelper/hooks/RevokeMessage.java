package me.qiwu.QQHelper.hooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/4/19.
 */

public class RevokeMessage extends ReflectHelper {
    private int message=-2031;
    private Class<?>mMessageRecord;
    private Class<?>mQQMessageFacade;
    private Class<?>mUniteGrayTipParam;
    private Class<?>mRevokeMsgInfo;
    private Class<?>mBaseMessageManager;
    private Object OQQMessageFacade;
    private Object OQQAppInterface;
    private Class<?>mMessageRecordFactory;
    private Class<?>mContactUtils;
    private SettingUtils settingUtils=new SettingUtils();
    public RevokeMessage(ClassLoader classLoader) throws ClassNotFoundException {
        init(classLoader);
    }
    private void init(ClassLoader classLoader) throws ClassNotFoundException {
        mBaseMessageManager=classLoader.loadClass(BaseMessageManager);
        mQQMessageFacade=classLoader.loadClass(QQMessageFacade);
        mMessageRecord=classLoader.loadClass(MessageRecord);
        mUniteGrayTipParam=classLoader.loadClass(UniteGrayTipParam);
        mRevokeMsgInfo=classLoader.loadClass(RevokeMsgInfo);
        mMessageRecordFactory=classLoader.loadClass(MessageRecordFactory);
        mContactUtils=classLoader.loadClass(ContactUtils);
    }
    public void preventRevoke(){
        XposedHelpers.findAndHookMethod(mQQMessageFacade, "a", ArrayList.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (settingUtils.getBoolean("isShowRevkeMessage")){
                    reload(methodHookParam);
                    ArrayList arrayList=(ArrayList)methodHookParam.args[0];
                    if (arrayList!=null&&!arrayList.isEmpty()){
                        setRevokeMessage(arrayList);
                    }
                }
                return null;
            }
        });
    }

    private void setRevokeMessage(ArrayList arrayList) throws Throwable {
        Object revokeMsg=arrayList.get(0);
        String frienduin=(String) getObjectField(revokeMsg,"a","String");
        String fromuin=(String) getObjectField(revokeMsg,"b","String");
        String senderuid=(String) getObjectField(revokeMsg,"c","String");
        int istroop=(int) getObjectField(revokeMsg,"a",int.class);
        long time=(long) getObjectField(revokeMsg,"c",long.class);
        long msguid=(long)getObjectField(revokeMsg,"b",long.class);
        long shmsgseq=(long)getObjectField(revokeMsg,"a",long.class);
        String selfuin=getmyselfuin();
        int isAdmin=(int)getObjectField(revokeMsg,"e",int.class);
        if (isStart(selfuin,fromuin)){
            showRevokeTip(selfuin,frienduin,fromuin,time,message,istroop,msguid,shmsgseq,isAdmin);
        }

    }

    private void showRevokeTip(String selfuin,String frienduin,String fromuin,long time,int messageType,int istroop,long msguid,long shmsgseq,int isAdmin) throws Throwable {
        Object messageRecord=getMethod(mMessageRecordFactory,"a",mMessageRecord,int.class).invoke(mMessageRecordFactory.newInstance(),messageType);
        String msg;
        if (istroop==0){
            msg="对方"+settingUtils.getString(SettingUtils.SETTING_KEY_REVOKEMSG,"尝试撤回一条消息");
            XposedHelpers.callMethod(messageRecord,"init",selfuin,fromuin,fromuin,msg,time,messageType,istroop,time);
        }else {
            msg=getTroopFriendName(fromuin,frienduin,istroop,isAdmin);
            XposedHelpers.callMethod(messageRecord,"init",selfuin,frienduin,fromuin,msg,time,messageType,istroop,time);
        }
        XposedHelpers.setObjectField(messageRecord,"msgUid", msguid);
        XposedHelpers.setObjectField(messageRecord,"shmsgseq", shmsgseq);
        XposedHelpers.setObjectField(messageRecord,"isread", true);
        List<Object> list=new ArrayList<>();
        list.add(messageRecord);
        if (!list.isEmpty())
            getMethod(mQQMessageFacade,"a","void",List.class,String.class).invoke(OQQMessageFacade,list,selfuin);
    }

    private String getmyselfuin(){
        if (OQQAppInterface==null)return null;
        return (String)XposedHelpers.callMethod(OQQAppInterface,"getCurrentAccountUin");
    }


    private String getTroopFriendName(String fromuin,String frienduin,int istroop,int isAdmin){
        int a=istroop==1?1:2;
        int b=0;
        String message;
        if (isAdmin==1||isAdmin==2){
            String admin=isAdmin==1?"管理员":"群主";
            message=admin+(String)XposedHelpers.callStaticMethod(mContactUtils,"a",OQQAppInterface,fromuin,frienduin,a,b)+settingUtils.getString(SettingUtils.SETTING_KEY_REVOKEADMMSG,"尝试撤回一条成员消息");
        }else {
            message=(String)XposedHelpers.callStaticMethod(mContactUtils,"a",OQQAppInterface,fromuin,frienduin,a,b)+settingUtils.getString(SettingUtils.SETTING_KEY_REVOKEADMMSG,"尝试撤回一条成员消息");
        }
        if (message!=null&&!message.isEmpty())return message;
        return frienduin;
    }

    private void reload(XC_MethodHook.MethodHookParam methodHookParam){
        if (OQQAppInterface==null) OQQAppInterface = getObjectField(methodHookParam.thisObject, "a", "QQAppInterface");
        if (OQQMessageFacade==null) OQQMessageFacade=methodHookParam.thisObject;
    }

    private boolean isStart(String selfuin,String fromuin){
        if (isCallingFrom("C2CMessageProcessor")) return false;
        if (selfuin.equals(fromuin))return false;
        return true;
    }
}
