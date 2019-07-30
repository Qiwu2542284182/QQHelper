package me.qiwu.QQHelper.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.ReflectHelper;

public class ChatFragmentHook extends ReflectHelper {
    public static Object currentSessionInfo;
    private ClassLoader classLoader;
    public ChatFragmentHook(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    public void init(){
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.ChatFragment", classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object baseChatPie = getObject(param.thisObject,XposedHelpers.findClass(BaseChatPie,classLoader),"a");
                String name = baseChatPie.getClass().getName();
                if (name.endsWith("FriendChatPie") || name.endsWith("TroopChatPie") || name.endsWith("DiscussChatPie") || name.endsWith("StrangerChatPie")){
                    currentSessionInfo = getObject(baseChatPie,XposedHelpers.findClass("com.tencent.mobileqq.activity.aio.SessionInfo",classLoader),"a");

                }
            }
        });
    }
}
