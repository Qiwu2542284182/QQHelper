package me.qiwu.QQHelper.hooks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.R;
import me.qiwu.QQHelper.adapter.TroopAndFriendSelectAdpter;
import me.qiwu.QQHelper.beans.ContactInfo;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.QQHelper;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.SettingUtils;
import me.qiwu.QQHelper.utils.VersionUtil;

/**
 * Created by Deng on 2018/6/20.
 */

public class MessageHook extends ReflectHelper {
    private Class<?>mQQAppInterface;
    private Class<?>mMessageInfo;
    private Class<?>mPttItemBuilder;
    private Class<?>mMessageForPtt;
    private Class<?>mChatActivityFacade;
    private Class<?>mQQCustomMenu;
    private Class<?>mBaseChatPie;
    private Class<?> mXEditTextEx;
    private TroopAndFriendSelectAdpter troopAndFriendSelectAdpter;
    private SettingUtils settingUtils=new SettingUtils();
    private ClassLoader classLoader;
    public MessageHook(ClassLoader classLoader) throws ClassNotFoundException {
        mQQAppInterface=classLoader.loadClass(QQAppInterface);
        //mMessageInfo=classLoader.loadClass(MessageInfo);
        mPttItemBuilder=classLoader.loadClass("acyw");
        //mMessageForPtt=classLoader.loadClass(MessageForPtt);
        mChatActivityFacade=classLoader.loadClass("aaae");
        //mQQCustomMenu=classLoader.loadClass(QQCustomMenu);
        mXEditTextEx=classLoader.loadClass(XEditTextEx);
        mBaseChatPie = XposedHelpers.findClass("com.tencent.mobileqq.activity.BaseChatPie",classLoader);
        this.classLoader=classLoader;
    }

    public void init() throws InvocationTargetException, IllegalAccessException {
        //cancelAtAll();
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_PTTSHARE)){
            pttShare();
        }
        //showChatTail();
    }

    /*private void showChatTail(){
        if (!settingUtils.getBoolean(SettingUtils.SETTING_KEY_STARTCHATTAIL))return;
        XposedHelpers.findAndHookMethod(mBaseChatPie, "a", String.class, "com.tencent.mobileqq.activity.ChatActivityFacade$SendMsgParams", ArrayList.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                String tail=getTail(settingUtils.getString(SettingUtils.SETTING_KEY_CHATTAILMSG,""));
                String msg = param.args[0].toString();
                if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_BANCHATTAIL)&&(tail.length()+msg.length())>100)return;
                param.args[0] = msg + tail;
            }
        });
    }

    private String getTail(String msg){
        if (!msg.equals("")){
            if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_CHATTAILSTOCH)){
                String[]msgs=msg.split(",");
                int num=msgs.length>1?new Random().nextInt(msgs.length):0;
                return msgs[num];
            }else {
                return msg;
            }
        };
        return "";
    }


    private void cancelAtAll(){
        final int AtAllNum= VersionUtil.isMore780()?13:12;
        XposedHelpers.findAndHookMethod(mMessageInfo,"a", mQQAppInterface, boolean.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int isAtAll=(int)param.getResult();
                if (isAtAll==AtAllNum){
                    String uin=(String)param.args[2];
                    String selectUin=settingUtils.getString("cancelAt_"+QQHelper.getCurrentAccountUin(),"");
                    String[]strings=selectUin.split(",");
                    for (String s:strings){
                        if (s.equals(uin))param.setResult(0);
                    }
                }
            }
        });
    }*/


    //语音转发
    private void pttShare(){
        Method method=getMethod(mPttItemBuilder,"a","axle", View.class);
        if (method!=null){
            //去除带图片的菜单
            //添加自己的菜单
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object[]objects=(Object[]) param.getResult();
                    Object QQCustomMenuItem=objects[0].getClass().newInstance();
                    setObjectField(QQCustomMenuItem,"a","int",R.id.ptt_share);
                    setObjectField(QQCustomMenuItem,"a","String","转发");
                    objects= Arrays.copyOf(objects,objects.length+1);
                    objects[objects.length-1]=QQCustomMenuItem;
                    param.setResult(objects);
                }
            });
            //添加点击事件
            XposedHelpers.findAndHookMethod(mPttItemBuilder, "a", int.class, Context.class, ChatMessage, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    int id=(int)methodHookParam.args[0];
                    Context context=(Context)methodHookParam.args[1];
                    Object chatMessage=methodHookParam.args[2];
                    if (id==R.id.ptt_share){
                        String url=(String)XposedHelpers.callMethod(chatMessage,"getLocalFilePath");
                        showSelectDialog(context,url);
                        File file=new File(url);
                        if (!file.exists()){
                            Toast.makeText(context,"未找到语音文件",Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                    return XposedBridge.invokeOriginalMethod(methodHookParam.method,methodHookParam.thisObject,methodHookParam.args);
                }
            });
        }

    }

    private void showSelectDialog(final Context context, final String url) throws InvocationTargetException, IllegalAccessException {
        final AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("发送到")
                .setView(getView(context))
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList arrayList=troopAndFriendSelectAdpter.getSelectInfo();
                        if (!arrayList.isEmpty()){
                            boolean isSuccess=true;
                            for (int i=0;i<arrayList.size();i++){
                                ContactInfo contactInfo=(ContactInfo)arrayList.get(i);
                                try {
                                    XposedHelpers.callStaticMethod(mChatActivityFacade,"a",QQHelper.getQQAppInterface(),QQHelper.getSessionInfo(contactInfo.getUin(),contactInfo.getIstroop()),url);
                                } catch (Exception e){
                                    isSuccess=false;
                                    XposedBridge.log(e);
                                }
                            }
                            Toast.makeText(context,"发送"+(isSuccess?"成功":"失败"),Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .setNeutralButton("全选",null)
                .create();
        //alertdialog延迟一毫秒显示，防止头像不显示
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(16.0f);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff4284f3);

                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        troopAndFriendSelectAdpter.setAllSelect();
                    }
                });
            }
        },100);
    }

    private View getView(Context context) throws InvocationTargetException, IllegalAccessException {

        final EditText editText=new EditText(context);
        editText.setBackgroundColor(0x00000000);
        editText.setHint("搜索");
        editText.setTextSize(18.0f);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,30.0f));
        layoutParams.setMargins(DensityUtil.dip2px(context,30.0f),0,DensityUtil.dip2px(context,30.0f),10);
        editText.setLayoutParams(layoutParams);
        final ListView listView=new ListView(context);
        troopAndFriendSelectAdpter=new TroopAndFriendSelectAdpter(context);
        listView.setAdapter(troopAndFriendSelectAdpter);
        listView.setDivider(new ColorDrawable(0x00000000));
        listView.setSelector(new ColorDrawable(0x00000000));
        RadioGroup radioGroup=new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setGravity(Gravity.CENTER);
        RadioButton friend=new RadioButton(context);
        friend.setChecked(true);
        friend.setText("好友");
        friend.setTextColor(Color.BLACK);
        friend.setId(R.id.select_friend);
        RadioButton group=new RadioButton(context);
        group.setText("群聊");
        group.setTextColor(Color.BLACK);
        group.setId(R.id.select_group);
        radioGroup.addView(friend);
        radioGroup.addView(group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.select_friend){
                    ((RadioButton)group.getChildAt(0)).setChecked(true);
                    ((RadioButton)group.getChildAt(1)).setChecked(false);
                    troopAndFriendSelectAdpter.setmFriendInfo();

                }else if (checkedId==R.id.select_group){
                    ((RadioButton)group.getChildAt(1)).setChecked(true);
                    ((RadioButton)group.getChildAt(0)).setChecked(false);
                    troopAndFriendSelectAdpter.setmGroupInfo();
                }
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                troopAndFriendSelectAdpter.setData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TroopAndFriendSelectAdpter.ViewHolder viewHolder=(TroopAndFriendSelectAdpter.ViewHolder)view.getTag();
                viewHolder.cBox.toggle();
            }
        });
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(radioGroup);
        linearLayout.addView(editText);
        linearLayout.addView(listView);
        return linearLayout;
    }

}
