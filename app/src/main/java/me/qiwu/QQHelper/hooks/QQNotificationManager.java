package me.qiwu.QQHelper.hooks;

import android.annotation.TargetApi;
import android.app.AndroidAppHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.KeyAgreement;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/5/1.
 */

public class QQNotificationManager extends ReflectHelper{
    private ClassLoader classLoader;
    private Class<?>mmMsgNotifyManager;
    private Class<?>mQNotificationManager;
    private Class<?>mBaseApplication;
    private Class<?>mMobileQQService;
    private Class<?>mToServiceMsg;
    private Class<?>mBadgeUtils;
    private Class<?> mQQNotificationManager;
    private Class<?> mCommonBadgeUtilImpl;
    private Object ServiceMsg;
    private Context context;
    private Object manager;
    private ArrayList<String>tags = new ArrayList<>();
    private SettingUtils settingUtils=new SettingUtils();
    public QQNotificationManager(ClassLoader classLoader) throws ClassNotFoundException {
        this.classLoader=classLoader;
        mmMsgNotifyManager=XposedHelpers.findClass(MsgNotifyManager,classLoader);
        mBaseApplication=classLoader.loadClass(BaseApplication);
        mMobileQQService=classLoader.loadClass(MobileQQService);
        mToServiceMsg=classLoader.loadClass(ToServiceMsg);
        mQNotificationManager=classLoader.loadClass(QNotificationManager);
        mBadgeUtils=classLoader.loadClass(BadgeUtils);
        mCommonBadgeUtilImpl = XposedHelpers.findClass("com.tencent.commonsdk.badge.CommonBadgeUtilImpl",classLoader);
        context=getContext();

        manager = XposedHelpers.newInstance(mQNotificationManager,context);
        try {
            mQQNotificationManager = classLoader.loadClass("com.tencent.commonsdk.util.notification.QQNotificationManager");
        } catch (Exception e){
            XposedBridge.log(e);
        }
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_SHOWEVERYNOTIFY)){
            showEveryNotify();
        }
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_NOTIFYSETGROUP)){
            keepApartMessage();
            removeOriginalNotifition();
        }
    }

    public void showEveryNotify(){
        if (mmMsgNotifyManager!=null){
            Method NumberMethod=getMethod(mmMsgNotifyManager,"b",int.class);
            XposedBridge.hookMethod(NumberMethod, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(1);
                }
            });
        }

    }

    public void keepApartMessage(){

        XposedHelpers.findAndHookMethod(mMobileQQService, "c", mToServiceMsg, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                ServiceMsg=methodHookParam.thisObject;

                Object toServiceMsg=methodHookParam.args[0];
                Bundle notifitionMsg = (Bundle)getObjectField(toServiceMsg,"extraData","Bundle");
                String[] msg = notifitionMsg.getStringArray("cmds");
                Intent intent = notifitionMsg.getParcelable("intent");
                Bitmap bitmap = notifitionMsg.getParcelable("bitmap");
                int istroop = intent.getIntExtra("uintype",-1) == -1 ? intent.getIntExtra("param_uinType",-1) : intent.getIntExtra("uintype",-1);
                String frienduin=intent.getStringExtra("uin");
                if ((istroop==0||istroop==1||istroop==3000)&&msg.length==3){
                    Notification notification = getNotification(bitmap,msg,intent);
                    XposedHelpers.callMethod(methodHookParam.thisObject,"b");
                    setBadge(notification);
                    //XposedHelpers.callStaticMethod(mCommonBadgeUtilImpl,"setBadge",context,66);
                    if (manager !=null){
                        String tag = getTag(frienduin,istroop);
                        if (!tags.contains(tag)){
                            tags.add(tag);
                        }
                        XposedHelpers.callMethod(manager,"cancelUseTag","",tag,121);
                        XposedHelpers.callMethod(manager,"notifyUseTag","",tag,121,notification);
                    }
                    Intent intent2 = new Intent("com.tencent.msg.newmessage");
                    intent2.setPackage("com.tencent.mobileqq");
                    intent2.putExtra("cmds", msg);
                    intent2.putExtra("intent", intent);
                    intent2.putExtra("type", 0);
                    intent2.putExtra("bitmap", bitmap);
                    context.sendBroadcast(intent2);

                    return null;
                }

                return XposedBridge.invokeOriginalMethod(methodHookParam.method,methodHookParam.thisObject,methodHookParam.args);
            }
        });

    }

    public void removeOriginalNotifition(){
        if (mQQNotificationManager!=null){
            XposedHelpers.findAndHookMethod(mQQNotificationManager, "cancel", String.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (!tags.isEmpty()){
                        for (int i=0;i<tags.size();i++){
                            XposedHelpers.callMethod(manager,"cancelUseTag","",tags.get(i),121);
                        }
                        tags.clear();
                    }
                }
            });
        } else {
            XposedHelpers.findAndHookMethod(mQNotificationManager, "cancel", String.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int id=(int)param.args[1];
                    if (id == 121){
                        XposedHelpers.callMethod(param.thisObject,"cancelAll");
                    }
                }
            });
        }

    }

    private String getTag(String senderuid,int istroop){
        String troop=null;
        if (istroop==0)troop="0000";
        if (istroop==1)troop="0001";
        if (istroop==3000)troop="3000";
        if (senderuid.length()<10){
            for (int i=0;i<10-senderuid.length();i++){
                senderuid="0"+senderuid;
            }
        }
        if (troop!=null&&!troop.isEmpty())return senderuid+troop;
        return null;
    }

    public Context getContext() {
        if ( mBaseApplication== null) return AndroidAppHelper.currentApplication().getApplicationContext();
        return (Context) XposedHelpers.callStaticMethod(mBaseApplication, "getContext");
    }

    public int getSmallIconId(){
        return XposedHelpers.getStaticIntField(mBaseApplication,"appnewmsgicon");
    }


    protected Notification getNotification(Bitmap bitmap,String[]msg,Intent intent) throws InvocationTargetException, IllegalAccessException {
        PendingIntent contextIntent=PendingIntent.getActivity(context,getNotificationId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder=new Notification.Builder(context)
                .setTicker(msg[0])
                .setContentTitle(msg[1])
                .setColor(0x0079ff)
                .setContentText(msg[2])
                .setContentIntent(contextIntent)
                .setDeleteIntent(getDeleteIntent())
                .setAutoCancel(true)
                .setLights(Color.GREEN,2000,2000)
                .setSmallIcon(getSmallIconId())
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setPriority(1)
                .setVibrate(new long[0])
                .setGroup("QQ")
                .setGroupSummary(true);


        if (bitmap!=null)builder.setLargeIcon(bitmap);
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_NOTIFYBIGSTYLE)){
            Notification.BigTextStyle bigTextStyle=new Notification.BigTextStyle();
            bigTextStyle.setBigContentTitle(msg[1]);
            int length=Integer.valueOf(settingUtils.getString(SettingUtils.SETTING_KEY_NOTIFYMAXNUM,"100"));
            String message=msg[2];
                if (length>0&&message.length()>length){
                    message=message.substring(0,length)+"...";
                }
            bigTextStyle.bigText(message);
            builder.setStyle(bigTextStyle);
        }

        return builder.build();
    }

    protected int getNotificationId() {
        return (int) (System.currentTimeMillis() & 0xfffffff);
    }

    private PendingIntent getDeleteIntent() throws InvocationTargetException, IllegalAccessException {
        Method pendingIntent=getMethod(mMobileQQService,"a",PendingIntent.class);
        pendingIntent.setAccessible(true);
        return (PendingIntent)pendingIntent.invoke(ServiceMsg);
    }


    private int getNum(Notification  notification){
        String title=notification.extras.getString(Notification.EXTRA_TITLE);

        if (title!=null&&!title.isEmpty()){
            Matcher matcher= Pattern.compile("(\\d+)\\S{1,3}新消息\\)?$").matcher(title);
            if (matcher.find()){
                return Integer.valueOf(matcher.group(1));
            }
        }

        return 1;
    }

    private void setBadge(Notification notification)  {
        XposedHelpers.callStaticMethod(mBadgeUtils,"a",context,getNum(notification),notification);
    }
}
