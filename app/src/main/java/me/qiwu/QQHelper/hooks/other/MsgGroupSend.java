package me.qiwu.QQHelper.hooks.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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

/**
 * Created by Deng on 2018/8/6.
 */

public class MsgGroupSend extends ReflectHelper {
    private String sendMsg;
    private Class<?>mChatActivityFacade;
    private Class<?>mChatActivityFacade$SendMsgParams;
    private TroopAndFriendSelectAdpter troopAndFriendSelectAdpter;
    public MsgGroupSend(ClassLoader classLoader) throws ClassNotFoundException {
        mChatActivityFacade=classLoader.loadClass(ChatActivityFacade);
        mChatActivityFacade$SendMsgParams=classLoader.loadClass(ChatActivityFacade$SendMsgParams);
    }

    public void showWriteMsgEditDialog(final Context context){
        LinearLayout linearLayout=getEditView(context);
        final EditText editText=(EditText)linearLayout.getChildAt(0);
        AlertDialog alertDialog=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("输入群发文本")
                .setView(linearLayout)
                .setPositiveButton("选择群发对象",null)
                .setNegativeButton("取消",null)
                .create();
        alertDialog.show();
        setEditDialogStyle(alertDialog);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=editText.getText().toString();
                if (msg.isEmpty()||msg.equals("")){
                    Toast.makeText(context,"请输入文本消息",Toast.LENGTH_SHORT).show();
                }else {
                    sendMsg=msg;
                    try {
                        showSelectDialog(context);
                    } catch (Exception e){
                        XposedBridge.log(e);
                    }
                }
            }
        });
    }

    private LinearLayout getEditView(Context context){
        int padding= DensityUtil.dip2px(context,20.0f);
        //去除editView焦点
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        EditText editText=new EditText(context);
        editText.setTextColor(Color.BLACK);
        editText.setSingleLine(false);
        editText.setMinLines(4);
        editText.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(padding,DensityUtil.dip2px(context,10.0f),padding,10);
        editText.setLayoutParams(layoutParams);
        linearLayout.addView(editText);
        return linearLayout;
    }
    private void setEditDialogStyle(AlertDialog alertDialog){
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(20.0f);
        } catch (Exception e){
            XposedBridge.log(e);
        }
    }
    private void showSelectDialog(final Context context) throws InvocationTargetException, IllegalAccessException {
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
                                    XposedHelpers.callStaticMethod(mChatActivityFacade,"a",QQHelper.getQQAppInterface(),context,QQHelper.getSessionInfo(contactInfo.getUin(),contactInfo.getIstroop()),sendMsg,new ArrayList<>(),mChatActivityFacade$SendMsgParams.newInstance());
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
