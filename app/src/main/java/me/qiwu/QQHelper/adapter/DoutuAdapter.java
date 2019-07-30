package me.qiwu.QQHelper.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.hooks.ChatFragmentHook;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.QQHelper;

/**
 * Created by Deng on 2018/7/26.
 */

public class    DoutuAdapter extends BaseAdapter {
    private List mdata;//图片
    private int page=0;//当前图片页数
    private List<String>mImgUrl=new ArrayList<>();
    private SparseBooleanArray isSelected=new SparseBooleanArray();
    private Context context;
    private Class<?>mChatActivityFacade;
    public DoutuAdapter(Context context, Class<?>mChatActivityFacade){
        this.context=context;
        this.mChatActivityFacade=mChatActivityFacade;
        mdata=new ArrayList();
    }
    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mdata==null||mdata.isEmpty()){
            return null;
        }
        ViewHolder viewHolder=null;
        if (convertView==null){
            FrameLayout frameLayout=(FrameLayout)getView(context);
            convertView=frameLayout;
            viewHolder=new ViewHolder();
            viewHolder.img=(ImageView)frameLayout.getChildAt(0);
            viewHolder.checkBox=(ImageView)frameLayout.getChildAt(1);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(3, Color.parseColor("#ff4284f3"));
        drawable.setColor(Color.parseColor("#00000000"));
        viewHolder.checkBox.setBackground(drawable);
        viewHolder.checkBox.setVisibility(isSelected.get(position)?View.VISIBLE:View.GONE);
        viewHolder.img.setBackground((Drawable)mdata.get(position));
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected.put(position,!isSelected.get(position));
                finalViewHolder.checkBox.setVisibility(isSelected.get(position)?View.VISIBLE:View.GONE);
            }
        });
        return convertView;
    }


    public void updataData(Drawable drawable,String url){
        if (!mImgUrl.contains(url)){
            mdata.add(drawable);
            isSelected.put(getCount(),false);
            mImgUrl.add(url);
        }
    }

    public void saveAndSendPic(){
        if (isSelected.size()!=0){
            final File file=new File(Environment.getExternalStorageDirectory().getPath()+"/QQHelper/imgTemp");
            if (file.isFile())return;
            if (!file.exists())file.mkdirs();
            for (int i=0;i<isSelected.size();i++){
                if (isSelected.get(i)){
                    try {
                        String url=mImgUrl.get(i);
                        final String s=(String) XposedHelpers.callMethod(mdata.get(i),"saveTo",file.getPath()+url.substring(url.lastIndexOf("/")));
                        XposedHelpers.callStaticMethod(mChatActivityFacade,"a",
                                QQHelper.getQQAppInterface(),
                                context,
                                ChatFragmentHook.currentSessionInfo,
                                s,
                                1034);
                    }catch (Exception e){
                        XposedBridge.log(e);
                    }
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        f.delete();
                    }
                }
            },3000);
        }

    }

    public int getPage(){
        return this.page;
    }
    public void setPage(int num){
        page=num;
    }
    public boolean isMaxPage(){
        return page==50;
    }

    public void reload(){
        page=0;
        mdata.clear();
        isSelected.clear();
        mImgUrl.clear();
        notifyDataSetChanged();
    }

    private View getView(Context context){
        int wh= DensityUtil.dip2px(context,120.0f);
        int padding= DensityUtil.dip2px(context,5.0f);
        FrameLayout frameLayout=new FrameLayout(context);
        frameLayout.setPadding(0,padding,0,padding);
        ImageView imageView=new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(wh,wh);
        layoutParams.gravity= Gravity.CENTER;
        imageView.setLayoutParams(layoutParams);
        ImageView imageView1=new ImageView(context);
        FrameLayout.LayoutParams layoutParams1=new FrameLayout.LayoutParams(wh+5,wh+5);
        layoutParams1.gravity= Gravity.CENTER;
        imageView1.setLayoutParams(layoutParams1);
        frameLayout.addView(imageView1);
        frameLayout.addView(imageView);
        return frameLayout;
    }

    class ViewHolder{
        public ImageView img;
        public ImageView checkBox;
    }
}
