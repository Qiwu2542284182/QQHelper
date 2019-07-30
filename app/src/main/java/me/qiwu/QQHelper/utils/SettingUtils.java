package me.qiwu.QQHelper.utils;

import android.app.AndroidAppHelper;
import android.content.Context;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XSharedPreferences;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.MainHook;

/**
 * Created by Deng on 2018/4/26.
 */

public class SettingUtils {
    public static final String SETTING_KEY_STARTREVOKE="isStartRevoke";
    public static final String SETTING_KEY_SHOWREVOKEMSG="isShowRevkeMsg";
    public static final String SETTING_KEY_REVOKEMSG="revokeMsg";
    public static final String SETTING_KEY_REVOKEADMMSG="revokeMsg_adm";
    public static final String SETTING_KEY_PREFLASHPIC="preFlashPic";
    public static final String SETTING_KEY_SHOWASPIC="showAsPic";
    public static final String SETTING_KEY_BANHOTPICMASK="banHotPicMask";
    public static final String SETTING_KEY_SHOWEVERYNOTIFY="showEveryNotify";
    public static final String SETTING_KEY_NOTIFYSETGROUP="setGroup";
    public static final String SETTING_KEY_NOTIFYBIGSTYLE="setBigTextStyle";
    public static final String SETTING_KEY_NOTIFYMAXNUM="maxNum";
    public static final String SETTING_KEY_PREATALLMSG="preAtAllMsg";
    public static final String SETTING_KEY_SELECTGROUP="selectGroup";
    public static final String SETTING_KEY_PREUPDATA="preUpdata";
    public static final String SETTING_KEY_DIYFONT="diyFont";
    public static final String SETTING_KEY_FONTSIZE="fontSize";
    public static final String SETTING_KEY_QRLOGIN="qrLigon";
    public static final String SETTING_KEY_CHEAT="cheat";
    public static final String SETTING_KEY_CHEATBEFORE="cheat_before";
    public static final String SETTING_KEY_CHEATNOW="cheat_now";
    public static final String SETTING_KEY_CHEATBEFORETOUCI="cheat_before_touzi";
    public static final String SETTING_KEY_CHEATBEFORECAIQUAN="cheat_before_caiquan";
    public static final String SETTING_KEY_PTTSHARE="ptt_share";
    public static final String SETTING_KEY_DOUTU="doutu";
    public static final String SETTING_KEY_SAVEMOREPIC="saveMorePic";
    public static final String SETTING_KEY_MSGSENDGROUP="msg_send_group";
    public static final String SETTING_KEY_STARTCHATTAIL="start_chat_tail";
    public static final String SETTING_KEY_CHATTAILSTOCH="chat_tail_stochastic";
    public static final String SETTING_KEY_CHATTAILMSG="chat_tail_msg";
    public static final String SETTING_KEY_BANCHATTAIL="ban_chat_tail";
    public static final String SETTING_KEY_STARTFINGERPRINT="start_fingerprint";
    public static final String SETTING_KEY_FINGERPSW="fingerPrintPasswaord";
    public static final String SETTING_KEY_THEME="theme";
    public static final String SETTING_KEY_STARTTRANS="startTrans";
    public static final String SETTING_KEY_TRANSAPPID="transAppId";
    public static final String SETTING_KEY_TRANSKEY="transKey";
    public static final String SETTING_KEY_TRANSLANG="transLang";
    public static final String SETTING_KEY_TRANSUP = "transUp";

    private static WeakReference<XSharedPreferences> xSharedPreferences = new WeakReference<>(null);
        private static XSharedPreferences getPref() {
            XSharedPreferences preferences = xSharedPreferences.get();
            if (preferences == null) {
                preferences = new XSharedPreferences(getPackageName(),BuildConfig.APPLICATION_ID);
                preferences.makeWorldReadable();
                xSharedPreferences = new WeakReference<>(preferences);
            } else {
                preferences.reload();
            }
            return preferences;
        }
    public  boolean getBoolean(String key){
        return getPref().getBoolean(key,false);
    }
    public int getInt(String key){return getPref().getInt(key,0);}
    public String getString(String key){return getPref().getString(key,"");}
    public String getString(String key,String defealtValue){return getPref().getString(key,defealtValue);}

    public float getFontSize(){
        return Float.valueOf(getString("fontSize"));
    }
    public static boolean getBoolean(Context context,String key){
        if (context!=null&&key!=null){
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getBoolean(key,false);
        }
        return false;
    }

    public static String getString(Context context,String key){
        if (context!=null&&key!=null){
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString(key,"");
        }
        return "";
    }
    public static int getInt(Context context,String key){
        if (context!=null&&key!=null){
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getInt(key,0);
        }
        return 0;
    }
    public static void setBoolean(Context context,String key,boolean newValue){
        if (context!=null&&key!=null){
            context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putBoolean(key,newValue).apply();
        }
    }
    public static void setString(Context context,String key,String newValue){
        if (context!=null&&key!=null){
            context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putString(key,newValue).apply();
        }
    }
    public static void setInt(Context context,String key,int Value){
        if (context!=null&&key!=null){
            context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putInt(key,Value).apply();
        }
    }

    public static String getPackageName(){
        return AndroidAppHelper.currentPackageName();
    }
}
