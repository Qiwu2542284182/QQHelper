package me.qiwu.QQHelper.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONObject;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.BuildConfig;
import me.qiwu.QQHelper.R;
import me.qiwu.QQHelper.hooks.other.MsgGroupSend;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.PasswordUil;
import me.qiwu.QQHelper.utils.QQHelper;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.utils.ReflectionUtil;
import me.qiwu.QQHelper.utils.SettingUtils;
import me.qiwu.QQHelper.adapter.TroopSelectAdapter;

/**
 * Created by Deng on 2018/7/12.
 */

public class SettingView extends ReflectHelper {
    private Class<?>mQQSetting;
    private Class<?>mFormSwitchItem;
    private Class<?>mFormSimpleItem;
    private boolean isAdd=false;
    private ClassLoader classLoader;
    private int diceNum=0;
    private int morraNum=0;
    private final String[]diceItem={"1","2","3","4","5","6"};
    private final String[]morraItem={"石头","剪刀","布"};
    private final String[] items = new String[] {"防撤回","消息相关","通知栏消息提醒", "其他功能" ,"关于"};
    public SettingView(ClassLoader classLoader) throws ClassNotFoundException {
        mQQSetting=classLoader.loadClass(QQSettingSettingActivity);
        mFormSwitchItem=classLoader.loadClass(FormSwitchItem);
        mFormSimpleItem=classLoader.loadClass(FormSimpleItem);
        this.classLoader=classLoader;
    }
    public void addSettingView(){
        if (isAdd)return;
        isAdd=true;
        XposedHelpers.findAndHookMethod(mQQSetting, "doOnCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View SimpleItem=null;
                Object QQview=getObjectField(param.thisObject,"a","FormSimpleItem");
                if (QQview==null){
                    Object TimView=getObjectField(param.thisObject,"a","FormCommonSingleLineItem");
                    SimpleItem=(View)TimView;
                }else {
                    SimpleItem=(View)QQview;
                }
                final Context context=SimpleItem.getContext();
                final Object formSimpleItem=SimpleItem.getClass().getConstructor(Context.class).newInstance(context);
                XposedHelpers.callMethod(formSimpleItem,"setLeftText","辅助模块");
                XposedHelpers.callMethod(formSimpleItem,"setRightText", BuildConfig.VERSION_NAME);
                final LinearLayout linearLayout = (LinearLayout) SimpleItem.getParent();
                linearLayout.addView((View) formSimpleItem,0);
                ((View) formSimpleItem).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                .setTitle("辅助模块"+BuildConfig.VERSION_NAME)
                                .setItems(items, null)
                                .create();
                        alertDialog.show();
                        setSettingDialogStyle(alertDialog,context);
                    }
                });
            }
        });
        XposedHelpers.findAndHookMethod(mQQSetting, "doOnDestroy", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                isAdd=false;
            }
        });
    }

    private void setSettingDialogStyle(AlertDialog alertDialog, final Context context) {
        final int padding= DensityUtil.dip2px(context,30.0f);
        final int listViewHeight=DensityUtil.dip2px(context,48.0f);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(20.0f);
            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mListView");
            mMessage.setAccessible(true);
            final ListView mlistView = (ListView) mMessage.get(mAlertController);
            mlistView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView itemText = (TextView) view;
                    itemText.setPadding(padding, 0, padding, 0);
                    itemText.setTextColor(Color.BLACK);
                    itemText.setTextSize(18.0f);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listViewHeight);
                    itemText.setLayoutParams(layoutParams);
                    return view;
                }
            });
            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            try {
                                showRevokeDialog(context,position);
                            } catch (Exception e){
                                showErrorToast(context,position);
                                XposedBridge.log(e);
                            }
                            break;
                        case 1:
                            try {
                                showMessageDialog(context,position);
                            } catch (Exception e){
                                XposedBridge.log(e);
                                showErrorToast(context,position);
                            }
                            break;
                        case 2:
                            try {
                                showNotifyDialog(context,position);
                            }catch (Exception e){
                                XposedBridge.log(e);
                                showErrorToast(context,position);
                            }
                            break;
                        case 3:
                            try {
                                showFingerPrintDialog(context,position);
                            } catch (Exception e){
                                XposedBridge.log(e);
                                showErrorToast(context,position);
                            }
                            break;
                        case 4:
                            try {
                                showOtherDialog(context,position);
                            } catch (Exception e){
                                XposedBridge.log(e);
                                showErrorToast(context,position);
                            }
                            break;
                        case 5:
                            try{
                                showAboutDialog(context);
                            }catch (Exception e){
                                showErrorToast(context,position);
                            }

                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }



    private void showErrorToast(Context context,int i){
        Toast.makeText(context,"开启"+items[i]+"界面出错",Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog(final Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("关于")
                .setMessage("当前版本："+BuildConfig.VERSION_NAME+"\nQQ的辅助模块，为QQ添加一些功能。\n目前功能包括：\n" +
                        "● 防撤回、闪照转图片（可直接查看和保存,EdXP开启闪照标识会导致头像不断刷新或无法加载，谨慎开启）\n" +
                        "● 屏蔽@全体消息、语音消息转发、群发文本消息、聊天小尾巴、聊天消息翻译（需要自己申请百度翻译AppId和Key）\n" +
                        "● 通知栏消息分栏显示（取代原来的有XX个联系人给你发来XX条信息）\n" +
                        "● 指纹支付\n" +
                        "● 屏蔽更新提醒、自定义字体大小、发送网络表情、收藏更多表情、扫一扫自动登录、猜拳骰子作弊\n" +
                        "仅做学习交流使用，请勿用于非法用途。\n若发现bug，在酷安反馈@祈无")
                .setNegativeButton("打赏",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("come_from", 5);
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("targetUin", "2542284182");
                            jSONObject.put("targetNickname", "祈无");
                            jSONObject.put("sign", "");
                            jSONObject.put("trans_fee", "");
                            jSONObject.put("source", "1");
                            jSONObject.put("desc", "");
                            intent.putExtra("extra_data", jSONObject.toString());
                            intent.putExtra("app_info", "appid#20000001|bargainor_id#1000026901|channel#wallet");
                            intent.putExtra("callbackSn", "0");
                            intent.setClassName(context, "com.tencent.mobileqq.activity.qwallet.TransactionActivity");
                            context.startActivity(intent);

                        }catch (Exception e){
                            XposedBridge.log(e);
                        }
                    }
                })
                .setPositiveButton("确定",null)
                .create();
        alertDialog.show();
        setAboutDialogStyle(alertDialog);
    }

    private void setAboutDialogStyle(AlertDialog alertDialog){
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff4284f3);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(20.0f);
            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(18.0f);
        } catch (Exception e){
            XposedBridge.log(e);
        }
    }

    private void showRevokeDialog(Context context,int position) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(items[position])
                .setView(getRevokeSettingView(context))
                .create();
        alertDialog.show();
    }

    private void showNotifyDialog(Context context,int position) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(items[position])
                .setMessage("开启消息分组后将显示每条消息，取代原来的有XX个联系人给你发来XX条消息")
                .setView(getNotifyView(context))
                .create();
        alertDialog.show();
    }

    private void showMessageDialog(Context context,int position) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(items[position])
                .setView(getMessageView(context))
                .create();
        alertDialog.show();
    }

    private void showFingerPrintDialog(Context context,int position) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(items[position])
                .setView(getFingerPrintView(context))
                .create();
        alertDialog.show();
    }

    private void showOtherDialog(Context context,int position) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(items[position])
                .setView(getOtherView(context))
                .create();
        alertDialog.show();
    }

    private void showSelectGroupDialog(final Context context) throws InvocationTargetException, IllegalAccessException {
        String selectGroupUin=context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getString("cancelAt_"+QQHelper.getCurrentAccountUin(),"");
        ArrayList<String> isSelectUin=new ArrayList<>();
        if (!selectGroupUin.equals("")||!selectGroupUin.isEmpty()){
            isSelectUin=new ArrayList(Arrays.asList(selectGroupUin.split(",")));
        }
        final EditText editText=new EditText(context);
        editText.setBackgroundColor(0x00000000);
        editText.setHint("搜索");
        editText.setTextSize(18.0f);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(context,40.0f));
        layoutParams.setMargins(DensityUtil.dip2px(context,30.0f),0,DensityUtil.dip2px(context,30.0f),10);
        editText.setLayoutParams(layoutParams);
        final ListView listView=new ListView(context);
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        linearLayout.addView(listView);
        final TroopSelectAdapter baseAdapter=new TroopSelectAdapter(context,isSelectUin,true);
        listView.setAdapter(baseAdapter);
        listView.setSelector(new ColorDrawable(0x00000000));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               baseAdapter.click(view);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                baseAdapter.setSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("选择群聊")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putString("cancelAt_"+QQHelper.getCurrentAccountUin(),baseAdapter.getSelectUin()).apply();
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
                        baseAdapter.setSelectAll();
                    }
                });
            }
        },100);



    }

    private void showChatTailDialog(Context context) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        LinearLayout linearLayout=getLinearLayout(context);
        int padding= DensityUtil.dip2px(context,20.0f);
        linearLayout.setPadding(padding,0,padding,25);
        linearLayout.addView(getFormSwitchItem(context,"开启聊天小尾巴",SettingUtils.SETTING_KEY_STARTCHATTAIL));
        linearLayout.addView(getFormSwitchItem(context,"使用随机小尾巴",SettingUtils.SETTING_KEY_CHATTAILSTOCH));
        linearLayout.addView(getFormSimpleItem(context,"设置聊天小尾巴","",SettingUtils.SETTING_KEY_CHATTAILMSG,""));
        linearLayout.addView(getFormSwitchItem(context,"输入框超过100字不使用小尾巴",SettingUtils.SETTING_KEY_BANCHATTAIL));
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("聊天小尾巴设置")
                .setMessage("若开启随机小尾巴，请将小尾巴内容以,（英文逗号）相隔")
                .setView(linearLayout)
                .create();
        alertDialog.show();
    }

    private void showTransSettingDialog(Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LinearLayout linearLayout = getLinearLayout(context);
        int padding= DensityUtil.dip2px(context,20.0f);
        linearLayout.setPadding(padding,0,padding,25);
        linearLayout.addView(getFormSwitchItem(context,"开启消息翻译",SettingUtils.SETTING_KEY_STARTTRANS));
        linearLayout.addView(getFormSimpleItem(context,"百度翻译AppId","",SettingUtils.SETTING_KEY_TRANSAPPID,""));
        linearLayout.addView(getFormSimpleItem(context,"百度翻译Key","",SettingUtils.SETTING_KEY_TRANSKEY,""));
        linearLayout.addView(getFormSimpleItem(context,"翻译后的语言","",SettingUtils.SETTING_KEY_TRANSLANG,"en"));
        linearLayout.addView(getFormSwitchItem(context,"翻译结果位于原来消息之上",SettingUtils.SETTING_KEY_TRANSUP));
        AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("消息翻译")
                .setMessage("翻译后的语言：英语（en），日语（jp），法语（fra），其他自行百度")
                .setView(linearLayout)
                .create();
        alertDialog.show();
    }

    private View getRevokeSettingView(Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        LinearLayout linearLayout=getLinearLayout(context);
        linearLayout.addView(getFormSwitchItem(context,"防止消息撤回", SettingUtils.SETTING_KEY_STARTREVOKE));
        linearLayout.addView(getFormSwitchItem(context,"显示撤回提示",SettingUtils.SETTING_KEY_SHOWREVOKEMSG));
        linearLayout.addView(getFormSimpleItem(context,"自定义撤回提示","",SettingUtils.SETTING_KEY_REVOKEMSG,"尝试撤回一条消息"));
        linearLayout.addView(getFormSimpleItem(context,"自定义管理员撤回提示","",SettingUtils.SETTING_KEY_REVOKEADMMSG,"尝试撤回一条成员消息"));
        linearLayout.addView(getFormSwitchItem(context,"以图片方式打开闪照",SettingUtils.SETTING_KEY_SHOWASPIC));
        linearLayout.addView(getFormSwitchItem(context,"禁用闪照标识",SettingUtils.SETTING_KEY_BANHOTPICMASK));
        return linearLayout;
    }


    private View getNotifyView(Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LinearLayout linearLayout=getLinearLayout(context);
        linearLayout.addView(getFormSwitchItem(context,"显示每条消息",SettingUtils.SETTING_KEY_SHOWEVERYNOTIFY));
        linearLayout.addView(getFormSwitchItem(context,"消息分组",SettingUtils.SETTING_KEY_NOTIFYSETGROUP));
        linearLayout.addView(getFormSwitchItem(context,"多行显示单条信息",SettingUtils.SETTING_KEY_NOTIFYBIGSTYLE));
        linearLayout.addView(getFormSimpleItem(context,"单条消息最大字数","",SettingUtils.SETTING_KEY_NOTIFYMAXNUM,"100"));
        return linearLayout;
    }

    private View getMessageView(final Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LinearLayout linearLayout=getLinearLayout(context);
        linearLayout.addView(getFormSwitchItem(context,"屏蔽@全体消息",SettingUtils.SETTING_KEY_PREATALLMSG));
        View view=getFormSimpleItem(context,"选择屏蔽群聊","",SettingUtils.SETTING_KEY_SELECTGROUP,"");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showSelectGroupDialog(context);
                }catch (Exception e){
                    XposedBridge.log(e);
                }

            }
        });
        linearLayout.addView(view);
        linearLayout.addView(getFormSwitchItem(context,"语音消息转发",SettingUtils.SETTING_KEY_PTTSHARE));
        View view1=getFormSimpleItem(context,"群发文本消息","",SettingUtils.SETTING_KEY_MSGSENDGROUP,"");
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new MsgGroupSend(classLoader).showWriteMsgEditDialog(context);
                }catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        });
        linearLayout.addView(view1);
        View view2=getFormSimpleItem(context,"聊天小尾巴","","","");
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showChatTailDialog(context);
                } catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        });
        linearLayout.addView(view2);
        View view3=getFormSimpleItem(context,"聊天消息翻译","","","");
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showTransSettingDialog(context);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    XposedBridge.log(e);
                }
            }
        });
        linearLayout.addView(view3);
        return linearLayout;
    }

    private View getFingerPrintView(final Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LinearLayout linearLayout=getLinearLayout(context);
        linearLayout.addView(getFormSwitchItem(context,"开启指纹支付",SettingUtils.SETTING_KEY_STARTFINGERPRINT));
        final String key=SettingUtils.SETTING_KEY_FINGERPSW;
        View view=getFormSimpleItem(context,"支付密码",SettingUtils.getString(context,key).equals("")?"未保存":"已保存",key,"");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int padding= DensityUtil.dip2px(context,20.0f);
                //去除editView焦点
                LinearLayout linearLayout=new LinearLayout(context);
                linearLayout.setFocusable(true);
                linearLayout.setFocusableInTouchMode(true);
                final EditText editText=new EditText(context);
                editText.setTextColor(Color.BLACK);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                try {
                    editText.setText(PasswordUil.Decrypt(SettingUtils.getString(context,key)));
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(context,40.0f));
                layoutParams.setMargins(padding,DensityUtil.dip2px(context,10.0f),padding,10);
                editText.setLayoutParams(layoutParams);
                linearLayout.addView(editText);
                AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("输入密码")
                        .setView(linearLayout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    SettingUtils.setString(context,key, PasswordUil.Encrypt(editText.getText().toString()));
                                } catch (Exception e) {
                                    XposedBridge.log(e);
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
            }
        });
        linearLayout.addView(view);
        return linearLayout;
    }


    private View getOtherView(final Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final LinearLayout linearLayout=getLinearLayout(context);
        linearLayout.addView(getFormSwitchItem(context,"屏蔽更新提醒",SettingUtils.SETTING_KEY_PREUPDATA));
        linearLayout.addView(getFormSwitchItem(context,"自定义字体大小",SettingUtils.SETTING_KEY_DIYFONT));
        linearLayout.addView(getFormSimpleItem(context,"设置字体大小","",SettingUtils.SETTING_KEY_FONTSIZE,"16"));
        linearLayout.addView(getFormSwitchItem(context,"发送网络表情（长按笑脸按钮）",SettingUtils.SETTING_KEY_DOUTU));
        linearLayout.addView(getFormSwitchItem(context,"收藏更多表情（保存在本地）",SettingUtils.SETTING_KEY_SAVEMOREPIC));
        linearLayout.addView(getFormSwitchItem(context,"扫一扫自动登录",SettingUtils.SETTING_KEY_QRLOGIN));
        linearLayout.addView(getFormSwitchItem(context,"自定义骰子/猜拳",SettingUtils.SETTING_KEY_CHEAT));
        /**View view=getFormSimpleItem(context,"自定义骰子/猜拳","",SettingUtils.SETTING_KEY_CHEAT,"");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout1=getLinearLayout(context);
                try {

                    linearLayout1.addView(getFormSwitchItem(context,"预先设置结果",SettingUtils.SETTING_KEY_CHEATBEFORE));
                    View touzi=getFormSimpleItem(context,"预设骰子结果",String.valueOf(SettingUtils.getInt(context,SettingUtils.SETTING_KEY_CHEATBEFORETOUCI)+1),SettingUtils.SETTING_KEY_CHEATBEFORETOUCI,"0");
                    touzi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                    .setTitle("自定义骰子")
                                    .setSingleChoiceItems(diceItem, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            diceNum=which;
                                        }
                                    })
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SettingUtils.setInt(context,SettingUtils.SETTING_KEY_CHEATBEFORETOUCI,diceNum);
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        }
                    });
                    int num=SettingUtils.getInt(context,SettingUtils.SETTING_KEY_CHEATBEFORECAIQUAN);
                    View caiquan=getFormSimpleItem(context,"预设猜拳结果",num==0?"石头":num==1?"剪刀":"布",SettingUtils.SETTING_KEY_CHEATBEFORECAIQUAN,"0");
                    caiquan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                    .setTitle("自定义猜拳")
                                    .setSingleChoiceItems(morraItem, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            morraNum=which;
                                        }
                                    })
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SettingUtils.setInt(context,SettingUtils.SETTING_KEY_CHEATBEFORECAIQUAN,morraNum);
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        }
                    });
                    linearLayout1.addView(touzi);
                    linearLayout1.addView(caiquan);
                    linearLayout1.addView(getFormSwitchItem(context,"发送骰子/猜拳时选择点数",SettingUtils.SETTING_KEY_CHEATNOW));

                } catch (Exception e){
                    XposedBridge.log(e);
                }
                AlertDialog alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("自定义骰子/猜拳")
                        .setMessage("如果启用发送骰子/猜拳时选择点数时发送表情时闪退，请使用预设值,修改预设值重启QQ生效")
                        .setView(linearLayout1)
                        .create();
                alertDialog.show();
            }
        });
        linearLayout.addView(view);**/
        return linearLayout;
    }


    private LinearLayout getLinearLayout(Context context){
        int padding= DensityUtil.dip2px(context,20.0f);
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(padding,15,padding,25);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(new ColorDrawable(Color.GRAY));
        linearLayout.setDividerPadding(15);
        return linearLayout;
    }

    private View getFormSwitchItem(Context context,String text,String key) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        boolean isCheck=SettingUtils.getBoolean(context,key);
        Object formSwitchItem=mFormSwitchItem.getConstructor(Context.class,AttributeSet.class).newInstance(context,null);
        XposedHelpers.callMethod(formSwitchItem,"setText",text);
        XposedHelpers.callMethod(formSwitchItem,"setOnCheckedChangeListener",new SwitchListener());
        XposedHelpers.callMethod(formSwitchItem,"setChecked",isCheck);
        //防止夜间主题影响控件背景和字体颜色
        Method method=getMethod(mFormSwitchItem,"a","TextView");
        if (method!=null){
            TextView textView=(TextView) method.invoke(formSwitchItem);
            textView.setTextColor(Color.BLACK);
        }
        Method method1=getMethod(mFormSwitchItem,"a","Switch");
        if (method1!=null){
            View view=(View)method1.invoke(formSwitchItem);
            view.setTag(key);
        }
        RelativeLayout relativeLayout=(RelativeLayout)formSwitchItem;
        relativeLayout.setBackground(null);
        return relativeLayout;
    }

    private View getFormSimpleItem(Context context,String leftText,String rightText,String key,String defaelyValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object formSimpleItem=mFormSimpleItem.getConstructor(Context.class, AttributeSet.class).newInstance(context,null);
        XposedHelpers.callMethod(formSimpleItem,"setLeftText",leftText);
            XposedHelpers.callMethod(formSimpleItem,"setRightText", rightText);
        //防止夜间主题影响控件背景和字体颜色
        Method method=getMethod(mFormSimpleItem,"a","TextView");
        if (method!=null){
            TextView textView=(TextView) method.invoke(formSimpleItem);
            textView.setTextColor(Color.BLACK);
        }
        RelativeLayout relativeLayout=(RelativeLayout)formSimpleItem;
        relativeLayout.setBackground(null);
        relativeLayout.setTag(R.id.tag_first,leftText);
        relativeLayout.setTag(R.id.tag_second,key);
        relativeLayout.setTag(R.id.tag_third,defaelyValue);
        relativeLayout.setOnClickListener(new ItemOnClickListener());
        return relativeLayout;
    }
}
