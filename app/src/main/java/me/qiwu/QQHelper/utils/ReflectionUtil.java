package me.qiwu.QQHelper.utils;

import android.app.AndroidAppHelper;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ReflectionUtil {

    public static Context getContext(){
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }


    public static Method findMethodIfExists(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
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

    public static Method findMethodByName(Class<?>clazz, String name){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods){
            if (method.getName().equals(name)){
                return method;
            }
        }
        return null;
    }

    public static Object getObjectField(Object o,String fieldName,Class<?>type){
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
    public static Object getObjectFieldWithoutName(Object o,String type){
        Field[]fields=o.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.getGenericType().toString().contains(type)){
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

    public static Object getObjectField(Object o,String fieldName,String type){
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

    public static <T> T getObject(Object obj, Class<?> type, String name) {
        return getObject(obj.getClass(), type, name, obj);
    }

    public static <T> T getObject(Class clazz, Class<?> type, String name) {
        return getObject(clazz, type, name, null);
    }

    @SuppressWarnings ("unchecked")
    public static <T> T getObject(Class clazz, Class<?> type, String name, Object obj) {
        try {
            Field field = findField(clazz, type, name);
            return field == null ? null : (T) field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field findField(Class<?> clazz, Class<?> type, String name) {
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

    public static void setObjectFieldByClass(Class<?>clazz,Object o,String fieldName,String type,Object setValue){
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
    public static Object getObjectFieldByClass(Class<?>clazz,Object o,String fieldName,Class<?>type){
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
    public static void setObjectField(Object o,String fieldName,String type,Object setValue){
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
    public static void setObjectFieldByClass(Class<?>clazz,Object o,String fieldName,Class<?>type,Object setValue){
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

    public static boolean isCallingFrom(String classname) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.toString().contains(classname)) {
                return true;
            }
        }
        return false;
    }

    public static void showEveryField(Object o,Class<?>clazz) throws IllegalAccessException {
        Field[]fields=clazz.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            if (field.get(o)!=null){
                XposedBridge.log(field.getName()+":"+field.get(o).toString());
            }

        }
    }

}
