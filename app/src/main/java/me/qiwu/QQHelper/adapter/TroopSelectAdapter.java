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
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.QQHelper;

/**
 * Created by Deng on 2018/7/20.
 */

public class TroopSelectAdapter extends BaseAdapter {
    private List<Map<String,Object>>mdata;
    private List<Map<String,Object>>originalmdata=new ArrayList<Map<String, Object>>();
    private Context context;
    private ArrayList troopname;
    private ArrayList troopDrawable;
    private ArrayList troopuin;
    private ArrayList isSelectUin;
    private Map<String,Boolean>isSelected=new HashMap<>();
    private boolean isShowCheckBox;
    public TroopSelectAdapter(Context context, ArrayList isSelectUin, boolean isShowCheckBox) throws InvocationTargetException, IllegalAccessException {
        this.context=context;
        this.troopname=QQHelper.getTroopName();
        this.troopDrawable=QQHelper.getTroopDrawable();
        this.troopuin=QQHelper.getTroopUin();
        this.isShowCheckBox=isShowCheckBox;
        this.isSelectUin=isSelectUin;
        init();

    }

    private void init() {
        if (troopname==null||troopname.isEmpty()){
            Toast.makeText(context,"暂无可选择的群聊",Toast.LENGTH_SHORT).show();
        }else {
            mdata=new ArrayList<Map<String, Object>>();
            for (int i = 0; i < troopname.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("face", troopDrawable.get(i));
                map.put("name", troopname.get(i));
                map.put("uin",troopuin.get(i));
                mdata.add(map);
            }
            for (int i = 0; i <mdata.size(); i++) {
                String uin=mdata.get(i).get("uin").toString();
                if (isSelectUin==null||isSelectUin.isEmpty()){
                    isSelected.put(uin,false);
                }else if (isSelectUin.contains(uin)){
                    isSelected.put(uin, true);
                }else {
                    isSelected.put(uin,false);
                }
            }
            originalmdata.addAll(mdata);
        }

    }

    @Override
    public int getCount() {
        return mdata.size();
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
        if (troopname==null||troopname.isEmpty())return null;


        ViewHolder viewHolder=null;
        if (convertView==null){
            LinearLayout linearLayout=getListItem(context);
            viewHolder=new ViewHolder();
            convertView=linearLayout;
            viewHolder.img=(ImageView) linearLayout.getChildAt(1);
            viewHolder.title=(TextView)linearLayout.getChildAt(2);
            viewHolder.cBox=(CheckBox)linearLayout.getChildAt(0);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.img.setBackground((Drawable) mdata.get(position).get("face"));
        viewHolder.title.setText(mdata.get(position).get("name").toString());
        if (isShowCheckBox){
            viewHolder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSelected.put(mdata.get(position).get("uin").toString(),isChecked);
                }
            });
            viewHolder.cBox.setChecked(isSelected.get(mdata.get(position).get("uin").toString()));
        }
        return convertView;
    }

    public String getSelectUin(){
        String allSelectUin="";
        for (int i=0;i<isSelected.size();i++){
            if (troopuin==null||troopuin.isEmpty())return "";
            String uin=(String) troopuin.get(i);
            if (isSelected.get(uin)){
                allSelectUin=allSelectUin+uin+",";
            }
        }
        return allSelectUin.substring(0,allSelectUin.length()-1);
    }

    public void setSelectAll(){
        if (mdata==null||mdata.isEmpty())return;
        for (int i=0;i<mdata.size();i++){
            isSelected.put(mdata.get(i).get("uin").toString(),true);
        }
        notifyDataSetChanged();
    }

    public void setSearch(String msg){
        if (troopname==null||troopname.isEmpty())return;
        mdata.clear();
        if (msg==null||msg.isEmpty()||msg.equals("")){
            mdata.addAll(originalmdata);
            notifyDataSetChanged();
        }else {
            for (int i = 0; i < troopname.size(); i++) {
                if (troopname.get(i).toString().contains(msg)){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("face", troopDrawable.get(i));
                    map.put("name", troopname.get(i));
                    map.put("uin",troopuin.get(i));
                    mdata.add(map);
                }
            }
            notifyDataSetChanged();
        }


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

    public void click(View view){
        ViewHolder viewHolder=(ViewHolder)view.getTag();
        viewHolder.cBox.toggle();
    }
    class ViewHolder {
        public ImageView img;
        public TextView title;
        public CheckBox cBox;
    }
}
