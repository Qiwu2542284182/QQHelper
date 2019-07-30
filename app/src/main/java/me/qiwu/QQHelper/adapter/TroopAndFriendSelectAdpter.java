package me.qiwu.QQHelper.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import me.qiwu.QQHelper.beans.ContactInfo;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.QQHelper;

/**
 * Created by Deng on 2018/8/1.
 */

public class TroopAndFriendSelectAdpter extends BaseAdapter {
    private ArrayList<ContactInfo>mFriendInfo=new ArrayList<>();
    private ArrayList<ContactInfo>mGroupInfo=new ArrayList<>();
    private ArrayList<ContactInfo>mCurrentInfo=new ArrayList<>();
    private Map<String,Boolean>mIsSelect=new HashMap<>();
    private int mCurrentNum=0;//0为好友，1为群聊
    private String searchMsg="";
    private Context context;

    public TroopAndFriendSelectAdpter(Context context) throws InvocationTargetException, IllegalAccessException {
        this.context=context;
        init();
    }

    private void init() throws InvocationTargetException, IllegalAccessException {
        ArrayList mFriendName = QQHelper.getFriendNick();
        ArrayList mFriendUin = QQHelper.getFriendUin();
        ArrayList mFriendDrawable = QQHelper.getFriendDawable();
        ArrayList mTroopName = QQHelper.getTroopName();
        ArrayList mTroopUin = QQHelper.getTroopUin();
        ArrayList mTroopDrawable = QQHelper.getTroopDrawable();
        if (mFriendName!=null){
            for (int i=0;i<mFriendName.size();i++){
                ContactInfo contactInfo=new ContactInfo();
                contactInfo.setHead((Drawable) mFriendDrawable.get(i));
                contactInfo.setUin((String) mFriendUin.get(i));
                contactInfo.setName((String)mFriendName.get(i));
                contactInfo.setIstroop(0);
                mFriendInfo.add(contactInfo);
                mIsSelect.put(contactInfo.getId(),false);
            }
        }
        if (mTroopName!=null){
            for (int i=0;i<mTroopName.size();i++){
                ContactInfo contactInfo=new ContactInfo();
                contactInfo.setHead((Drawable) mTroopDrawable.get(i));
                contactInfo.setUin((String) mTroopUin.get(i));
                contactInfo.setName((String)mTroopName.get(i));
                contactInfo.setIstroop(1);
                mGroupInfo.add(contactInfo);
                mIsSelect.put(contactInfo.getId(),false);
            }
        }
        mCurrentInfo.addAll(mFriendInfo);
    }
    @Override
    public int getCount() {
        return mCurrentInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            LinearLayout linearLayout=getListItem(context);
            convertView=linearLayout;
            viewHolder.cBox=(CheckBox) linearLayout.getChildAt(0);
            viewHolder.img=(ImageView)linearLayout.getChildAt(1);
            viewHolder.title=(TextView)linearLayout.getChildAt(2);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.title.setText(mCurrentInfo.get(position).getName());
        viewHolder.img.setBackground(mCurrentInfo.get(position).getHead());
        viewHolder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               mIsSelect.put(mCurrentInfo.get(position).getId(),isChecked);
            }
        });
        viewHolder.cBox.setChecked(mIsSelect.get(mCurrentInfo.get(position).getId()));
        return convertView;
    }

    private LinearLayout getListItem(Context context){
        int padding= DensityUtil.dip2px(context,20.0f);
        int imgPadding= DensityUtil.dip2px(context,10.0f);
        int imgHeight= DensityUtil.dip2px(context,40.0f);
        LinearLayout linearLayout=new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(padding,15,padding,25);
        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity= Gravity.CENTER_VERTICAL;
        CheckBox check=new CheckBox(context);
        check.setFocusable(false);
        check.setClickable(false);
        ImageView imageView=new ImageView(context);
        LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(imgHeight,imgHeight);
        layoutParams2.gravity=Gravity.CENTER_VERTICAL;
        layoutParams2.setMargins(imgPadding,0,imgPadding,0);
        imageView.setLayoutParams(layoutParams2);
        TextView textView=new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18.0f);
        linearLayout.addView(check,layoutParams1);
        linearLayout.addView(imageView);
        linearLayout.addView(textView,layoutParams1);
        return linearLayout;

    }

    public void setmFriendInfo(){
        if (mCurrentNum==1){
            mCurrentNum=0;
            mCurrentInfo.clear();
            mCurrentInfo.addAll(mFriendInfo);
            notifyDataSetChanged();
        }

    }

    public void setmGroupInfo(){
        if (mCurrentNum==0){
            mCurrentNum=1;
            mCurrentInfo.clear();
            mCurrentInfo.addAll(mGroupInfo);
            notifyDataSetChanged();
        }
    }

    public void setData(String searchMsg){
        this.searchMsg=searchMsg;
        mCurrentInfo.clear();
        if (searchMsg.equals("")||searchMsg.isEmpty()){
            if (mCurrentNum==0){
                mCurrentInfo.addAll(mFriendInfo);
            }else if (mCurrentNum==1){
                mCurrentInfo.addAll(mGroupInfo);
            }
        }else {
            if (mCurrentNum==0){
                for (int i=0;i<mFriendInfo.size();i++){
                    if (mFriendInfo.get(i).getName().contains(searchMsg)){
                        mCurrentInfo.add(mFriendInfo.get(i));
                    }
                }
            }else if (mCurrentNum==1){
                for (int i=0;i<mGroupInfo.size();i++){
                    if (mGroupInfo.get(i).getName().contains(searchMsg)){
                        mCurrentInfo.add(mGroupInfo.get(i));
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setAllSelect(){
        for (int i=0;i<mCurrentInfo.size();i++){
            mIsSelect.put(mCurrentInfo.get(i).getId(),true);
        }
        if (mCurrentNum==0){
            if (searchMsg.equals("")){
                for (int i=0;i<mFriendInfo.size();i++){
                    mIsSelect.put(mFriendInfo.get(i).getId(),true);
                }
            }else {
                for (int i=0;i<mFriendInfo.size();i++){
                    if (mFriendInfo.get(i).getName().contains(searchMsg)) {
                        mIsSelect.put(mFriendInfo.get(i).getId(),true);
                    }
                }
            }
        }else if (mCurrentNum==1){
            if (searchMsg.equals("")){
                for (int i=0;i<mGroupInfo.size();i++){
                    mIsSelect.put(mGroupInfo.get(i).getId(),true);
                }
            }else {
                for (int i=0;i<mGroupInfo.size();i++){
                    if (mGroupInfo.get(i).getName().contains(searchMsg)) {
                        mIsSelect.put(mGroupInfo.get(i).getId(),true);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList getSelectInfo(){
        ArrayList arrayList=new ArrayList();
        for (int i=0;i<mFriendInfo.size();i++){
            if (mIsSelect.get(mFriendInfo.get(i).getId())){
                arrayList.add(mFriendInfo.get(i));
            }
        }
        for (int i=0;i<mGroupInfo.size();i++){
            if (mIsSelect.get(mGroupInfo.get(i).getId())){
                arrayList.add(mGroupInfo.get(i));
            }
        }
        return arrayList;
    }

    public class ViewHolder {
        public ImageView img;
        public TextView title;
        public CheckBox cBox;
    }
}
