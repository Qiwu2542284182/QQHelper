package me.qiwu.QQHelper.utils;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Deng on 2019/3/11.
 */

public class QQHelper {
    private static WeakReference<ClassLoader>classLoaderWeakReference = new WeakReference<>(null);
    public static void init(ClassLoader classLoader){
        if (classLoaderWeakReference.get()==null){
            classLoaderWeakReference = new WeakReference<>(classLoader);
        }
    }

    public static Context getContext(){
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }

    public static Object getQQAppInterface(){
        Class<?> mBaseApplicationImpl = XposedHelpers.findClass("com.tencent.common.app.BaseApplicationImpl",classLoaderWeakReference.get());
        return XposedHelpers.callMethod(XposedHelpers.callStaticMethod(mBaseApplicationImpl,"getApplication"),"getRuntime");
    }

    public static String getCurrentAccountUin(){
        return (String)XposedHelpers.callMethod(getQQAppInterface(),"getCurrentAccountUin");
    }

    public static Activity getActivity(){
        return (Activity)XposedHelpers.getObjectField(XposedHelpers.findClass("com.tencent.mobileqq.app.BaseActivity",classLoaderWeakReference.get()),"sTopActivity");
    }

    public static String getCurrentNickname(){
        return (String)XposedHelpers.callMethod(getQQAppInterface(),"getCurrentNickname");
    }

    public static String getSignature(){
        return getContext().getSharedPreferences("qqsettingme_signature" + getCurrentAccountUin(),0).getString("plainText","编辑个性签名");
    }

    public static Drawable getFriendHead(String uin){
        return (Drawable)XposedHelpers.callStaticMethod(XposedHelpers.findClass("awyo",classLoaderWeakReference.get()),"a",getQQAppInterface(),uin,(byte)3);
    }

    public static Drawable getTroopHead(String uin){
        return (Drawable)XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mobileqq.util.FaceDrawable",classLoaderWeakReference.get()),"a",getQQAppInterface(),4,uin);
    }

    public static Object getSessionInfo(String uin,int istroop){
        Class<?>mclazz = XposedHelpers.findClass("com.tencent.mobileqq.activity.aio.SessionInfo",classLoaderWeakReference.get());
        Object sessionInfo = XposedHelpers.newInstance(mclazz);
        setObjectField(sessionInfo,"a","String",uin);
        setObjectField(sessionInfo,"a","int",istroop);
        return sessionInfo;
    }

    public static ArrayList<String> getTroopUin() throws InvocationTargetException, IllegalAccessException {
        if (getQQAppInterface()!=null){
            Object mTroopManager = XposedHelpers.callMethod(getQQAppInterface(), "getManager", 51);
            Method method = getMethod(mTroopManager.getClass(),"a","ArrayList");
            ArrayList arrayList = (ArrayList)method.invoke(mTroopManager);
            ArrayList<String> troopuin=new ArrayList<String>();
            for (int i=0;i<arrayList.size();i++){
                String name=(String)getObjectFieldByClass(XposedHelpers.findClass("com.tencent.mobileqq.data.TroopInfo",classLoaderWeakReference.get()),arrayList.get(i),"troopuin","String");
                troopuin.add(name);
            }
            return troopuin;
        }
        return new ArrayList<>();
    }

    public static ArrayList getTroopName() throws InvocationTargetException, IllegalAccessException {
        if (getQQAppInterface()!=null){
            Object mTroopManager = XposedHelpers.callMethod(getQQAppInterface(), "getManager", 51);
            Method method = getMethod(mTroopManager.getClass(),"a","ArrayList");
            ArrayList arrayList = (ArrayList)method.invoke(mTroopManager);
            ArrayList<String> troopname=new ArrayList<String>();
            for (int i=0;i<arrayList.size();i++){
                String name=(String)getObjectFieldByClass(XposedHelpers.findClass("com.tencent.mobileqq.data.TroopInfo",classLoaderWeakReference.get()),arrayList.get(i),"troopname","String");
                troopname.add(name);
            }
            return troopname;
        }
        return null;
    }



    public static ArrayList getTroopDrawable() throws InvocationTargetException, IllegalAccessException {
        ArrayList arraylist=getTroopUin();
        ArrayList<Drawable>drawable=new ArrayList<>();
        if (arraylist!=null){
            for (int i=0;i<arraylist.size();i++){
                String uin=(String) arraylist.get(i);
                drawable.add(getTroopHead(uin));
            }
            return drawable;
        }
        return null;
    }

    public static ArrayList getFriendsInfo(){
        ArrayList arrayList=new ArrayList();
        String selfUin=getCurrentAccountUin();
        Object mFriendManager = XposedHelpers.callMethod(getQQAppInterface(), "getManager", 50);
        ConcurrentHashMap concurrentHashMap=(ConcurrentHashMap)getObjectField(mFriendManager,"c","ConcurrentHashMap");
        if (concurrentHashMap!=null){
            for (Object key:concurrentHashMap.keySet()){
                Object friend=concurrentHashMap.get(key);
                String uin=(String)getObjectField(friend,"uin","String");
                if (!uin.equals(selfUin)){
                    arrayList.add(friend);
                }
            }
        }
        return arrayList;
    }

    public static ArrayList getFriendUin(){
        ArrayList arrayList=getFriendsInfo();
        ArrayList<String>uins=new ArrayList<>();
        if (arrayList!=null){
            for (int i=0;i<arrayList.size();i++){
                String uin=(String)getObjectFieldByClass(XposedHelpers.findClass("com.tencent.mobileqq.data.Friends",classLoaderWeakReference.get()),arrayList.get(i),"uin","String");
                uins.add(uin);
            }
        }
        return uins;
    }

    public static ArrayList getFriendNick(){
        ArrayList arrayList=getFriendsInfo();
        ArrayList<String>nicks=new ArrayList<>();
        if (arrayList!=null){
            for (int i=0;i<arrayList.size();i++){
                Object frined=arrayList.get(i);
                String nick=(String) XposedHelpers.callMethod(frined,"getFriendNick");
                nicks.add(nick);
            }
        }
        return nicks;
    }

    public static ArrayList getFriendDawable(){
        ArrayList arrayList=getFriendUin();
        ArrayList<Drawable>heads=new ArrayList<>();
        if (arrayList!=null){
            for (int i=0;i<arrayList.size();i++){
                String uin=(String)arrayList.get(i);
                Drawable drawable=getFriendHead(uin);
                heads.add(drawable);
            }

        }
        return heads;
    }

    private static void setObjectField(Object o,String fieldName,String type,Object setValue){
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

    private static Object getObjectField(Object o,String fieldName,String type){
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

    private static Method getMethod(Class<?>clazz, String methodName, String returnType, Class<?>... parameters){
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

    private static Object getObjectFieldByClass(Class<?>clazz,Object o,String fieldName,String type){
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
}
