package me.qiwu.QQHelper.hooks.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.QQHelper.utils.DensityUtil;
import me.qiwu.QQHelper.utils.HttpUtil;
import me.qiwu.QQHelper.utils.ReflectHelper;
import me.qiwu.QQHelper.adapter.DoutuAdapter;
import me.qiwu.QQHelper.utils.SettingUtils;

/**
 * Created by Deng on 2018/7/24.
 */

public class Doutu extends ReflectHelper {
    private Class<?>mAIOPanelUtiles;
    private Class<?>mURLDrawable;
    private Class<?>mChatActivityFacade;
    private Class<?>mPanelIconLinearLayout;
    private DoutuAdapter doutuAdapter;
    private SearchView searchView;
    private SettingUtils  settingUtils=new SettingUtils();
    public Doutu(ClassLoader classLoader) throws ClassNotFoundException {;
        mAIOPanelUtiles=classLoader.loadClass(AIOPanelUtiles);
        mURLDrawable=classLoader.loadClass(URLDrawable);
        mChatActivityFacade=classLoader.loadClass(ChatActivityFacade);
        mPanelIconLinearLayout=classLoader.loadClass(PanelIconLinearLayout);
    }

    public void init(){
        if (settingUtils.getBoolean(SettingUtils.SETTING_KEY_DOUTU)){
            Method method=getMethod(mPanelIconLinearLayout,"a","void");
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    LinearLayout linearLayout=(LinearLayout)param.thisObject;
                    if (linearLayout.getChildCount()==4)return;
                    //群聊界面有个隐藏的机器人按钮
                    int num=linearLayout.getChildCount()-2;//非群聊界面的笑脸按钮在倒数第二个
                    if (linearLayout.getChildAt(num+1).getVisibility()==View.GONE)num=num-1;//群聊界面在倒数第三个
                    linearLayout.getChildAt(num).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                showDoutuDialog(v.getContext());
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                            return true;
                        }
                    });
                }
            });
        }

    }

    private void showDoutuDialog(final Context context) throws Exception{
        LinearLayout linearLayout=(LinearLayout)getView(context);
        AlertDialog alertDialog=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("搜索网络表情")
                .setCancelable(false)
                .setView(linearLayout)
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doutuAdapter.saveAndSendPic();
                    }
                })
                .setNegativeButton("取消",null)
                .setNeutralButton("加载更多", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadMorePic(doutuAdapter,context,searchView,searchView.getQuery().toString(),true);
                    }
                })
                .create();
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
                loadMorePic(doutuAdapter,context,searchView,searchView.getQuery().toString(),false);
            }
        });
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

    private View getView(final Context context) throws Exception {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        GridView gridView=new GridView(context);
        gridView.setColumnWidth(DensityUtil.dip2px(context,60.0f));
        gridView.setNumColumns(2);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setSelector(android.R.color.transparent);
        gridView.setGravity(Gravity.CENTER);
        doutuAdapter=new DoutuAdapter(context,mChatActivityFacade);
        gridView.setAdapter(doutuAdapter);
        searchView=new SearchView(context);
        //隐藏searchView的搜索图标
        int magId = context.getResources().getIdentifier("android:id/search_mag_icon",null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,40.0f));
        layoutParams.setMargins(DensityUtil.dip2px(context,20.0f),DensityUtil.dip2px(context,10.0f),DensityUtil.dip2px(context,20.0f),10);
        searchView.setLayoutParams(layoutParams);
        searchView.setQueryHint("搜索");
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                loadMorePic(doutuAdapter,context,searchView,query,true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        linearLayout.addView(searchView);
        linearLayout.addView(gridView);
        return linearLayout;
    }

    private void loadMorePic(final DoutuAdapter doutuAdapter, final Context context, final SearchView searchView, final String query, final boolean isNewMessage){
        doutuAdapter.notifyDataSetChanged();
        if (!isNewMessage){
            if (doutuAdapter.isMaxPage()){
                Toast.makeText(context,"没有更多图片",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (query==null||query.isEmpty()||query.equals(""))return;
        if (isNewMessage){
            doutuAdapter.reload();
        }
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //隐藏软键盘
                InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                doutuAdapter.notifyDataSetChanged();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                int page=doutuAdapter.getPage()+1;
                int morePage=doutuAdapter.getPage()+3;
                while (page!=morePage){
                    try {
                        URL url=new URL("https://www.doutula.com/api/search?keyword="+query+"&mime=0&page="+String.valueOf(page));
                        HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setReadTimeout(5000);
                        InputStream inputStream=httpURLConnection.getInputStream();
                        String msg=HttpUtil.InputStreamToString(inputStream);
                        if (msg.equals("")){
                            XposedBridge.log("获取图片信息出错");
                        }else {
                            JSONObject jsonObject=new JSONObject(msg);
                            int isNormal=jsonObject.getInt("status");
                            if (isNormal==0)return;
                            JSONObject jsonObject1= jsonObject.getJSONObject("data");
                            JSONArray jsonArray=jsonObject1.getJSONArray("list");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject2=jsonArray.getJSONObject(i);
                                String s=jsonObject2.getString("image_url");
                                if (s.startsWith("http")&&(s.endsWith("jpg")||s.endsWith("gif"))){
                                    XposedBridge.log(s);
                                    doutuAdapter.updataData(getImg(new URL(s)),s);
                                }
                            }
                            handler.sendMessage(Message.obtain());
                            int num=jsonObject1.getInt("more");
                            if (num==0||page+1>50){
                                page=morePage;
                                doutuAdapter.setPage(50);
                            }else {
                                page=page+1;
                                doutuAdapter.setPage(page);
                            }
                        }
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            }
        }).start();
    }

    private  Drawable getImg(URL url){
        Object img=XposedHelpers.callStaticMethod(mURLDrawable,"getDrawable",url,0,0,null,null,true);
        return (Drawable)img;
    }


}
