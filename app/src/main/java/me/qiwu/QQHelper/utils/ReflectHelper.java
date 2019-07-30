package me.qiwu.QQHelper.utils;

import android.app.AndroidAppHelper;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Deng on 2018/4/20.
 */

public class ReflectHelper {
    public String QQMessageFacade="com.tencent.mobileqq.app.message.QQMessageFacade";
    public String UniteGrayTipParam="com.tencent.mobileqq.graytip.UniteGrayTipParam";
    public String QQAppInterface="com.tencent.mobileqq.app.QQAppInterface";
    public String RevokeMsgInfo="com.tencent.mobileqq.revokemsg.RevokeMsgInfo";
    public String BaseMessageManager="com.tencent.mobileqq.app.message.BaseMessageManager";
    public String MessageRecord="com.tencent.mobileqq.data.MessageRecord";
    public String MessageRecordFactory="com.tencent.mobileqq.service.message.MessageRecordFactory";
    public String MsgNotifyManager="com.tencent.mobileqq.app.msgnotify.MsgNotifyManager";
    public String ContactUtils="com.tencent.mobileqq.utils.ContactUtils";
    public String FlashPicHelper="com.tencent.mobileqq.app.FlashPicHelper";
    public String HotChatFlashPicActivity="com.tencent.mobileqq.dating.HotChatFlashPicActivity";
    public String QQSettingSettingActivity="com.tencent.mobileqq.activity.QQSettingSettingActivity";
    public String BaseApplication="com.tencent.qphone.base.util.BaseApplication";
    public String MobileQQService="com.tencent.mobileqq.service.MobileQQService";
    public String ChatMessage="com.tencent.mobileqq.data.ChatMessage";
    public String SessionInfo="com.tencent.mobileqq.activity.aio.SessionInfo";
    public String ToServiceMsg="com.tencent.qphone.base.remote.ToServiceMsg";
    public String SplashActivity="com.tencent.mobileqq.activity.SplashActivity";
    public String QNotificationManager="com.tencent.mobileqq.msf.sdk.QNotificationManager";
    public String BadgeUtils="com.tencent.util.BadgeUtils";
    public String QRLoginActivity="com.tencent.biz.qrcode.activity.QRLoginActivity";
    public String UpgradeController="com.tencent.mobileqq.app.upgrade.UpgradeController";
    public String FontSettingManager="com.tencent.mobileqq.app.FontSettingManager";
    public String BaseApplicationImpl="com.tencent.common.app.BaseApplicationImpl";
    public String DevlockQuickLoginActivity="com.tencent.mobileqq.activity.DevlockQuickLoginActivity";
    public String MsgProxyUtils="com.tencent.mobileqq.app.message.MsgProxyUtils";
    public String MessageInfo="com.tencent.mobileqq.troop.data.MessageInfo";
    public String FavEmoConstant="com.tencent.mobileqq.emosm.favroaming.FavEmoConstant";
    public String FavEmoRoamingHandler="com.tencent.mobileqq.app.FavEmoRoamingHandler";
    public String EmoAddedAuthCallback="com.tencent.mobileqq.emosm.favroaming.EmoAddedAuthCallback";
    public String UpCallBack$SendResult="com.tencent.mobileqq.pic.UpCallBack$SendResult";
    public String ItemBuilderFactory="com.tencent.mobileqq.activity.aio.item.ItemBuilderFactory";
    public String FlashPicItemBuilder="com.tencent.mobileqq.activity.aio.item.FlashPicItemBuilder";
    public String PicItemBuilder="com.tencent.mobileqq.activity.aio.item.PicItemBuilder";
    public String BaseChatItemLayout="com.tencent.mobileqq.activity.aio.BaseChatItemLayout";
    public String FlashPicItemBuilder$FlashPicHolder="com.tencent.mobileqq.activity.aio.item.FlashPicItemBuilder$FlashPicHolder";
    public String FormSwitchItem="com.tencent.mobileqq.widget.FormSwitchItem";
    public String FormSimpleItem="com.tencent.mobileqq.widget.FormSimpleItem";
    public String BaseBubbleBuilder$ViewHolder="com.tencent.mobileqq.activity.aio.BaseBubbleBuilder$ViewHolder";
    public String OnLongClickAndTouchListener="com.tencent.mobileqq.activity.aio.OnLongClickAndTouchListener";
    public String MessageForPic="com.tencent.mobileqq.data.MessageForPic";
    public String ChatThumbView="com.tencent.mobileqq.activity.aio.item.ChatThumbView";
    public String BannerManager="com.tencent.mobileqq.activity.recent.BannerManager";
    public String EmoticonPanelLinearLayout ="com.tencent.mobileqq.emoticonview.EmoticonPanelLinearLayout";
    public String PngFrameUtil ="com.tencent.mobileqq.magicface.drawable.PngFrameUtil";
    public String TroopInfo="com.tencent.mobileqq.data.TroopInfo";
    public String TroopManager="com.tencent.mobileqq.app.TroopManager";
    public String FaceDrawable="com.tencent.mobileqq.util.FaceDrawable";
    public String BaseChatPie="com.tencent.mobileqq.activity.BaseChatPie";
    public String ChatActivityFacade ="com.tencent.mobileqq.activity.ChatActivityFacade";
    public String AIOPanelUtiles ="com.tencent.mobileqq.activity.aio.panel.AIOPanelUtiles";
    public String PttItemBuilder="com.tencent.mobileqq.activity.aio.item.PttItemBuilder";
    public String MessageForPtt="com.tencent.mobileqq.data.MessageForPtt";
    public String QQCustomMenu="com.tencent.mobileqq.utils.dialogutils.QQCustomMenu";
    public String Friends="com.tencent.mobileqq.data.Friends";
    public String ChatActivityFacade$SendMsgParams="com.tencent.mobileqq.activity.ChatActivityFacade$SendMsgParams";
    public String QWalletPluginProxyActivity="cooperation.qwallet.plugin.QWalletPluginProxyActivity";
    public String XEditTextEx="com.tencent.widget.XEditTextEx";
    public String URLDrawable="com.tencent.image.URLDrawable";
    public String PanelIconLinearLayout="com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout";

    public Object getObjectField(Object o,String fieldName,Class<?>type){
        Field[]fields=o.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().equals(type.getName())){
                try{
                    return field.get(o);
                }catch (Exception e){
                    XposedBridge.log(e);
                    return null;
                }
            }
        }
        return null;
    }
    public Object getObjectField(Object o,String fieldName,String type){
        Field[]fields=o.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().contains(type)){
                try{
                    return field.get(o);
                }catch (Exception e){
                    XposedBridge.log(e);
                    return null;
                }
            }
        }
        return null;
    }

    protected <T> T getObject(Object obj, Class<?> type, String name) {
        return getObject(obj.getClass(), type, name, obj);
    }

    protected <T> T getObject(Class clazz, Class<?> type, String name) {
        return getObject(clazz, type, name, null);
    }

    @SuppressWarnings ("unchecked")
    protected <T> T getObject(Class clazz, Class<?> type, String name, Object obj) {
        try {
            Field field = findField(clazz, type, name);
            return field == null ? null : (T) field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    protected Field findField(Class<?> clazz, Class<?> type, String name) {
        if (clazz != null && type != null && !name.isEmpty()) {
            Class<?> clz = clazz;
            do {
                for (Field field : clz.getDeclaredFields()) {
                    if (field.getType() == type && field.getName()
                            .equals(name)) {
                        field.setAccessible(true);
                        return field;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
        }
        return null;
    }

    public void setObjectField(Object o,String fieldName,Class<?>type,Object setValue){
        Field[]fields=o.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().equals(type.getName())){
                try{
                    field.set(o,setValue);
                }catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        }
    }
    public void setObjectField(Object o,String fieldName,String type,Object setValue){
        Field[]fields=o.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().contains(type)){
                try{
                    field.set(o,setValue);
                }catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        }
    }
    public void setObjectFieldByClass(Class<?>clazz,Object o,String fieldName,Class<?>type,Object setValue){
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().equals(type.getName())){
                try{
                    field.set(o,setValue);
                }catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        }
    }
    public void setObjectFieldByClass(Class<?>clazz,Object o,String fieldName,String type,Object setValue){
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().contains(type)){
                try{
                    field.set(o,setValue);
                }catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        }
    }
    public Object getObjectFieldByClass(Class<?>clazz,Object o,String fieldName,Class<?>type){
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().equals(type.getName())){
                try{
                    return field.get(o);
                }catch (Exception e){
                    XposedBridge.log(e);
                    return null;
                }
            }
        }
        return null;
    }
    public Object getObjectFieldByClass(Class<?>clazz,Object o,String fieldName,String type){
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getName().equals(fieldName)&&field.getGenericType().toString().contains(type)){
                try{
                    return field.get(o);
                }catch (Exception e){
                    XposedBridge.log(e);
                    return null;
                }
            }
        }
        return null;
    }
    public Method getMethod(Class<?>clazz, String methodName, Class<?> returnType, Class<?>... parameters){
        for (Method method:clazz.getDeclaredMethods()){
            if (method.getName().equals(methodName) && method.getReturnType()==returnType&&method.getParameterTypes().length==parameters.length){
                if (method.getParameterTypes().length==parameters.length){
                    Class<?>[] pars = method.getParameterTypes();
                    boolean findMethod = true;
                    for (int i = 0; i < parameters.length; i++) {
                        if (pars[i] != parameters[i]) {
                            findMethod = false;
                            break;
                        }
                    }
                    if (findMethod) {
                        return method;
                    }
                }

            }
        }
        XposedBridge.log("cannot find method:"+clazz.getName()+"."+methodName);
        return null;
    }

    protected Method findMethodIfExists(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        if (clazz != null && returnType != null && !methodName.isEmpty()) {
            Class<?> clz = clazz;
            do {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(clazz, returnType, parameterTypes);
                for (Method method : methods) {
                    if (method.getName()
                            .equals(methodName)) {
                        return method;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
        }
        return null;
    }

    public Method getMethod(Class<?>clazz, String methodName, String returnType, Class<?>... parameters){
        for (Method method:clazz.getDeclaredMethods()){
            if (method.getName().equals(methodName) && method.getReturnType().toString().contains(returnType)&&method.getParameterTypes().length==parameters.length){
                if (method.getParameterTypes().length==parameters.length){
                    Class<?>[] pars = method.getParameterTypes();
                    boolean findMethod = true;
                    for (int i = 0; i < parameters.length; i++) {
                        if (pars[i] != parameters[i]) {
                            findMethod = false;
                            break;
                        }
                    }
                    if (findMethod) {
                        return method;
                    }
                }

            }
        }
        XposedBridge.log("cannot find method:"+clazz.getName()+"."+methodName);
        return null;
    }

    public void showEveryField(Object o,Class<?>clazz) throws IllegalAccessException {
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.get(o)!=null){
                XposedBridge.log(field.getName()+":"+field.get(o).toString());
            }

        }
    }
    public boolean isCallingFrom(String classname) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.toString().contains(classname)) {
                return true;
            }
        }
        return false;
    }

    public Context getContext(){
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }
}
